package com.test.migration.service.translate.bnf.common.variable;

import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class VariableDeclaratorListTranslate {


    /**
     * variableDeclaratorList
     * :	variableDeclarator (',' variableDeclarator)*
     * ;
     */
    public String translateVariableDeclaratorList(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableDeclaratorList) {
            System.out.println("RULE_variableDeclaratorList 为null");
            return "";
        }
        List<ParserRuleContext> variableDeclaratorCtxList = Lists.newArrayList();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childNode = (RuleContext) child;
            if (childNode.getRuleIndex() == Java8Parser.RULE_variableDeclarator) {
                variableDeclaratorCtxList.add((ParserRuleContext) childNode);
            }
        }

        StringBuilder variableDeclarators = new StringBuilder();
        for (int i = 0; i < variableDeclaratorCtxList.size(); i++) {
            if (i == variableDeclaratorCtxList.size() - 1) {
                variableDeclarators.append(translateVariableDeclarator(variableDeclaratorCtxList.get(i)));
            } else {
                variableDeclarators.append(translateVariableDeclarator(variableDeclaratorCtxList.get(i))).append(",");
            }
        }

        return variableDeclarators.toString();
    }

    /**
     * variableDeclarator
     * :	variableDeclaratorId ('=' variableInitializer)?
     * ;
     */
    public String translateVariableDeclarator(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableDeclarator) {
            System.out.println("variableDeclaratorContext 为null");
            return "";
        }

        String variableDeclaratorId = "";
        String variableInitializer = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) child;
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorId) {
                VariableDeclaratorIdTranslate variableDeclaratorIdTranslate = new VariableDeclaratorIdTranslate();
                variableDeclaratorId = variableDeclaratorIdTranslate.translateVariableDeclaratorId((ParserRuleContext) childRuleContext);
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableInitializer) {
                variableInitializer = translateVariableInitializer((ParserRuleContext) childRuleContext);
            }
        }

        // TODO： 暂未解决多个'='问题（int a =2, b=3;）
        return StringUtils.isBlank(variableInitializer) ? variableDeclaratorId
                : variableDeclaratorId + "=" + variableInitializer;
    }

    /**
     * variableInitializer
     * :	expression
     * |	arrayInitializer
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateVariableInitializer(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableInitializer) {
            System.out.println("VariableInitializer 为null");
            return "";
        }
        ParserRuleContext childRule = (ParserRuleContext) ctx.getChild(0);

        if (childRule.getRuleIndex() == Java8Parser.RULE_arrayInitializer) {
            System.out.println("暂不支持RULE_arrayInitializer");
            return "";
        }
        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        if (childRule.getRuleIndex() == Java8Parser.RULE_expression) {
            return expressionTranslate.translateExpression(childRule);
        }
        System.out.println("translateVariableInitializer 失败");
        return "";
    }
}
