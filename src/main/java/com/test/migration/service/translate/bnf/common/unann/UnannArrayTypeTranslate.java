package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class UnannArrayTypeTranslate {

    /**
     * unannArrayType
     * 	:	unannPrimitiveType dims
     * 	|	unannClassOrInterfaceType dims
     * 	|	unannTypeVariable dims
     * 	;
     */
    public String translateUnannArrayType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannArrayType) {
            System.out.println("RULE_unannArrayType 为null");
            return "";
        }






        return "";
    }

    /**
     * unannTypeVariable
     * 	:	Identifier
     * 	;
     */
    public String translateUnannTypeVariable(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannTypeVariable) {
            System.out.println("RULE_unannTypeVariable 为null");
            return "";
        }

        return ctx.getText();
    }

    public String translateUnannClassOrInterfaceType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannClassOrInterfaceType) {
            System.out.println("translateUnannClassOrInterfaceType 为null");
            return "";
        }

        // TODO 类/接口类型，从映射表中找对应关系
        // TODO TIPS 不考虑存在注解和范型的情况
        return ReplaceRuleService.replaceClassOrInterfaceType(ctx.getText());
    }



}
