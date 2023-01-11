import numpy as np
import ast
import sqlite3
import sys
import datetime
from nltk.corpus import wordnet as wn
from munkres import Munkres

from TokenMatch import match

"""
为了优化性能，需要将中间结果做缓存，避免重复计算：
缓存1. 每个单词对应的近义词集合
缓存2. 两个单词之间的相似度
缓存3. 两个短语之间的相似度

java调用python每次都是初始化dict 所以用redis存储
wordSysnDict = dict()
wordSimDict = dict()
phraseSimDict = dict()

"""
wordSysnDict = dict()
wordSimDict = dict()
phraseSimDict = dict()
tokenRelatedSimThreshold = 0.7
stringSimThreshold = 0.9995


def getVectorDict(vectorDictFilepath: str):
    """
    获取向量字典
    key：单词
    value：单词对应的向量
    :param apiVectorDictFilepath: 词向量文件
    :return:
    """
    apiVectorDict = dict()
    with open(vectorDictFilepath, 'r') as file:
        for line in file.readlines():
            kv = line.strip().split('|')
            list1 = kv[1].split(',')
            list2 = list()
            for i in list1:
                list2.append(float(i))
            arr = np.array(list2)
            apiVectorDict[str(kv[0])] = arr
    return apiVectorDict


def loadApiDataFromSQLLite(taskId: str, dbFilepath: str):
    conn = sqlite3.connect(dbFilepath)
    # api_basic表结构:
    # id, task_id, filepath, class_name, api_name,
    # type, method_word_sequence, token_sequence, token_vector
    cursor = conn.cursor()
    androidApiQuerySql = "select * from api_basic where task_id = " + taskId + " and type = 1"
    cursor.execute(androidApiQuerySql)
    androidApis = cursor.fetchall()

    harmonyApiQuerySql = "select * from api_basic where task_id = " + taskId + " and type = 0"
    cursor.execute(harmonyApiQuerySql)
    harmonyApis = cursor.fetchall()

    return androidApis, harmonyApis


def cos_sim(a, b):
    if not (np.any(a)) or not (np.any(b)):
        return 0
    a_norm = np.linalg.norm(a)
    b_norm = np.linalg.norm(b)
    cos = np.dot(a, b) / (a_norm * b_norm)
    return cos


"""
计算两个方法名向量相似度，判断是否匹配
筛选出的api完全匹配（同名api）可能有多个，这种情况下继续根据类名来筛选，eg：

harmony api：Animator#start
android api：AnimatorSet#start，Animator#start
预期结果：Animator#start 和 Animator#start

返回格式：找到匹配：harmonyId,androidMappingId;没有找到匹配：空字符串""
"""


def calculateStringSimilarity(androidApis, harmonyApi, apiVectorDict: dict, classNameVectorDict: dict):
    global stringSimThreshold
    androidApiSimDict = dict()
    harmonyApiVec = apiVectorDict[str(harmonyApi[0])]

    for androidApi in androidApis:
        androidApiVec = apiVectorDict[str(androidApi[0])]
        # dict结构：key：apiId，value：相似度
        androidApiSimDict[androidApi[0]] = cos_sim(harmonyApiVec, androidApiVec)

    # 改为二级筛选，先按照api来筛选，如果有多个最高相似匹配，就再按照class name来筛选
    # androidApiMappingTopKList：[1:1, 2:1, 3:1]
    androidApiMappingTopKList = sorted(androidApiSimDict.items(), key=lambda x: x[1], reverse=True)[0:5]
    # 字符相似度小于0.9995, 直接返回没匹配上 androidMappingTopKApi[0][1]:第1个匹配对的相似度大小
    topApiSimilarity = androidApiMappingTopKList[0][1]
    if topApiSimilarity < stringSimThreshold:
        return ""

    topSimApiDictList = list()
    for apiDict in androidApiMappingTopKList:
        if apiDict[1] >= topApiSimilarity:
            topSimApiDictList.append(apiDict)

    # todo 测试代码
    # return str(harmonyApi[0]) + '-' + str(topSimApiDicList[0][0])

    # 二级筛选
    # 如果匹配出的安卓api只有1个，直接返回匹配结果
    if len(topSimApiDictList) == 1:
        return str(harmonyApi[0]) + '-' + str(topSimApiDictList[0][0])

    # 二级筛选：根据class name的字符相似度选出最匹配的
    androidClassNameSimDict = dict()
    harmonyClassNameVec = classNameVectorDict[str(harmonyApi[0])]
    # androidApiDict：key：apiId value：classNameVector
    for androidSimApiDict in topSimApiDictList:
        androidClassNameSimDict[androidSimApiDict[0]] = cos_sim(harmonyClassNameVec, classNameVectorDict[str(androidSimApiDict[0])])
    androidClassNameSimDictTopKList = sorted(androidClassNameSimDict.items(), key=lambda x: x[1], reverse=True)[0:2]

    top = androidClassNameSimDictTopKList[0]

    return str(harmonyApi[0]) + '-' + str(top[0])


def calculateApiMappings(androidApis, harmonyApis, apiVectorDict, classVectorDict):
    """
    计算api mapping

    :param androidApis:
    :param harmonyApis:
    :param apiVectorDict:
    :return:
    """
    # [1-2,3-4,5-6]
    apiMappings = list()
    # 方法名相似度匹配
    for harmonyApi in harmonyApis:
        result = calculateStringSimilarity(androidApis, harmonyApi, apiVectorDict, classVectorDict)
        if result != "":
            apiMappings.append(result)
        else:
            # 方法名关联词相似度匹配
            result = tokenRelatedSimilarity(androidApis, harmonyApi)
            if result != "":
                apiMappings.append(result)
    return apiMappings


def generateWordSysnDict(taskId: str, dbFilepath: str):
    """
    生成word -> wn.synsets 字典，优化性能
    :param taskId:
    :param dbFilepath:
    :return:
    """
    global wordSysnDict
    if len(wordSysnDict.keys()) != 0:
        return

    conn = sqlite3.connect(dbFilepath)
    cursor = conn.cursor()
    # api_basic表结构:
    # id, task_id, filepath, class_name, api_name,
    # type, method_word_sequence, token_sequence, token_vector
    apiQuerySql = "select method_word_sequence from api_basic where task_id = " + taskId
    cursor.execute(apiQuerySql)
    methodWordSequences = cursor.fetchall()

    for wordSeq in methodWordSequences:
        words = wordSeq[0].split(',')
        for word in words:
            if wordSysnDict.keys().__contains__(word):
                continue
            wordSysnDict[word] = wn.synsets(word)


def generatePhraseSimGraph(sourceWords, targetWords):
    """
    phrase1: set pro1
    phrase2: set pro2

    =>

    graph:
    [
     [1,0],
     [0,0]
    ]
    """
    global wordSysnDict
    global wordSimDict

    graph = list()
    for sourceWord in sourceWords:
        wordSimList = list()
        for targetWord in targetWords:
            path_sim = 0
            wordSimKey = str(sourceWord) + "_" + str(targetWord)
            # 性能优化：之前已经计算过两个word之间的相似度了，那就返回之前的计算结果
            if wordSimDict.keys().__contains__(wordSimKey):
                path_sim = wordSimDict[wordSimKey]
            else:
                sourceSynset = wordSysnDict[sourceWord]
                targetSynset = wordSysnDict[targetWord]
                for ss in sourceSynset:
                    for ts in targetSynset:
                        sim = ss.path_similarity(ts)
                        if sim:
                            path_sim = max(path_sim, sim)
                wordSimDict[wordSimKey] = path_sim
            wordSimList.append(path_sim)
        graph.append(wordSimList)
    return graph


def calculateTotalSim(graph):
    """
    使用km算法来计算graph（两个短语组成）最大权值
    这里权值就是用wordnet计算出的两个单词的相似度
    :param graph:
    :return:
    """
    # km算法默认计算的是最小权匹配，这里做处理改为最大权值匹配
    cost_graph = []
    for row in graph:
        cost_row = []
        for col in row:
            cost_row += [- col]
        cost_graph += [cost_row]

    m = Munkres()
    indexes = m.compute(cost_graph)

    total = 0
    for row, column in indexes:
        value = graph[row][column]
        total += value

    return total


def calculatePhraseSimilarity(sourcePhrase, targetPhrase):
    """
    计算两个phrase之间的相似度

    :param sourcePhrase: set pro1
    :param targetPhrase: set pro2
    :return:
    """
    global phraseSimDict
    phraseSimKey = str(sourcePhrase) + "_" + str(targetPhrase)
    # 之前已经计算过两个phrase之间的相似度，直接返回
    if phraseSimDict.keys().__contains__(phraseSimKey):
        return phraseSimDict[phraseSimKey]
    else:
        sourceWords = sourcePhrase.split(',')
        targetWords = targetPhrase.split(',')
        graph = generatePhraseSimGraph(sourceWords, targetWords)
        totalSim = calculateTotalSim(graph)
        sim = totalSim / ((len(sourceWords) + len(targetWords)) / 2)
        phraseSimDict[phraseSimKey] = sim

        return sim


"""
计算两个方法名关联词相似度，判断是否匹配
"""


def tokenRelatedSimilarity(androidApis, harmonyApi):
    global tokenRelatedSimThreshold
    for androidApi in androidApis:
        sim = calculatePhraseSimilarity(androidApi[6], harmonyApi[6])
        if sim > tokenRelatedSimThreshold:
            return str(harmonyApi[0]) + '-' + str(androidApi[0])
    return ""


if __name__ == "__main__":
    # harmonyApiId, taskId, dbFilepath

    generateWordSysnDict(sys.argv[1], sys.argv[2])
    androidApis, harmonyApis = loadApiDataFromSQLLite(sys.argv[1], sys.argv[2])
    apiVectorDict = getVectorDict(sys.argv[3])
    classVectorDict = getVectorDict(sys.argv[4])
    resultLine = calculateApiMappings(androidApis, harmonyApis, apiVectorDict, classVectorDict)
    print(resultLine)

    # """
    # e1 = datetime.datetime.now()
    # generateWordSysnDict(
    #     '1',
    #     '/Users/gaoyi/IdeaProjects/TestMigrationV2/data.db'
    # )
    # e2 = datetime.datetime.now()
    # print(e2-e1)
    #
    #
    # e1 = datetime.datetime.now()
    # androidApis, harmonyApis = loadApiDataFromSQLLite(
    #     '1',
    #     '/Users/gaoyi/IdeaProjects/TestMigrationV2/data.db'
    # )
    # e2 = datetime.datetime.now()
    # print(e2-e1)
    #
    # e1 = datetime.datetime.now()
    # apiVectorDict = getVectorDict(
    #     '/Users/gaoyi/IdeaProjects/TestMigrationV2/doc/word2vec/apiVectorDict.txt'
    # )
    # classNameVectorDict = getVectorDict(
    #     '/Users/gaoyi/IdeaProjects/TestMigrationV2/doc/word2vec/classVectorDict.txt'
    # )
    # e2 = datetime.datetime.now()
    # print(e2-e1)
    #
    # e1 = datetime.datetime.now()
    # resultLine = calculateApiMappings(androidApis, harmonyApis, apiVectorDict, classNameVectorDict)
    # e2 = datetime.datetime.now()
    # print(e2-e1)
    #
    # print(resultLine)
    # """

# 0:00:00.687661
# 0:00:00.000294
# 0:00:00.002523
# 0:00:03.871548
# ['51-7', '58-31', '65-25', '68-4', '69-5', '70-3', '71-3', '72-2']
