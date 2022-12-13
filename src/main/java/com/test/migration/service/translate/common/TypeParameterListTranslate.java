package com.test.migration.service.translate.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class TypeParameterListTranslate {

    public String translateTypeParameterList(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_typeParameterList)) {
            System.out.println("RULE_typeParameterList 没找到，不科学");
            return null;
        }

        return "<" + ">";
    }
}
