package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 映射规则的自动写入
 * mappingRule
 */
public class MappingRuleWriter {
    public static void main(String[] args) {
        writeApiMappingProperties("1","3");
    }

    public static void writeApiMappingProperties(String key, String value) {
        try {
            writeProperties("mappingRule/apiMapping.properties",key, value);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeClassNameMappingProperties(String key, String value) throws IOException {
        writeProperties("mappingRule/classNameMapping.properties",key, value);
    }

    public static void writeOtherMappingProperties(String key, String value) throws IOException {
        writeProperties("mappingRule/otherMapping.properties",key, value);
    }

    private static void writeProperties(String propertyName, String key, String value) throws FileNotFoundException {
        Properties properties = new Properties();

        try {
            // 使用“输出流”，将Properties集合中的KV键值对，写入*.properties文件
            // 注意这个路径是编译后的文件路径 在target目录下
            String filepath = ClassLoader.getSystemResource(propertyName).getPath();
            // 避免重复写入
            Properties currentProperties = readProperties(propertyName);
            String currentValue = currentProperties.getProperty(key);
            if(StringUtils.equals(value, currentValue)){
                return;
            }

            FileOutputStream fos = new FileOutputStream(filepath, true);
            properties.setProperty(key, value);
            properties.store(new OutputStreamWriter(fos, StandardCharsets.UTF_8), StringUtils.EMPTY);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Properties readProperties(String propertyName){
        Properties properties = new Properties();
        InputStream in = ClassLoader.getSystemResourceAsStream(propertyName);
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

}
