package com.test.migration.dao;

import com.test.migration.entity.po.ApiBasic;
import com.test.migration.entity.po.ApiMapping;

import java.util.List;

public interface ApiMappingDao {
    void dropTable();
    void createTable();
    void batchInsert(List<ApiMapping> apiMappings);

    List<ApiMapping> selectByTaskId(Integer taskId);
}
