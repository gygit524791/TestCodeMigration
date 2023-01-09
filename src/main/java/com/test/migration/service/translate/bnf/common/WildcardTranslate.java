package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class WildcardTranslate {

    /**
     * wildcard
     * :	annotation* '?' wildcardBounds?
     * ;
     */
    public String translateWildCard(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_wildcard)) {
            System.out.println("RULE_wildcard 没找到，不科学");
            return null;
        }
        ParserRuleContext wildcardBoundsCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_wildcardBounds) {
                wildcardBoundsCtx = (ParserRuleContext) childRuleContext;
            }
        }
        WildcardBoundsTranslate wildcardBoundsTranslate = new WildcardBoundsTranslate();
        String wildcardBounds = wildcardBoundsCtx == null ? "" : wildcardBoundsTranslate.translateWildcardBounds(wildcardBoundsCtx);
        return "? " + wildcardBounds;
    }
}
