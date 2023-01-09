package com.test.migration.service.translate.bnf.common;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

import java.util.List;
import java.util.stream.Collectors;

public class TypeArgumentListTranslate {

    /**
     * typeArgumentList
     * :	typeArgument (',' typeArgument)*
     * ;
     */
    public String translateTypeArgumentList(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_typeArgumentList)) {
            System.out.println("RULE_typeArgumentList 没找到");
            return null;
        }
        List<ParserRuleContext> typeArgumentCtxList = Lists.newArrayList();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_typeArgument) {
                typeArgumentCtxList.add((ParserRuleContext) childRuleContext);
            }
        }

        TypeArgumentTranslate typeArgumentTranslate = new TypeArgumentTranslate();
        List<String> typeArgumentList = typeArgumentCtxList.stream()
                .map(typeArgumentTranslate::translateTypeArgument)
                .collect(Collectors.toList());

        return Joiner.on(",").join(typeArgumentList);
    }
}
