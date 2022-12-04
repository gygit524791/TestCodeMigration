package utils;

import com.test.migration.entity.TaskParameter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TaskParameterReader {

    private static TaskParameter taskParameter;

    public static TaskParameter getTaskParameter() {
        if (taskParameter != null) {
            return taskParameter;
        }
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
                .pythonBinPath(properties.getProperty("pythonBinPath"))
                .corpusFilepath(properties.getProperty("corpusFilepath"))
                .wordVecModelFilepath(properties.getProperty("wordVecModelFilepath"))
                .pythonCppExtractor(properties.getProperty("pythonCppExtractor"))
                .pythonWordVec(properties.getProperty("pythonWordVec"))
                .pythonCalcTokenVec(properties.getProperty("pythonCalcTokenVec"))
                .pythonCalcSimilarity(properties.getProperty("pythonCalcSimilarity"))
                .dbFilepath(properties.getProperty("dbFilepath"))
                .apiVectorDictFilepath(properties.getProperty("apiVectorDictFilepath"))
                .apiWordListFilepath(properties.getProperty("apiWordListFilepath"))
                .build();
    }


}
