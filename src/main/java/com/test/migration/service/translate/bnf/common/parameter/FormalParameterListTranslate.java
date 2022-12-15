package com.test.migration.service.translate.bnf.common.parameter;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class FormalParameterListTranslate {

    /**
     * formalParameterList
     * :	formalParameters ',' lastFormalParameter
     * |	lastFormalParameter
     * ;
     */
    public String translateFormalParameterList(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_formalParameterList) {
            System.out.println("RULE_formalParameterList 没找到，不科学");
            return null;
        }
        LastFormalParameterTranslate lastFormalParameterTranslate = new LastFormalParameterTranslate();
        //lastFormalParameter
        if (ctx.getChildCount() == 1) {
            return lastFormalParameterTranslate.translateLastFormalParameter((ParserRuleContext) ctx.getChild(0));
        }
        //formalParameters ',' lastFormalParameter

        FormalParametersTranslate formalParametersTranslate = new FormalParametersTranslate();
        return formalParametersTranslate.translateFormalParameters((ParserRuleContext) ctx.getChild(0)) + "," +
                lastFormalParameterTranslate.translateLastFormalParameter((ParserRuleContext) ctx.getChild(2));
    }
}
