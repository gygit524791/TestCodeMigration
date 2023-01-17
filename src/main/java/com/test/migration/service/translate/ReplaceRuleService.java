package com.test.migration.service.translate;

import com.test.migration.antlr.java.Java8Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ReplaceRuleService {

    /**
     * 链式调用
     */
    public static String replaceMethodInvocationTypeName(String originTranslate,
                                                         String typeName, String methodNameIdentifier, String argumentList) {
        List<MappingRuleLoader.MethodInvocationTypeNameRule> apiMapping = MappingRuleLoader.apiMapping;

        return replaceMethodInvocationTypeName(originTranslate, typeName, methodNameIdentifier, argumentList, apiMapping);
    }

    private static String replaceMethodInvocationTypeName(String originTranslate,
                                                          String typeName, String methodNameIdentifier, String argumentList,
                                                          List<MappingRuleLoader.MethodInvocationTypeNameRule> apiMapping) {
        String type = TestCodeContext.typeNameMap.getOrDefault(typeName, StringUtils.EMPTY);
        MappingRuleLoader.MethodInvocationTypeNameRule methodInvocationTypeNameRule = apiMapping.stream()
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
        Map<String, String> testToolMethodNameMapping = MappingRuleLoader.testToolApiMapping;
        if (testToolMethodNameMapping.containsKey(methodName)) {
            return testToolMethodNameMapping.get(methodName);
        }

        return methodName;
    }

    public static String replaceClassOrInterfaceType(String clsName) {
        Map<String, String> referenceMapping = MappingRuleLoader.classNameMapping;
        if (StringUtils.equals("String", clsName)) {
            return referenceMapping.get(clsName);
        }

        // 内部类 改为A::B
        boolean isInnerCls = TestCodeContext.ClassMemberDeclaration.classes.stream()
                .anyMatch(x -> StringUtils.equals(x.name, clsName));
        if (isInnerCls) {
            return TestCodeContext.className + "::" + clsName;
        }

        // hint for mismatch
        if (!referenceMapping.containsKey(clsName)) {
            TranslateHint.misMatchCodes.add(clsName);
        }

        return referenceMapping.getOrDefault(clsName, clsName) + " * ";
    }

}
