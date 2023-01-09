package com.test.migration.service.translate.bnf.common.cls;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class InterfaceTypeLfnoClassOrInterfaceTypeTranslate {

    /**
     * interfaceType_lfno_classOrInterfaceType
     * :	classType_lfno_classOrInterfaceType
     * ;
     */
    public String translateInterfaceType_lfno_classOrInterfaceType(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_interfaceType_lfno_classOrInterfaceType)) {
            System.out.println("RULE_interfaceType_lfno_classOrInterfaceType 没找到，不科学");
            return null;
        }
        ParserRuleContext child = (ParserRuleContext) ctx.getChild(0);
        ClassTypeLfnoClassOrInterfaceTypeTranslate translate = new ClassTypeLfnoClassOrInterfaceTypeTranslate();
        return translate.translateClassType_lfno_classOrInterfaceType(child);
    }
}
