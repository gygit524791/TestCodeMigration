package com.test.migration.service.translate.bnf.statement.noshortif;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.unann.UnannTypeTranslate;
import com.test.migration.service.translate.bnf.common.variable.VariableDeclaratorIdTranslate;
import com.test.migration.service.translate.bnf.expression.ExpressionTranslate;
import com.test.migration.service.translate.bnf.expression.StatementExpressionTranslate;
import com.test.migration.service.translate.bnf.statement.LocalVariableDeclarationStatementTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

import java.util.List;
import java.util.stream.Collectors;

public class ForStatementNoShortIfTranslate {

    /**
     * forStatementNoShortIf
     * 	:	basicForStatementNoShortIf
     * 	|	enhancedForStatementNoShortIf
     * 	;
     */
    public String translateForStatementNoShortIf(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_forStatementNoShortIf) {
            System.out.println("RULE_forStatementNoShortIf 没找到");
            return null;
        }

        ParserRuleContext child = (ParserRuleContext) ctx.getChild(0);
        if (child.getRuleIndex() == Java8Parser.RULE_basicForStatementNoShortIf) {
            return translateBasicForStatementNoShortIf(child);
        }

        if (child.getRuleIndex() == Java8Parser.RULE_enhancedForStatementNoShortIf) {
            return translateEnhancedForStatementNoShortIf(child);
        }

        System.out.println("translateForStatementNoShortIf error");
        return null;
    }


    /**
     * basicForStatement
     * :	'for' '(' forInit? ';' expression? ';' forUpdate? ')' statementNoShortIf
     * ;
     */
    public String translateBasicForStatementNoShortIf(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_basicForStatement) {
            System.out.println("RULE_basicForStatement 没找到，不科学");
            return null;
        }
        ParserRuleContext forInitCtx = null;
        ParserRuleContext expressionCtx = null;
        ParserRuleContext forUpdateCtx = null;
        ParserRuleContext statementNoShortIfCtx = null;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_forInit) {
                forInitCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_expression) {
                expressionCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_forUpdate) {
                forUpdateCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statementNoShortIf) {
                statementNoShortIfCtx = (ParserRuleContext) childRuleContext;
            }
        }

        String forInit = forInitCtx == null ? "" : translateForInit(forInitCtx);

        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        String expression = expressionCtx == null ? "" : expressionTranslate.translateExpression(expressionCtx);

        String forUpdate = forUpdateCtx == null ? "" : translateForUpdate(forUpdateCtx);

        StatementNoShortIfTranslate statementTranslate = new StatementNoShortIfTranslate();
        String statementNoShortIf = statementTranslate.translateStatementNoShortIf(statementNoShortIfCtx);

        return "for" + "(" + forInit + ";" + expression + ";" + forUpdate + ")" + statementNoShortIf;
    }

    /**
     * enhancedForStatement
     * :	'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statementNoShortIf
     * ;
     */
    public String translateEnhancedForStatementNoShortIf(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_enhancedForStatementNoShortIf) {
            System.out.println("RULE_enhancedForStatementNoShortIf 没找到");
            return null;
        }

        ParserRuleContext unannTypeCtx = null;
        ParserRuleContext variableDeclaratorIdCtx = null;
        ParserRuleContext expressionCtx = null;
        ParserRuleContext statementNoShortIfCtx = null;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_unannType) {
                unannTypeCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorId) {
                variableDeclaratorIdCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_expression) {
                expressionCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statementNoShortIf) {
                statementNoShortIfCtx = (ParserRuleContext) childRuleContext;
            }
        }

        UnannTypeTranslate unannTypeTranslate = new UnannTypeTranslate();
        String unannType = unannTypeTranslate.translateUnannType(unannTypeCtx);

        VariableDeclaratorIdTranslate variableDeclaratorIdTranslate = new VariableDeclaratorIdTranslate();
        String variableDeclaratorId = variableDeclaratorIdTranslate.translateVariableDeclaratorId(variableDeclaratorIdCtx);

        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        String expression = expressionTranslate.translateExpression(expressionCtx);

        StatementNoShortIfTranslate statementTranslate = new StatementNoShortIfTranslate();
        String statementNoShortIf = statementTranslate.translateStatementNoShortIf(statementNoShortIfCtx);

        return "for" + "(" + unannType + variableDeclaratorId + ":" + expression + ")" + statementNoShortIf;
    }


    /**
     * forInit
     * :	statementExpressionList
     * |	localVariableDeclaration
     * ;
     */
    public String translateForInit(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_forInit) {
            System.out.println("RULE_forInit 没找到，不科学");
            return null;
        }
        ParserRuleContext child = (ParserRuleContext) ctx.getChild(0);
        if (child.getRuleIndex() == Java8Parser.RULE_statementExpressionList) {
            return translateStatementExpressionList(child);
        }

        if (child.getRuleIndex() == Java8Parser.RULE_localVariableDeclaration) {
            LocalVariableDeclarationStatementTranslate translate = new LocalVariableDeclarationStatementTranslate();
            return translate.translateLocalVariableDeclaration(child);
        }
        System.out.println("RULE_forInit error");
        return null;
    }


    /**
     * forUpdate
     * :	statementExpressionList
     * ;
     */
    public String translateForUpdate(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_forUpdate) {
            System.out.println("RULE_forUpdate 没找到，不科学");
            return null;
        }
        ParserRuleContext child = (ParserRuleContext) ctx.getChild(0);
        if (child.getRuleIndex() == Java8Parser.RULE_statementExpressionList) {
            return translateStatementExpressionList(child);
        }
        System.out.println("RULE_forInit error");
        return null;
    }


    /**
     * statementExpressionList
     * :	statementExpression (',' statementExpression)*
     * ;
     */
    public String translateStatementExpressionList(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_statementExpressionList) {
            System.out.println("RULE_statementExpressionList 没找到，不科学");
            return null;
        }

        List<ParserRuleContext> statementExpressionCtxList = Lists.newArrayList();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_statementExpression) {
                statementExpressionCtxList.add((ParserRuleContext) childRuleContext);
            }
        }
        StatementExpressionTranslate statementExpressionTranslate = new StatementExpressionTranslate();
        List<String> statementExpressionList = statementExpressionCtxList.stream()
                .map(statementExpressionTranslate::translateStatementExpression)
                .collect(Collectors.toList());
        return Joiner.on(",").join(statementExpressionList);
    }


}
