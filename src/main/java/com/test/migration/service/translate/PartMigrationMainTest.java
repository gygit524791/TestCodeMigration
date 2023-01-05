package com.test.migration.service.translate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.bnf.common.method.MethodHeaderTranslate;
import com.test.migration.service.translate.bnf.statement.BlockStatementTranslate;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class PartMigrationMainTest {

    /**
     * 1. 构建bs model(BlockStatementTreeNode)
     * 2. 执行bs的translate，收集hint
     * 3. 执行部分迁移bs调整策略，构建调整策略map
     * 4. 执行bs的translate，依据上述map，构建出部分迁移最终结果。
     */

    /**
     * bs(blockStatement)调整策略map
     * key：bs的代码行号 + $ + bs经过translate之后的字符串（唯一确定是哪个bs）
     * value：调整策略（keep，remove）
     */
    public static Map<String, String> blockStatementModifyMap = Maps.newHashMap();

    public static class MethodDeclarationNode {

        public String methodHeader;

        public List<BlockStatementTreeNode> blockStatementTreeNodes;
    }

    /**
     * 完整的代码语句：method/if/for等
     */
    public static class BlockStatementTreeNode {

        /**
         * 完整的代码语句，比如一个完整的method，for，if结构等
         */
        ParserRuleContext blockStatement;

        String translateBlockStatement;

        String bsStartLine;

        /**
         * 直接子blockStatement
         */
        List<BlockStatementTreeNode> subBlockStatementTreeNodes;

        /**
         * 整体hint
         */
        String hint;

        /**
         * nonBlockStatement转换失败时，对应的失败信息收集到nonBlockStatementHint
         */
        String nonBlockStatementHint;


        public boolean isNonBlockStatementTranslateFail() {
            return StringUtils.isNotBlank(nonBlockStatementHint);
        }

    }

    /**
     *
     */
    public BlockStatementTreeNode buildMethodBlockStatementTree(ParserRuleContext parserRuleContext) {
        if (parserRuleContext.getRuleIndex() != Java8Parser.RULE_blockStatement) {
            return null;
        }

        BlockStatementTreeNode treeNode = new BlockStatementTreeNode();
        treeNode.blockStatement = parserRuleContext;

        // 构建直接子blockStatement

        // DFS遍历 遇到bs即加入到subBlockStatementTreeNodes，然后返回上一个节点跳过该节点继续遍历
        List<ParserRuleContext> subBlockStatementTreeNodes = Lists.newArrayList();
        Stack<ParserRuleContext> stack = new Stack<>();
        for (int i = parserRuleContext.getChildCount() - 1; i >= 0; i--) {
            if (parserRuleContext.getChild(i) instanceof RuleContext) {
                stack.push((ParserRuleContext) parserRuleContext.getChild(i));
            }
        }

        while (!stack.isEmpty()) {
            ParserRuleContext ruleContext = stack.pop();
            if (ruleContext.getRuleIndex() == Java8Parser.RULE_blockStatement) {
                subBlockStatementTreeNodes.add(ruleContext);
                continue;
            }

            for (int i = ruleContext.getChildCount() - 1; i >= 0; i--) {
                if (ruleContext.getChild(i) instanceof RuleContext) {
                    stack.push((ParserRuleContext) ruleContext.getChild(i));
                }
            }
        }

        //递归构建直接子bs
        treeNode.subBlockStatementTreeNodes = subBlockStatementTreeNodes.stream()
                .map(this::buildMethodBlockStatementTree)
                .collect(Collectors.toList());

        return treeNode;
    }


    public void testSubBlockStatementTag() {
        MappingRuleLoader.load();
        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/example/android/test/ValueAnimatorTests.java";
//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/translate/source/BDemo.java";
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromFileName(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
        ParseTree parseTree = parser.compilationUnit();

        TestCodeContext.init();
        TestCodeVisitor testCodeVisitor = new TestCodeVisitor();
        testCodeVisitor.visit(parseTree);

        testCodeVisitor.getTypeNameMap().put("mActivityRule", "ActivityTestRule<AnimatorSetActivity>");
        ReplaceRuleService.typeNameMap = testCodeVisitor.getTypeNameMap();

        TranslateCodeCollector.init();
        TranslateCodeCollector.className = TestCodeContext.className;
        // 处理每个method
        for (ParserRuleContext ctx : TestCodeContext.methodDeclarationCtxList) {

            // 1. 构建部分迁移model
            MethodDeclarationNode methodDeclarationNode = buildBSModel(ctx);

            // 2. 填充hint
            List<BlockStatementTreeNode> blockStatementTreeNodes = methodDeclarationNode.blockStatementTreeNodes;
            fillBSModel(blockStatementTreeNodes);

            // 3. 执行部分迁移bs调整策略，构建调整策略map
            for (BlockStatementTreeNode blockStatementTreeNode : blockStatementTreeNodes) {
                executePartMigrationModifyStrategy(blockStatementTreeNode);
            }

            // 4.

        }
    }

    private void executePartMigrationModifyStrategy(BlockStatementTreeNode blockStatementTreeNode) {
        String key = blockStatementTreeNode.bsStartLine + "$" + blockStatementTreeNode.translateBlockStatement;

        // 初始化默认值
        blockStatementModifyMap.put(key, "keep");

        // 策略1 如果存在nonBlockStatementHint，直接remove
        if (StringUtils.isNotBlank(blockStatementTreeNode.nonBlockStatementHint)) {
            blockStatementModifyMap.put(key, "remove");
            return;
        }

        // 策略2 本语句，如果是assert语句，直接remove
        if (blockStatementTreeNode.translateBlockStatement.startsWith("assert")) {
            blockStatementModifyMap.put(key, "remove");
            return;
        }

        // 策略3 直接子语句bs，如果顺序在最后一个的bs存在hint，直接remove
        List<BlockStatementTreeNode> subBlockStatementTreeNodes = blockStatementTreeNode.subBlockStatementTreeNodes;
        if (subBlockStatementTreeNodes.size() > 0) {
            BlockStatementTreeNode lastSubBlockStatementTreeNode = subBlockStatementTreeNodes.get(subBlockStatementTreeNodes.size() - 1);
            String subKey = lastSubBlockStatementTreeNode.bsStartLine + "$" + lastSubBlockStatementTreeNode.translateBlockStatement;
            if (StringUtils.isNotBlank(lastSubBlockStatementTreeNode.nonBlockStatementHint)) {
                blockStatementModifyMap.put(subKey, "remove");
            }
        }

        // 递归处理子bs
        for (BlockStatementTreeNode statementTreeNode : subBlockStatementTreeNodes) {
            executePartMigrationModifyStrategy(statementTreeNode);
        }
    }

    private void fillBSModel(List<BlockStatementTreeNode> blockStatementTreeNodes) {
        if (blockStatementTreeNodes.isEmpty()) {
            return;
        }

        BlockStatementTranslate blockStatementTranslate = new BlockStatementTranslate();
        for (BlockStatementTreeNode blockStatementTreeNode : blockStatementTreeNodes) {
            ParserRuleContext blockStatement = blockStatementTreeNode.blockStatement;
            String translateBlockStatement = blockStatementTranslate.translateBlockStatement(blockStatement);

            // 填充bs直接转换后的结果
            blockStatementTreeNode.translateBlockStatement = translateBlockStatement;

            // 填充bs源码开始行号
            blockStatementTreeNode.bsStartLine = String.valueOf(blockStatement.getStart().getLine());

            // 填充bs转换的hint
            blockStatementTreeNode.hint = TranslateHint.misMatchCodesToString();

            // 填充非子句部分的hint
            blockStatementTreeNode.nonBlockStatementHint = fetchNonBlockStatementHint(translateBlockStatement);

            // 递归填充子bs
            fillBSModel(blockStatementTreeNode.subBlockStatementTreeNodes);
        }
    }

    /**
     *
     */
    private String fetchNonBlockStatementHint(String translateBlockStatement) {
        if (StringUtils.isBlank(translateBlockStatement)) {
            return StringUtils.EMPTY;
        }

        String[] bsHints = translateBlockStatement.split(TranslateHint.BS_HINT_TAG);
        if (bsHints.length < 2) {
            return StringUtils.EMPTY;
        }
        return bsHints[1];
    }

    private MethodDeclarationNode buildBSModel(ParserRuleContext ctx) {
        MethodDeclarationNode methodDeclarationNode = new MethodDeclarationNode();
        ParserRuleContext methodHeaderRule = null;
        ParserRuleContext methodBodyRule = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_methodHeader) {
                methodHeaderRule = (ParserRuleContext) ctx.getChild(i);
            }
            if (ctx.getChild(i) instanceof RuleContext &&
                    ((RuleContext) ctx.getChild(i)).getRuleIndex() == Java8Parser.RULE_methodBody) {
                methodBodyRule = (ParserRuleContext) ctx.getChild(i);
            }
        }

        List<ParserRuleContext> blockStatements = fetchBlockStatementsByMethodBody(methodBodyRule);

        // methodHeader
        MethodHeaderTranslate methodHeaderTranslate = new MethodHeaderTranslate();
        methodDeclarationNode.methodHeader = methodHeaderTranslate.translateMethodHeader(methodHeaderRule);

        // method的直接bs
        methodDeclarationNode.blockStatementTreeNodes = blockStatements.stream()
                .map(this::buildMethodBlockStatementTree)
                .collect(Collectors.toList());

        return methodDeclarationNode;
    }

    private List<ParserRuleContext> fetchBlockStatementsByMethodBody(ParserRuleContext ctx) {
        if (ctx == null || ctx.getRuleIndex() != Java8Parser.RULE_methodBody) {
            return Lists.newArrayList();
        }

        ParseTree child = ctx.getChild(0);
        boolean isRuleContext = child instanceof RuleContext;
        if (!isRuleContext) {
            return Lists.newArrayList();
        }
        if (child.getChildCount() == 2) {
            return Lists.newArrayList();
        }

        List<ParserRuleContext> blockStatementsList = Lists.newArrayList();
        for (int i = 0; i < child.getChildCount(); i++) {
            if (!(child.getChild(i) instanceof RuleContext)) {
                continue;
            }
            blockStatementsList.add((ParserRuleContext) child.getChild(i));
        }

        return blockStatementsList;
    }

}
