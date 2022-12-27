package com.test.migration.service.translate.bnf.statement;

import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.declaration.ClassDeclarationTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

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
            System.out.println("RULE_block 没找到，不科学");
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
            System.out.println("RULE_blockStatements 没找到，不科学");
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
        StringBuilder stringBuilder = new StringBuilder();
        blockStatementList.forEach(blockStatement -> stringBuilder.append(translateBlockStatement(blockStatement)));
        return stringBuilder.toString();
    }

    /**
     * lockStatement
     * :	localVariableDeclarationStatement
     * |	classDeclaration
     * |	statement
     * ;
     *
     * @param ctx
     * @return
     */
    private String translateBlockStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_blockStatement) {
            System.out.println("RULE_blockStatement 没找到，不科学");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_localVariableDeclarationStatement) {
            LocalVariableDeclarationStatementTranslate subTranslate = new LocalVariableDeclarationStatementTranslate();
            return subTranslate.translateLocalVariableDeclarationStatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_classDeclaration) {
            ClassDeclarationTranslate subTranslate = new ClassDeclarationTranslate();
            return subTranslate.translateClassDeclaration(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statement) {
            StatementTranslate subTranslate = new StatementTranslate();
            return subTranslate.translateStatement(childRuleContext);
        }

        System.out.println("translateBlockStatement 不可能到这里");
        return null;
    }


}