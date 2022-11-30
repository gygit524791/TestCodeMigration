package com.test.migration.dao;

import com.test.migration.entity.po.TranslateTest;

import java.util.List;

public interface TranslateTestDao {
    void dropTable();

    void createTable();

    void batchInsert(List<TranslateTest> translateTests);

}
