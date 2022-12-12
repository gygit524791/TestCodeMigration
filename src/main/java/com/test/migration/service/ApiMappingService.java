package com.test.migration.service;

import com.google.common.collect.Lists;
import com.test.migration.dao.ApiMappingDao;
import com.test.migration.entity.TaskParameter;
import com.test.migration.entity.po.ApiBasic;
import com.test.migration.entity.po.ApiMapping;
import org.apache.ibatis.session.SqlSession;
import utils.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApiMappingService {

    ApiBasicService apiBasicService = new ApiBasicService();

    public void calculateApiMappings() {
        System.out.println("开始执行mapping计算");
        long startTime = System.currentTimeMillis();
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        // 调用python相似度计算脚本
        String[] tokenArgs = new String[]{
                taskParameter.getPythonBinPath(),
                taskParameter.getPythonCalcSimilarity(),
                String.valueOf(taskParameter.getTaskId()),
                taskParameter.getDbFilepath(),
                taskParameter.getApiVectorDictFilepath(),
                taskParameter.getClassVectorDictFilepath()
        };

        List<String> resultLines = CallPythonUtil.call(tokenArgs);

        StringBuilder resultLine = new StringBuilder();
        resultLines.forEach(resultLine::append);
        List<String> apiMappings = JsonUtil.jsonToList(resultLine.toString(), String.class);
        List<ApiMapping> mappings = buildApiMappings(apiMappings);
        batchSave(mappings);

        // 保存到mapping规则中
        saveMappingRule(mappings);

        long endTime = System.currentTimeMillis();
        System.out.println("执行mapping计算完毕，耗时（秒）：" + (endTime - startTime) / 1000);
    }

    private void saveMappingRule(List<ApiMapping> mappings) {
        List<Integer> apiBasicIds = Lists.newArrayList();
        apiBasicIds.addAll(mappings.stream().map(ApiMapping::getSourceApiId).toList());
        apiBasicIds.addAll(mappings.stream().map(ApiMapping::getTargetApiId).toList());

        if (apiBasicIds.size() == 0) {
            return;
        }

        List<ApiBasic> apiBasics = apiBasicService.selectByIds(apiBasicIds);
        Map<Integer, ApiBasic> apiBasicMap = apiBasics.stream().collect(Collectors.toMap(ApiBasic::getId, Function.identity()));
        mappings.forEach(mapping -> {
            ApiBasic sourceApi = apiBasicMap.get(mapping.getSourceApiId());
            ApiBasic targetApi = apiBasicMap.get(mapping.getTargetApiId());
            // 安卓的api
            String value = sourceApi.getClassName() + "->" + sourceApi.getApiName();
            // 鸿蒙的api
            String key = targetApi.getClassName() + "->" + targetApi.getApiName();
            MappingRuleWriter.writeApiMappingProperties(key, value);
        });
    }

    private List<ApiMapping> buildApiMappings(List<String> apiMappings) {
        Integer taskId = TaskParameterReader.getTaskParameter().getTaskId();
        if (apiMappings == null || apiMappings.size() == 0) {
            return Lists.newArrayList();
        }
        return apiMappings.stream()
                .map(apiMapping -> {
                    String[] split = apiMapping.split("-");
                    return ApiMapping.builder()
                            .taskId(taskId)
                            .sourceApiId(Integer.parseInt(split[0]))
                            .targetApiId(Integer.parseInt(split[1]))
                            .build();
                }).collect(Collectors.toList());
    }

    /**
     * CRUD
     **/
    public List<ApiMapping> selectByTaskId(Integer taskId) {
        List<ApiMapping> list = Lists.newArrayList();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiMappingDao mapper = session.getMapper(ApiMappingDao.class);
            list = mapper.selectByTaskId(taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void batchSave(List<ApiMapping> apiMappings) {
        if (apiMappings == null || apiMappings.size() == 0) {
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiMappingDao mapper = session.getMapper(ApiMappingDao.class);
            mapper.batchInsert(apiMappings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
