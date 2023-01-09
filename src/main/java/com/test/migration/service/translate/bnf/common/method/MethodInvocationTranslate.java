package com.test.migration.service.translate.bnf.common.method;

import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import com.test.migration.service.translate.bnf.common.ArgumentListTranslate;
import com.test.migration.service.translate.bnf.common.ExpressionNameTranslate;
import com.test.migration.service.translate.bnf.common.TypeArgumentsTranslate;
import com.test.migration.service.translate.bnf.common.primary.PrimaryTranslate;
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
     */
    public String translateMethodInvocation(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodInvocation) {
            System.out.println("RULE_methodInvocation 为null");
            return null;
        }
        // 1.获取各个类型子节点
        ParseTree identifier = null;
        ParserRuleContext methodNameCtx = null;
        ParserRuleContext argumentListCtx = null;
        ParserRuleContext typeArgumentsCtx = null;
        ParserRuleContext typeNameCtx = null;
        ParserRuleContext expressionNameCtx = null;
        ParserRuleContext primaryCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_methodName) {
                    methodNameCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_argumentList) {
                    argumentListCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_typeArguments) {
                    typeArgumentsCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_typeName) {
                    typeNameCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_expressionName) {
                    expressionNameCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_primary) {
                    primaryCtx = (ParserRuleContext) child;
                }
            }
            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode;
                }
            }
        }
        ArgumentListTranslate argumentListTranslate = new ArgumentListTranslate();
        TypeArgumentsTranslate typeArgumentsTranslate = new TypeArgumentsTranslate();
        ExpressionNameTranslate expressionNameTranslate = new ExpressionNameTranslate();
        PrimaryTranslate primaryTranslate = new PrimaryTranslate();

        String identifierStr = identifier == null ? "" : identifier.getText();
        String methodName = methodNameCtx == null ? "" : translateMethodInvocationMethodName(methodNameCtx);
        String argumentList = argumentListCtx == null ? "" : argumentListTranslate.translateArgumentList(argumentListCtx);
        String typeArguments = typeArgumentsCtx == null ? "" : typeArgumentsTranslate.translateTypeArguments(typeArgumentsCtx);
        String typeName = typeNameCtx == null ? "" : translateTypeName(typeNameCtx);
        String expressionName = expressionNameCtx == null ? "" : expressionNameTranslate.translateExpressionName(expressionNameCtx);
        String primary = primaryCtx == null ? "" : primaryTranslate.translatePrimary(primaryCtx);

        // 2. 判断第一个孩子节点的类型
        ParseTree firstChild = ctx.getChild(0);
        boolean isRuleContext = firstChild instanceof RuleContext;
        if (!isRuleContext) {
            //'super' '.' typeArguments? Identifier '(' argumentList? ')'
            return "super" + "->" + typeArguments + " " + identifierStr + "(" + argumentList + ")";
        } else {
            int ruleIndex = ((RuleContext) firstChild).getRuleIndex();

            if (ruleIndex == Java8Parser.RULE_methodName) {
                //methodName '(' argumentList? ')'
                return methodName + "(" + argumentList + ")";
            }

            if (ruleIndex == Java8Parser.RULE_expressionName) {
                //expressionName '.' typeArguments? Identifier '(' argumentList? ')'
                return expressionName + "->" + typeArguments + " " + identifierStr + "(" + argumentList + ")";
            }

            if (ruleIndex == Java8Parser.RULE_primary) {
                //primary '.' typeArguments? Identifier '(' argumentList? ')'
                return primary + "->" + typeArguments + " " + identifierStr + "(" + argumentList + ")";
            }

            if (ruleIndex == Java8Parser.RULE_typeName) {
                // 判断ctx中是否包含super
                boolean haveSuper = false;
                for (int i = 0; i < ctx.getChildCount(); i++) {
                    if (StringUtils.equals("super", ctx.getChild(i).getText())) {
                        haveSuper = true;
                    }
                }

                //typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
                if (haveSuper) {
                    return typeName + "->" + "super" + "->" + typeArguments + " " + identifierStr + "(" + argumentList + ")";
                }

                //typeName '.' typeArguments? Identifier '(' argumentList? ')'
                else {
                    return translateMethodInvocationTypeName(ctx);
                }
            }
        }

        System.out.println("translateMethodInvocation error");
        return null;
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
        // 1.获取各个类型子节点
        ParseTree identifier = null;
        ParserRuleContext methodNameCtx = null;
        ParserRuleContext argumentListCtx = null;
        ParserRuleContext typeArgumentsCtx = null;
        ParserRuleContext typeNameCtx = null;
        ParserRuleContext expressionNameCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_methodName) {
                    methodNameCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_argumentList) {
                    argumentListCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_typeArguments) {
                    typeArgumentsCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_typeName) {
                    typeNameCtx = (ParserRuleContext) child;
                }
                if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_expressionName) {
                    expressionNameCtx = (ParserRuleContext) child;
                }
            }
            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode;
                }
            }
        }
        ArgumentListTranslate argumentListTranslate = new ArgumentListTranslate();
        TypeArgumentsTranslate typeArgumentsTranslate = new TypeArgumentsTranslate();
        ExpressionNameTranslate expressionNameTranslate = new ExpressionNameTranslate();

        String identifierStr = identifier == null ? "" : identifier.getText();
        String methodName = methodNameCtx == null ? "" : translateMethodInvocationMethodName(methodNameCtx);
        String argumentList = argumentListCtx == null ? "" : argumentListTranslate.translateArgumentList(argumentListCtx);
        String typeArguments = typeArgumentsCtx == null ? "" : typeArgumentsTranslate.translateTypeArguments(typeArgumentsCtx);
        String typeName = typeNameCtx == null ? "" : translateTypeName(typeNameCtx);
        String expressionName = expressionNameCtx == null ? "" : expressionNameTranslate.translateExpressionName(expressionNameCtx);

        // 2. 判断第一个孩子节点的类型
        ParseTree firstChild = ctx.getChild(0);
        boolean isRuleContext = firstChild instanceof RuleContext;
        if (!isRuleContext) {
            //'super' '.' typeArguments? Identifier '(' argumentList? ')'
            return "super" + "->" + typeArguments + " " + identifierStr + "(" + argumentList + ")";
        } else {
            int ruleIndex = ((RuleContext) firstChild).getRuleIndex();

            if (ruleIndex == Java8Parser.RULE_methodName) {
                //methodName '(' argumentList? ')'
                return methodName + "(" + argumentList + ")";
            }

            if (ruleIndex == Java8Parser.RULE_expressionName) {
                //expressionName '.' typeArguments? Identifier '(' argumentList? ')'
                return expressionName + "->" + typeArguments + " " + identifierStr + "(" + argumentList + ")";
            }

            if (ruleIndex == Java8Parser.RULE_typeName) {
                // 判断ctx中是否包含super
                boolean haveSuper = false;
                for (int i = 0; i < ctx.getChildCount(); i++) {
                    if (StringUtils.equals("super", ctx.getChild(i).getText())) {
                        haveSuper = true;
                    }
                }

                //typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
                if (haveSuper) {
                    return typeName + "->" + "super" + "->" + typeArguments + " " + identifierStr + "(" + argumentList + ")";
                }

                //typeName '.' typeArguments? Identifier '(' argumentList? ')'
                else {
                    return translateMethodInvocationTypeName(ctx);
                }
            }
        }

        System.out.println("translateMethodInvocation error");
        return null;
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

        return "->" + typeArguments + " " + identifier + "(" + argumentList + ")";
    }

    /**
     */
    public String translateMethodInvocationMethodName(ParserRuleContext ctx) {
        String methodName = translateMethodName(ctx);

        return ReplaceRuleService.replaceMethodInvocationMethodName(methodName);
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
