package com.test.migration.service.translate.bnf.common.array;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

public class ArrayAccessLfnoPrimaryTranslate {

    /**
     * arrayAccess_lfno_primary
     * 	:	(	expressionName '[' expression ']'
     * 		|	primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']'
     * 		)*
     * 	;
     * @param ctx
     * @return
     */
    public String translateArrayAccessLfnoPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_arrayAccess_lfno_primary) {
            Log.error("RULE_arrayAccess_lfno_primary error");
            return null;
        }

        return ctx.getText();
    }
}
