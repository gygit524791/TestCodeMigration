package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ResultTranslate {

    /**
     * result
     * 	:	unannType
     * 	|	'void'
     * 	;
     */
    public String translateResult(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_result) {
            System.out.println("RULE_result 没找到，不科学");
            return null;
        }

        ParseTree child = ctx.getChild(0);
        boolean isRuleContext = child instanceof RuleContext;
        if (!isRuleContext) {
            return ctx.getText();
        }

        UnannTypeTranslate unannTypeTranslate = new UnannTypeTranslate();
        return unannTypeTranslate.translateUnannType((ParserRuleContext) child);
    }
}