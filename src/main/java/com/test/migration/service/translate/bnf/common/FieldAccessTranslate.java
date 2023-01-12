package com.test.migration.service.translate.bnf.common;

import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.primary.PrimaryTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class FieldAccessTranslate {

    /**
     * fieldAccess
     * :	primary '.' Identifier
     * |	'super' '.' Identifier
     * |	typeName '.' 'super' '.' Identifier
     * ;
     */
    public String translateFieldAccess(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_fieldAccess) {
            System.out.println("RULE_fieldAccess 没找到");
            return null;
        }
        ParseTree identifier = null;
        ParserRuleContext primaryCtx = null;
        ParserRuleContext typeNameCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_primary) {
                    primaryCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_typeName) {
                    typeNameCtx = (ParserRuleContext) child;
                }
            }
            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode;
                }
            }
        }

        PrimaryTranslate primaryTranslate = new PrimaryTranslate();
        String identifierStr = identifier == null ? "" : identifier.getText();
        String primary = primaryCtx == null ? "" : primaryTranslate.translatePrimary(primaryCtx);
        String typeName = typeNameCtx == null ? "" : translateTypeName(typeNameCtx);

        ParseTree firstChild = ctx.getChild(0);
        if (firstChild instanceof TerminalNode) {
            //'super' '.' Identifier
            return "super" + "->" + identifierStr;
        } else {
            RuleContext firstChildRuleContext = (RuleContext) firstChild;
            //primary '.' Identifier
            if (firstChildRuleContext.getRuleIndex() == Java8Parser.RULE_primary) {
                return primary + "->" + identifierStr;
            }

            //typeName '.' 'super' '.' Identifier
            if (firstChildRuleContext.getRuleIndex() == Java8Parser.RULE_typeName) {
                return typeName + "->" + "super" + "->" + identifierStr;
            }
        }

        return ctx.getText();
    }

    public String translateTypeName(ParserRuleContext ctx) {
        if (ctx.getRuleIndex() != Java8Parser.RULE_typeName) {
            System.out.println("typeNameRule 没找到，不科学:" + ctx.getText());
            return null;
        }
        return ctx.getText();
    }
}
