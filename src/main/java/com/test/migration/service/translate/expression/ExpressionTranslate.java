package com.test.migration.service.translate.expression;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.common.AssignmentTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ExpressionTranslate {
    public String translateExpression(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_expression) {
            System.out.println("expressionRule 没找到，不科学");
            return null;
        }
        // 只可能有1个孩子节点 lambdaExpression或assignmentExpression
        ParseTree child = ctx.getChild(0);
        RuleContext childRuleContext = (RuleContext) child;
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_lambdaExpression) {
            System.out.println("暂不支持lambda表达式");
            return null;
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_assignmentExpression) {
            return translateAssignmentExpression((ParserRuleContext) childRuleContext);
        }

        System.out.println("translateExpression失败，不科学");
        return null;
    }

    /**
     * @param ctx
     * @return
     */
    private String translateAssignmentExpression(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_assignmentExpression) {
            System.out.println("assignmentExpressionRule 没找到，不科学");
            return null;
        }
        // 只可能有1个孩子节点 conditionalExpression或assignment
        ParseTree child = ctx.getChild(0);
        RuleContext childRuleContext = (RuleContext) child;
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_conditionalExpression) {
            ConditionalExpressionTranslate conditionalExpressionTranslate = new ConditionalExpressionTranslate();
            return conditionalExpressionTranslate.translateConditionalExpression((ParserRuleContext) childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_assignment) {
            AssignmentTranslate translate = new AssignmentTranslate();
            return translate.translateAssignment((ParserRuleContext) childRuleContext);
        }

        System.out.println("translateAssignmentExpression失败，不科学");
        return null;
    }


}
