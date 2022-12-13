package com.test.migration.service.translate.declaration;

import org.antlr.v4.runtime.ParserRuleContext;

public class InterfaceDeclarationTranslate {

    /**
     *
     * TODO 目前看起来不需要interface
     *
     * @param ctx
     * @return
     */
    public String translateInterfaceDeclaration(ParserRuleContext ctx) {
        return ctx.getText();
    }
}
