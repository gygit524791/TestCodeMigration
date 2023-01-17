package com.test.migration.service.translate.bnf.common.array;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ArrayAccessTranslate {

    /**
     * arrayAccess
     * 	:	(	expressionName '[' expression ']'
     * 		|	primaryNoNewArray_lfno_arrayAccess '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lf_arrayAccess '[' expression ']'
     * 		)*
     * 	;
     */
    public String translateArrayAccess(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_arrayAccess) {
            System.out.println("RULE_arrayAccess 没找到，不科学");
            return null;
        }


        return ctx.getText();
    }
}
