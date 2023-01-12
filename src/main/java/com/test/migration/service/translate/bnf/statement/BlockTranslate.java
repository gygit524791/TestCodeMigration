package com.test.migration.service.translate.bnf.statement;

import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.TranslateCodeCollector;
import com.test.migration.service.translate.TranslateHint;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.Log;

import java.util.List;

public class BlockTranslate {

    /**
     * block
     * :	'{' blockStatements? '}'
     * ;
     * ?含义：>=0个
     *
     * @param ctx
     * @return
     */
    public String translateBlock(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_block) {
            Log.error("RULE_block 没找到，不科学");
            return null;
        }
        // 没有blockStatements
        if (ctx.getChildCount() == 2) {
            return "{ }";
        }

        // 对于method里面的每个blockStatement
        List<ParserRuleContext> blockStatementsList = Lists.newArrayList();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            blockStatementsList.add((ParserRuleContext) child);
        }

        StringBuilder blockStatementsStr = new StringBuilder();
        blockStatementsList.forEach(blockStatements -> blockStatementsStr.append(translateBlockStatements(blockStatements)));
        return "{ " + blockStatementsStr + " }";
    }


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
            Log.error("RULE_blockStatements 没找到，不科学");
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