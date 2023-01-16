package com.test.migration;

import com.test.migration.service.TranslateTestService;

public class TranslateMain {

    /**
     */
    public static void main(String[] args) {
        TranslateTestService translateTestService = new TranslateTestService();
        translateTestService.translateCode();
    }


}
