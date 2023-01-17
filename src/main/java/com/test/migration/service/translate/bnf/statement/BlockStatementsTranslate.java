package com.test.migration.service.translate.bnf.statement;

import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.Log;

import java.util.List;

public class BlockStatementsTranslate {
   
    /**
     * blockStatements
     * :	blockStatement+
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateBlockStatements(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_blockStatements) {
            Log.error("RULE_blockStatements error");
            return null;
        }
        List<ParserRuleContext> blockStatementList = Lists.newArrayList();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            blockStatementList.add((ParserRuleContext) child);
        }
        StringBuilder blockStatementListStr = new StringBuilder();
        BlockStatementTranslate blockStatementTranslate = new BlockStatementTranslate();
        blockStatementList.forEach(blockStatement -> {
            String translateBlockStatement = blockStatementTranslate.translateBlockStatement(blockStatement);
            blockStatementListStr.append(translateBlockStatement);
        });
        return blockStatementListStr.toString();
    }


}