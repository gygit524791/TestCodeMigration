package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.primary.ArrayAccessLfnoPrimaryTranslate;
import com.test.migration.service.translate.bnf.common.primary.ArrayAccessTranslate;
import com.test.migration.service.translate.bnf.common.primary.FieldAccessLfnoPrimaryTranslate;
import com.test.migration.service.translate.bnf.expression.ExpressionTranslate;
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
     */
    public String translateLeftHandSide(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_leftHandSide) {
            System.out.println("RULE_leftHandSide 没找到，不科学");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_expressionName) {
            ExpressionNameTranslate translate = new ExpressionNameTranslate();
            return translate.translateExpressionName(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_fieldAccess) {
            FieldAccessTranslate translate = new FieldAccessTranslate();
            return translate.translateFieldAccess(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_arrayAccess) {
            ArrayAccessTranslate translate = new ArrayAccessTranslate();
            return translate.translateArrayAccess(childRuleContext);
        }

        System.out.println("translateLeftHandSide error");
        return null;
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
