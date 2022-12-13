package com.test.migration.service.translate.common.parameter;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.common.UnannTypeTranslate;
import com.test.migration.service.translate.common.variable.VariableDeclaratorIdTranslate;
import com.test.migration.service.translate.common.variable.VariableDeclaratorListTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

public class FormalParameterTranslate {

    /**
     * formalParameter
     * :	variableModifier* unannType variableDeclaratorId
     * ;
     */
    public String translateFormalParameter(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_formalParameter) {
            System.out.println("RULE_formalParameter 没找到，不科学");
            return null;
        }

        ParserRuleContext unannTypeCtx = null;
        ParserRuleContext variableDeclaratorIdCtx = null;
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
        }

        UnannTypeTranslate unannTypeTranslate = new UnannTypeTranslate();
        String unannType = unannTypeTranslate.translateUnannType(unannTypeCtx);

        VariableDeclaratorIdTranslate variableDeclaratorIdTranslate = new VariableDeclaratorIdTranslate();
        String variableDeclaratorList = variableDeclaratorIdTranslate.translateVariableDeclaratorId(variableDeclaratorIdCtx);

        return unannType + " " + variableDeclaratorList;
    }
}
