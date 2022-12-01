package com.test.migration.service;

import com.google.common.collect.Lists;
import com.test.migration.dao.ApiBasicDao;
import com.test.migration.dao.ApiMappingDao;
import com.test.migration.entity.TaskParameter;
import com.test.migration.entity.po.ApiBasic;
import com.test.migration.entity.po.ApiMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import utils.CallPythonUtil;
import utils.JsonUtil;
import utils.MyBatisUtil;
import utils.ResourceReader;

import java.util.List;
import java.util.Objects;

public class ApiMappingService {

    public void calculateApiMappings() {
        System.out.println("开始执行mapping计算");
        long startTime = System.currentTimeMillis();
        TaskParameter taskParameter = ResourceReader.getTaskParameter();
        ApiBasicService apiBasicService = new ApiBasicService();
        List<ApiBasic> apiBasics = apiBasicService.selectByTaskId(taskParameter.getTaskId());

        List<ApiBasic> harmonyApis = apiBasics.stream()
                .filter(api -> Objects.equals(api.getType(), 0))
                .toList();

        int totalHarmonyApiSize = harmonyApis.size();
        int index = 0;
        List<ApiMapping> apiMappings = Lists.newArrayList();
        for (ApiBasic harmonyApi : harmonyApis) {
            index++;
            System.out.println("开始计算harmony api映射关系,basicId:" + harmonyApi.getId()
                    + "，seqId:" + harmonyApi.getId()
                    + "，word seq:" + harmonyApi.getMethodWordSequence()
                    + "，进度：" + index + "/" + totalHarmonyApiSize);

            // 调用python相似度计算脚本
            String[] tokenArgs = new String[]{
                    taskParameter.getPythonBinPath(),
                    taskParameter.getPythonCalcSimilarity(),
                    String.valueOf(harmonyApi.getId()),
                    // 在python代码中用taskId从数据库查所有android api
                    String.valueOf(taskParameter.getTaskId()),
                    taskParameter.getDbFilepath()
            };

            // 格式：harmonyId,androidMappingId
            List<String> resultLines = CallPythonUtil.call(tokenArgs);
            String resultLine = resultLines.stream().findFirst().orElse(StringUtils.EMPTY);
            if (StringUtils.isBlank(resultLine)) {
                System.out.println("相似度计算结果为空");
                continue;
            }

            apiMappings.addAll(buildApiMappings(taskParameter.getTaskId(), resultLine));
        }

        batchSave(apiMappings);
        long endTime = System.currentTimeMillis();
        System.out.println("执行mapping计算完毕，耗时（秒）：" + (endTime - startTime) / 1000);
    }

    private List<ApiMapping> buildApiMappings(Integer taskId, String resultLine) {
        if (StringUtils.isBlank(resultLine)) {
            return Lists.newArrayList();
        }

        List<ApiMapping> apiMappings = Lists.newArrayList();
        String[] mappingStr = resultLine.split(";");
        // 解析python的mapping结果
        for (String mapping : mappingStr) {
            String[] mappingIds = mapping.split(",");
            Integer harmonyApiId = Integer.parseInt(mappingIds[0]);
            Integer androidApiId = Integer.parseInt(mappingIds[1]);

            ApiMapping apiMapping = ApiMapping.builder()
                    .taskId(taskId)
                    .sourceApiId(harmonyApiId)
                    .targetApiId(androidApiId)
                    .build();

            apiMappings.add(apiMapping);
        }

        return apiMappings;
    }


    /** CRUD **/
    public List<ApiMapping> selectByTaskIdAndType(Integer taskId, Integer type) {
        List<ApiMapping> list = null;

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiMappingDao mapper = session.getMapper(ApiMappingDao.class);
            list = mapper.selectByTaskIdAndType(taskId, type);
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
