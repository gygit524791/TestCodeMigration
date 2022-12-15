package com.test.migration.service.translate.bnf.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.expression.StatementExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionStatement {

    /**
     * expressionStatement
     * 	:	statementExpression ';'
     * 	;
     * @param ctx
     * @return
     */
    public String translateExpressionStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_expressionStatement) {
            System.out.println("RULE_block 没找到，不科学");
            return null;
        }
        StatementExpressionTranslate statementExpressionTranslate = new StatementExpressionTranslate();
        return statementExpressionTranslate.translateStatementExpression((ParserRuleContext) ctx.getChild(0)) + ";";
    }


}
