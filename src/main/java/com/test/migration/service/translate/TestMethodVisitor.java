package com.test.migration.service.translate;

import com.google.common.collect.Maps;
import com.test.migration.antlr.java.Java8BaseVisitor;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import lombok.Data;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
public class TestMethodVisitor extends Java8BaseVisitor<RuleNode> {

    /**
     * key:methodName
     * value:blockStatements
     */
    public Map<String, ParserRuleContext> methodBlockMap;

    /**
     * typeName对应的类型mapping表，举个例子：
     *
     * typeName.methodIdentifier:
     * mActivityRule.runOnUiThread(()->{
     *
     * });
     * typeName是mActivityRule，对应的类型是ActivityTestRule<AnimatorSetActivity>，mapping规则中保存的是ActivityTestRule<AnimatorSetActivity>
     *     因此需要一个从mActivityRule到ActivityTestRule<AnimatorSetActivity>的映射关系
     * methodIdentifier是runOnUiThread
     *
     *
     * TODO
     *
     */
    public Map<String, String> typeNameMap = Maps.newHashMap();

    @Override
    public RuleNode visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        String methodName = fetchPublicMethodName(ctx);
        if (methodBlockMap.containsKey(methodName)) {
            methodBlockMap.put(methodName, ctx);
        }
        return visitChildren(ctx);
    }

    private String fetchPublicMethodName(Java8Parser.MethodDeclarationContext ctx) {
        // 获取method的Declaration
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext node = (RuleContext) child;

            // 过滤掉非public的方法
            if (node.getRuleIndex() == Java8Parser.RULE_methodModifier) {
                // 存在注解的方法
                boolean existAnnotation = isExistAnnotation(node);
                if (existAnnotation) {
                    continue;
                }

                if (!StringUtils.equalsAnyIgnoreCase(node.getText(), "public")) {
                    return StringUtils.EMPTY;
                }
            }

            if (node.getRuleIndex() != Java8Parser.RULE_methodHeader) {
                continue;
            }

            for (int j = 0; j < node.getChildCount(); j++) {
                // 获取method的Header
                ParseTree methodHeaderChild = node.getChild(j);
                boolean isMethodHeaderChildRuleContext = methodHeaderChild instanceof RuleContext;
                if (!isMethodHeaderChildRuleContext) {
                    continue;
                }
                RuleContext methodHeaderChildNode = (RuleContext) methodHeaderChild;
                if (methodHeaderChildNode.getRuleIndex() != Java8Parser.RULE_methodDeclarator) {
                    continue;
                }

                // 获取method的Identifier
                for (int k = 0; k < methodHeaderChildNode.getChildCount(); k++) {
                    ParseTree child1 = methodHeaderChildNode.getChild(k);
                    if (child1 instanceof TerminalNode terminalNode) {
                        if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                            return terminalNode.getText();
                        }
                    }
                }
            }
        }
        return StringUtils.EMPTY;
    }

    private static boolean isExistAnnotation(RuleContext node) {
        boolean existAnnotation = false;
        for (int j = 0; j < node.getChildCount(); j++) {
            boolean isChildRuleContext = node.getChild(j) instanceof RuleContext;
            if (!isChildRuleContext) {
                continue;
            }
            RuleContext methodModifierNode = (RuleContext) node.getChild(j);
            if (methodModifierNode.getRuleIndex() == Java8Parser.RULE_annotation) {
                existAnnotation = true;
            }
        }
        return existAnnotation;
    }

}
