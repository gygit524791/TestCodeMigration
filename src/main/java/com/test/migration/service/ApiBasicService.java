package com.test.migration.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.test.migration.antlr.Java8Lexer;
import com.test.migration.antlr.Java8Parser;
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
import org.apache.lucene.util.CollectionUtil;
import org.jetbrains.annotations.NotNull;
import utils.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ApiBasicService {

    /**
     * 提取api基础信息
     */
    public void extractApiBasic() {
        try {
            // java文件提取api基础信息
            extractAndroidApiBasic();

            // cpp文件提取api基础信息
            extractHarmonyApiBasic();

            // 生成api的vector并保存
            generateTokenVector();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateTokenVector() {
        TaskParameter taskParameter = ResourceReader.getTaskParameter();
        // 根据任务id查所有token序列
        List<ApiBasic> apiBasics = selectByTaskId(taskParameter.getTaskId());

        // 生成token文本语料库
        generateTokenCorpus(apiBasics, taskParameter);

        apiBasics.forEach(api -> {
            String tokenVec = generateTokenVec(api.getTokenSequence(), taskParameter);
            api.setTokenVector(tokenVec);
            update(api);
        });
    }


    private void extractAndroidApiBasic() throws IOException {
        TaskParameter taskParameter = ResourceReader.getTaskParameter();
        List<String> moduleApiFilepath = GetFoldFileNames.readfileWithType(taskParameter.getTargetFilepath(), "java");
        // 过滤掉测试相关的类文件
//        moduleApiFilepath = filterTestFile(moduleApiFilepath);
        List<ApiBasic> apiBasics = moduleApiFilepath.stream()
                .flatMap(filepath -> parseAndroidApiBasic(filepath, taskParameter).stream())
                .collect(Collectors.toList());
        batchSave(apiBasics);
    }

    private void extractHarmonyApiBasic() throws IOException {
        TaskParameter taskParameter = ResourceReader.getTaskParameter();
        List<String> moduleApiFilepath = GetFoldFileNames.readfileWithType(taskParameter.getSourceFilepath(), "h");
        List<ApiBasic> apiBasics = moduleApiFilepath.stream()
                .flatMap(filepath -> parseHarmonyApiBasic(filepath, taskParameter).stream())
                .collect(Collectors.toList());
        batchSave(apiBasics);
    }

    private List<ApiBasic> parseAndroidApiBasic(String filePath, TaskParameter taskParameter) {
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
        TaskParameter.PythonScriptPath pythonScriptPath = JsonUtil.jsonToPojo(taskParameter.getPythonScriptPath(),
                TaskParameter.PythonScriptPath.class);
        if (pythonScriptPath == null) {
            return Lists.newArrayList();
        }

        String[] args = new String[]{taskParameter.getPythonBinPath(), pythonScriptPath.getCppExtractor(), filepath};

        List<String> resultLines = CallPythonUtil.call(args);

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
                    .type(1)
                    .methodWordSequence(Joiner.on(",").join(Preprocess.generateWordSequence(split[1])))
                    .tokenSequence(Joiner.on(",").join(Preprocess.preprocess(split[1])))
                    .build();
        }).collect(Collectors.toList());
    }


    /**
     * 根据api序列生成文本语料库，用于生成vec
     */
    private void generateTokenCorpus(List<ApiBasic> apiBasics, TaskParameter taskParameter) {
        TaskParameter.PythonScriptPath pythonScriptPath = JsonUtil.jsonToPojo(taskParameter.getPythonScriptPath(),
                TaskParameter.PythonScriptPath.class);
        if (pythonScriptPath == null) {
            return;
        }

        // 取出所有token序列
        List<String> tokens = apiBasics.stream()
                .map(api -> api.getTokenSequence().replace(",", " "))
                .collect(Collectors.toList());

        // 填充token语料库文件
        fillCorpus(taskParameter, tokens);

        // 调用python生成词向量文件
        String[] args = new String[]{
                taskParameter.getPythonBinPath(),
                pythonScriptPath.getWordVec(),
                taskParameter.getCorpusFilepath(),
                taskParameter.getWordVecModelFilepath()
        };
        CallPythonUtil.call(args);
    }

    private String generateTokenVec(String tokenSequence, TaskParameter taskParameter) {
        TaskParameter.PythonScriptPath pythonScriptPath = JsonUtil.jsonToPojo(taskParameter.getPythonScriptPath(),
                TaskParameter.PythonScriptPath.class);
        if (pythonScriptPath == null) {
            return "";
        }

        String[] tokenArgs = new String[]{
                taskParameter.getPythonBinPath(),
                pythonScriptPath.getCalcTokenVec(),
                tokenSequence,
                taskParameter.getWordVecModelFilepath()
        };
        List<String> resultLines = CallPythonUtil.call(tokenArgs);
        return normalizeTokenVector(resultLines);
    }

    @NotNull
    private static String normalizeTokenVector(List<String> resultLines) {
        StringBuilder vecLine = new StringBuilder();
        for (String line : resultLines) {
            vecLine.append(" ").append(line);
        }
        String[] vecLineStr = vecLine.toString().trim().split(" ");
        StringBuilder normalVecLine = new StringBuilder();
        Arrays.stream(vecLineStr)
                .filter(StringUtils::isNotBlank)
                .forEach(lineStr -> normalVecLine.append(lineStr.trim()).append(","));

        // todo lowB实现 后续优化 1771,1236
        if (normalVecLine.length() == 0) {
            return StringUtils.EMPTY;
        }
        return normalVecLine.substring(0, normalVecLine.length() - 1).replace("[,", "[");
    }

    private void fillCorpus(TaskParameter taskParameter, List<String> tokens) {
        String corpusFilepath = taskParameter.getCorpusFilepath();
        try {
            FileWriteUtil.writeDataToFile(tokens, corpusFilepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** CRUD **/
    public List<ApiBasic> selectByTaskId(Integer taskId) {
        List<ApiBasic> list = null;

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            ApiBasicDao mapper = session.getMapper(ApiBasicDao.class);
            list = mapper.selectByTaskId(taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ApiBasic> selectByIds(List<Integer> ids) {
        List<ApiBasic> list = null;

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
