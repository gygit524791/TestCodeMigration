package com.test.migration.service.translate.common;

import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.common.parameter.FormalParameterListTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MethodDeclaratorTranslate {

    /**
     * methodDeclarator
     * :	Identifier '(' formalParameterList? ')' dims?
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateMethodDeclarator(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodDeclarator) {
            System.out.println("RULE_methodDeclarator 没找到，不科学");
            return null;
        }
        String identifier = "";
        String formalParameterList = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode terminalNode) {
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode.getText();
                }
            }

            if (child instanceof RuleContext ruleContext) {
                if (ruleContext.getRuleIndex() == Java8Parser.RULE_formalParameterList) {
                    FormalParameterListTranslate formalParameterListTranslate = new FormalParameterListTranslate();
                    formalParameterList = formalParameterListTranslate.translateFormalParameterList((ParserRuleContext) ruleContext);
                }
            }
        }
        return identifier + "(" + formalParameterList + ")";
    }
}
