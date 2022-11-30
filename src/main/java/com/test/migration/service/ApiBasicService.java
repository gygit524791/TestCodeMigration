package com.test.migration.service;

import com.test.migration.dao.ApiBasicDao;
import com.test.migration.po.ApiBasic;
import org.apache.ibatis.session.SqlSession;
import utils.MyBatisUtil;

import java.util.List;

public class ApiBasicService {

    public List<ApiBasic> selectAll() {
        List<ApiBasic> list = null;

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao mapper = session.getMapper(ApiBasicDao.class);
            list = mapper.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
