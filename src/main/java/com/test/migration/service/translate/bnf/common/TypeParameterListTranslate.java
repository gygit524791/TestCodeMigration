package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

public class TypeParameterListTranslate {

    /**
     * typeParameterList
     * 	:	typeParameter (',' typeParameter)*
     * 	;
     * @param ctx
     * @return
     */
    public String translateTypeParameterList(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_typeParameterList)) {
            Log.error("RULE_typeParameterList error");
            return null;
        }

        return "<" + ">";
    }
}
