package com.test.migration.service.translate.bnf.common.cls;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import com.test.migration.service.translate.TranslateCodeCollector;
import com.test.migration.service.translate.bnf.common.ArgumentListTranslate;
import com.test.migration.service.translate.bnf.common.ExpressionNameTranslate;
import com.test.migration.service.translate.bnf.common.primary.PrimaryTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.Log;

public class ClassInstanceCreationExpressionTranslate {

    /**
     * classInstanceCreationExpression
     * :	'new' typeArguments? annotation* Identifier ('.' annotation* Identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * |	expressionName '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * |	primary '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * ;
     * <p>
     * typeArguments, typeArgumentsOrDiamond:范型，均不转换
     */
    public String translateClassInstanceCreationExpression(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_classInstanceCreationExpression) {
            Log.error("RULE_classInstanceCreationExpression error");
            return null;
        }
        ParseTree child = ctx.getChild(0);
        boolean isRuleContext = child instanceof RuleContext;

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

        //'new' typeArguments? annotation* Identifier ('.' annotation* Identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // 简化转换为：'new' Identifier '(' argumentList? ')' classBody?
        if (!isRuleContext) {
            return "new " + identifier + "(" + argumentList + ")" + classBody;
        }

        // expressionName '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // 简化转换为：expressionName '.' 'new' Identifier '(' argumentList? ')' classBody?
        if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_expressionName) {
            ExpressionNameTranslate expressionNameTranslate = new ExpressionNameTranslate();
            String expressionName = expressionNameTranslate.translateExpressionName((ParserRuleContext) child);

            return expressionName + "." + "new " + identifier + "(" + argumentList + ")" + classBody;
        }


        // primary '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // 简化转换为：primary '.' 'new' Identifier '(' argumentList? ')' classBody?
        if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_primary) {
            PrimaryTranslate primaryTranslate = new PrimaryTranslate();
            String primary = primaryTranslate.translatePrimary((ParserRuleContext) child);
            return primary + "." + "new " + identifier + "(" + argumentList + ")" + classBody;
        }
        Log.error("translateClassInstanceCreationExpression error");
        return null;
    }

}
