package com.test.migration.service.translate.common;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;

public class AssignmentTranslate {

    /**
     * assignment
     * :	leftHandSide assignmentOperator expression
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateAssignment(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_assignment) {
            System.out.println("RULE_assignment 没找到，不科学");
            return null;
        }
        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        return translateLeftHandSide((ParserRuleContext) ctx.getChild(0))
                + translateAssignmentOperator((ParserRuleContext) ctx.getChild(1))
                + expressionTranslate.translateExpression((ParserRuleContext) ctx.getChild(2));
    }

    /**
     * leftHandSide
     * :	expressionName
     * |	fieldAccess
     * |	arrayAccess
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateLeftHandSide(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_leftHandSide) {
            System.out.println("RULE_leftHandSide 没找到，不科学");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() != Java8Parser.RULE_expressionName) {
            System.out.println("目前只支持leftHandSide子节点expressionName的解析");
            return null;
        }
        ExpressionNameTranslate translate = new ExpressionNameTranslate();

        return translate.translateExpressionName(childRuleContext);
    }

    /**
     * assignmentOperator
     * :	'='
     * |	'*='
     * |	'/='
     * |	'%='
     * |	'+='
     * |	'-='
     * |	'<<='
     * |	'>>='
     * |	'>>>='
     * |	'&='
     * |	'^='
     * |	'|='
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateAssignmentOperator(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_assignmentOperator) {
            System.out.println("RULE_assignmentOperator 没找到，不科学");
            return null;
        }
        return ctx.getText();
    }


}
