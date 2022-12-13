package com.test.migration.service.translate.common;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.MappingRuleLoader;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Map;

public class UnannTypeTranslate {

    public String translateUnannType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannType) {
            System.out.println("unannTypeContext 为null");
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

            // todo 数组类型 暂不支持
            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannArrayType) {
                System.out.println("暂不支持RULE_unannArrayType, 目前直接返回文本");
                return referenceTypeRuleContext.getText();
            }

            // TODO: 待调研
            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannTypeVariable) {
                System.out.println("暂不支持RULE_unannTypeVariable");
                return "";
            }
        }
        return "";
    }

    public String translateUnannClassOrInterfaceType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannClassOrInterfaceType) {
            System.out.println("translateUnannClassOrInterfaceType 为null");
            return "";
        }

        // TODO 类/接口类型，从映射表中找对应关系
        Map<String, String> referenceMapping = queryReferenceMapping();
        return "";
    }

    public Map<String, String> queryReferenceMapping() {
        return MappingRuleLoader.commonClassNameMapping;
    }
}
