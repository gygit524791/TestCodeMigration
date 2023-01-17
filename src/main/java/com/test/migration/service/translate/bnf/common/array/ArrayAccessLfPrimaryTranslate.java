package com.test.migration.service.translate.bnf.common.array;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

public class ArrayAccessLfPrimaryTranslate {

    /**
     * arrayAccess_lf_primary
     * 	:	(	primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary '[' expression ']'
     * 		)*
     * 	;
     * @param ctx
     * @return
     */
    public String translateArrayAccessLfPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_arrayAccess_lf_primary) {
            Log.error("RULE_arrayAccess_lf_primary error");
            return null;
        }

        return ctx.getText();
    }
}
