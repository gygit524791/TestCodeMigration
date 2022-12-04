package com.test.migration.service.translate.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionNameTranslate {

    /**
     * expressionName
     * :	Identifier
     * |	ambiguousName '.' Identifier
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateExpressionName(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_expressionName) {
            System.out.println("RULE_expressionName 没找到，不科学");
            return null;
        }
        if (ctx.getChildCount() == 1) {
            return ctx.getText();
        }
        return translateAmbiguousName((ParserRuleContext) ctx.getChild(0)) + "." + ctx.getChild(2).getText();
    }

    public String translateAmbiguousName(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_ambiguousName) {
            System.out.println("RULE_ambiguousName 没找到，不科学");
            return null;
        }
        if (ctx.getChildCount() == 1) {
            return ctx.getText();
        }

        return translateAmbiguousName((ParserRuleContext) ctx.getChild(0)) + "." + ctx.getChild(2).getText();
    }


}
