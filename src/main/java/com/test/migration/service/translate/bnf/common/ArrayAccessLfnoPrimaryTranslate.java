package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ArrayAccessLfnoPrimaryTranslate {

    public String translateArrayAccessLfnoPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_arrayAccess_lfno_primary) {
            System.out.println("RULE_arrayAccess_lfno_primary 没找到，不科学");
            return null;
        }

        System.out.println("RULE_arrayAccess_lfno_primary 建设中");

        return "";
    }
}
