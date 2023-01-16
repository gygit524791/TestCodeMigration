package com.test.migration.service.translate.bnf.common.array;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.dims.DimsTranslate;
import com.test.migration.service.translate.bnf.common.cls.ClassOrInterfaceTypeTranslate;
import com.test.migration.service.translate.bnf.common.variable.TypeVariableTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ArrayTypeTranslate {

    /**
     * arrayType
     * :	primitiveType dims
     * |	classOrInterfaceType dims
     * |	typeVariable dims
     * ;
     */
    public String translateArrayType(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_arrayType)) {
            System.out.println("RULE_arrayType 没找到");
            return null;
        }

        // 1.获取各个类型子节点
        ParserRuleContext primitiveTypeCtx = null;
        ParserRuleContext dimsCtx = null;
        ParserRuleContext classOrInterfaceTypeCtx = null;
        ParserRuleContext typeVariableCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_primitiveType) {
                    primitiveTypeCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_dims) {
                    dimsCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_classOrInterfaceType) {
                    classOrInterfaceTypeCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_typeVariable) {
                    typeVariableCtx = (ParserRuleContext) child;
                }
            }
        }
        DimsTranslate dimsTranslate = new DimsTranslate();
        ClassOrInterfaceTypeTranslate classOrInterfaceTypeTranslate = new ClassOrInterfaceTypeTranslate();
        TypeVariableTranslate typeVariableTranslate = new TypeVariableTranslate();
        String primitiveType = primitiveTypeCtx == null ? "" : primitiveTypeCtx.getText();
        String dims = dimsCtx == null ? "" : dimsTranslate.translateDims(dimsCtx);
        String classOrInterfaceType = classOrInterfaceTypeCtx == null ? "" : classOrInterfaceTypeTranslate.translateClassOrInterfaceType(classOrInterfaceTypeCtx);
        String typeVariable = typeVariableCtx == null ? "" : typeVariableTranslate.translateTypeVariable(typeVariableCtx);

        // 2. 判断第一个孩子节点的类型
        RuleContext firstChild = (RuleContext) ctx.getChild(0);
        int ruleIndex = firstChild.getRuleIndex();

        //primitiveType dims
        if (ruleIndex == Java8Parser.RULE_primitiveType) {
            return primitiveType + " " + dims;
        }

        //classOrInterfaceType dims
        if (ruleIndex == Java8Parser.RULE_expressionName) {
            return classOrInterfaceType + " " + dims;
        }

        //typeVariable dims
        if (ruleIndex == Java8Parser.RULE_typeName) {
            return typeVariable + " " + dims;
        }

        System.out.println("translateArrayType error");
        return null;
    }


}
