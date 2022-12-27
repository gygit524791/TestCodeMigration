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



    public static String replaceClassOrInterfaceType(String clsName) {
        Map<String, String> referenceMapping = MappingRuleLoader.commonClassNameMapping;
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


    /**
     * 存在替换规则
     * new 后面的identifier 相当于typeName
     */
    public static String replaceClassInstanceCreationIdentifier(ParserRuleContext ctx) {
        String identifier = "";
        for (int k = 0; k < ctx.getChildCount(); k++) {
            ParseTree child1 = ctx.getChild(k);
            if (child1 instanceof TerminalNode terminalNode) {
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode.getText();
                    // 只取第一个identifier，取到后必须break
                    break;
                }
            }
        }

        Map<String, String> commonClassNameMapping = MappingRuleLoader.commonClassNameMapping;

        // 内部类 改为A::B
        String finalIdentifier = identifier;
        boolean isInnerCls = TestCodeContext.ClassMemberDeclaration.classes.stream()
                .anyMatch(x -> StringUtils.equals(x.name, finalIdentifier));
        if (isInnerCls) {
            return TestCodeContext.className + "::" + identifier;
        }

        // hint for mismatch
        if (!commonClassNameMapping.containsKey(identifier)) {
            TranslateHint.misMatchCodes.add(ctx.getText());
        }

        return commonClassNameMapping.getOrDefault(identifier, identifier);
    }


}
