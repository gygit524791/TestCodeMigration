package com.test.migration;

import com.test.migration.service.TranslateTestService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class TranslateMain {
    public static void main(String[] args) throws Exception {
        TranslateTestService translateTestService = new TranslateTestService();

        long s4 = System.currentTimeMillis();
        translateTestService.translateCode();
//        copyFileUsingChannel();
        long e4 = System.currentTimeMillis();

        System.out.println("代码转换完成，耗时（毫秒）：" + (e4 - s4) );
    }

























//    private static void copyFileUsingChannel() throws Exception {
//
//        File source = new File("/Users/gaoyi/IdeaProjects/TestMigrationV2/doc/demo/animator_test.cpp");
//        File dest = new File("/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/output/animator_test.cpp");
//
//        FileChannel sourceChannel = null;
//        FileChannel destChannel = null;
//        try {
//            sourceChannel = new FileInputStream(source).getChannel();
//            destChannel = new FileOutputStream(dest).getChannel();
//            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
//        }finally{
//            sourceChannel.close();
//            destChannel.close();
//        }
//    }
}
