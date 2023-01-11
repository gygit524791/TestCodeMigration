package com.test.migration;

import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
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
import utils.LogUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TranslateMain {
    /**
     *
     *
     * TODO 合并到TranslateTestService&doTranslate方法中去
     */
    public static void main(String[] args) throws Exception {
        MappingRuleLoader.load();
        LogUtil.info("aaaa");

//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/example/android/test/ValueAnimatorTests.java";
        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/translate/source/BDemo.java";
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

        // 哪些测试方法需要被迁移，后续改为TranslateTest$testMethodApiInvocation
        String json = "{\"testArePropertiesAnimating\":[291],\"testCancelAllAnimations\":[304]}";

        Map<String, List<Integer>> map = JsonUtil.jsonToPojo(json, Map.class);
        List<String> migrateTestMethods = map == null ? Lists.newArrayList() : Lists.newArrayList(map.keySet());
        TestCodeFilter.filterMethodDeclarationCtxList(migrateTestMethods);

        testCodeVisitor.getTypeNameMap().put("mActivityRule", "ActivityTestRule<AnimatorSetActivity>");
        ReplaceRuleService.typeNameMap = testCodeVisitor.getTypeNameMap();

        translate();

        TranslateCodeGenerator.doGenerate();
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
            translateCode.misMatchCodes = TranslateHint.misMatchCodes.stream().distinct().collect(Collectors.toList());

            TranslateCodeCollector.classDeclarationTranslateCodes.add(translateCode);
        }

        // 成员变量迁移
        FieldDeclarationTranslate fieldDeclarationTranslate = new FieldDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.fieldDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = fieldDeclarationTranslate.translateFieldDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.misMatchCodes.stream().distinct().collect(Collectors.toList());

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

            TranslateCodeCollector.MethodTranslateCode.clearMethodLine();
        }

        // 方法部分迁移
        PartMigrationProcessor partMigrationProcessor = new PartMigrationProcessor();
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            TranslateCodeCollector.PartMigrationMethodTranslateCode partMigrationMethodTranslateCode = new TranslateCodeCollector.PartMigrationMethodTranslateCode();
            partMigrationMethodTranslateCode.translateCode = partMigrationProcessor.doPartMigrationTranslate(parserRuleContext);

            TranslateCodeCollector.partMigrationMethodTranslateCodes.add(partMigrationMethodTranslateCode);
        }

    }


}
