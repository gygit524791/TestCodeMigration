package utils;

import com.test.migration.dao.ApiBasicDao;
import com.test.migration.dao.ApiMappingDao;
import com.test.migration.dao.TranslateTestDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtil {

    private static SqlSessionFactory factory = null;

    // 使用static静态代码块，随着类的加载而加载，只执行一次
    static {
        try {
            String resource = "mybatis/mybatis-config.xml";
            // 加载MyBatis的主配置文件
            InputStream inputStream = Resources.getResourceAsStream(resource);
            // 通过构建器（SqlSessionFactoryBuilder）构建一个SqlSessionFactory工厂对象
            factory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqlSession getSqlSession() throws IOException {
        return factory.openSession();
    }

    public static void initTable() {
        dropTable();
        createTable();
    }

    private static void dropTable() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao apiBasicMapper = session.getMapper(ApiBasicDao.class);
            apiBasicMapper.dropTable();

            ApiMappingDao apiMappingMapper = session.getMapper(ApiMappingDao.class);
            apiMappingMapper.dropTable();

            TranslateTestDao translateTestMapper = session.getMapper(TranslateTestDao.class);
            translateTestMapper.dropTable();

            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao apiBasicMapper = session.getMapper(ApiBasicDao.class);
            apiBasicMapper.createTable();

            ApiMappingDao apiMappingMapper = session.getMapper(ApiMappingDao.class);
            apiMappingMapper.createTable();

            TranslateTestDao translateTestMapper = session.getMapper(TranslateTestDao.class);
            translateTestMapper.createTable();
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
