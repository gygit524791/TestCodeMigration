package com.test.migration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.entity.po.TranslateTest;
import com.test.migration.service.TranslateTestService;
import com.test.migration.service.translate.*;
import com.test.migration.service.translate.bnf.common.method.MethodDeclarationTranslate;
import com.test.migration.service.translate.bnf.declaration.ClassDeclarationTranslate;
import com.test.migration.service.translate.bnf.declaration.FieldDeclarationTranslate;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.JsonUtil;
import utils.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TranslateMain {

    /**
     * TODO 合并到TranslateTestService&doTranslate方法中去
     */

    public static void main(String[] args) {
        TranslateTestService translateTestService = new TranslateTestService();
        List<TranslateTest> translateTests = translateTestService.selectByTaskId(1);

        translateTests.forEach(s -> {
            try {
                process(s.getTestFilepath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static void process(String filepath) throws Exception {
        MappingRuleLoader.load();

//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/translate/source/BDemo.java";
//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/migration/case/Case1.java";

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
        // 打印typeName
        printTypeNameMapMessage();

        // TODO 哪些测试方法需要被迁移，后续改为TranslateTest$testMethodApiInvocation
        String json = "{\"testAddListener\":[291]," +
                "\"testCancelAllAnimations\":[304]}";

        Map<String, List<Integer>> map = JsonUtil.jsonToPojo(json, Map.class);
        List<String> migrateTestMethods = map == null ? Lists.newArrayList() : Lists.newArrayList(map.keySet());

        TestCodeFilter.filterMethodDeclarationCtxList(migrateTestMethods);
        TestCodeContext.typeNameMap.put("mActivityRule", "ActivityTestRule<AnimatorSetActivity>");

        Log.info("开始i执行翻译");
        translate();
        Log.info("开始i执行生成");
        TranslateCodeGenerator.doGenerate();
    }

    private static void printTypeNameMapMessage() {
        // typename
        Log.info("TypeName Map message:");
        TestCodeContext.typeNameMap.forEach((key, value) -> {
            Log.info("typeName:" + key + ", typeValue:" + value);
        });
    }
    public static List<String> tmp() {
        TranslateTestService translateTestService = new TranslateTestService();
        List<TranslateTest> translateTests = translateTestService.selectByTaskId(1);
        Set<String> testNumSet = Sets.newHashSet();
        Set<Integer> apiNumSet = Sets.newHashSet();
        for (TranslateTest translateTest :
                translateTests) {
            Map<String, List<Integer>> map = JsonUtil.jsonToPojo(translateTest.getTestMethodApiInvocation(), Map.class);
            map.forEach((key, value) -> {
                testNumSet.add(key);
                apiNumSet.addAll(value);

            });
        }
        return Lists.newArrayList(testNumSet);
    }

    private static void translate() {
        TranslateCodeCollector.init();
        TranslateCodeCollector.className = TestCodeContext.className;

        // 类迁移
        ClassDeclarationTranslate classDeclarationTranslate = new ClassDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.classDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = classDeclarationTranslate.translateClassDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.formatMisMatchCodes(TranslateHint.misMatchCodes);

            TranslateCodeCollector.classDeclarationTranslateCodes.add(translateCode);
        }

        // 成员变量迁移
        FieldDeclarationTranslate fieldDeclarationTranslate = new FieldDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.fieldDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = fieldDeclarationTranslate.translateFieldDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.formatMisMatchCodes(TranslateHint.misMatchCodes);

            TranslateCodeCollector.fieldDeclarationTranslateCodes.add(translateCode);
        }

        // 方法迁移
        MethodDeclarationTranslate methodDeclarationTranslate = new MethodDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.MethodTranslateCode.methodStartLine = parserRuleContext.getStart().getLine();
            TranslateCodeCollector.MethodTranslateCode.methodEndLine = parserRuleContext.getStop().getLine();

            methodDeclarationTranslate.translateMethodDeclaration(parserRuleContext);
            // methodHeader信息
            // method blockStatement信息
            TranslateCodeCollector.MethodTranslateCode methodTranslateCode = new TranslateCodeCollector.MethodTranslateCode();
            methodTranslateCode.methodHeaderTranslateCode = TranslateCodeCollector.methodHeaderTranslateCode;
            methodTranslateCode.blockStatementTranslateCodes = TranslateCodeCollector.blockStatementTranslateCodes;
            TranslateCodeCollector.methodDeclarationTranslateCodes.add(methodTranslateCode);
//            System.out.println(methodTranslateCode);

            TranslateCodeCollector.MethodTranslateCode.clearMethodLine();
        }

        // 方法部分迁移
        PartMigrationProcessor partMigrationProcessor = new PartMigrationProcessor();
        TranslateCodeCollector.isFullTranslate = false; // 内心在滴血。。。
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            TranslateCodeCollector.PartMigrationMethodTranslateCode partMigrationMethodTranslateCode = new TranslateCodeCollector.PartMigrationMethodTranslateCode();
            partMigrationMethodTranslateCode.translateCode = partMigrationProcessor.doPartMigrationTranslate(parserRuleContext);
            TranslateCodeCollector.partMigrationMethodTranslateCodes.add(partMigrationMethodTranslateCode);
        }

    }


}
