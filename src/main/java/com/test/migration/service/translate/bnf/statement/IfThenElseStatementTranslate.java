package com.test.migration.service.translate.bnf.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.expression.ExpressionTranslate;
import com.test.migration.service.translate.bnf.statement.noshortif.StatementNoShortIfTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import utils.Log;

public class IfThenElseStatementTranslate {

    /**
     * ifThenElseStatement
     * :	'if' '(' expression ')' statementNoShortIf 'else' statement
     * ;
     */
    public String translateIfThenElseStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_ifThenElseStatement) {
            Log.error("RULE_ifThenElseStatement error");
            return null;
        }

        ParserRuleContext expressionCtx = null;
        ParserRuleContext statementNoShortIfCtx = null;
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
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statementNoShortIf) {
                statementNoShortIfCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statement) {
                statementCtx = (ParserRuleContext) childRuleContext;
            }
        }

        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        String expression = expressionTranslate.translateExpression(expressionCtx);

        StatementNoShortIfTranslate statementNoShortIfTranslate = new StatementNoShortIfTranslate();
        String statementNoShortIf = statementNoShortIfTranslate.translateStatementNoShortIf(statementNoShortIfCtx);

        StatementTranslate statementTranslate = new StatementTranslate();
        String statement = statementTranslate.translateStatement(statementCtx);

        return "if" + "(" + expression + ")" + statementNoShortIf + "else" + statement;

    }

}
