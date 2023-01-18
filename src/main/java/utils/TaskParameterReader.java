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
        Log.info("task parameter: "+taskParameter);
        return taskParameter;
    }

    public static TaskParameter buildTaskParameter() {
        String userDir = System.getProperty("user.dir");
        String pythonPath = userDir + File.separator + "model" + File.separator + "python" + File.separator;
        String word2vecPath = userDir + File.separator + "model" + File.separator + "word2vec" + File.separator;

        // python文件路径
        String pythonCppExtractor = pythonPath + "CppExtractor.py";
        String pythonWordVec = pythonPath + "WordVec.py";
        String pythonCalcTokenVec = pythonPath + "CalcTokenVec.py";
        String pythonCalcSimilarity = pythonPath + "CalcSimilarity.py";

        //中间过程：生成词向量文件的路径
        String corpusFilepath = word2vecPath + "corpus.txt";
        String wordVecModelFilepath = word2vecPath + "word2vec.model";
        String apiVectorDictFilepath = word2vecPath + "apiVectorDict.txt";
        String classVectorDictFilepath = word2vecPath + "classVectorDict.txt";
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

        // 获取文件路径
        String sourceProjectFilepath = properties.getProperty("sourceProjectFilepath");
        String targetProjectFilepath = properties.getProperty("targetProjectFilepath");
        String sourceModuleFilepath = properties.getProperty("sourceModuleFilepath");
        String targetModuleFilepath = properties.getProperty("targetModuleFilepath");
//        String sourceFilepath

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
