package com.test.migration.service.translate.bnf.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.TryStatementTranslate;
import org.antlr.v4.runtime.ParserRuleContext;

public class StatementWithoutTrailingSubStatementTranslate {
    /**
     * statementWithoutTrailingSubstatement
     * :  block
     * |  emptyStatement
     * |  expressionStatement
     * |  assertStatement
     * |  switchStatement
     * |  doStatement
     * |  breakStatement
     * |  continueStatement
     * |  returnStatement
     * |  synchronizedStatement
     * |  throwStatement
     * |  tryStatement
     * ;
     *
     *
     * breakStatement
     * 	:	'break' Identifier? ';'
     * 	;
     *
     * continueStatement
     * 	:	'continue' Identifier? ';'
     * 	;
     * @param ctx
     * @return
     */
    public String translateStatementWithoutTrailingSubstatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_statementWithoutTrailingSubstatement) {
            System.out.println("RULE_statementWithoutTrailingSubstatement 没找到，不科学");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_block) {
            BlockTranslate translate = new BlockTranslate();
            return translate.translateBlock(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_emptyStatement) {
            return ";";
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_returnStatement) {
            ReturnStatementTranslate translate = new ReturnStatementTranslate();
            return translate.translateReturnStatement(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_expressionStatement) {
            ExpressionStatement translate = new ExpressionStatement();
            return translate.translateExpressionStatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_doStatement) {
            DoStatementTranslate translate = new DoStatementTranslate();
            return translate.translateDoStatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_throwStatement) {
            ThrowStatementTranslate translate = new ThrowStatementTranslate();
            return translate.translateThrowStatement(childRuleContext);
        }

        /** 待测试 **/
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_breakStatement) {
            return childRuleContext.getText();
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_continueStatement) {
            return childRuleContext.getText();
        }

        /** 暂不考虑 **/
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_switchStatement) {
            System.out.println("RULE_switchStatement 建设中");
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_synchronizedStatement) {
            System.out.println("RULE_synchronizedStatement 建设中");
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_tryStatement) {
            TryStatementTranslate translate = new TryStatementTranslate();
            return translate.translateTryStatement(childRuleContext);
        }
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_assertStatement) {
            System.out.println("RULE_assertStatement 建设中");
            return null;
        }

        return null;
    }



}
