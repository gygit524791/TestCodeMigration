package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class UnannTypeTranslate {

    public String translateUnannType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannType) {
            System.out.println("RULE_unannType 为null");
            return "";
        }
        //要么是unannPrimitiveType，要么是unannReferenceType
        ParseTree child = ctx.getChild(0);
        RuleContext childNode = (RuleContext) child;
        //unannPrimitiveType直接返回自身（基础类型java和c++都一样）
        if (childNode.getRuleIndex() == Java8Parser.RULE_unannPrimitiveType) {
            return childNode.getText();
        }

        // 引用类型
        if (childNode.getRuleIndex() == Java8Parser.RULE_unannReferenceType) {
            // 只可能有一个孩子节点，类型是：类/接口 数组  unannTypeVariable之一
            ParseTree referenceTypeChild = childNode.getChild(0);
            RuleContext referenceTypeRuleContext = (RuleContext) referenceTypeChild;
            // 类/接口类型
            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannClassOrInterfaceType) {
                return translateUnannClassOrInterfaceType((ParserRuleContext) referenceTypeRuleContext);
            }

            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannArrayType) {
                UnannArrayTypeTranslate translate = new UnannArrayTypeTranslate();
                return translate.translateUnannArrayType((ParserRuleContext) referenceTypeChild);
            }

            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannTypeVariable) {
                UnannTypeVariableTranslate translate = new UnannTypeVariableTranslate();
                return translate.translateUnannTypeVariable((ParserRuleContext) referenceTypeRuleContext);
            }
        }

        System.out.println("translateUnannType error");

        return "";
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
