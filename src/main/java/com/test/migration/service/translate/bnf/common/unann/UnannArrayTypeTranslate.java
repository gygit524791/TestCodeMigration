package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.dims.DimsTranslate;
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

        // 1.获取各个类型子节点
        ParserRuleContext unannPrimitiveTypeCtx = null;
        ParserRuleContext unannClassOrInterfaceTypeCtx = null;
        ParserRuleContext unannTypeVariableCtx = null;
        ParserRuleContext dimsCtx = null;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_unannPrimitiveType) {
                    unannPrimitiveTypeCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_unannClassOrInterfaceType) {
                    unannClassOrInterfaceTypeCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_unannTypeVariable) {
                    unannTypeVariableCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_dims) {
                    dimsCtx = (ParserRuleContext) child;
                }
            }
        }
        UnannPrimitiveTypeTranslate unannPrimitiveTypeTranslate = new UnannPrimitiveTypeTranslate();
        UnannClassOrInterfaceTypeTranslate unannClassOrInterfaceTypeTranslate = new UnannClassOrInterfaceTypeTranslate();
        UnannTypeVariableTranslate unannTypeVariableTranslate = new UnannTypeVariableTranslate();
        DimsTranslate dimsTranslate = new DimsTranslate();

        String unannPrimitiveType = unannPrimitiveTypeCtx == null ? "" : unannPrimitiveTypeTranslate.translateUnannPrimitiveType(unannPrimitiveTypeCtx);
        String unannClassOrInterfaceType = unannClassOrInterfaceTypeCtx == null ? "" : unannClassOrInterfaceTypeTranslate.translateUnannClassOrInterfaceType(unannClassOrInterfaceTypeCtx);
        String unannTypeVariable = unannTypeVariableCtx == null ? "" : unannTypeVariableTranslate.translateUnannTypeVariable(unannTypeVariableCtx);
        String dims = dimsCtx == null ? "" : dimsTranslate.translateDims(dimsCtx);

        // 2. 判断第一个孩子节点的类型
        RuleContext firstChild = (RuleContext) ctx.getChild(0);
        int ruleIndex = firstChild.getRuleIndex();

        //primitiveType dims
        if (ruleIndex == Java8Parser.RULE_unannPrimitiveType) {
            return unannPrimitiveType + " " + dims;
        }

        //classOrInterfaceType dims
        if (ruleIndex == Java8Parser.RULE_unannClassOrInterfaceType) {
            return unannClassOrInterfaceType + " " + dims;
        }

        //typeVariable dims
        if (ruleIndex == Java8Parser.RULE_unannTypeVariable) {
            return unannTypeVariable + " " + dims;
        }

        System.out.println("translateArrayType error");
        return null;
    }

}
