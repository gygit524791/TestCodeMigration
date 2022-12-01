package com.test.migration.service.translate.common;

import com.test.migration.antlr.Java8Lexer;
import com.test.migration.antlr.Java8Parser;
import com.test.migration.service.translate.statement.BlockTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MethodDeclarationTranslate {

    /**
     * methodDeclaration
     * :  methodModifier* methodHeader methodBody
     * ;
     * <p>
     * methodBody
     * :  block
     * |  ';'
     * ;
     * 只翻译到methodBody，其它拼接
     *
     * @param ctx
     * @return
     */
    public String translateMethodDeclaration(ParserRuleContext ctx) {

        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_methodDeclaration)) {
            System.out.println("RULE_methodDeclaration 没找到，不科学");
            return null;
        }

        //筛选出methodBody
        ParserRuleContext methodBodyRule = null;
        ParserRuleContext methodHeaderRule = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_methodBody) {
                methodBodyRule = (ParserRuleContext) ctx.getChild(i);
            }

            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_methodHeader) {
                methodHeaderRule = (ParserRuleContext) ctx.getChild(i);
            }
        }

        if (methodBodyRule == null) {
            System.out.println("methodBodyRule为空");
            return null;
        }
        //methodBody的孩子要么是block，要么是;
        String methodBody = "";
        if (methodBodyRule.getChild(0) instanceof RuleContext) {
            BlockTranslate blockTranslate = new BlockTranslate();
            methodBody = blockTranslate.translateBlock((ParserRuleContext) methodBodyRule.getChild(0));
        } else {
            methodBody = ";";
        }

        String methodName = fetchMethodName(methodHeaderRule);

        return "void " + methodName + methodBody;
    }

    /**
     * @param ctx methodHeader
     * @return
     */
    public String fetchMethodName(ParserRuleContext ctx) {
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
                if (child1 instanceof TerminalNode terminalNode) {
                    if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                        return terminalNode.getText();
                    }
                }
            }

        }
        return "";
    }
}