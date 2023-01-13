package com.test.migration.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.test.migration.dao.ApiMappingDao;
import com.test.migration.entity.TaskParameter;
import com.test.migration.entity.po.ApiBasic;
import com.test.migration.entity.po.ApiMapping;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.session.SqlSession;
import utils.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApiMappingService {

    ApiBasicService apiBasicService = new ApiBasicService();

    public void calculateApiMappings() {
        Log.info("开始执行mapping计算");
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

        List<String> resultLines = CallUtil.call(tokenArgs);

        StringBuilder resultLine = new StringBuilder();
        resultLines.forEach(resultLine::append);
        List<String> apiMappings = JsonUtil.jsonToList(resultLine.toString(), String.class);

        // 构建mapping结构
        List<ApiMapping> apiMappingList = buildApiMappings(apiMappings);

        Map<Integer, ApiBasic> apiBasicMap = queryApiBasicMap(apiMappingList);

        // 过滤掉重复mapping
        apiMappingList = filterSameMapping(apiMappingList, apiBasicMap);

        // mapping结果保存到数据库，便于后续使用和分析
        batchSave(apiMappingList);

        // 保存到mapping规则中
        doCalculateMappingRule(apiMappingList, apiBasicMap);

        long endTime = System.currentTimeMillis();
        Log.info("执行mapping计算完毕，耗时（秒）：" + (endTime - startTime) / 1000);
    }

    /**
     * 过滤掉重复classname 和 api的mapping
     */
    private List<ApiMapping> filterSameMapping(List<ApiMapping> mappings, Map<Integer, ApiBasic> apiBasicMap) {
        if (mappings == null || mappings.size() == 0) {
            return Lists.newArrayList();
        }
        Set<String> mappingKeySet = Sets.newHashSet();
        List<ApiMapping> filterMappings = Lists.newArrayList();

        for (ApiMapping apiMapping : mappings) {
            ApiBasic sourceApi = apiBasicMap.get(apiMapping.getSourceApiId());
            ApiBasic targetApi = apiBasicMap.get(apiMapping.getTargetApiId());
            String key = sourceApi.getClassName() + "$" + sourceApi.getApiName() + ":" + targetApi.getClassName() + "$" + targetApi.getApiName();
            if (!mappingKeySet.contains(key)) {
                mappingKeySet.add(key);
                filterMappings.add(apiMapping);
            }
        }
        return filterMappings;
    }

    private Map<Integer, ApiBasic> queryApiBasicMap(List<ApiMapping> mappings) {
        List<Integer> apiBasicIds = Lists.newArrayList();
        apiBasicIds.addAll(mappings.stream().map(ApiMapping::getSourceApiId).collect(Collectors.toList()));
        apiBasicIds.addAll(mappings.stream().map(ApiMapping::getTargetApiId).collect(Collectors.toList()));
        if (apiBasicIds.size() == 0) {
            return null;
        }
        List<ApiBasic> apiBasics = apiBasicService.selectByIds(apiBasicIds);

        return apiBasics.stream()
                .collect(Collectors.toMap(ApiBasic::getId, Function.identity()));
    }

    private void doCalculateMappingRule(List<ApiMapping> apiMappingList, Map<Integer, ApiBasic> apiBasicMap) {
        if (apiMappingList == null || apiMappingList.size() == 0) {
            return;
        }
        // 将api mapping写入到配置文件中
        apiMappingList.forEach(mapping -> {
            ApiBasic sourceApi = apiBasicMap.get(mapping.getSourceApiId());
            ApiBasic targetApi = apiBasicMap.get(mapping.getTargetApiId());
            // 安卓的api
            String value = sourceApi.getClassName() + "->" + sourceApi.getApiName();
            // 鸿蒙的api
            String key = targetApi.getClassName() + "->" + targetApi.getApiName();

            MappingRuleWriter.writeApiMappingProperties(key, value);
        });

        // 计算class mapping
        classNameMapping(apiMappingList, apiBasicMap);
    }


    private void classNameMapping(List<ApiMapping> apiMappingList, Map<Integer, ApiBasic> apiBasicMap) {
        Map<String, Integer> classNameNumMapping = Maps.newHashMap();

        apiMappingList.forEach(mapping -> {
            ApiBasic sourceApi = apiBasicMap.get(mapping.getSourceApiId());
            ApiBasic targetApi = apiBasicMap.get(mapping.getTargetApiId());

            String mappingKey = sourceApi.getClassName() + "#" + targetApi.getClassName();
            // (AClass#BClass, 1), (AClass#CClass, 1)
            classNameNumMapping.merge(mappingKey, 1, Integer::sum);
        });

        List<ClassApiMappingNum> classApiMappingNumList = Lists.newArrayList();
        classNameNumMapping.forEach((classNameMapKey, apiNum) -> {
            String[] classNameMap = classNameMapKey.split("#");
            classApiMappingNumList.add(ClassApiMappingNum.builder()
                    .sourceClassName(classNameMap[0])
                    .targetClassName(classNameMap[1])
                    .mappingApiNum(apiNum)
                    .build());
        });

        calculateClassNameMappings(classApiMappingNumList);
    }

    private void calculateClassNameMappings(List<ClassApiMappingNum> classApiMappingNumList) {
        Map<String, List<ClassApiMappingNum>> classApiMappingMap = classApiMappingNumList.stream()
                .collect(Collectors.groupingBy(ClassApiMappingNum::getSourceClassName));

        classApiMappingMap.forEach((sourceKey, mappingList) -> {
            // 取api mapping数目最多的一对，如果相同则取第一个
            ClassApiMappingNum finalMapping = mappingList.stream()
                    .max(Comparator.comparing(ClassApiMappingNum::getMappingApiNum))
                    .orElse(null);
            if (finalMapping == null) {
                Log.error("class mapping 不存在");
                return;
            }

            try {
                MappingRuleWriter.writeClassNameMappingProperties(finalMapping.getSourceClassName(), finalMapping.getTargetClassName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    /**
     * api匹配数量统计
     */
    @Data
    @Builder
    public static class ClassApiMappingNum {
        private String sourceClassName;
        private String targetClassName;
        private Integer mappingApiNum;
    }

}
