package com.test.migration.service.translate.bnf.common.cls;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import com.test.migration.service.translate.bnf.common.ArgumentListTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import utils.Log;

public class ClassInstanceCreationExpressionLfPrimaryTranslate {
    /**
     * classInstanceCreationExpression_lf_primary
     * :	'.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * ;
     */
    public String translateClassInstanceCreationExpression_lf_primary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_classInstanceCreationExpression_lf_primary) {
            Log.error("RULE_classInstanceCreationExpression_lf_primary error");
            return null;
        }

        // '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // 简化为 '.' 'new' Identifier '(' argumentList? ')' classBody?
        // 获取第一个Identifier
        String identifier = ReplaceRuleService.replaceClassInstanceCreationIdentifier(ctx);

        // 获取argumentList
        ParserRuleContext argumentListRule = null;
        ParserRuleContext classBodyRule = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_argumentList) {
                argumentListRule = (ParserRuleContext) ctx.getChild(i);
            }
            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_classBody) {
                classBodyRule = (ParserRuleContext) ctx.getChild(i);
            }
        }

        String argumentList = "";
        //argumentListRule可能为空
        if (argumentListRule != null) {
            ArgumentListTranslate argumentListTranslate = new ArgumentListTranslate();
            argumentList = argumentListTranslate.translateArgumentList(argumentListRule);
        }

        String classBody = "";
        if (classBodyRule != null) {
            ClassBodyTranslate classBodyTranslate = new ClassBodyTranslate();
            classBody = classBodyTranslate.translateClassBody(classBodyRule);
        }

        //'.' 'new' Identifier '(' argumentList? ')' classBody?
        return "." + "new " + " " + identifier + "(" + argumentList + ")" + classBody;
    }
}
