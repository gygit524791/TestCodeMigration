package com.test.migration.service.translate;

import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Parser;
import lombok.Data;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * 代码转换上下文
 * <p>
 * 存储代码转换过程需要的中间信息
 * 简单实现：用static变量暂存antlr解析和生成的待转换code信息
 * 在antlr执行visitor完毕后，所有上下文信息将被填充，这些信息便于后续在其它地方使用，相当于一个简易内存数据库
 */
public class TestCodeContext {
    public static String className = "";
    public static List<ParserRuleContext> classDeclarationCtxList = Lists.newArrayList();
    public static List<ParserRuleContext> classBodyDeclarationCtxList = Lists.newArrayList();
    public static List<ParserRuleContext> fieldDeclarationCtxList = Lists.newArrayList();
    public static List<ParserRuleContext> methodDeclarationCtxList = Lists.newArrayList();

    public static void loadDeclaration() {
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

    /**
     * 保存test code里面定义的类属性基础信息（name，type）：变量，常量等
     */

    public static class ClassMemberDeclaration {
        // field,method,class,interface
        public static List<Field> fields = Lists.newArrayList();
        public static List<Method> methods = Lists.newArrayList();
        public static List<Class> classes = Lists.newArrayList();
        public static List<Interface> interfaces = Lists.newArrayList();
        @Data
        public static class Field {
            public  String type;
            public  String name;
        }
        @Data
        public static class Method {
            public static String name;
        }
        @Data
        public static class Class {
            public  String name;
        }

        public static class Interface {
            public static String name;
        }
    }
}
