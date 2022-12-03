package com.test.migration;

import com.test.migration.service.TranslateTestService;

public class TranslateMain {
    public static void main(String[] args) {
        TranslateTestService translateTestService = new TranslateTestService();
        long s4 = System.currentTimeMillis();
        translateTestService.translateCode();
        long e4 = System.currentTimeMillis();
        System.out.println("代码转换完成，耗时（秒）：" + (e4 - s4) / 1000);
    }
}
