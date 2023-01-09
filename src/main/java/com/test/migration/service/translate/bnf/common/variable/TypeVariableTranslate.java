package com.test.migration.service.translate.bnf.common.variable;

import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class TypeVariableTranslate {

    /**
     * typeVariable
     * :	annotation* Identifier
     * ;
     */
    public String translateTypeVariable(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_typeVariable)) {
            System.out.println("RULE_typeVariable 没找到，不科学");
            return null;
        }
        String identifier = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode.getText();
                }
            }
        }

        return identifier;
    }
}
