package com.test.migration.service.translate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import utils.MappingRuleReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


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

    /**
     * 简化映射规则
     * mActivityRule.runOnUiThread在迁移过程中去掉，只保留body
     * methodInvocation
     * typeName,identifier
     * <p>
     * TODO 想不清楚，这个该用什么结构来描述，能保证可扩展，先跳过
     */
    public static Set<String> simplyMethodInvocationTypeNameMapping = Sets.newHashSet();

    static {
        simplyMethodInvocationTypeNameMapping.add("mActivityRule");
    }

    public static Map<String, List<String>> simplyMethodInvocationIdentifierMapping = Maps.newHashMap();

    static {
        simplyMethodInvocationIdentifierMapping.put("mActivityRule", Arrays.asList("runOnUiThread"));
    }

    /**
     *
     */
    public static Map<String, Map<String, String>> commonTypeNameMethodInvocationMapping = Maps.newHashMap();

    static {
        // typename.identifier => repalceIdentifier
        //Thread sleep Sleep
        commonTypeNameMethodInvocationMapping.put("Thread", ImmutableMap.of("sleep", "Sleep"));
    }

//    public static Map<String, Map<String, String>> typeNameMethodInvocationMapping = Maps.newHashMap();
//    static {
//        typeNameMethodInvocationMapping.put("Thread",  ImmutableMap.of("sleep","Sleep"));
//    }


    public static Map<String, String> basicMapping = Maps.newHashMap();

    static {
        basicMapping.put("String", "std::string");
    }


//    public static Map<String, String> testToolMethodNameMapping = Maps.newHashMap();
//
//    static {
//        testToolMethodNameMapping.put("assertTrue", "ASSERT_TRUE");
//        testToolMethodNameMapping.put("assertFalse", "ASSERT_FALSE");
//        testToolMethodNameMapping.put("assertEquals", "ASSERT_EQ");
//        testToolMethodNameMapping.put("assertNotEquals", "ASSERT_NE");
////        testToolMethodNameMapping.put("assertNotNull", "ASSERT_NE(NULL, )");
//        testToolMethodNameMapping.put("assertNotSame", "ASSERT_NE");
////        testToolMethodNameMapping.put("assertNull", "ASSERT_EQ(NULL,)");
//        testToolMethodNameMapping.put("assertSame", "ASSERT_EQ");
////        testToolMethodNameMapping.put("assertThat", "");
////        testToolMethodNameMapping.put("", "EXPECT_EQ");
//    }


    public static Map<String, String> methodNameMapping = Maps.newHashMap();

    static {
        methodNameMapping.put("isPaused", "IsPending");
        methodNameMapping.put("start", "Play");
        methodNameMapping.put("isStarted", "IsRunning");
        methodNameMapping.put("isRunning", "IsRunning");
        methodNameMapping.put("pause", "Pause");
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
