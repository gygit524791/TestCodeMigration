package com.test.migration.service.translate.bnf.common.array;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ArrayCreationExpressionTranslate {

    /**
     * arrayCreationExpression
     * 	:	'new' primitiveType dimExprs dims?
     * 	|	'new' classOrInterfaceType dimExprs dims?
     * 	|	'new' primitiveType dims arrayInitializer
     * 	|	'new' classOrInterfaceType dims arrayInitializer
     * 	;
     */
    public String translateArrayCreationExpression(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_arrayCreationExpression)) {
            System.out.println("RULE_arrayCreationExpression 没找到");
            return null;
        }

        System.out.println("translateArrayCreationExpression 建设中，返回源代码");
        return ctx.getText();
    }
}
