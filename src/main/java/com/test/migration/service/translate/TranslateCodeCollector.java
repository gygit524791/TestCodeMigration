package com.test.migration.service.translate;

import com.google.common.collect.Lists;
import lombok.ToString;

import java.util.List;

/**
 * 收集转换后的testCode，两个类别：
 * 1.testCode类成员变量：属性，内部类，非测试方法；其中每一个都带有对应的hint
 * <p>
 * <p>
 * 2.测试方法
 * 方法由blockStatement组成，测试方法用一组[blockStatement,hint]组成
 * <p>
 * 在translate的时候：1.收集转换后的结果 2.收集hint
 * 方便后续生成最终的迁移后代码
 * <p>
 * 每次执行translate之前，调用context的init方法初始化
 * translate过程中会不断填充成员变量，在translate执行完成后，完成填充
 */
public class TranslateCodeCollector {

    public static String className;
    public static List<TranslateCode> fieldDeclarationTranslateCodes;
    public static List<TranslateCode> classDeclarationTranslateCodes;
    public static List<MethodTranslateCode> methodDeclarationTranslateCodes;


    /*** TMP ***/
    public static MethodTranslateCode.MethodHeaderTranslateCode methodHeaderTranslateCode;
    public static List<MethodTranslateCode.BlockStatementTranslateCode> blockStatementTranslateCodes;
    public static int methodStartLine;
    public static int methodEndLine;

    public static void init() {
        className = "";
        fieldDeclarationTranslateCodes = Lists.newArrayList();
        classDeclarationTranslateCodes = Lists.newArrayList();
        methodDeclarationTranslateCodes = Lists.newArrayList();
        methodHeaderTranslateCode = new MethodTranslateCode.MethodHeaderTranslateCode();
        blockStatementTranslateCodes = Lists.newArrayList();
    }

    public static class TranslateCode {
        public String translateCode;
        public List<String> misMatchCodes;
    }


    public static class MethodTranslateCode {

        public MethodHeaderTranslateCode methodHeaderTranslateCode;
        public List<BlockStatementTranslateCode> blockStatementTranslateCodes;

        /*** tmp ***/
        public static int methodStartLine;
        public static int methodEndLine;

        public static void clearMethodLine() {
            methodStartLine = 0;
            methodEndLine = 0;
        }

        @ToString
        public static class MethodHeaderTranslateCode {
            public String translateCode;
            public List<String> misMatchCodes;
        }

        @ToString
        public static class BlockStatementTranslateCode {
            public String translateCode;
            public List<String> misMatchCodes;
        }
    }


}
