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
            // harmony cpp文件提取api基础信息
            extractSourceApiBasic();

            // android java文件提取api基础信息
            extractTargetApiBasic();

            // 生成api的vector并保存
            generateTokenVector();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateTokenVector() {
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        // 根据任务id查所有token序列
        List<ApiBasic> apiBasics = selectByTaskId(taskParameter.getTaskId());

        // 生成token文本语料库
        generateTokenCorpus(apiBasics, taskParameter);

        generateTokenVecDict(taskParameter);
        Log.info("生成api向量（用于mapping计算）完成");
    }

    private void generateTokenVecDict(TaskParameter taskParameter) {
        String[] tokenArgs = new String[]{
                taskParameter.getPythonBinPath(),
                taskParameter.getPythonCalcTokenVec(),
                String.valueOf(taskParameter.getTaskId()),
                taskParameter.getDbFilepath(),
                taskParameter.getWordVecModelFilepath(),
                taskParameter.getApiVectorDictFilepath(),
                taskParameter.getClassVectorDictFilepath()
        };
        CallUtil.call(tokenArgs);
    }

    private void extractTargetApiBasic() throws IOException {
        Log.info("开始提取目标API信息");
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<String> targetFilepathList = Splitter.on(",").splitToList(taskParameter.getTargetFilepath());
        List<String> moduleApiFilepath = targetFilepathList.stream()
                .flatMap(targetFilepath -> {
                    try {
                        return GetFoldFileNames.readfileWithType(targetFilepath, "java").stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .distinct()
                .collect(Collectors.toList());

        // 过滤掉测试相关的类文件
        moduleApiFilepath = filterTestFile(moduleApiFilepath);
        List<ApiBasic> apiBasics = moduleApiFilepath.stream()
                .flatMap(filepath -> parseTargetApiBasic(filepath, taskParameter).stream())
                .filter(this::filterUselessApi)
                .collect(Collectors.toList());

        batchSave(apiBasics);

        Log.info("目标API信息提取完成");
    }

    private void extractSourceApiBasic() throws IOException {
        Log.info("开始提取源项目API信息");

        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<String> sourceFilepathList = Splitter.on(",").splitToList(taskParameter.getSourceFilepath());
        List<String> moduleApiFilepath = sourceFilepathList.stream()
                .flatMap(sourceFilepath -> {
                    try {
                        return GetFoldFileNames.readfileWithType(sourceFilepath, "h").stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .distinct()
                .collect(Collectors.toList());
        moduleApiFilepath = filterTestFile(moduleApiFilepath);
        List<ApiBasic> apiBasics = moduleApiFilepath.stream()
                .flatMap(filepath -> parseHarmonyApiBasic(filepath, taskParameter).stream())
                .filter(this::filterUselessApi)
                .collect(Collectors.toList());
        batchSave(apiBasics);

        Log.info("源项目API信息提取完成");
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
    private boolean filterUselessApi(ApiBasic apiBasic) {
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


    private List<ApiBasic> parseTargetApiBasic(String filePath, TaskParameter taskParameter) {
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
        return apiExtractor.getApiBasics();
    }


    private List<ApiBasic> parseHarmonyApiBasic(String filepath, TaskParameter taskParameter) {
        String[] args = new String[]{taskParameter.getPythonBinPath(), taskParameter.getPythonCppExtractor(), filepath};

        List<String> resultLines = CallUtil.call(args);

        return buildHarmonyApiBasic(taskParameter.getTaskId(), filepath, resultLines);

    }

    private List<ApiBasic> buildHarmonyApiBasic(Integer taskId, String filepath, List<String> resultLines) {
        String apiLines = resultLines.stream().findFirst().orElse(StringUtils.EMPTY);
        List<String> apis = JsonUtil.jsonToList(apiLines, String.class);
        return apis.stream().map(line -> {
            String[] split = line.split(",");
            return ApiBasic.builder()
                    .taskId(taskId)
                    .filepath(filepath)
                    .className(split[0])
                    .apiName(split[1])
                    .type(0)
                    .methodWordSequence(Joiner.on(",").join(Preprocess.generateWordSequence(split[1])))
                    .tokenSequence(Joiner.on(",").join(Preprocess.preprocess(split[1])))
                    .classNameTokenSequence(Joiner.on(",").join(Preprocess.preprocess(split[0])))
                    .build();
        }).collect(Collectors.toList());
    }


    /**
     * 根据api序列生成文本语料库，用于生成vec
     */
    private void generateTokenCorpus(List<ApiBasic> apiBasics, TaskParameter taskParameter) {
        // 取出所有token序列
        List<String> tokens = Lists.newArrayList();
        tokens.addAll(apiBasics.stream().map(api -> api.getTokenSequence().replace(",", " ")).collect(Collectors.toList()));
        tokens.addAll(apiBasics.stream().map(api -> api.getClassNameTokenSequence().replace(",", " ")).collect(Collectors.toList()));

        // 填充token语料库文件
        fillCorpus(taskParameter, tokens);

        // 调用python生成词向量文件
        String[] args = new String[]{
                taskParameter.getPythonBinPath(),
                taskParameter.getPythonWordVec(),
                taskParameter.getCorpusFilepath(),
                taskParameter.getWordVecModelFilepath()
        };
        CallUtil.call(args);
    }

    private void fillCorpus(TaskParameter taskParameter, List<String> tokens) {
        String corpusFilepath = taskParameter.getCorpusFilepath();
        try {
            FileWriteUtil.writeDataToFile(tokens, corpusFilepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
