package com.test.migration.service.translate.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.MappingRuleLoader;
import com.test.migration.service.translate.expression.ExpressionTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 目前支持下面类型的解析
 * int a = 1;  //直接赋值类型 directAssignment
 * int a = func(); //私有方法调用/继承类方法调用（这个暂不考虑），需要迁移调用到私有方法，如果私有方法迁移失败，则整体迁移失败
 * int a = c.func(1);//对象方法调用
 * int a = Test.func();//类静态方法调用
 * int a = this.a;//this访问
 * int a = new Test();// 创建类对象
 * int a = b * c;// 赋值语句本身是个表达式
 * <p>
 * ---暂不支持：
 * int a = Test::abc;
 * <p>
 * <p>
 * 当前限定的variableInitializer翻译：
 * 1.不支持arrayInitializer
 * 2.expression:
 * 不支持不含primaryNoNewArray_lfno_primary子节点的情况
 * 不支持arrayCreationExpression
 * 方法调用：
 * 不支持methodInvocation_lf_primary
 */
public class LocalVariableDeclarationStatementTranslate {
    /**
     * localVariableDeclarationStatement
     * 	:	localVariableDeclaration ';'
     * 	;
     *
     * 	localVariableDeclaration
     * 	:	variableModifier* unannType variableDeclaratorList
     * 	;
     * @param ctx
     * @return
     */
    public String translateLocalVariableDeclarationStatement(ParserRuleContext ctx) {
        if (ctx.getRuleIndex() != Java8Parser.RULE_localVariableDeclarationStatement) {
            System.out.println("localVariableDeclarationStatementContext 为null");
            return "";
        }
        ParserRuleContext localVariableDeclaration = (ParserRuleContext) ctx.getChild(0);

        // find unanntype variableDeclaratorList
        ParserRuleContext unannTypeCtx = null;
        ParserRuleContext VariableDeclaratorListCtx = null;
        for (int i = 0; i < localVariableDeclaration.getChildCount(); i++) {
            boolean isRuleContext = localVariableDeclaration.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) localVariableDeclaration.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_unannType) {
                unannTypeCtx = (ParserRuleContext) childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorList) {
                VariableDeclaratorListCtx = (ParserRuleContext) childRuleContext;
            }
        }
        return translateUnannType(unannTypeCtx) + translateVariableDeclaratorList(VariableDeclaratorListCtx);
    }

    /**
     *
     */
    public String translateUnannType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannType) {
            System.out.println("unannTypeContext 为null");
            return "";
        }
        //要么是unannPrimitiveType，要么是unannReferenceType
        ParseTree child = ctx.getChild(0);
        RuleContext childNode = (RuleContext) child;
        //unannPrimitiveType直接返回自身（基础类型java和c++都一样）
        if (childNode.getRuleIndex() == Java8Parser.RULE_unannPrimitiveType) {
            return childNode.getText();
        }

        // 引用类型
        if (childNode.getRuleIndex() == Java8Parser.RULE_unannReferenceType) {
            // 只可能有一个孩子节点，类型是：类/接口 数组  unannTypeVariable之一
            ParseTree referenceTypeChild = childNode.getChild(0);
            RuleContext referenceTypeRuleContext = (RuleContext) referenceTypeChild;
            // 类/接口类型
            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannClassOrInterfaceType) {
                return translateUnannClassOrInterfaceType((ParserRuleContext) referenceTypeRuleContext);
            }

            // todo 数组类型 暂不支持
            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannArrayType) {
                System.out.println("暂不支持RULE_unannArrayType, 目前直接返回文本");
                return referenceTypeRuleContext.getText();
            }

            // TODO: 待调研
            if (referenceTypeRuleContext.getRuleIndex() == Java8Parser.RULE_unannTypeVariable) {
                System.out.println("暂不支持RULE_unannTypeVariable");
                return "";
            }
        }
        return "";
    }

    public String translateUnannClassOrInterfaceType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannClassOrInterfaceType) {
            System.out.println("translateUnannClassOrInterfaceType 为null");
            return "";
        }

        // TODO 类/接口类型，从映射表中找对应关系
        Map<String, String> referenceMapping = queryReferenceMapping();
        return "";
    }


    /**
     * variableDeclaratorList
     * :	variableDeclarator (',' variableDeclarator)*
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateVariableDeclaratorList(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableDeclaratorList) {
            System.out.println("RULE_variableDeclaratorList 为null");
            return "";
        }
        // TODO 先考虑单个变量声明，多变量声明（int a=1,b=2）后续解决
        ParserRuleContext child = (ParserRuleContext) ctx.getChild(0);
        return translateVariableDeclarator(child);
    }

    /**
     * variableDeclarator
     * :	variableDeclaratorId ('=' variableInitializer)?
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateVariableDeclarator(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableDeclarator) {
            System.out.println("variableDeclaratorContext 为null");
            return "";
        }

        String variableDeclaratorId = "";
        String variableInitializer = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext childRuleContext = (RuleContext) child;
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorId) {
                variableDeclaratorId = translateVariableDeclaratorId((ParserRuleContext) childRuleContext);
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableInitializer) {
                variableInitializer = translateVariableInitializer((ParserRuleContext) childRuleContext);
            }
        }

        // TODO： 暂未解决多个'='问题（int a =2, b=3;）
        return StringUtils.isBlank(variableInitializer) ? variableDeclaratorId
                : variableDeclaratorId + "=" + variableInitializer;
    }

    /**
     * 变量标识符，直接返回text
     * 比如：int a = 3   返回的是a
     */
    public String translateVariableDeclaratorId(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableDeclaratorId) {
            System.out.println("variableDeclaratorIdContext 为null");
            return "";
        }
        return ctx.getText();
    }

    /**
     * variableInitializer
     * :	expression
     * |	arrayInitializer
     * ;
     *
     * @param ctx
     * @return
     */
    public String translateVariableInitializer(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_variableInitializer) {
            System.out.println("VariableInitializer 为null");
            return "";
        }
        ParserRuleContext childRule = (ParserRuleContext) ctx.getChild(0);

        if (childRule.getRuleIndex() == Java8Parser.RULE_arrayInitializer) {
            System.out.println("暂不支持RULE_arrayInitializer");
            return "";
        }
        ExpressionTranslate expressionTranslate = new ExpressionTranslate();
        if (childRule.getRuleIndex() == Java8Parser.RULE_expression) {
            return expressionTranslate.translateExpression(childRule);
        }
        System.out.println("translateVariableInitializer 失败");
        return "";
    }

    public Map<String, String> queryReferenceMapping() {
        return MappingRuleLoader.basicMapping;
    }

    /**
     * 数值和boolean类型，直接转换
     */
    public String unannPrimitiveType(String originType) {
        return originType;
    }

    public String unannReferenceType(String originType) {
        return null;
    }

    //variableDeclaratorList
    public String variableDeclaratorList() {
        return null;
    }


}
