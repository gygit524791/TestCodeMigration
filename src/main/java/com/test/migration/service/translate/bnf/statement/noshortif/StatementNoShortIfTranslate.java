package com.test.migration.service.translate.bnf.statement.noshortif;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.expression.ExpressionTranslate;
import com.test.migration.service.translate.bnf.statement.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class StatementNoShortIfTranslate {

    /**
     * statementNoShortIf
     * :	statementWithoutTrailingSubstatement
     * |	labeledStatementNoShortIf
     * |	ifThenElseStatementNoShortIf
     * |	whileStatementNoShortIf
     * |	forStatementNoShortIf
     * ;
     */
    public String translateStatementNoShortIf(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_statementNoShortIf) {
            System.out.println("RULE_statementNoShortIf 没找到");
            return null;
        }

        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statementWithoutTrailingSubstatement) {
            StatementWithoutTrailingSubStatementTranslate translate = new StatementWithoutTrailingSubStatementTranslate();
            return translate.translateStatementWithoutTrailingSubstatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_labeledStatementNoShortIf) {
            LabeledStatementNoShortIfTranslate translate = new LabeledStatementNoShortIfTranslate();
            return translate.translateLabeledStatementNoShortIf(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_ifThenElseStatementNoShortIf) {
            IfThenElseStatementNoShortIfTranslate translate = new IfThenElseStatementNoShortIfTranslate();
            return translate.translateIfThenElseNoShortIfStatement(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_whileStatementNoShortIf) {
            WhileStatementNoShortIfTranslate translate = new WhileStatementNoShortIfTranslate();
            return translate.translateWhileStatementNoShortIf(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_forStatementNoShortIf) {
            ForStatementNoShortIfTranslate translate = new ForStatementNoShortIfTranslate();
            return translate.translateForStatementNoShortIf(childRuleContext);
        }

        System.out.println("translateStatementNoShortIf error");
        return null;

    }

}
