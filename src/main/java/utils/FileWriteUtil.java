package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class FileWriteUtil {

    public static void writeDataToFile(List<String> dataLines, String filePath) throws Exception {
        BufferedWriter bufferedWriter = null;
        bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
        for (String dataLine : dataLines) {
            bufferedWriter.write(dataLine + "\n");
        }
        bufferedWriter.close();
    }

    public static void main(String[] args) throws Exception {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/gaoyi/IdeaProjects/testSoftware/doc/demodata/result_android.txt"));
        for (int i = 0; i < 100; i++) {
            bufferedWriter.write(i + "123" + "\n");
        }
        bufferedWriter.close();
    }

}
