package com.test.migration.service.translate.bnf.common.primary;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ArrayAccessLfPrimaryTranslate {

    public String translateArrayAccessLfPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_arrayAccess_lf_primary) {
            System.out.println("RULE_arrayAccess_lfno_primary 没找到，不科学");
            return null;
        }

        return ctx.getText();
    }
}
