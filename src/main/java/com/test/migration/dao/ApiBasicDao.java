package com.test.migration.dao;

import com.test.migration.po.ApiBasic;

import java.util.List;

public interface ApiBasicDao {
    void dropTable();

    void createTable();

    void insert(ApiBasic apiBasic);

    void batchInsert(List<ApiBasic> apiBasics);

    ApiBasic getById(Integer id);

    List<ApiBasic> selectAll();

    List<ApiBasic> selectByTaskId(Integer taskId);

    List<ApiBasic> selectByIds(List<Integer> ids);
}
