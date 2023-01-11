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
        bufferedWriter.flush();
        bufferedWriter.close();
    }


}
