package com.test.migration.service.translate.bnf.common.method;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.TranslateCodeCollector;
import com.test.migration.service.translate.TranslateHint;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class MethodDeclarationTranslate {

    /**
     * methodDeclaration
     * :  methodModifier* methodHeader methodBody
     * ;
     */
    public String translateMethodDeclaration(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_methodDeclaration)) {
            System.out.println("RULE_methodDeclaration 没找到，不科学");
            return null;
        }

        //筛选出methodBody
        ParserRuleContext methodHeaderRule = null;
        ParserRuleContext methodBodyRule = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_methodHeader) {
                methodHeaderRule = (ParserRuleContext) ctx.getChild(i);
            }
            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_methodBody) {
                methodBodyRule = (ParserRuleContext) ctx.getChild(i);
            }
        }

        if (methodBodyRule == null) {
            System.out.println("methodBodyRule为空");
            return null;
        }

        MethodHeaderTranslate methodHeaderTranslate = new MethodHeaderTranslate();
        String methodHeader = methodHeaderTranslate.translateMethodHeader(methodHeaderRule);
        // 判断是否待翻译method中的内部方法，如果是就不收集header了
        boolean isInnerMethod = ctx.getStart().getLine() < TranslateCodeCollector.methodStartLine && ctx.getStop().getLine() > TranslateCodeCollector.methodEndLine;
        if (!isInnerMethod) {
            // 收集methodHeader信息
            // TODO 存在bug
            TranslateCodeCollector.MethodTranslateCode.MethodHeaderTranslateCode methodHeaderTranslateCode = new TranslateCodeCollector.MethodTranslateCode.MethodHeaderTranslateCode();
            methodHeaderTranslateCode.translateCode = methodHeader;
            methodHeaderTranslateCode.misMatchCodes = TranslateHint.misMatchCodes;
            TranslateCodeCollector.methodHeaderTranslateCode = methodHeaderTranslateCode;
        }


        MethodBodyTranslate methodBodyTranslate = new MethodBodyTranslate();
        String methodBody = methodBodyTranslate.translateMethodBody(methodBodyRule);
        return methodHeader + " " + methodBody;
    }

}