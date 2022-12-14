package com.test.migration.service.translate;

import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * 代码转换上下文
 *
 * 存储代码转换过程需要的中间信息
 * 简单实现：用static变量暂存antlr解析和生成的待转换code信息
 * 在antlr执行visitor完毕后，所有上下文信息将被填充，便于后续在其它地方使用，相当于一个简易内存数据库
 */
public class TestCodeContext {
    public static String className = "";
    public static List<ParserRuleContext> classBodyDeclarationCtxList = Lists.newArrayList();
    public static List<ParserRuleContext> fieldDeclarationCtxList = Lists.newArrayList();
    public static List<ParserRuleContext> methodDeclarationCtxList = Lists.newArrayList();
    public static List<ParserRuleContext> classDeclarationCtxList = Lists.newArrayList();



    public static void loadDeclaration() {
        if (TestCodeContext.classBodyDeclarationCtxList.isEmpty()) {
            return;
        }

        for (ParserRuleContext parserRuleContext : TestCodeContext.classBodyDeclarationCtxList) {
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
                TestCodeContext.fieldDeclarationCtxList.add((ParserRuleContext) subNode);
            }
            if (subNode.getRuleIndex() == Java8Parser.RULE_methodDeclaration) {
                TestCodeContext.methodDeclarationCtxList.add((ParserRuleContext) subNode);
            }
            if (subNode.getRuleIndex() == Java8Parser.RULE_classDeclaration) {
                TestCodeContext.classDeclarationCtxList.add((ParserRuleContext) subNode);
            }
        }
    }
//    public static
}
