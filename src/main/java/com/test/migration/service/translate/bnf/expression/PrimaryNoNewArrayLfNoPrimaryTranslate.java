package com.test.migration.service.translate.bnf.expression;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.ArrayAccessLfnoPrimaryTranslate;
import com.test.migration.service.translate.bnf.common.FieldAccessLfnoPrimaryTranslate;
import com.test.migration.service.translate.bnf.common.cls.ClassInstanceCreationExpressionTranslate;
import com.test.migration.service.translate.bnf.common.method.MethodInvocationTranslate;
import com.test.migration.service.translate.bnf.statement.BlockTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class PrimaryNoNewArrayLfNoPrimaryTranslate {
    /**
     * primaryNoNewArray_lfno_primary
     * :	literal
     * |	typeName ('[' ']')* '.' 'class'
     * |	unannPrimitiveType ('[' ']')* '.' 'class'
     * |	'void' '.' 'class'
     * |	'this'
     * |	typeName '.' 'this'
     * |	'(' expression ')'
     * |	classInstanceCreationExpression_lfno_primary
     * |	fieldAccess_lfno_primary
     * |	arrayAccess_lfno_primary
     * |	methodInvocation_lfno_primary
     * |	methodReference_lfno_primary
     * ;
     *
     * @param ctx
     * @return
     */
    public String translatePrimaryNoNewArray_lfno_primary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_primaryNoNewArray_lfno_primary) {
            System.out.println("RULE_primaryNoNewArray_lfno_primary 没找到，不科学");
            return null;
        }
        if (ctx.getChildCount() != 1) {
            System.out.println(ctx.getText());
            System.out.println("RULE_primaryNoNewArray_lfno_primary暂不支持多子节点的解析类型");
            return ctx.getText();
        }

        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);


        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_literal) {
            return translateLiteral(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_classInstanceCreationExpression_lfno_primary) {
            // TODO 这里有坑
//            return translateVariableInitializerWithClassInstanceCreation(childRuleContext);
            ClassInstanceCreationExpressionTranslate classInstanceCreationExpressionTranslate = new ClassInstanceCreationExpressionTranslate();
            return classInstanceCreationExpressionTranslate.translateClassInstanceCreationExpression_lfno_primary(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_fieldAccess_lfno_primary) {
            FieldAccessLfnoPrimaryTranslate translate = new FieldAccessLfnoPrimaryTranslate();
            return translate.translateFieldAccessLfnoPrimary(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_arrayAccess_lfno_primary) {
            ArrayAccessLfnoPrimaryTranslate translate = new ArrayAccessLfnoPrimaryTranslate();
            return translate.translateArrayAccessLfnoPrimary(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_methodInvocation_lfno_primary) {
            MethodInvocationTranslate methodInvocationTranslate = new MethodInvocationTranslate();
            return methodInvocationTranslate.translateMethodInvocationLfNoPrimary(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_methodReference_lfno_primary) {
            System.out.println("RULE_methodReference_lfno_primary 暂不支持");
            return ctx.getText();
        }

        System.out.println("translateVariableInitializer 不支持的解析类型");
        return "";
    }

    public String translateLiteral(ParserRuleContext ctx) {
        return ctx.getText();
    }

    /**
     * classInstanceCreationExpression_lfno_primary
     * :	'new' typeArguments? annotation* Identifier ('.' annotation* Identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * |	expressionName '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * ;
     * <p>
     * classBody
     * :	'{' classBodyDeclaration* '}'
     * ;
     * <p>
     * classBodyDeclaration
     * :	classMemberDeclaration
     * |	instanceInitializer
     * |	staticInitializer
     * |	constructorDeclaration
     * ;
     * <p>
     * classMemberDeclaration
     * :	fieldDeclaration
     * |	methodDeclaration
     * |	classDeclaration
     * |	interfaceDeclaration
     * |	';'
     * ;
     *  todo 简化处理，目标是迁移测试方法，如果方法中存在内部类定义，直接取类中的方法体
     *
     * @param ctx
     * @return
     */
    public String translateVariableInitializerWithClassInstanceCreation(ParserRuleContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext
                    && ((ParserRuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_classBody) {
                ParserRuleContext classBody = (ParserRuleContext) ctx.getChild(i);
                for (int j = 0; j < classBody.getChildCount(); j++) {
                    if (classBody.getChild(j) instanceof RuleContext
                            && ((ParserRuleContext) classBody.getChild(j)).getRuleIndex() == Java8Parser.RULE_classBodyDeclaration) {
                        ParserRuleContext classBodyDeclaration = (ParserRuleContext) classBody.getChild(j);
                        for (int k = 0; k < classBodyDeclaration.getChildCount(); k++) {
                            if (classBodyDeclaration.getChild(k) instanceof RuleContext
                                    && ((ParserRuleContext) classBodyDeclaration.getChild(k)).getRuleIndex() == Java8Parser.RULE_classMemberDeclaration) {
                                ParserRuleContext classMemberDeclaration = (ParserRuleContext) classBodyDeclaration.getChild(k);
                                for (int l = 0; l < classMemberDeclaration.getChildCount(); l++) {
                                    if (classMemberDeclaration.getChild(l) instanceof RuleContext
                                            && ((ParserRuleContext) classMemberDeclaration.getChild(l)).getRuleIndex() == Java8Parser.RULE_methodDeclaration) {
                                        ParserRuleContext methodDeclaration = (ParserRuleContext) classMemberDeclaration.getChild(l);
                                        for (int m = 0; m < methodDeclaration.getChildCount(); m++) {
                                            if (methodDeclaration.getChild(m) instanceof RuleContext
                                                    && ((ParserRuleContext) methodDeclaration.getChild(m)).getRuleIndex() == Java8Parser.RULE_methodBody) {
                                                ParserRuleContext methodBody = (ParserRuleContext) methodDeclaration.getChild(m);
                                                BlockTranslate blockTranslate = new BlockTranslate();
                                                return blockTranslate.translateBlock((ParserRuleContext) methodBody.getChild(0));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
