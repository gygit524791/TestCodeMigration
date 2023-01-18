package com.test.migration.service.translate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.test.migration.entity.TaskParameter;
import org.apache.commons.lang3.StringUtils;
import utils.CallUtil;
import utils.FileWriteUtil;
import utils.Log;
import utils.TaskParameterReader;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收集转换后的testCode，两个类别：
 * 1.testCode类成员变量：属性，内部类，非测试方法；其中每一个都带有对应的hint
 * <p>
 * 2.测试方法
 * 方法由blockStatement组成，测试方法用一组[blockStatement,hint]组成
 */
public class TranslateCodeGenerator {
    public static void doGenerate() {
        Log.info("开始执行代码生成");

        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        String filepath = taskParameter.getOutputFilepath() + TranslateCodeCollector.className + ".cpp";

        //将TranslateCodeCollector的结果整理并输出到一个文件中
        Log.info("将TranslateCodeCollector的结果整理并输出到一个文件中");
        writeMigrationResultToFile(filepath);

        //代码格式化
        formatMigrationResultFile(filepath);

        Log.info("代码生成执行完毕");
    }

    /**
     * 将TranslateCodeCollector的结果整理并输出到一个文件中
     */
    public static void writeMigrationResultToFile(String filepath) {
        List<String> fileLines = Lists.newArrayList();
        fileLines.add("#include \"gtest/gtest.h\"");
        fileLines.add("using namespace testing;");
        fileLines.add("using namespace testing::ext;");
        fileLines.add("class " + TranslateCodeCollector.className + " : public testing::Test {");
        fileLines.add("public:");
        // 写入类成员属性
        for (TranslateCodeCollector.TranslateCode translateCode : TranslateCodeCollector.fieldDeclarationTranslateCodes) {
            addHintIfNeed(translateCode.misMatchCodes, fileLines);
            fileLines.add(translateCode.translateCode);
            // 换行
            fileLines.add("");
        }

        // 写入内部类
        for (TranslateCodeCollector.TranslateCode translateCode : TranslateCodeCollector.classDeclarationTranslateCodes) {
            addHintIfNeed(translateCode.misMatchCodes, fileLines);
            fileLines.add("class "+ translateCode.translateCode);
            // 换行
            fileLines.add("");
        }

        // 写入方法
        for (TranslateCodeCollector.MethodTranslateCode methodTranslateCode : TranslateCodeCollector.methodDeclarationTranslateCodes) {
            addHintIfNeed(methodTranslateCode.methodHeaderTranslateCode.misMatchCodes, fileLines);
            fileLines.add(methodTranslateCode.methodHeaderTranslateCode.translateCode);
            fileLines.add("{");
            for (TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode blockStatementTranslateCode : methodTranslateCode.blockStatementTranslateCodes) {
                addHintIfNeed(blockStatementTranslateCode.misMatchCodes, fileLines);
                fileLines.add(blockStatementTranslateCode.translateCode);
            }
            fileLines.add("}");
            // 换行
            fileLines.add("");
        }

        // 写入部分迁移方法
        for (TranslateCodeCollector.PartMigrationMethodTranslateCode translateCode : TranslateCodeCollector.partMigrationMethodTranslateCodes) {
            fileLines.add(translateCode.translateCode);
        }

        // 测试类结尾
        fileLines.add("}");

        // 写入文件
        try {
            FileWriteUtil.writeDataToFile(fileLines, filepath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 如果有hint，那么添加代码注释，如果没有hint就return
     */
    private static void addHintIfNeed(List<String> translateCode, List<String> fileLines) {
        String formatHintLine = formatHintLine(translateCode);
        if (StringUtils.isNotBlank(formatHintLine)) {
            fileLines.add("//以下组件没有匹配:");
            fileLines.add(formatHintLine);
        }
    }

    /**
     * 将输出的文件进行代码格式化处理
     */
    public static void formatMigrationResultFile(String filepath) {
        String cmd = "clang-format " + filepath;
        List<String> lines = CallUtil.callCMD(cmd);

        try {
            new File(filepath).deleteOnExit();
            String[] fileStr = filepath.split(File.separator);

            String oldFileName = fileStr[fileStr.length - 1];
            String newFileName = "test_" + oldFileName.toLowerCase();
            fileStr[fileStr.length - 1] = newFileName;

            List<String> filePathStr = Arrays.stream(fileStr).collect(Collectors.toList());
            String formatFilepath = Joiner.on(File.separator).join(filePathStr);

            FileWriteUtil.writeDataToFile(lines, formatFilepath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String formatHintLine(List<String> misMatchCodes) {
        if (misMatchCodes == null || misMatchCodes.size() == 0) {
            return StringUtils.EMPTY;
        }
        return "//" + Joiner.on(",").join(misMatchCodes);
    }


}
