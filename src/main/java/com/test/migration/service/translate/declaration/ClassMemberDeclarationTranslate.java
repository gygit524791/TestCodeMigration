package com.test.migration.service.translate.declaration;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ClassMemberDeclarationTranslate {

    /**
     * classMemberDeclaration
     * :	fieldDeclaration
     * |	methodDeclaration
     * |	classDeclaration
     * |	interfaceDeclaration
     * |	';'
     * ;
     */
    public String translateClassMemberDeclaration(ParserRuleContext ctx) {
        ParseTree child = ctx.getChild(0);
        boolean isRuleContext = child instanceof RuleContext;
        // ;
        if (!isRuleContext) {
            return ctx.getText();
        }
        ParserRuleContext node = (ParserRuleContext) child;

        //fieldDeclaration
        if (node.getRuleIndex() == Java8Parser.RULE_fieldDeclaration) {
            FieldDeclarationTranslate subTranslate = new FieldDeclarationTranslate();
            return subTranslate.translateFieldDeclaration(node);
        }

        //methodDeclaration
        if (node.getRuleIndex() == Java8Parser.RULE_methodDeclaration) {
            MethodDeclarationTranslate subTranslate = new MethodDeclarationTranslate();
            return subTranslate.translateMethodDeclaration(node);
        }

        //classDeclaration
        if (node.getRuleIndex() == Java8Parser.RULE_fieldDeclaration) {
            ClassDeclarationTranslate subTranslate = new ClassDeclarationTranslate();
            return subTranslate.translateClassDeclaration(node);
        }

        //interfaceDeclaration
        if (node.getRuleIndex() == Java8Parser.RULE_fieldDeclaration) {
            InterfaceDeclarationTranslate subTranslate = new InterfaceDeclarationTranslate();
            return subTranslate.translateInterfaceDeclaration(node);
        }

        return null;
    }
}