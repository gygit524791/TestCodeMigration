package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnannPrimitiveTypeTranslate {

    /**
     * unannPrimitiveType
     * 	:	numericType
     * 	|	'boolean'
     * 	;
     */
    public String translateUnannPrimitiveType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannPrimitiveType) {
            System.out.println("RULE_unannPrimitiveType 为null");
            return "";
        }

        return ctx.getText();
    }

}
