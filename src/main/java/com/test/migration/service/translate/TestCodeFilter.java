package com.test.migration.service.translate;

import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 过滤掉不需要进行迁移的代码结构：
 * 类成员变量
 * test方法
 */
public class TestCodeFilter {
    public static void filterFieldDeclarationCtxList() {
    }

    public static void filterMethodDeclarationCtxList(List<String> migrateTestMethods) {
        TestCodeContext.methodDeclarationCtxList = TestCodeContext.methodDeclarationCtxList.stream()
                .filter(ctx -> {
                    String methodName = fetchMethodName(ctx);
                    if (methodName.toLowerCase().startsWith("test") || methodName.toLowerCase().endsWith("test")) {
                        return migrateTestMethods.contains(methodName);
                    }
                    // 不是test方法不做过滤
                    return true;
                }).collect(Collectors.toList());
    }


    public static void filterClassDeclarationCtxList() {
    }


    private static String fetchMethodName(ParserRuleContext ctx) {
        ParserRuleContext methodHeaderCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_methodHeader) {
                    methodHeaderCtx = (ParserRuleContext) child;
                }
            }
        }
        if (methodHeaderCtx == null) {
            return StringUtils.EMPTY;
        }

        return fetchMethodNameByHeader(methodHeaderCtx);
    }

    private static String fetchMethodNameByHeader(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodHeader) {
            return "";
        }

        for (int j = 0; j < ctx.getChildCount(); j++) {
            // 获取method的Header
            ParseTree methodHeaderChild = ctx.getChild(j);
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
                if (child1 instanceof TerminalNode) {
                    TerminalNode terminalNode = (TerminalNode) child1;
                    if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                        return terminalNode.getText();
                    }
                }
            }

        }
        return "";
    }
}
