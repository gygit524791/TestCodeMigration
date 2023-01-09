package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;

public class WildcardBoundsTranslate {

    /**
     * wildcardBounds
     * :	'extends' referenceType
     * |	'super' referenceType
     * ;
     */
    public String translateWildcardBounds(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_wildcardBounds)) {
            System.out.println("RULE_wildcardBounds 没找到，不科学");
            return null;
        }
        ReferenceTypeTranslate referenceTypeTranslate = new ReferenceTypeTranslate();
        if (StringUtils.equals(ctx.getChild(0).getText(), "extends")) {
            return "extends" + referenceTypeTranslate.translateReferenceType((ParserRuleContext) ctx.getChild(1));
        }

        if (StringUtils.equals(ctx.getChild(0).getText(), "super")) {
            return "super" + referenceTypeTranslate.translateReferenceType((ParserRuleContext) ctx.getChild(1));
        }

        return null;
    }
}
