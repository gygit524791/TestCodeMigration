package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class FieldAccessLfnoPrimaryTranslate {

    /**
     * fieldAccess_lfno_primary
     * :	'super' '.' Identifier
     * |	typeName '.' 'super' '.' Identifier
     * ;
     * <p>
     * eg：
     * super.xxx;
     * FieldAccessLfnoPrimaryTranslate.super.xxx;
     */
    public String translateFieldAccessLfnoPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_fieldAccess_lfno_primary) {
            System.out.println("RULE_fieldAccess_lfno_primary 没找到，不科学");
            return null;
        }

        // 'super' '.' Identifier
        if (ctx.getChildCount() == 3) {
            return ctx.getText();
        } else {
            // typeName '.' 'super' '.' Identifier
            RuleContext ruleContext = (RuleContext) ctx.getChild(0);
            String clsName = ReplaceRuleService.replaceClassOrInterfaceType(ruleContext.getText());
            return clsName + "." + "super" + "." + ctx.getChild(ctx.getChildCount() - 1).getText();
        }
    }
}
