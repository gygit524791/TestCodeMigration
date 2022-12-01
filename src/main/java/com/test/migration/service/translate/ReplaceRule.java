package com.test.migration.service.translate;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReplaceRule {

    public static String replaceMethodInvocation(String originTranslate,
                                          String typeName, String methodNameIdentifier, String argumentList) {
        String s = replaceSimpleMethodInvocation(originTranslate, typeName, methodNameIdentifier, argumentList);

        return replaceTypeNameMethodInvocation(s, typeName, methodNameIdentifier, argumentList);
    }

    public static String replaceSimpleMethodInvocation(String originTranslate,
                                                       String typeName, String methodNameIdentifier, String argumentList) {
        Set<String> simplyMethodInvocationTypeNameMapping = MappingRule.simplyMethodInvocationTypeNameMapping;
        Map<String, List<String>> simplyMethodInvocationIdentifierMapping = MappingRule.simplyMethodInvocationIdentifierMapping;

        // Thread.sleep=>Sleep
        if (simplyMethodInvocationTypeNameMapping.contains(typeName)) {
            boolean contains = simplyMethodInvocationIdentifierMapping.get(typeName).contains(methodNameIdentifier);
            if (contains) {
                return argumentList;
            }
        }

        // String originTranslate = typeName + "." + methodNameIdentifier + "(" + argumentList + ")";
        Map<String, String> methodNameMapping = MappingRule.methodNameMapping;
        if (methodNameMapping.containsKey(methodNameIdentifier)) {
            return typeName + "." + methodNameMapping.get(methodNameIdentifier) + "(" + argumentList + ")";
        }

        return originTranslate;
    }

    public static String replaceTypeNameMethodInvocation(String originTranslate,
                                                         String typeName, String methodNameIdentifier, String argumentList) {
        Map<String, Map<String, String>> commonTypeNameMethodInvocationMapping = MappingRule.commonTypeNameMethodInvocationMapping;

        if (commonTypeNameMethodInvocationMapping.containsKey(typeName)) {
            Map<String, String> replaceIdentifierMap = commonTypeNameMethodInvocationMapping.get(typeName);
            if (replaceIdentifierMap.containsKey(methodNameIdentifier)) {
                return replaceIdentifierMap.get(methodNameIdentifier) + "(" + argumentList + ")";
            }
        }
        return originTranslate;
    }


    public static String replaceMethodName(String methodName) {
        Map<String, String> testToolMethodNameMapping = MappingRule.testToolMethodNameMapping;
        if (testToolMethodNameMapping.containsKey(methodName)) {
            return testToolMethodNameMapping.get(methodName);
        }

        return methodName;
    }

}
