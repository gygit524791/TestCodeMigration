package utils;

import com.test.migration.entity.TaskParameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TaskParameterReader {
    private static TaskParameter taskParameter;

    public static TaskParameter getTaskParameter() {
        if (taskParameter != null) {
            return taskParameter;
        }
        taskParameter = buildTaskParameter();
        return taskParameter;
    }

    public static TaskParameter buildTaskParameter() {
        String userDir = System.getProperty("user.dir");
        String pythonPath = userDir + File.separator + "doc" + File.separator + "python" + File.separator;
        String word2vecPath = userDir + File.separator + "doc" + File.separator + "word2vec";

        // python文件路径
        String pythonCppExtractor = pythonPath + "CppExtractor.py";
        String pythonWordVec = pythonPath + "WordVec.py";
        String pythonCalcTokenVec = pythonPath + "CalcTokenVec.py";
        String pythonCalcSimilarity = pythonPath + "CalcSimilarity.py";

        //中间过程：生成词向量文件的路径
        String corpusFilepath = word2vecPath + File.separator + "corpus.txt";
        String wordVecModelFilepath = word2vecPath + File.separator + "word2vec.model";
        String apiVectorDictFilepath = word2vecPath + File.separator + "apiVectorDict.txt";
        String classVectorDictFilepath = word2vecPath + File.separator + "classVectorDict.txt";
        // 文件输出路径
        String outputFilepath = userDir + File.separator + "output" + File.separator;
        //临时数据库文件路径
        String dbFilepath = userDir + File.separator + "data.db";

        InputStream in = ClassLoader.getSystemResourceAsStream("task.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return TaskParameter.builder()
                .taskId(Integer.parseInt(properties.getProperty("taskId")))
                .sourceFilepath(properties.getProperty("sourceFilepath"))
                .targetFilepath(properties.getProperty("targetFilepath"))
                .targetSourceCodeFilepath(properties.getProperty("targetSourceCodeFilepath"))
                .outputFilepath(outputFilepath)
                .pythonBinPath(properties.getProperty("pythonBinPath"))
                .corpusFilepath(corpusFilepath)
                .wordVecModelFilepath(wordVecModelFilepath)
                .pythonCppExtractor(pythonCppExtractor)
                .pythonWordVec(pythonWordVec)
                .pythonCalcTokenVec(pythonCalcTokenVec)
                .pythonCalcSimilarity(pythonCalcSimilarity)
                .dbFilepath(dbFilepath)
                .apiVectorDictFilepath(apiVectorDictFilepath)
                .classVectorDictFilepath(classVectorDictFilepath)
                .apiWordListFilepath(properties.getProperty("apiWordListFilepath"))
                .build();

    }


}
