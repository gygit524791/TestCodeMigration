package com.test.migration.service.translate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.migration.antlr.java.Java8BaseVisitor;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TestCodeVisitor extends Java8BaseVisitor<RuleNode> {
    private String className;
    private List<ParserRuleContext> classBodyDeclarationCtxList = Lists.newArrayList();
    private List<ParserRuleContext> fieldDeclarationCtxList = Lists.newArrayList();
    private List<ParserRuleContext> methodDeclarationCtxList = Lists.newArrayList();
    private List<ParserRuleContext> classDeclarationCtxList = Lists.newArrayList();

    /**
     * typeName对应的类型mapping表，举个例子：
     * <p>
     * typeName.methodIdentifier:
     * mActivityRule.runOnUiThread(()->{
     * <p>
     * });
     * typeName是mActivityRule，对应的类型是ActivityTestRule<AnimatorSetActivity>，mapping规则中保存的是ActivityTestRule<AnimatorSetActivity>
     * 因此需要一个从mActivityRule到ActivityTestRule<AnimatorSetActivity>的映射关系
     * methodIdentifier是runOnUiThread
     * <p>
     * <p>
     * TODO
     */
    private Map<String, String> typeNameMap = Maps.newHashMap();

    @Override
    public RuleNode visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        if (StringUtils.isBlank(className)) {
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                if (child instanceof TerminalNode terminalNode) {
                    if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                        className = terminalNode.getText();
                    }
                }
            }
        }
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitClassBody(Java8Parser.ClassBodyContext ctx) {
        if (classBodyDeclarationCtxList.isEmpty()) {
            for (int i = 0; i < ctx.getChildCount(); i++) {
                boolean isRuleContext = ctx.getChild(i) instanceof RuleContext;
                if (!isRuleContext) {
                    continue;
                }
                RuleContext childRuleContext = (RuleContext) ctx.getChild(i);
                if (childRuleContext.getRuleIndex() == Java8Parser.RULE_classBodyDeclaration) {
                    classBodyDeclarationCtxList.add((ParserRuleContext) childRuleContext);
                }
            }
        }
        return visitChildren(ctx);
    }

    public void loadDeclaration() {
        if (classBodyDeclarationCtxList.isEmpty()) {
            return;
        }

        for (ParserRuleContext parserRuleContext : classBodyDeclarationCtxList) {
            ParseTree child = parserRuleContext.getChild(0);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext node = (RuleContext) child;
            if (node.getRuleIndex() != Java8Parser.RULE_classMemberDeclaration) {
                continue;
            }

            ParseTree declarationChild = node.getChild(0);
            boolean isSubRuleContext = declarationChild instanceof RuleContext;
            if (!isSubRuleContext) {
                continue;
            }

            RuleContext subNode = (RuleContext) declarationChild;
            if (subNode.getRuleIndex() == Java8Parser.RULE_fieldDeclaration) {
                fieldDeclarationCtxList.add((ParserRuleContext) subNode);
            }
            if (subNode.getRuleIndex() == Java8Parser.RULE_methodDeclaration) {
                methodDeclarationCtxList.add((ParserRuleContext) subNode);
            }
            if (subNode.getRuleIndex() == Java8Parser.RULE_classDeclaration) {
                classDeclarationCtxList.add((ParserRuleContext) subNode);
            }
        }
    }


    private static boolean isExistAnnotation(RuleContext node) {
        boolean existAnnotation = false;
        for (int j = 0; j < node.getChildCount(); j++) {
            boolean isChildRuleContext = node.getChild(j) instanceof RuleContext;
            if (!isChildRuleContext) {
                continue;
            }
            RuleContext methodModifierNode = (RuleContext) node.getChild(j);
            if (methodModifierNode.getRuleIndex() == Java8Parser.RULE_annotation) {
                existAnnotation = true;
            }
        }
        return existAnnotation;
    }

}
