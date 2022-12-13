package com.test.migration.service.translate.declaration;

import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ClassBodyDeclarationTranslate {


    /**
     * classBodyDeclaration
     * 	:	classMemberDeclaration
     * 	|	instanceInitializer
     * 	|	staticInitializer
     * 	|	constructorDeclaration
     * 	;
     */
    public String translateClassBodyDeclaration(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_classBodyDeclaration)) {
            System.out.println("RULE_classBodyDeclaration 没找到，不科学");
            return null;
        }

        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);

        //classMemberDeclaration
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_classMemberDeclaration) {
            ClassMemberDeclarationTranslate subTranslate = new ClassMemberDeclarationTranslate();
            return subTranslate.translateClassMemberDeclaration(childRuleContext);
        }

        //instanceInitializer
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_instanceInitializer) {
            InstanceInitializerTranslate subTranslate = new InstanceInitializerTranslate();
            return subTranslate.translateInstanceInitializer(childRuleContext);
        }

        //staticInitializer
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_staticInitializer) {
            StaticInitializerTranslate subTranslate = new StaticInitializerTranslate();
            return subTranslate.translateStaticInitializer(childRuleContext);
        }

        //constructorDeclaration
        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_constructorDeclaration) {
            ConstructorDeclarationTranslate subTranslate = new ConstructorDeclarationTranslate();
            return subTranslate.translateConstructorDeclaration(childRuleContext);
        }

        return null;
    }
}
