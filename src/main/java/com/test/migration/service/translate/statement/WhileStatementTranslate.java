package com.test.migration.service.translate.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class WhileStatementTranslate {

    /**
     * whileStatement
     * :  'while' '(' expression ')' statement
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateWhileStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_whileStatement) {
            System.out.println("RULE_whileStatement 没找到，不科学");
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
        return "while" + "(" + expression + ")" + statement;
    }


}
