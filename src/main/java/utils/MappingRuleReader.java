package utils;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class MappingRuleReader {

    public static List<String> readLinesFromProperties(String propertyName) {
        List<String> lines = Lists.newArrayList();
        String filepath = ClassLoader.getSystemResource(propertyName).getPath();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filepath));
            while (br.readLine() != null) {
                String line = br.readLine();
                if (line.trim().startsWith("#")) {
                    continue;
                }
                lines.add(br.readLine());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return lines;
    }
}
