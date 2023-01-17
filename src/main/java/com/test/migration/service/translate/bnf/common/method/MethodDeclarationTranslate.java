package com.test.migration.service.translate.bnf.common.method;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.TranslateCodeCollector;
import com.test.migration.service.translate.TranslateHint;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import utils.Log;

import java.util.stream.Collectors;

public class MethodDeclarationTranslate {

    /**
     * methodDeclaration
     * :  methodModifier* methodHeader methodBody
     * ;
     */
    public String translateMethodDeclaration(ParserRuleContext ctx) {
        if ((ctx == null) || (ctx.getRuleIndex() != Java8Parser.RULE_methodDeclaration)) {
            Log.error("RULE_methodDeclaration error");
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
            Log.error("methodBodyRule error");
            return null;
        }

        MethodHeaderTranslate methodHeaderTranslate = new MethodHeaderTranslate();
        String methodHeader = methodHeaderTranslate.translateMethodHeader(methodHeaderRule);
        // 判断是否待翻译method中的内部方法，如果是就不收集header了
        boolean isInnerMethod = ctx.getStart().getLine() > TranslateCodeCollector.MethodTranslateCode.methodStartLine
                && ctx.getStop().getLine() < TranslateCodeCollector.MethodTranslateCode.methodEndLine;
//        Log.info(
//                "line info:"
//                        + " ctx start :" + ctx.getStart().getLine()
//                        + " ctx end:" + ctx.getStop().getLine()
//                        + " methodStartLine:" + TranslateCodeCollector.MethodTranslateCode.methodStartLine
//                        + " methodEndLine:" + TranslateCodeCollector.MethodTranslateCode.methodEndLine
//        );

        if (!isInnerMethod) {
            // 收集methodHeader信息
            TranslateCodeCollector.MethodTranslateCode.MethodHeaderTranslateCode methodHeaderTranslateCode = new TranslateCodeCollector.MethodTranslateCode.MethodHeaderTranslateCode();
            methodHeaderTranslateCode.translateCode = methodHeader;
            methodHeaderTranslateCode.misMatchCodes = TranslateHint.formatMisMatchCodes(TranslateHint.misMatchCodes);
            TranslateCodeCollector.methodHeaderTranslateCode = methodHeaderTranslateCode;
        }

        Log.info("methodHeader:" + methodHeader);

        MethodBodyTranslate methodBodyTranslate = new MethodBodyTranslate();
        String methodBody = methodBodyTranslate.translateMethodBody(methodBodyRule);

//        if (!isInnerMethod) {
//            System.out.println("not " + methodBody);
//        } else {
//            System.out.println("is " + methodBody);
//        }


        System.out.println("m==========blockStatementTranslateCodes=========1");
        TranslateCodeCollector.blockStatementTranslateCodes.forEach(x-> System.out.println(x.translateCode));
        System.out.println("m==========blockStatementTranslateCodes=========2");

        return methodHeader + " " + methodBody;
    }

}