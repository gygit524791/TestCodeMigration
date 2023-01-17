package com.test.migration.service.translate.bnf.declaration;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

public class InstanceInitializerTranslate {

    /**
     * instanceInitializer
     * 	:	block
     * 	;
     * @param ctx
     * @return
     */
    public String translateInstanceInitializer(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_instanceInitializer)) {
            Log.error("RULE_instanceInitializer error");
            return null;
        }

        return null;
    }
}
