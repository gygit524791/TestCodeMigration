package com.test.migration.service.translate.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class ReturnStatementTranslate {

    public String translateReturnStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_returnStatement) {
            System.out.println("RULE_returnStatement 没找到，不科学");
            return null;
        }
        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext
                    && ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_expression) {
                stringBuilder.append(expressionTranslate.translateExpression((ParserRuleContext) ctx.getChild(i)));
            }
        }
        return "return " + stringBuilder + ";";
    }
}
