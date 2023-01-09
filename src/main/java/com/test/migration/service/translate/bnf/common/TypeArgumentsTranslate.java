package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class TypeArgumentsTranslate {

    /**
     * typeArguments
     * :	'<' typeArgumentList '>'
     * ;
     */
    public String translateTypeArguments(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_typeArguments)) {
            System.out.println("RULE_typeArguments 没找到，不科学");
            return null;
        }

        String typeArgumentList = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                RuleContext ruleContext = (RuleContext) child;
                if (ruleContext.getRuleIndex() == Java8Parser.RULE_typeArgumentList) {
                    TypeArgumentListTranslate typeArgumentListTranslate = new TypeArgumentListTranslate();
                    typeArgumentList = typeArgumentListTranslate.translateTypeArgumentList((ParserRuleContext) ruleContext);
                }
            }
        }

        return "<" + typeArgumentList + ">";
    }
}
