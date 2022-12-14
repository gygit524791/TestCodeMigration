package com.test.migration.service.translate;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ReplaceRuleService {

    /**
     * 前置条件；由visitor负责初始化
     *
     * @see TestCodeVisitor
     */
    public static Map<String, String> typeNameMap;

    /**
     * 链式调用
     */
    public static String replaceMethodInvocationTypeName(String originTranslate,
                                                         String typeName, String methodNameIdentifier, String argumentList) {
        List<MappingRuleLoader.MethodInvocationTypeNameRule> customMapping = MappingRuleLoader.customMethodInvocationTypeNameMapping;
        List<MappingRuleLoader.MethodInvocationTypeNameRule> commonMapping = MappingRuleLoader.commonMethodInvocationTypeNameMapping;

        String replace1 = replaceMethodInvocationTypeName(originTranslate, typeName, methodNameIdentifier, argumentList, customMapping);
        String replace2 = replaceMethodInvocationTypeName(replace1, typeName, methodNameIdentifier, argumentList, commonMapping);

        return replace2;
    }

    private static String replaceMethodInvocationTypeName(String originTranslate,
                                                          String typeName, String methodNameIdentifier, String argumentList,
                                                          List<MappingRuleLoader.MethodInvocationTypeNameRule> customMapping) {
        String type = typeNameMap.getOrDefault(typeName, StringUtils.EMPTY);
        MappingRuleLoader.MethodInvocationTypeNameRule methodInvocationTypeNameRule = customMapping.stream()
                .filter(x -> StringUtils.equals(type, x.getSourceClassName())
                        && StringUtils.equals(methodNameIdentifier, x.getSourceMethodName()))
                .findFirst().orElse(null);

        // String originTranslate = typeName + "." + methodNameIdentifier + "(" + argumentList + ")";
        if (methodInvocationTypeNameRule != null) {
            if (StringUtils.isBlank(methodInvocationTypeNameRule.getTargetClassName())
                    && StringUtils.isBlank(methodInvocationTypeNameRule.getTargetMethodName())) {
                return argumentList;
            }

            if (StringUtils.isNotBlank(methodInvocationTypeNameRule.getTargetClassName())
                    && StringUtils.isNotBlank(methodInvocationTypeNameRule.getTargetMethodName())) {
                return typeName + "->" + methodInvocationTypeNameRule.getTargetMethodName() + "(" + argumentList + ")";
            }

        }

        return originTranslate;
    }

    public static String replaceMethodInvocationMethodName(String methodName) {
        String replace1 = replaceTestToolMethodNameMapping(methodName);

        return replace1;
    }

    private static String replaceTestToolMethodNameMapping(String methodName) {
        Map<String, String> testToolMethodNameMapping = MappingRuleLoader.testToolMethodNameMapping;
        if (testToolMethodNameMapping.containsKey(methodName)) {
            return testToolMethodNameMapping.get(methodName);
        }

        return methodName;
    }


}
