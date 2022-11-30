package com.test.migration.service.extract;



import com.test.migration.antlr.Java8BaseVisitor;
import com.test.migration.antlr.Java8Lexer;
import com.test.migration.antlr.Java8Parser;
import com.test.migration.po.ApiBasic;
import lombok.Getter;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class APIExtractorService extends Java8BaseVisitor<RuleNode> {

    private String filePath;
    private Integer taskId;
    private List<ApiBasic> apiBasics = new ArrayList<>();

    public APIExtractorService(Integer taskId, String filePath) {
        this.taskId = taskId;
        this.filePath = filePath;
    }

    /**
     * 方法声明
     */
    @Override
    public RuleNode visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        String methodName = fetchPublicMethodName(ctx);
        fillApiBasic(methodName);
        return visitChildren(ctx);
    }

    private String fetchPublicMethodName(Java8Parser.MethodDeclarationContext ctx) {
        // 获取method的Declaration
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext node = (RuleContext) child;

            // 过滤掉非public的方法
            if (node.getRuleIndex() == Java8Parser.RULE_methodModifier) {
                // 存在注解的方法
                boolean existAnnotation = isExistAnnotation(node);
                if (existAnnotation) {
                    continue;
                }

                if (!StringUtils.equalsAnyIgnoreCase(node.getText(), "public")) {
                    return StringUtils.EMPTY;
                }
            }

            if (node.getRuleIndex() != Java8Parser.RULE_methodHeader) {
                continue;
            }

            for (int j = 0; j < node.getChildCount(); j++) {
                // 获取method的Header
                ParseTree methodHeaderChild = node.getChild(j);
                boolean isMethodHeaderChildRuleContext = methodHeaderChild instanceof RuleContext;
                if (!isMethodHeaderChildRuleContext) {
                    continue;
                }
                RuleContext methodHeaderChildNode = (RuleContext) methodHeaderChild;
                if (methodHeaderChildNode.getRuleIndex() != Java8Parser.RULE_methodDeclarator) {
                    continue;
                }

                // 获取method的Identifier
                for (int k = 0; k < methodHeaderChildNode.getChildCount(); k++) {
                    ParseTree child1 = methodHeaderChildNode.getChild(k);
                    if (child1 instanceof TerminalNode terminalNode) {
                        if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                            return terminalNode.getText();
                        }
                    }
                }
            }
        }
        return StringUtils.EMPTY;
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

    private void fillApiBasic(String apiName) {
        if (StringUtils.isBlank(apiName)) {
            return;
        }

        ApiBasic apiBasic = ApiBasic.builder()
                .taskId(taskId)
                .filepath(filePath)
                .className(fetchClassNameWithFilePath(filePath))
                .apiName(apiName)
                .type(0)
                .build();
        apiBasics.add(apiBasic);
    }

    private String fetchClassNameWithFilePath(String filePath) {
        String[] split = filePath.split("/");
        String classFileName = split[split.length - 1];
        return classFileName.split("\\.")[0];
    }
}
