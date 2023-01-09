package com.test.migration.service.translate.bnf.common.method;

import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import com.test.migration.service.translate.bnf.common.ArgumentListTranslate;
import com.test.migration.service.translate.bnf.common.ExpressionNameTranslate;
import com.test.migration.service.translate.bnf.common.TypeArgumentsTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

public class MethodInvocationTranslate {

    /**
     * methodInvocation
     * :	methodName '(' argumentList? ')'
     * |	typeName '.' typeArguments? Identifier '(' argumentList? ')'
     * |	expressionName '.' typeArguments? Identifier '(' argumentList? ')'
     * |	primary '.' typeArguments? Identifier '(' argumentList? ')'
     * |	'super' '.' typeArguments? Identifier '(' argumentList? ')'
     * |	typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateMethodInvocation(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodInvocation) {
            System.out.println("RULE_methodInvocation 为null");
            return null;
        }
        return parseMethodInvocation(ctx);
    }


    /**
     * methodInvocation_lfno_primary
     * :	methodName '(' argumentList? ')'
     * |	typeName '.' typeArguments? Identifier '(' argumentList? ')'
     * |	expressionName '.' typeArguments? Identifier '(' argumentList? ')'
     * |	'super' '.' typeArguments? Identifier '(' argumentList? ')'
     * |	typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
     * ;
     * <p>
     * 方法调用，antlr将方法调用的写法格式划分为3类12种（很多都不常用）
     * methodInvocation_lfno_primary
     */
    public String translateMethodInvocationLfNoPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodInvocation_lfno_primary) {
            System.out.println("RULE_methodInvocation_lfno_primary 为null");
            return null;
        }
        return parseMethodInvocation(ctx);
    }

    /**
     * methodInvocation_lf_primary
     * :	'.' typeArguments? Identifier '(' argumentList? ')'
     * ;
     */
    public String translateMethodInvocationLfPrimary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodInvocation_lf_primary) {
            System.out.println("RULE_methodInvocation_lf_primary 为null");
            return null;
        }
        String identifier = StringUtils.EMPTY;
        ParserRuleContext typeArgumentsCtx = null;
        ParserRuleContext argumentListCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext) {
                RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
                if (childRuleContext.getRuleIndex() == Java8Parser.RULE_typeArguments) {
                    typeArgumentsCtx = (ParserRuleContext) childRuleContext;
                }
                if (childRuleContext.getRuleIndex() == Java8Parser.RULE_argumentList) {
                    argumentListCtx = (ParserRuleContext) childRuleContext;
                }
            }
            if (ctx.getChild(i) instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) ctx.getChild(i);
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode.getText();
                }
            }
        }

        TypeArgumentsTranslate typeArgumentsTranslate = new TypeArgumentsTranslate();
        String typeArguments = typeArgumentsCtx == null ? "" : typeArgumentsTranslate.translateTypeArguments(typeArgumentsCtx);

        ArgumentListTranslate argumentListTranslate = new ArgumentListTranslate();
        String argumentList = argumentListCtx == null ? "" : argumentListTranslate.translateArgumentList(argumentListCtx);

        return "." + typeArguments + " " + identifier + "(" + argumentList + ")";
    }

    private String parseMethodInvocation(ParserRuleContext ctx) {
        // 格式类型检查：暂不支持的不做翻译
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child.getText().contains("super")) {
                System.out.println("不支持的方法调用格式: super " + ctx.getText());
                return null;
            }
            // 暂不支持：expressionName '.' typeArguments? Identifier '(' argumentList? ')'
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childNode = (RuleContext) child;
            if (childNode.getRuleIndex() == Java8Parser.RULE_expressionName) {
                ExpressionNameTranslate translate = new ExpressionNameTranslate();
                return translate.translateExpressionName((ParserRuleContext) childNode);
            }
        }

        // 转换两种格式的方法调用：
        // methodName '(' argumentList? ')' 或者
        // typeName '.' typeArguments? Identifier '(' argumentList? ')'
        ParseTree firstChild = ctx.getChild(0);
        boolean isRuleContext = firstChild instanceof RuleContext;
        if (!isRuleContext) {
            System.out.println("不支持的方法调用格式（按说不可能走到这里）:  " + ctx.getText());
            return null;
        }
        RuleContext firstChildRule = (RuleContext) firstChild;
        // todo ctx参数可能有问题
        // 处理typeName '.' typeArguments? Identifier '(' argumentList? ')'
        if (firstChildRule.getRuleIndex() == Java8Parser.RULE_typeName) {
            return translateMethodInvocationTypeName(ctx);
        }
        if (firstChildRule.getRuleIndex() == Java8Parser.RULE_methodName) {
            return translateMethodInvocationMethodName(ctx);
        }
        System.out.println("不支持的方法调用格式（按说也不可能走到这里）:  " + ctx.getText());
        return null;
    }


    /**
     * 格式：methodName '(' argumentList? ')'
     *
     * @param ctx methodInvocation_lfno_primary
     * @return
     */
    public String translateMethodInvocationMethodName(ParserRuleContext ctx) {
        String methodName = translateMethodName((ParserRuleContext) ctx.getChild(0));
        ArgumentListTranslate argumentListTranslate = new ArgumentListTranslate();
        StringBuilder argumentList = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext && ((RuleContext) ctx.getChild(i)).getRuleIndex()
                    == Java8Parser.RULE_argumentList) {
                argumentList.append(argumentListTranslate.translateArgumentList((ParserRuleContext) ctx.getChild(i)));
            }
        }
        return ReplaceRuleService.replaceMethodInvocationMethodName(methodName) + "(" + argumentList + ")";
    }

    /**
     * typeName '.' typeArguments? Identifier '(' argumentList? ')'
     * a.func(1,2)
     *
     * @param ctx methodInvocation_lfno_primary
     * @return
     */
    public String translateMethodInvocationTypeName(ParserRuleContext ctx) {
        String typeName = translateTypeName((ParserRuleContext) ctx.getChild(0));
        String methodNameIdentifier = fetchMethodNameIdentifier(ctx);
        ArgumentListTranslate argumentListTranslate = new ArgumentListTranslate();

        StringBuilder argumentList = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext && ((RuleContext) ctx.getChild(i)).getRuleIndex()
                    == Java8Parser.RULE_argumentList) {
                argumentList.append(argumentListTranslate.translateArgumentList((ParserRuleContext) ctx.getChild(i)));
            }
        }
        String originTranslate = typeName + "->" + methodNameIdentifier + "(" + argumentList + ")";
        return ReplaceRuleService.replaceMethodInvocationTypeName(originTranslate, typeName, methodNameIdentifier, argumentList.toString());
    }

    public String translateMethodName(ParserRuleContext ctx) {
        if (ctx.getRuleIndex() != Java8Parser.RULE_methodName) {
            System.out.println("methodname 没找到，不科学:" + ctx.getText());
            return null;
        }
        return ctx.getText();
    }

    /**
     * @param ctx methodInvocation_lfno_primary
     * @return
     */
    private String fetchMethodNameIdentifier(ParserRuleContext ctx) {
        for (int k = 0; k < ctx.getChildCount(); k++) {
            ParseTree child1 = ctx.getChild(k);
            if (child1 instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child1;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    return terminalNode.getText();
                }
            }
        }
        System.out.println("fetchMethodNameIdentifier 失败");
        return "";
    }

    public String translateTypeName(ParserRuleContext ctx) {
        if (ctx.getRuleIndex() != Java8Parser.RULE_typeName) {
            System.out.println("typeNameRule 没找到，不科学:" + ctx.getText());
            return null;
        }
        return ctx.getText();
    }
}
