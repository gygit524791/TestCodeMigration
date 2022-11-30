package com.test.migration.service.invocation;

import com.test.migration.antlr.Java8Lexer;
import com.test.migration.antlr.Java8Parser;
import com.test.migration.antlr.Java8BaseVisitor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Getter
public class ApiInvocationVisitor extends Java8BaseVisitor<RuleNode> {

    /**
     * tmp
     */
    private MethodInvocation invocation;
    /**
     * 遍历器不应该做过多遍历之外的逻辑（构建/收集结果），后面可以考虑监听器方式实现
     */
    private List<MethodInvocation> invocationList = new ArrayList<>();


    @Override
    public RuleNode visitClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * 方法声明
     */
    @Override
    public RuleNode visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        // 方法内部定义的内部方法，调用关系算到外层方法中
        MethodInvocation parentMethod = invocationList.stream()
                .filter(invoke -> invoke.getStartTokenIndex() < ctx.getStart().getStartIndex())
                .filter(invoke -> invoke.getEndTokenIndex() > ctx.getStop().getStopIndex())
                .findFirst().orElse(null);

        if (parentMethod == null) {
            invocation = new MethodInvocation();
            invocation.setStartTokenIndex(ctx.getStart().getStartIndex());
            invocation.setEndTokenIndex(ctx.getStop().getStopIndex());
            invocationList.add(invocation);

            // 获取调用者方法名
            String methodName = fetchCallerMethodName(ctx);
            invocation.setCaller(methodName);

//        if (invocation.getCallee() != null && invocation.getCaller() != null) {
//            invocationList.add(invocation);
//        }
        }

        // 遍历AST
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        fillCalleeMethodInvocation(ctx);
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        fillCalleeMethodInvocation(ctx);
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        fillCalleeMethodInvocation(ctx);
        return visitChildren(ctx);
    }

    private void fillCalleeMethodInvocation(ParserRuleContext ctx) {
        // class中没有方法声明直接调用方法，暂不考虑该case
        if (invocation == null) {
            return;
        }
        String calleeMethodName = fetchCalleeMethodName(ctx);
        if (invocation.getCallee() == null) {
            invocation.setCallee(new HashSet<>(Collections.singleton(calleeMethodName)));
        } else {
            invocation.getCallee().add(calleeMethodName);
        }
    }

    private String fetchCallerMethodName(Java8Parser.MethodDeclarationContext ctx) {
        // 获取method的Declaration
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext node = (RuleContext) child;
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
                    if (child1 instanceof TerminalNode) {
                        TerminalNode terminalNode = (TerminalNode) child1;
                        if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                            return terminalNode.getText();
                        }
                    }
                }
            }
        }
        return "";
    }

    private String fetchCalleeMethodName(ParserRuleContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode node = (TerminalNode) child;
                if (node.getSymbol().getType() == Java8Lexer.Identifier) {
                    return node.getText();
                }
            }
            if (child instanceof RuleContext) {
                RuleContext node = (RuleContext) child;
                if (node.getRuleIndex() == Java8Parser.RULE_methodName) {
                    return node.getText();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Data
    @ToString
    public static class MethodInvocation {
        //调用方法
        private String caller;
        private Integer startTokenIndex;
        private Integer endTokenIndex;
        //被调方法
        private Set<String> callee;
    }

}


