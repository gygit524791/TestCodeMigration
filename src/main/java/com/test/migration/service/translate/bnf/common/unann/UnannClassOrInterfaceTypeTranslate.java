package com.test.migration.service.translate.bnf.common.unann;

import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.ReplaceRuleService;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnannClassOrInterfaceTypeTranslate {

    /**
     * unannClassOrInterfaceType
     * 	:	(	unannClassType_lfno_unannClassOrInterfaceType
     * 		|	unannInterfaceType_lfno_unannClassOrInterfaceType
     * 		)
     * 		(	unannClassType_lf_unannClassOrInterfaceType
     * 		|	unannInterfaceType_lf_unannClassOrInterfaceType
     * 		)*
     * 	;
     */
    public String translateUnannClassOrInterfaceType(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_unannClassOrInterfaceType) {
            System.out.println("translateUnannClassOrInterfaceType 为null");
            return "";
        }

        // TODO 类/接口类型，从映射表中找对应关系
        // TODO TIPS 不考虑存在注解和范型的情况
        return ReplaceRuleService.replaceClassOrInterfaceType(ctx.getText());
    }
}
