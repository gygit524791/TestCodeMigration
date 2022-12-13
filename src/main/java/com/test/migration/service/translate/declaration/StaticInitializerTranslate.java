package com.test.migration.service.translate.declaration;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class StaticInitializerTranslate {
    public String translateStaticInitializer(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_staticInitializer)) {
            System.out.println("RULE_staticInitializer 没找到，不科学");
            return null;
        }
        return null;
    }
}
