package com.test.migration.service.translate.bnf.common.array;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ArrayCreationExpressionTranslate {

    /**
     * arrayType
     * 	:	primitiveType dims
     * 	|	classOrInterfaceType dims
     * 	|	typeVariable dims
     * 	;
     */
    public String translateArrayType(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_arrayType)) {
            System.out.println("RULE_arrayType 没找到，不科学");
            return null;
        }

        return null;
    }
}