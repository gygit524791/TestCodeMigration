package com.test.migration.service.translate.bnf.statement;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class StatementTranslate {

    /**
     * statement
     * :  statementWithoutTrailingSubstatement
     * |  labeledStatement
     * |  ifThenStatement
     * |  ifThenElseStatement
     * |  whileStatement
     * |  forStatement
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateStatement(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_statement) {
            System.out.println("RULE_statement 没找到，不科学");
            return null;
        }
        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statementWithoutTrailingSubstatement) {
            StatementWithoutTrailingSubStatementTranslate translate = new StatementWithoutTrailingSubStatementTranslate();
            return translate.translateStatementWithoutTrailingSubstatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_labeledStatement) {
            System.out.println("RULE_labeledStatement 建设中");
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_ifThenStatement) {
            System.out.println("RULE_ifThenStatement 建设中");
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_ifThenElseStatement) {
            System.out.println("RULE_ifThenElseStatement 建设中");
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_whileStatement) {
            WhileStatementTranslate whileStatementTranslate = new WhileStatementTranslate();
            return whileStatementTranslate.translateWhileStatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_forStatement) {
            System.out.println("RULE_forStatement 建设中");
        }

        System.out.println("statement解析不可能来到这里，赶紧查一下bug");
        return null;
    }

}
