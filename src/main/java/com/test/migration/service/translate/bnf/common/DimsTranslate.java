package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class DimsTranslate {

    /**
     * typeArgument
     * 	:	referenceType
     * 	|	wildcard
     * 	;
     */
    public String translateTypeArguments(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_typeArguments)) {
            System.out.println("RULE_typeArguments 没找到，不科学");
            return null;
        }

        return null;
    }
}
