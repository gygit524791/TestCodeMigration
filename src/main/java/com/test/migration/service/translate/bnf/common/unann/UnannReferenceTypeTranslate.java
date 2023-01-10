package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnannReferenceTypeTranslate {

    /**
     * unannReferenceType
     * :	unannClassOrInterfaceType
     * |	unannTypeVariable
     * |	unannArrayType
     * ;
     */
    public String translateUnannReferenceType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannReferenceType) {
            System.out.println("translateUnannClassOrInterfaceType 为null");
            return "";
        }
        // 只可能有一个孩子节点，类型是：类/接口 数组  unannTypeVariable之一
        ParserRuleContext ctxChild = (ParserRuleContext) ctx.getChild(0);
        // 类/接口类型
        if (ctxChild.getRuleIndex() == Java8Parser.RULE_unannClassOrInterfaceType) {
            UnannClassOrInterfaceTypeTranslate translate = new UnannClassOrInterfaceTypeTranslate();
            return translate.translateUnannClassOrInterfaceType(ctxChild);
        }

        if (ctxChild.getRuleIndex() == Java8Parser.RULE_unannArrayType) {
            UnannArrayTypeTranslate translate = new UnannArrayTypeTranslate();
            return translate.translateUnannArrayType(ctxChild);
        }

        if (ctxChild.getRuleIndex() == Java8Parser.RULE_unannTypeVariable) {
            UnannTypeVariableTranslate translate = new UnannTypeVariableTranslate();
            return translate.translateUnannTypeVariable(ctxChild);
        }

        System.out.println("translateUnannReferenceType error");
        return null;
    }
}
