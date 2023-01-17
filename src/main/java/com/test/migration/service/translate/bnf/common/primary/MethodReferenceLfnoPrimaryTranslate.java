package com.test.migration.service.translate.bnf.common.primary;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

public class MethodReferenceLfnoPrimaryTranslate {

    /**
     * methodReference_lfno_primary
     * 	:	expressionName '::' typeArguments? Identifier
     * 	|	referenceType '::' typeArguments? Identifier
     * 	|	'super' '::' typeArguments? Identifier
     * 	|	typeName '.' 'super' '::' typeArguments? Identifier
     * 	|	classType '::' typeArguments? 'new'
     * 	|	arrayType '::' 'new'
     * 	;
     * @param ctx
     * @return
     */
    public String translateMethodReferenceLfnoPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodReference_lfno_primary) {
            Log.error("RULE_methodReference_lfno_primary error");
            return null;
        }
        Log.error("RULE_methodReference_lfno_primary");

        return null;
    }
}
