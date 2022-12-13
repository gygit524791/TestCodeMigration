package com.test.migration.service.translate.common;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.statement.BlockTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class MethodBodyTranslate {
    /**
     * methodBody
     * :	block
     * |	';'
     * ;
     */
    public String translateMethodBody(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodBody) {
            System.out.println("RULE_methodBody 没找到，不科学");
            return null;
        }

        ParseTree child = ctx.getChild(0);
        boolean isRuleContext = child instanceof RuleContext;
        if (!isRuleContext) {
            return ctx.getText();
        }

        BlockTranslate blockTranslate = new BlockTranslate();
        return blockTranslate.translateBlock((ParserRuleContext) child);
    }

}
