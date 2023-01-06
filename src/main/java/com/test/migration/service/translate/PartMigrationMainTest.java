package com.test.migration.service.translate;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
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

        String bsStartIndex;

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


        public boolean isTranslateBlockStatementFail() {
            if (StringUtils.isBlank(hint)) {
                return false;
            }
            List<String> hintList = Splitter.on(TranslateHint.BS_HINT_TAG).splitToList(hint).stream()
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());

            return !hintList.isEmpty();
        }

    }

    /**
     *
     */
    public BlockStatementTreeNode buildMethodBlockStatementTree(ParserRuleContext parserRuleContext) {
        if (parserRuleContext.getRuleIndex() != Java8Parser.RULE_blockStatement) {
            System.out.println("ahb");
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


    public static void main(String[] args) {
        MappingRuleLoader.load();
        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/translate/partMigrationTestDemo/ADemo.java";
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
        List<String> partTranslateMethods = Lists.newArrayList();
        BlockStatementTranslate blockStatementTranslate = new BlockStatementTranslate();
        PartMigrationMainTest partMigrationMainTest = new PartMigrationMainTest();

        for (ParserRuleContext ctx : TestCodeContext.methodDeclarationCtxList) {
            // 1. 构建部分迁移model
            MethodDeclarationNode methodDeclarationNode = partMigrationMainTest.buildBSModel(ctx);

            // 2. 填充hint
            List<BlockStatementTreeNode> blockStatementTreeNodes = methodDeclarationNode.blockStatementTreeNodes;
            partMigrationMainTest.fillBSModel(blockStatementTreeNodes);

            // 3.1 执行部分迁移bs调整策略，构建调整策略map
            for (BlockStatementTreeNode blockStatementTreeNode : blockStatementTreeNodes) {
                partMigrationMainTest.executePartMigrationModifyStrategy(blockStatementTreeNode);
            }

            // 3.2 todo 名字暂定
            partMigrationMainTest.dfsModify(blockStatementTreeNodes);

            // 4. 二次转换，并合并出部分迁移结果
            List<String> translateBlockStatements = methodDeclarationNode.blockStatementTreeNodes.stream()
                    .map(x -> blockStatementTranslate.translateBlockStatement(x.blockStatement))
                    .collect(Collectors.toList());


            String methodBlockStatements = Joiner.on("").join(translateBlockStatements);
            String translateMethod = methodDeclarationNode.methodHeader + "{" + methodBlockStatements + "}";
            partTranslateMethods.add(translateMethod);
        }

        for (String method : partTranslateMethods) {
            System.out.println("=====part - translate - method start=====");
            System.out.println(method);
            System.out.println("=====part - translate - method end=====");
            System.out.println();
        }

    }

    /**
     * b1 b2 b3
     *
     * @param blockStatementTreeNodes
     */
    private void dfsModify(List<BlockStatementTreeNode> blockStatementTreeNodes) {
        BlockStatementTreeNode parent = new BlockStatementTreeNode();

        parent.blockStatement = null;
        parent.translateBlockStatement = StringUtils.EMPTY;
        parent.subBlockStatementTreeNodes = blockStatementTreeNodes;
        parent.bsStartIndex = StringUtils.EMPTY;
        parent.hint = StringUtils.EMPTY;
        parent.nonBlockStatementHint = StringUtils.EMPTY;

        processDfs(parent);
    }

    private void processDfs(BlockStatementTreeNode blockStatementTreeNode) {
//
//        blockStatementModifyMap.forEach((k,v)->{
//            System.out.println(k+":"+v);
//        });
//        System.out.println("===----=== ");

        boolean setRemove = false;
        Stack<BlockStatementTreeNode> stack = new Stack<>();
        stack.push(blockStatementTreeNode);
        // dfs
        while (!stack.isEmpty()) {
            BlockStatementTreeNode statementTreeNode = stack.pop();
            String key = statementTreeNode.bsStartIndex;

            if (setRemove) {
                blockStatementModifyMap.put(key, "remove");
            } else {
                // 如果遇到调整策略是remove，那么后续的所有bs均改为remove
                if (StringUtils.equals("remove", blockStatementModifyMap.getOrDefault(key, "keep"))) {
                    setRemove = true;
                }
            }

            for (int i = statementTreeNode.subBlockStatementTreeNodes.size() - 1; i >= 0; i--) {
                stack.push(statementTreeNode.subBlockStatementTreeNodes.get(i));
            }
        }

//        blockStatementModifyMap.forEach((k,v)->{
//            System.out.println(k+":"+v);
//        });
    }

    private void executePartMigrationModifyStrategy(BlockStatementTreeNode blockStatementTreeNode) {
        String key = blockStatementTreeNode.bsStartIndex;

        // 初始化默认值
        blockStatementModifyMap.put(key, "keep");

        // 策略1 如果存在nonBlockStatementHint，直接remove
        if (StringUtils.isNotBlank(blockStatementTreeNode.nonBlockStatementHint)) {
            blockStatementModifyMap.put(key, "remove");
        }

        if (blockStatementTreeNode.isTranslateBlockStatementFail()) {
            blockStatementModifyMap.put(key, "remove");
        }

        // 递归处理子bs
        for (BlockStatementTreeNode statementTreeNode : blockStatementTreeNode.subBlockStatementTreeNodes) {
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

            // 填充bs直接转换后的结果
            blockStatementTreeNode.translateBlockStatement = blockStatementTranslate.translateBlockStatement(blockStatement);

            // 填充bs源码开始行号
            blockStatementTreeNode.bsStartIndex = String.valueOf(blockStatement.getStart().getStartIndex());

            // 填充bs转换的hint
            blockStatementTreeNode.hint = TranslateHint.misMatchCodesToString();

            // 填充非子句部分的hint
            blockStatementTreeNode.nonBlockStatementHint = fetchNonBlockStatementHint(blockStatementTreeNode.hint);

            // 递归填充子bs
            fillBSModel(blockStatementTreeNode.subBlockStatementTreeNodes);
        }
    }

    /**
     *
     */
    private String fetchNonBlockStatementHint(String blockStatementHint) {
        if (StringUtils.isBlank(blockStatementHint)) {
            return StringUtils.EMPTY;
        }

        String[] bsHints = blockStatementHint.split(TranslateHint.BS_HINT_TAG);
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
            ParseTree child = ctx.getChild(i);
            if (!(child instanceof RuleContext)) {
                continue;
            }
            if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_methodHeader) {
                methodHeaderRule = (ParserRuleContext) child;
            }

            if (((RuleContext) child).getRuleIndex() == Java8Parser.RULE_methodBody) {
                methodBodyRule = (ParserRuleContext) child;
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

        // { }
        if (child.getChildCount() == 2) {
            return Lists.newArrayList();
        }

        List<ParserRuleContext> blockStatementsList = Lists.newArrayList();
        for (int i = 0; i < child.getChildCount(); i++) {
            ParseTree subChild = child.getChild(i);
            if (!(subChild instanceof RuleContext)) {
                continue;
            }
            // blockStatements
            for (int j = 0; j < subChild.getChildCount(); j++) {
                // blockStatement
                blockStatementsList.add((ParserRuleContext) subChild.getChild(j));
            }
        }

        return blockStatementsList;
    }

}
