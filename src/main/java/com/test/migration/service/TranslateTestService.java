package com.test.migration.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.migration.antlr.Java8Lexer;
import com.test.migration.antlr.Java8Parser;
import com.test.migration.dao.ApiBasicDao;
import com.test.migration.dao.TranslateTestDao;
import com.test.migration.entity.TaskParameter;
import com.test.migration.entity.po.ApiBasic;
import com.test.migration.entity.po.ApiMapping;
import com.test.migration.entity.po.TranslateTest;
import com.test.migration.service.invocation.ApiInvocationVisitor;
import com.test.migration.service.translate.MethodVisitor;
import com.test.migration.service.translate.common.MethodDeclarationTranslate;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import utils.GetFoldFileNames;
import utils.JsonUtil;
import utils.MyBatisUtil;
import utils.ResourceReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TranslateTestService {

    private final ApiBasicService apiBasicService = new ApiBasicService();
    private final ApiMappingService apiMappingService = new ApiMappingService();

    public void generateTargetApiTest() {
        TaskParameter taskParameter = ResourceReader.getTaskParameter();
        List<ApiMapping> apiMappings = apiMappingService.selectByTaskIdAndType(taskParameter.getTaskId(), 1);
        List<Integer> targetApiIds = apiMappings.stream().map(ApiMapping::getId)
                .collect(Collectors.toList());
        List<ApiBasic> apiBasics = apiBasicService.selectByIds(targetApiIds);

        try {
            List<String> allTargetSourceCodeFilepathList = GetFoldFileNames.readfile(taskParameter.getTargetSourceCodeFilepath());
            Map<String, List<ApiBasic>> fileApiBasicMap = apiBasics.stream().collect(Collectors.groupingBy(ApiBasic::getFilepath));

            List<TranslateTest> translateTests = Lists.newArrayList();
            // 所有的api，以所在文件为单位（key）进行处理，避免文件多次解析
            fileApiBasicMap.forEach((filepath, fileApis) -> {
                Map<String, List<Integer>> testMethodInvokeApiMap = getTestMethodInvocationMap(allTargetSourceCodeFilepathList, filepath, fileApis);
                translateTests.add(TranslateTest.builder()
                        .taskId(taskParameter.getTaskId())
                        .testFilepath(filepath)
                        .testMethodApiInvocation(JsonUtil.objectToJson(testMethodInvokeApiMap))
                        .build());
            });

            batchSave(translateTests);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 代码转换：
     * 候选ut文件，按照ut中需要做迁移的test method，以文件为单位进行转换
     */
    public void translateCode() {
        TaskParameter taskParameter = ResourceReader.getTaskParameter();
        List<TranslateTest> translateTests = selectByTaskId(taskParameter.getTaskId());
        translateTests.forEach(translateTest -> {
            String translateCode = translateFile(translateTest);
            translateTest.setTranslateCode(translateCode);
            update(translateTest);
        });
    }

    private String translateFile(TranslateTest translateTest) {
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromFileName(translateTest.getTestFilepath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, List<String>> testMethodApiInvocationMap = JsonUtil.jsonToPojo(translateTest.getTestMethodApiInvocation(), Map.class);
        Map<String, ParserRuleContext> parserRuleContextMap = Maps.newHashMap();
        for (String testMethodName : testMethodApiInvocationMap.keySet()) {
            parserRuleContextMap.put(testMethodName, null);
        }
        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
        ParseTree parseTree = parser.compilationUnit();
        MethodVisitor methodVisitor = new MethodVisitor();
        methodVisitor.methodBlockMap = parserRuleContextMap;
        methodVisitor.visit(parseTree);
        MethodDeclarationTranslate translate = new MethodDeclarationTranslate();

        List<String> translateCodes = Lists.newArrayList();
        methodVisitor.methodBlockMap.forEach((k, v) -> {
            translateCodes.add(translate.translateMethodDeclaration(v));
        });

        return JsonUtil.objectToJson(translateCodes);
    }

    @NotNull
    private Map<String, List<Integer>> getTestMethodInvocationMap(List<String> allTargetSourceCodeFilepathList, String filepath,
                                                                  List<ApiBasic> fileApis) {
        String testFilepath = getTestFilepath(allTargetSourceCodeFilepathList, filepath);
        // antlr解析每个testFile，获取所有testMethod还有对应每个testMethod调用的方法列表（map结构）
        // caller：test方法，callee：test方法中调用的其它方法
        // 解析文件中的test方法，获取每个test方法和该test方法中调用了哪些方法
        Map<String, Set<String>> testMethodInvocationMap = parseMethods(testFilepath);
        // key: testMethodName, value:call api ids
        Map<String, List<Integer>> testMethodInvokeApiMap = Maps.newHashMap();
        testMethodInvocationMap.forEach((testMethodName, callee) -> {
            List<Integer> apiIds = fileApis.stream()
                    .filter(api -> callee.contains(api.getApiName()))
                    .map(ApiBasic::getId)
                    .collect(Collectors.toList());
            if (apiIds.size() > 0) {
                testMethodInvokeApiMap.put(testMethodName, apiIds);
            }
        });

        return testMethodInvokeApiMap;
    }


    @NotNull
    private String getTestFilepath(List<String> allTargetSourceCodeFilepathList, String filepath) {
        String className = getClassNameByFilepath(filepath);
        String prefix = "Test" + className;
        String suffix = className + "Test";

        // 遍历apiBasics， apiBasic中的文件，加上Test前后缀，然后从test文件中过滤出对应的testFile
        return allTargetSourceCodeFilepathList.stream()
                .filter(sourceFilepath -> sourceFilepath.contains(prefix) || sourceFilepath.contains(suffix))
                .findFirst().orElse(StringUtils.EMPTY);
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

    public static Map<String, Set<String>> parseMethods(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return Maps.newHashMap();
        }
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromFileName(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
        ParseTree parseTree = parser.compilationUnit();
        ApiInvocationVisitor androidMethodVisitor = new ApiInvocationVisitor();
        androidMethodVisitor.visit(parseTree);

        return androidMethodVisitor.getInvocationList().stream()
                .collect(Collectors.toMap(ApiInvocationVisitor.MethodInvocation::getCaller,
                        ApiInvocationVisitor.MethodInvocation::getCallee));
    }


    /** CRUD **/
    public void batchSave(List<TranslateTest> translateTests) {
        if (translateTests == null || translateTests.size() == 0) {
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            TranslateTestDao mapper = session.getMapper(TranslateTestDao.class);
            mapper.batchInsert(translateTests);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update(TranslateTest translateTest) {
        if (translateTest == null) {
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            TranslateTestDao mapper = session.getMapper(TranslateTestDao.class);
            mapper.update(translateTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TranslateTest> selectByTaskId(Integer taskId) {
        List<TranslateTest> list = null;

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            TranslateTestDao mapper = session.getMapper(TranslateTestDao.class);
            list = mapper.selectByTaskId(taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


}
