package com.test.migration.service.translate.declaration;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class InstanceInitializerTranslate {
    public String translateInstanceInitializer(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_instanceInitializer)) {
            System.out.println("RULE_instanceInitializer 没找到，不科学");
            return null;
        }

        return null;
    }
}
