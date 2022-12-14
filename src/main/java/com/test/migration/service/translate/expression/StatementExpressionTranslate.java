package com.test.migration.service.translate.expression;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.common.AssignmentTranslate;
import com.test.migration.service.translate.common.cls.ClassInstanceCreationExpressionTranslate;
import com.test.migration.service.translate.common.method.MethodInvocationTranslate;
import org.antlr.v4.runtime.ParserRuleContext;

public class StatementExpressionTranslate {

    /**
     * statementExpression
     * :	assignment
     * |	preIncrementExpression
     * |	preDecrementExpression
     * |	postIncrementExpression
     * |	postDecrementExpression
     * |	methodInvocation
     * |	classInstanceCreationExpression
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateStatementExpression(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_statementExpression) {
            System.out.println("RULE_block 没找到，不科学");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_assignment) {
            AssignmentTranslate translate = new AssignmentTranslate();
            return translate.translateAssignment(childRuleContext);
        }
        ConditionalExpressionTranslate conditionalExpressionTranslate = new ConditionalExpressionTranslate();
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_preIncrementExpression) {
            return conditionalExpressionTranslate.translatePreIncrementExpression(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_preDecrementExpression) {
            return conditionalExpressionTranslate.translatePreDecrementExpression(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_postIncrementExpression) {
            return conditionalExpressionTranslate.translatePostIncrementExpression(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_postDecrementExpression) {
            return conditionalExpressionTranslate.translatePostDecrementExpression(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_methodInvocation) {
            MethodInvocationTranslate translate = new MethodInvocationTranslate();
            return translate.translateMethodInvocation(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_classInstanceCreationExpression) {
            ClassInstanceCreationExpressionTranslate classInstanceCreationExpressionTranslate = new ClassInstanceCreationExpressionTranslate();
            return classInstanceCreationExpressionTranslate.translateClassInstanceCreationExpression(childRuleContext);
        }

        System.out.println("translateStatementExpression 不可能到这里");
        return null;
    }

}
