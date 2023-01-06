package com.test.migration.service.translate.bnf.common.primary;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class PrimaryNoNewArrayLfPrimaryTranslate {

    /**
     * primaryNoNewArray_lf_primary
     * 	:	classInstanceCreationExpression_lf_primary
     * 	|	fieldAccess_lf_primary
     * 	|	arrayAccess_lf_primary
     * 	|	methodInvocation_lf_primary
     * 	|	methodReference_lf_primary
     * 	;
     * @param ctx
     * @return
     */
    public String translatePrimaryNoNewArray_lf_primary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_primaryNoNewArray_lf_primary) {
            System.out.println("RULE_primaryNoNewArray_lf_primary 没找到，不科学");
            return null;
        }

return null;

    }

}
