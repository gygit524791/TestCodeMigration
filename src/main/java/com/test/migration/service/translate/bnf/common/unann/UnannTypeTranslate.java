package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class UnannTypeTranslate {

    /**
     * unannType
     * 	:	unannPrimitiveType
     * 	|	unannReferenceType
     * 	;
     */
    public String translateUnannType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannType) {
            System.out.println("RULE_unannType 为null");
            return "";
        }
        //要么是unannPrimitiveType，要么是unannReferenceType
        ParserRuleContext childNode = (ParserRuleContext)  ctx.getChild(0);
        //unannPrimitiveType直接返回自身（基础类型java和c++都一样）
        if (childNode.getRuleIndex() == Java8Parser.RULE_unannPrimitiveType) {
            UnannPrimitiveTypeTranslate translate = new UnannPrimitiveTypeTranslate();
            return translate.translateUnannPrimitiveType(childNode);
        }

        // 引用类型 unannReferenceType
        if (childNode.getRuleIndex() == Java8Parser.RULE_unannReferenceType) {
            UnannReferenceTypeTranslate translate = new UnannReferenceTypeTranslate();
            return translate.translateUnannReferenceType(childNode);
        }

        System.out.println("translateUnannType error");

        return "";
    }
}
