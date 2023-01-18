package com.test.migration.service;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.dao.ApiBasicDao;
import com.test.migration.entity.TaskParameter;
import com.test.migration.entity.po.ApiBasic;
import com.test.migration.service.extract.APIExtractorService;
import com.test.migration.service.preprocess.Preprocess;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import utils.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ApiBasicService {

    /**
     * 提取api基础信息
     */
    public void extractApiBasic() {
        try {
            // android java文件提取api基础信息
            extractSourceApiBasic();

            // harmony cpp文件提取api基础信息
            extractTargetApiBasic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractSourceApiBasic() {
        Log.info("开始提取源API信息");
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<String> sourceFilepathList = Splitter.on(",").splitToList(taskParameter.getSourceFilepath());
        List<String> moduleApiFilepath = sourceFilepathList.stream()
                .flatMap(filepath -> {
                    try {
                        return GetFoldFileNames.readfileWithType(filepath, "java").stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .distinct()
                .collect(Collectors.toList());

        // 过滤掉测试相关的类文件
        batchSaveApiBasic(taskParameter, moduleApiFilepath, true);

        Log.info("源API信息提取完成");
    }

    private void extractTargetApiBasic() throws IOException {
        Log.info("开始提取目标项目API信息");

        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<String> targetFilepathList = Splitter.on(",").splitToList(taskParameter.getTargetFilepath());
        List<String> moduleApiFilepath = targetFilepathList.stream()
                .flatMap(filepath -> {
                    try {
                        return GetFoldFileNames.readfileWithType(filepath, "h").stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .distinct()
                .collect(Collectors.toList());
        batchSaveApiBasic(taskParameter, moduleApiFilepath, false);

        Log.info("目标项目API信息提取完成");
    }

    private void batchSaveApiBasic(TaskParameter taskParameter, List<String> moduleApiFilepath, boolean isSource) {
        moduleApiFilepath = filterTestFile(moduleApiFilepath);
        List<ApiBasic> apiBasics;
        if (isSource) {
            apiBasics = moduleApiFilepath.stream()
                    .flatMap(filepath -> parseSourceApiBasic(filepath, taskParameter).stream())
                    .collect(Collectors.toList());
        } else {
            apiBasics = moduleApiFilepath.stream()
                    .flatMap(filepath -> parseTargetApiBasic(filepath, taskParameter).stream())
                    .collect(Collectors.toList());
        }

        List<ApiBasic> apis = apiBasics.stream()
                .filter(this::filterNonApi)
                .collect(Collectors.toList());
        batchSave(apis);

        List<ApiBasic> nonApis = apiBasics.stream()
                .filter(x -> !filterNonApi(x))
                .collect(Collectors.toList());

        // TODO type改成枚举
        int type = isSource ? 3 : 4;
        nonApis.forEach(x -> x.setType(type));
        batchSave(nonApis);
    }

    private List<String> filterTestFile(List<String> moduleApiFilepath) {

        return moduleApiFilepath.stream()
                .filter(filepath -> {
                    String className = getClassNameByFilepath(filepath);
                    return !className.toLowerCase().startsWith("test")
                            && !className.toLowerCase().endsWith("test")
                            && !className.toLowerCase().startsWith("tests")
                            && !className.toLowerCase().endsWith("tests");
                }).collect(Collectors.toList());
    }

    /**
     * 过滤掉构造函数
     * 过滤掉get/set/toString  (会存在一点误伤，比如一些功能性api命名就是setxxx。更合理的方式是考虑代码实现情况)
     *
     * @param apiBasic
     * @return false表示要过滤掉
     */
    private boolean filterNonApi(ApiBasic apiBasic) {
        // 过滤掉构造函数
        String apiName = apiBasic.getApiName().toLowerCase();
        boolean isConstructApi = StringUtils.equals(apiName, apiBasic.getClassName().toLowerCase());
        if (isConstructApi) {
            return false;
        }

        // 过滤掉get/set/toString
        if (apiName.startsWith("get") || apiName.startsWith("set") || apiName.startsWith("tostring")) {
            return false;
        }

        //log
        return true;
    }

    private String getClassNameByFilepath(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return StringUtils.EMPTY;
        }
        //todo 需要优化路径解析，用system
        String[] split = filepath.split("/");
        String classFile = split[split.length - 1];
        String[] split1 = classFile.split("\\.");
        return split1[0];
    }


    private List<ApiBasic> parseSourceApiBasic(String filePath, TaskParameter taskParameter) {
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromFileName(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
        ParseTree parseTree = parser.compilationUnit();
        APIExtractorService apiExtractor = new APIExtractorService(taskParameter.getTaskId(), filePath);
        apiExtractor.visit(parseTree);

        return buildSourceApiBasic(taskParameter.getTaskId(), filePath, apiExtractor.getApiNames());
    }


    private List<ApiBasic> parseTargetApiBasic(String filepath, TaskParameter taskParameter) {
        String[] args = new String[]{taskParameter.getPythonBinPath(), taskParameter.getPythonCppExtractor(), filepath};

        List<String> resultLines = CallUtil.call(args);

        return buildTargetApiBasic(taskParameter.getTaskId(), filepath, resultLines);
    }

    private List<ApiBasic> buildSourceApiBasic(Integer taskId, String filepath, List<String> apiNames) {
        String className = fetchClassNameWithFilePath(filepath);
        return apiNames.stream()
                .map(apiName -> ApiBasic.builder()
                        .taskId(taskId)
                        .filepath(filepath)
                        .className(className)
                        .apiName(apiName)
                        .type(0)
                        .methodWordSequence(Joiner.on(",").join(Preprocess.generateWordSequence(apiName)))
                        .tokenSequence(Joiner.on(",").join(Preprocess.preprocess(apiName)))
                        .classNameTokenSequence(className)
                        .build())
                .collect(Collectors.toList());
    }

    private List<ApiBasic> buildTargetApiBasic(Integer taskId, String filepath, List<String> resultLines) {
        String apiLines = resultLines.stream().findFirst().orElse(StringUtils.EMPTY);
        List<String> apis = JsonUtil.jsonToList(apiLines, String.class);
        return apis.stream().map(line -> {
            String[] split = line.split(",");
            return ApiBasic.builder()
                    .taskId(taskId)
                    .filepath(filepath)
                    .className(split[0])
                    .apiName(split[1])
                    .type(1)
                    .methodWordSequence(Joiner.on(",").join(Preprocess.generateWordSequence(split[1])))
                    .tokenSequence(Joiner.on(",").join(Preprocess.preprocess(split[1])))
                    .classNameTokenSequence(Joiner.on(",").join(Preprocess.preprocess(split[0])))
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * CRUD
     **/
    public List<ApiBasic> selectByTaskId(Integer taskId) {
        List<ApiBasic> list = Lists.newArrayList();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao mapper = session.getMapper(ApiBasicDao.class);
            list = mapper.selectByTaskId(taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ApiBasic> selectByIds(List<Integer> ids) {
        List<ApiBasic> list = Lists.newArrayList();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao mapper = session.getMapper(ApiBasicDao.class);
            list = mapper.selectByIds(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public void batchSave(List<ApiBasic> apiBasics) {
        if (apiBasics == null || apiBasics.size() == 0) {
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao mapper = session.getMapper(ApiBasicDao.class);
            mapper.batchInsert(apiBasics);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update(ApiBasic apiBasic) {
        if (apiBasic == null) {
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao mapper = session.getMapper(ApiBasicDao.class);
            mapper.update(apiBasic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String fetchClassNameWithFilePath(String filePath) {
        String[] split = filePath.split("/");
        String classFileName = split[split.length - 1];
        return classFileName.split("\\.")[0];
    }


}
