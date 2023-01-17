package com.test.migration.service.translate.bnf.common.primary;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.LiteralTranslate;
import com.test.migration.service.translate.bnf.common.array.ArrayAccessLfnoPrimaryTranslate;
import com.test.migration.service.translate.bnf.common.cls.ClassInstanceCreationExpressionLfnoPrimaryTranslate;
import com.test.migration.service.translate.bnf.common.method.MethodInvocationLfnoPrimaryTranslate;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.Log;

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
     */
    public String translatePrimaryNoNewArray_lfno_primary(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_primaryNoNewArray_lfno_primary) {
            Log.error("RULE_primaryNoNewArray_lfno_primary error");
            return null;
        }
        if (ctx.getChildCount() != 1) {
            return ctx.getText();
        }

        ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(0);

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_literal) {
            LiteralTranslate translate = new LiteralTranslate();
            return translate.translateLiteral(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_classInstanceCreationExpression_lfno_primary) {
            ClassInstanceCreationExpressionLfnoPrimaryTranslate translate = new ClassInstanceCreationExpressionLfnoPrimaryTranslate();
            return translate.translateClassInstanceCreationExpression_lfno_primary(childRuleContext);
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
            MethodInvocationLfnoPrimaryTranslate translate = new MethodInvocationLfnoPrimaryTranslate();
            return translate.translateMethodInvocationLfNoPrimary(childRuleContext);
        }

        if (childRuleContext.getRuleIndex() == Java8Parser.RULE_methodReference_lfno_primary) {
            MethodReferenceLfnoPrimaryTranslate translate = new MethodReferenceLfnoPrimaryTranslate();
            return translate.translateMethodReferenceLfnoPrimary(childRuleContext);
        }

        Log.error("translateVariableInitializer error");
        return null;
    }
}
