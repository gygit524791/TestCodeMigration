package com.test.migration.service.translate.bnf.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class IfThenStatementTranslate {

    /**
     * ifThenStatement
     * :	'if' '(' expression ')' statement
     * ;
     */
    public String translateIfThenStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_ifThenStatement) {
            System.out.println("RULE_ifThenStatement 没找到，不科学");
            return null;
        }

        ParserRuleContext expressionCtx = null;
        ParserRuleContext statementCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_expression) {
                expressionCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statement) {
                statementCtx = (ParserRuleContext) childRuleContext;
            }
        }
        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        String expression = expressionTranslate.translateExpression(expressionCtx);

        StatementTranslate statementTranslate = new StatementTranslate();
        String statement = statementTranslate.translateStatement(statementCtx);

        return "if" + "(" + expression + ")" + statement;

    }

}
