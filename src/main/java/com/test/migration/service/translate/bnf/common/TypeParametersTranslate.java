package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class TypeParametersTranslate {

    /**
     * typeParameters
     * 	:	'<' typeParameterList '>'
     * 	;
     */
    public String translateTypeParameters(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_typeParameters)) {
            System.out.println("RULE_typeParameters 没找到，不科学");
            return null;
        }

        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(1);
        TypeParameterListTranslate translate = new TypeParameterListTranslate();
        return "<" + translate.translateTypeParameterList(childRuleContext) + ">";
    }
}
