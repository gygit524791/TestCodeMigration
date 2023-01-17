package com.test.migration.service.translate.bnf.expression;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.ExpressionNameTranslate;
import com.test.migration.service.translate.bnf.common.primary.PrimaryTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.Log;

public class PostfixExpressionTranslate {

    /**
     * postfixExpression
     * :	(	primary
     * |	expressionName
     * )
     * (	postIncrementExpression_lf_postfixExpression
     * |	postDecrementExpression_lf_postfixExpression
     * )*
     * ;
     * <p>
     * 	TODO 目前只支持postfixExpression的子节点--primary或者expressionName的解析
     * 	     完整解析需要大量的工作
     *
     * @param ctx
     * @return
     */
    public String translatePostfixExpression(ParserRuleContext ctx) {
        if (ctx == null ||ctx.getRuleIndex() != Java8Parser.RULE_postfixExpression) {
            Log.error("postfixExpressionRule error");
            return null;
        }

        if (ctx.getChildCount() != 1) {
            Log.error("暂不支持的postfixExpression解析类型1");
            return null;
        }

        // child要么是primary，要么是expressionName
        ParseTree child = ctx.getChild(0);
        RuleContext childRuleContext = (RuleContext) child;

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_expressionName) {
            ExpressionNameTranslate expressionNameTranslate = new ExpressionNameTranslate();
            return expressionNameTranslate.translateExpressionName((ParserRuleContext) childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_primary) {
            PrimaryTranslate primaryTranslate = new PrimaryTranslate();
            return primaryTranslate.translatePrimary((ParserRuleContext) childRuleContext);
        }

        Log.error("暂不支持的postfixExpression解析类型2");
        return null;

    }


    public ParserRuleContext findSubRuleDfs(ParserRuleContext ctx, int subRuleIndex) {
        // 深度优先遍历 寻找指定rule
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childNode = (RuleContext) child;
            if (childNode.getRuleIndex() != subRuleIndex) {
                return findSubRuleDfs((ParserRuleContext) childNode, subRuleIndex);
            } else {
                return (ParserRuleContext) childNode;
            }
        }
        return null;
    }

}
