package com.test.migration.service.translate;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import utils.MappingRuleReader;

import java.util.List;
import java.util.Map;


@Data
public class MappingRuleLoader {

    public static Map<String, String> commonClassNameMapping;
    public static Map<String, String> testToolMethodNameMapping;
    public static List<MethodInvocationTypeNameRule> commonMethodInvocationTypeNameMapping;
    public static List<MethodInvocationTypeNameRule> customMethodInvocationTypeNameMapping;

    public static void load() {
        List<String> customMethodInvocationTypeNameMappingStr = MappingRuleReader.readLinesFromProperties("mappingRule/customMethodInvocationTypeNameMapping.properties");
        customMethodInvocationTypeNameMapping = customMethodInvocationTypeNameMappingStr.stream()
                .map(MappingRuleLoader::buildMapping).toList();

        List<String> commonMethodInvocationTypeNameMappingStr = MappingRuleReader.readLinesFromProperties("mappingRule/commonMethodInvocationTypeNameMapping.properties");
        commonMethodInvocationTypeNameMapping = commonMethodInvocationTypeNameMappingStr.stream()
                .map(MappingRuleLoader::buildMapping).toList();

        List<String> testToolMethodNameMappingStr = MappingRuleReader.readLinesFromProperties("mappingRule/testToolMethodNameMapping.properties");
        testToolMethodNameMapping = convertToMap(testToolMethodNameMappingStr);

        List<String> commonClassNameMappingStr = MappingRuleReader.readLinesFromProperties("mappingRule/commonClassNameMapping.properties");
        commonClassNameMapping = convertToMap(commonClassNameMappingStr);
    }

    private static Map<String, String> convertToMap(List<String> mappingStr) {
        Map<String, String> map = Maps.newHashMap();
        for (String line : mappingStr) {
            String[] mapping = line.split("=");
            map.put(mapping[0], mapping[1]);
        }
        return map;
    }

    private static MethodInvocationTypeNameRule buildMapping(String MappingLine) {
        String[] mappingStr = MappingLine.split("=");
        String sourceStr = mappingStr[0];
        String targetStr = mappingStr[1];

        MethodInvocationTypeNameRule rule = new MethodInvocationTypeNameRule();

        if (StringUtils.isNotBlank(sourceStr)) {
            String[] source = sourceStr.split("->");
            rule.setSourceClassName(source[0]);
            rule.setSourceMethodName(source[1]);
        } else {
            rule.setSourceClassName(StringUtils.EMPTY);
            rule.setSourceMethodName(StringUtils.EMPTY);
        }

        if (StringUtils.isNotBlank(targetStr)) {
            String[] target = targetStr.split("->");
            rule.setTargetClassName(target[0]);
            rule.setTargetMethodName(target[1]);
        } else {
            rule.setTargetClassName(StringUtils.EMPTY);
            rule.setTargetMethodName(StringUtils.EMPTY);
        }
        return rule;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MethodInvocationTypeNameRule {
        private String sourceClassName;
        private String sourceMethodName;
        private String targetClassName;
        private String targetMethodName;
    }

}
