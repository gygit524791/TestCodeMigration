package com.test.migration.service.translate.bnf.common.primary;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class FieldAccessLfPrimaryTranslate {

    /**
     * fieldAccess_lf_primary
     * :	'.' Identifier
     * ;
     */
    public String translateFieldAccessLfPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_fieldAccess_lf_primary) {
            System.out.println("RULE_fieldAccess_lf_primary 没找到，不科学");
            return null;
        }
        return ctx.getText();
    }
}
