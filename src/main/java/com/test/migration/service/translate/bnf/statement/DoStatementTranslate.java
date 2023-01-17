package com.test.migration.service.translate.bnf.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.Log;

public class DoStatementTranslate {
    /**
     * doStatement
     * 	:	'do' statement 'while' '(' expression ')' ';'
     * 	;
     * @param ctx
     * @return
     */
    public String translateDoStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_doStatement) {
            Log.error("RULE_doStatement error");
            return null;
        }

        String expression = "";
        String statement = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_expression) {
                ExpressionTranslate expressionTranslate = new ExpressionTranslate();
                expression = expressionTranslate.translateExpression((ParserRuleContext) child);
            }
            if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_statement) {
                StatementTranslate statementTranslate = new StatementTranslate();
                statement = statementTranslate.translateStatement((ParserRuleContext) child);
            }
        }
        return "do"+ statement+ "while" + "(" + expression + ")" + ";";
    }
}
