package com.test.migration.service.translate;

import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

/**
 * 收集转换后的testCode，两个类别：
 * 1.testCode类成员变量：属性，内部类，非测试方法；其中每一个都带有对应的hint
 * <p>
 * 2.测试方法
 * 方法由blockStatement组成，测试方法用一组[blockStatement,hint]组成
 */
public class TranslateCodeCollector {
    public static List<FieldDeclarationTranslateCode> fieldDeclarationTranslateCodes;
    public static List<ClassDeclarationTranslateCode> classDeclarationTranslateCodes;
    public static List<TestMethodTranslateCode> testMethodTranslateCodes;


    public static void init() {
        fieldDeclarationTranslateCodes = Lists.newArrayList();
        classDeclarationTranslateCodes = Lists.newArrayList();
        testMethodTranslateCodes = Lists.newArrayList();
    }


    public static class FieldDeclarationTranslateCode {
        public String translateCode;
        public TranslateHint translateHint;
    }

    public static class ClassDeclarationTranslateCode {
        public String translateCode;
        public TranslateHint translateHint;
    }

    /**
     * test method
     */
    public static class TestMethodTranslateCode {
        public String translateFullCode;
        public String translatePartCode;

        public ParserRuleContext methodParserRuleContext;

        public static class BlockStatementTranslateCode {
            public ParserRuleContext blockStatementParserRuleContext;
            public TranslateHint translateHint;
        }
    }


}
