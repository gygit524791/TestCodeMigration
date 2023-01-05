package com.test.migration.service.translate.bnf.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.PartMigrationMainTest;
import com.test.migration.service.translate.TranslateCodeCollector;
import com.test.migration.service.translate.TranslateHint;
import com.test.migration.service.translate.bnf.declaration.ClassDeclarationTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;

public class BlockStatementTranslate {

    /**
     * lockStatement
     * :	localVariableDeclarationStatement
     * |	classDeclaration
     * |	statement
     * ;
     */
    public String translateBlockStatement(ParserRuleContext ctx) {
        TranslateHint.init();
        TranslateHint.misMatchCodes.add(TranslateHint.BS_HINT_TAG);
        String translateBlockStatement = null;
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_blockStatement) {
            System.out.println("RULE_blockStatement 没找到，不科学");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_localVariableDeclarationStatement) {
            LocalVariableDeclarationStatementTranslate subTranslate = new LocalVariableDeclarationStatementTranslate();
            translateBlockStatement = subTranslate.translateLocalVariableDeclarationStatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_classDeclaration) {
            ClassDeclarationTranslate subTranslate = new ClassDeclarationTranslate();
            translateBlockStatement = subTranslate.translateClassDeclaration(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statement) {
            StatementTranslate subTranslate = new StatementTranslate();
            translateBlockStatement = subTranslate.translateStatement(childRuleContext);
        }

        if (translateBlockStatement == null) {
            System.out.println("translateBlockStatement 出错");
        }

        // 调整策略
        String key = ctx.getStart().getLine() + "$" + translateBlockStatement;
        String modifyType = PartMigrationMainTest.blockStatementModifyMap.getOrDefault(key, "keep");
        if (StringUtils.equals(modifyType, "remove")) {
            // 清空bs收集到的hint
            TranslateHint.init();
            System.out.println("命中 部分迁移调整策略，被remove");
            return "";
        }

        // 收集器
        TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode blockStatementTranslateCode = new TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode();
        blockStatementTranslateCode.translateCode = translateBlockStatement;
        blockStatementTranslateCode.misMatchCodes = TranslateHint.misMatchCodes;
        TranslateCodeCollector.blockStatementTranslateCodes.add(blockStatementTranslateCode);

        return translateBlockStatement;
    }
}
