package com.test.migration;

import com.test.migration.service.translate.PartMigrationProcessor;

public class TranslateMain {
    public static void main(String[] args) throws Exception {
//        TranslateTestService translateTestService = new TranslateTestService();
//
//        long s4 = System.currentTimeMillis();
//        translateTestService.translateCode();
////        copyFileUsingChannel();
//        long e4 = System.currentTimeMillis();
//
//        System.out.println("代码转换完成，耗时（毫秒）：" + (e4 - s4) );
//
//        System.out.println();

        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/migration/animator/AnimatorSetActivityTest.java";
//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/migration/animator/ValueAnimatorTests.java";
//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/migration/case/AnimatorSetActivityTest.java";
        PartMigrationProcessor processor = new PartMigrationProcessor();
        processor.partMigrationProcess(filepath);
    }


}
