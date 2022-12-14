package com.test.migration.service.translate.common.cls;

import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.MappingRuleLoader;
import com.test.migration.service.translate.common.ArgumentListTranslate;
import com.test.migration.service.translate.common.ClassBodyTranslate;
import com.test.migration.service.translate.common.ExpressionNameTranslate;
import com.test.migration.service.translate.common.PrimaryTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Map;

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
            System.out.println("RULE_classInstanceCreationExpression 没找到，不科学");
            return null;
        }
        ParseTree child = ctx.getChild(0);
        boolean isRuleContext = child instanceof RuleContext;

        // 获取第一个Identifier
        String identifier = getIdentifier(ctx);

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

        System.out.println("ERROR: translateClassInstanceCreationExpression");

        return null;
    }


    /**
     * classInstanceCreationExpression_lf_primary
     * :	'.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * ;
     */
    public String translateClassInstanceCreationExpression_lf_primary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_classInstanceCreationExpression_lf_primary) {
            System.out.println("RULE_classInstanceCreationExpression_lf_primary 没找到，不科学");
            return null;
        }

        // '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // 简化为 '.' 'new' Identifier '(' argumentList? ')' classBody?
        // 获取第一个Identifier
        String identifier = getIdentifier(ctx);

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


    /**
     * classInstanceCreationExpression_lfno_primary
     * :	'new' typeArguments? annotation* Identifier ('.' annotation* Identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * |	expressionName '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * ;
     */
    public String translateClassInstanceCreationExpression_lfno_primary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_classInstanceCreationExpression_lfno_primary) {
            System.out.println("RULE_classInstanceCreationExpression_lfno_primary 没找到，不科学");
            return null;
        }

        ParseTree child = ctx.getChild(0);
        boolean isRuleContext = child instanceof RuleContext;

        // 获取第一个Identifier, 这个new 后面的identifier 按说应该类似于typename 类型名称，需要做替换的
        String identifier = getIdentifier(ctx);

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

        //'new' typeArguments? annotation* Identifier ('.' annotation* Identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody
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

        System.out.println("ERROR: translateClassInstanceCreationExpression_lfno_primary");
        return null;
    }


    /**
     * 存在替换规则
     * new 后面的identifier 相当于typeName
     */
    private static String getIdentifier(ParserRuleContext ctx) {
        String identifier = "";
        for (int k = 0; k < ctx.getChildCount(); k++) {
            ParseTree child1 = ctx.getChild(k);
            if (child1 instanceof TerminalNode terminalNode) {
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode.getText();
                    // 只取第一个identifier，取到后必须break
                    break;
                }
            }
        }

        Map<String, String> commonClassNameMapping = MappingRuleLoader.commonClassNameMapping;
        return commonClassNameMapping.getOrDefault(identifier, identifier);
    }
}
