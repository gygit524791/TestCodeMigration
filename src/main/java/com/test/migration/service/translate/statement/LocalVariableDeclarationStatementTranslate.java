package com.test.migration.service.translate.statement;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.common.UnannTypeTranslate;
import com.test.migration.service.translate.common.variable.VariableDeclaratorListTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

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
     * :	localVariableDeclaration ';'
     * ;
     * <p>
     * localVariableDeclaration
     * :	variableModifier* unannType variableDeclaratorList
     * ;
     *
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
        ParserRuleContext variableDeclaratorListCtx = null;
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
                variableDeclaratorListCtx = (ParserRuleContext) childRuleContext;
            }
        }

        UnannTypeTranslate unannTypeTranslate = new UnannTypeTranslate();
        String unannType = unannTypeTranslate.translateUnannType(unannTypeCtx);
        VariableDeclaratorListTranslate variableDeclaratorListTranslate = new VariableDeclaratorListTranslate();
        String variableDeclaratorList = variableDeclaratorListTranslate.translateVariableDeclaratorList(variableDeclaratorListCtx);
        return unannType + variableDeclaratorList;
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
