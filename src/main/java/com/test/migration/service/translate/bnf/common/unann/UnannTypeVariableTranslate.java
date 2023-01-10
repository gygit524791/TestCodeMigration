package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnannTypeVariableTranslate {
    /**
     * unannTypeVariable
     * 	:	Identifier
     * 	;
     */
    public String translateUnannTypeVariable(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannTypeVariable) {
            System.out.println("RULE_unannTypeVariable 为null");
            return "";
        }

        return ctx.getText();
    }


}
