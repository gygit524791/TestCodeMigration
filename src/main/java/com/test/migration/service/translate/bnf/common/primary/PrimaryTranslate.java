package com.test.migration.service.translate.bnf.common.primary;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class PrimaryTranslate {

    /**
     * primary
     * 	:	(	primaryNoNewArray_lfno_primary
     * 		|	arrayCreationExpression
     * 		)
     * 		(	primaryNoNewArray_lf_primary
     * 		)*
     * 	;
     */
    public String translatePrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_primary) {
            System.out.println("RULE_primary 没找到，不科学");
            return null;
        }

        if (ctx.getChildCount() != 1) {
            System.out.println("暂不支持的解析translatePrimary");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() != Java8Parser.RULE_primaryNoNewArray_lfno_primary) {
            System.out.println("暂不支持RULE_primaryNoNewArray_lfno_primary以外的解析");
            return null;
        }
        PrimaryNoNewArrayLfNoPrimaryTranslate primaryNoNewArrayLfNoPrimaryTranslate = new PrimaryNoNewArrayLfNoPrimaryTranslate();
        return primaryNoNewArrayLfNoPrimaryTranslate.translatePrimaryNoNewArray_lfno_primary(childRuleContext);
    }
}
