import CppHeaderParser

# cppHeader = CppHeaderParser.CppHeader("../doc/harmony/demo/SimpleClass.h")


# 获取include的头文件名
# print(cppHeader.includes)

# 获取头文件中定义的类名
# for classname in cppHeader.classes.keys():
#     print(classname)

# 获取类所属的namespace
# print(cppHeader.classes['SampleClass']['namespace'])
# print(cppHeader.classes['AlphaClass']['namespace'])
# print(cppHeader.classes['OmegaClass']['namespace'])

# 获取类里的public方法
# for oneMethod in cppHeader.classes['AlphaClass']['methods']['public']:
#     print(oneMethod['name'])
# for kv in oneMethod.items():
#     print(kv[0], " : ", kv[1])

# print('---------------------\n')

import json
# from cppExtractor.HarmonyApiDocument import HarmonyApiDocument


import sys

def fetchCppHFileApiBasic(filepath: str):
    apiResultLines = list()
    failParseFiles = list()
    try:
        cppHeader = CppHeaderParser.CppHeader(filepath)
        for className in cppHeader.classes.keys():
            for method in cppHeader.classes[className]['methods']['public']:
                apiResultLines.append(className + "," + method['name'])
    except Exception as e:
        failParseFiles.append(filepath)

    print(apiResultLines)
    # print("解析失败文件：", failParseFiles)
    return apiResultLines


if __name__ == '__main__':
    fetchCppHFileApiBasic(sys.argv[1])

