package com.test.migration.service.translate.bnf.declaration;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

public class StaticInitializerTranslate {

    /**
     * staticInitializer
     * 	:	'static' block
     * 	;
     * @param ctx
     * @return
     */
    public String translateStaticInitializer(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_staticInitializer)) {
            Log.error("RULE_staticInitializer 没找到");
            return null;
        }
        return null;
    }
}
