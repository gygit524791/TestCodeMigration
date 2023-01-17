package com.test.migration.service.translate.bnf.declaration;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

public class ConstructorDeclarationTranslate {

    /**
     * constructorDeclaration
     * :	constructorModifier* constructorDeclarator throws_? constructorBody
     * ;
     */
    public String translateConstructorDeclaration(ParserRuleContext ctx) {

        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_constructorDeclaration)) {
            Log.error("RULE_constructorDeclaration error");
            return null;
        }

        Log.info("todo：constructorDeclaration代码转换 ");

        return ctx.getText();
    }
}
