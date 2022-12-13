package com.test.migration.service.translate.declaration;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.common.ClassBodyTranslate;
import com.test.migration.service.translate.common.TypeParametersTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.apache.commons.lang3.StringUtils;

public class NormalClassDeclarationTranslate {

    /**
     * normalClassDeclaration
     * :	classModifier* 'class' Identifier typeParameters? superclass? superinterfaces? classBody
     * ;
     * TIPS:
     * classModifier，superclass，superinterfaces均不考虑在内
     */
    public String translateNormalClassDeclaration(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_normalClassDeclaration)) {
            System.out.println("RULE_normalClassDeclaration 没找到，不科学");
            return null;
        }

        // Identifier
        String identifier = "";

        ParserRuleContext typeParametersRule = null;
        ParserRuleContext classBodyRule = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            // todo trick
            if (StringUtils.equals(ctx.getChild(i).getText(), "class")) {
                identifier = ctx.getChild(i + 1).getText();
            }

            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_typeParameters) {
                typeParametersRule = (ParserRuleContext) ctx.getChild(i);
            }

            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_classBody) {
                classBodyRule = (ParserRuleContext) ctx.getChild(i);
            }
        }

        // typeParameters 范型
        TypeParametersTranslate typeParametersTranslate = new TypeParametersTranslate();
        String typeParameters = typeParametersTranslate.translateTypeParameters(typeParametersRule);

        // classBody
        ClassBodyTranslate classBodyTranslate = new ClassBodyTranslate();
        String classBody = classBodyTranslate.translateClassBody(classBodyRule);

        return identifier + typeParameters + classBody;
    }
}
