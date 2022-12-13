package com.test.migration.service.translate.common.variable;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

public class VariableDeclaratorListTranslate {


    /**
     * variableDeclaratorList
     * :	variableDeclarator (',' variableDeclarator)*
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateVariableDeclaratorList(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableDeclaratorList) {
            System.out.println("RULE_variableDeclaratorList 为null");
            return "";
        }
        // TODO 先考虑单个变量声明，多变量声明（int a=1,b=2）后续解决
        ParserRuleContext child = (ParserRuleContext) ctx.getChild(0);
        return translateVariableDeclarator(child);
    }

    /**
     * variableDeclarator
     * :	variableDeclaratorId ('=' variableInitializer)?
     * ;
     *
     * @param ctx
     * @return
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
