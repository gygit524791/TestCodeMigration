package com.test.migration;

import com.test.migration.service.ApiBasicService;
import com.test.migration.service.ApiMappingService;
import com.test.migration.service.TranslateTestService;
import utils.MyBatisUtil;

public class Main {
    public static void main(String[] args) {
        // 初始化（删除，新建）api，mapping，translate表
        MyBatisUtil.initTable();

        // 获取api基础信息
        ApiBasicService apiBasicService = new ApiBasicService();
        apiBasicService.extractApiBasic();

        // 生成mapping关系
        ApiMappingService apiMappingService = new ApiMappingService();
        apiMappingService.calculateApiMappings();

        // 生成api关联test信息
        TranslateTestService translateTestService = new TranslateTestService();
        translateTestService.generateTargetApiTest();

    }

}
