package com.test.migration.service.translate.bnf.common.parameter;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class LastFormalParameterTranslate {

    /**
     * lastFormalParameter
     * :	variableModifier* unannType annotation* '...' variableDeclaratorId
     * |	formalParameter
     * ;
     */
    public String translateLastFormalParameter(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_lastFormalParameter) {
            System.out.println("RULE_lastFormalParameter 没找到，不科学");
            return null;
        }
        if (ctx.getChildCount() != 1) {
            // todo 补充 hint  （AnimatorSet...sets）
            System.out.println("translateLastFormalParameter 不支持的转换类型 "+ ctx.getText());
            return ctx.getText();
        }
        FormalParameterTranslate formalParameterTranslate = new FormalParameterTranslate();
        return formalParameterTranslate.translateFormalParameter((ParserRuleContext) ctx.getChild(0));
    }
}
