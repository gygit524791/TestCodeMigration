package com.test.migration;

import com.test.migration.service.ApiBasicService;
import com.test.migration.service.ApiMappingService;
import com.test.migration.service.TranslateTestService;
import utils.Log;
import utils.MyBatisUtil;

public class ApiMappingMain {
    public static void main(String[] args) {
        // 启动redis服务， redis用于存储一部分中间结果 避免重复计算 优化性能
        //brew services start redis
        // 初始化（删除，新建）api，mapping，translate表
        MyBatisUtil.initTable();
        // 获取api基础信息
        ApiBasicService apiBasicService = new ApiBasicService();
        long s1 = System.currentTimeMillis();
        apiBasicService.extractApiBasic();
        long e1 = System.currentTimeMillis();
        Log.info("获取api基础信息完成，耗时（毫秒）：" + (e1 - s1) );

        // 生成mapping关系
        ApiMappingService apiMappingService = new ApiMappingService();
        long s2 = System.currentTimeMillis();
        // 生成api的vector并保存
        apiMappingService.generateTokenVector();
        apiMappingService.calculateApiMappings();
        long e2 = System.currentTimeMillis();
        Log.info("生成mapping关系完成，耗时（毫秒）：" + (e2 - s2) );

        // 生成api关联test信息
        TranslateTestService translateTestService = new TranslateTestService();
        long s3 = System.currentTimeMillis();
        translateTestService.generateTargetApiTest();
        long e3 = System.currentTimeMillis();
        Log.info("生成api关联test信息完成，耗时（毫秒）：" + (e3 - s3) );
    }

}
