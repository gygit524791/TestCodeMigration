package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.statement.BlockTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class TryStatementTranslate {

    /**
     * tryStatement
     * :	'try' block catches
     * |	'try' block catches? finally_
     * |	tryWithResourcesStatement
     * ;
     */
    public String translateTryStatement(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_tryStatement)) {
            System.out.println("RULE_tryStatement 没找到");
            return null;
        }
        if (ctx.getChildCount() == 0) {
            return translateTryWithResourcesStatement((ParserRuleContext) ctx.getChild(0));
        }
        ParserRuleContext blockCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_block) {
                blockCtx = childRuleContext;
            }
        }
        BlockTranslate blockTranslate = new BlockTranslate();
        return blockTranslate.translateBlock(blockCtx);

    }


    public String translateTryWithResourcesStatement(ParserRuleContext ctx) {
        return ctx.getText();
    }
}
