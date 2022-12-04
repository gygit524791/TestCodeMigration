package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 映射规则的自动写入
 * mappingRule
 */
public class MappingRuleWriter {

    public static void writeApiMappingProperties(String key, String value) {
        try {
            writeProperties("mappingRule/apiMapping.properties",key, value);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeClassMappingProperties(String key, String value) throws IOException {
        writeProperties("mappingRule/classMapping.properties",key, value);
    }

    public static void writeOtherMappingProperties(String key, String value) throws IOException {
        writeProperties("mappingRule/otherMapping.properties",key, value);
    }

    public static void writePropertiesMappingProperties(String key, String value) throws IOException {
        writeProperties("mappingRule/propertyMapping.properties",key, value);
    }

    private static void writeProperties(String propertyName, String key, String value) throws FileNotFoundException {
        Properties properties = new Properties();
        // 使用“输出流”，将Properties集合中的KV键值对，写入*.properties文件
        // 注意这个路径是编译后的文件路径 在target目录下
        String filepath = ClassLoader.getSystemResource(propertyName).getPath();
        FileOutputStream fos = new FileOutputStream(filepath, true);

        try {
            properties.setProperty(key, value);
            properties.store(new OutputStreamWriter(fos, StandardCharsets.UTF_8), StringUtils.EMPTY);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
