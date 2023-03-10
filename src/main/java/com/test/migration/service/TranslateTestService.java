package com.test.migration.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.dao.TranslateTestDao;
import com.test.migration.entity.TaskParameter;
import com.test.migration.entity.po.ApiBasic;
import com.test.migration.entity.po.ApiMapping;
import com.test.migration.entity.po.TranslateTest;
import com.test.migration.service.invocation.ApiInvocationVisitor;
import com.test.migration.service.translate.*;
import com.test.migration.service.translate.bnf.common.method.MethodDeclarationTranslate;
import com.test.migration.service.translate.bnf.declaration.ClassDeclarationTranslate;
import com.test.migration.service.translate.bnf.declaration.FieldDeclarationTranslate;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import utils.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TranslateTestService {

    private final ApiBasicService apiBasicService = new ApiBasicService();
    private final ApiMappingService apiMappingService = new ApiMappingService();

    /**
     *
     */
    public void generateTargetApiTest() {
        Log.info("");
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<ApiMapping> apiMappings = apiMappingService.selectByTaskId(taskParameter.getTaskId());
        List<Integer> sourceApiIds = apiMappings.stream()
                .map(ApiMapping::getSourceApiId)
                .collect(Collectors.toList());
        if (sourceApiIds.size() == 0) {
            return;
        }

        List<ApiBasic> apiBasics = apiBasicService.selectByIds(sourceApiIds);

        try {
            String targetSourceCodeFilepath = taskParameter.getTargetSourceCodeFilepath();
            List<String> targetSourceCodeFilepathList = Splitter.on(",").splitToList(targetSourceCodeFilepath);
            List<String> allTargetSourceCodeFilepathList = targetSourceCodeFilepathList.stream()
                    .flatMap(filepath -> {
                        try {
                            return GetFoldFileNames.readfileWithType(filepath, "java").stream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, List<ApiBasic>> fileApiBasicMap = apiBasics.stream().collect(Collectors.groupingBy(ApiBasic::getFilepath));

            List<TranslateTest> translateTests = Lists.newArrayList();
            // filepath???????????????api??????????????????????????????key??????????????????????????????????????????
            // allTargetSourceCodeFilepathList???????????????????????????????????????????????????
            Set<String> tmpTestFilepathSets = Sets.newHashSet();
            fileApiBasicMap.forEach((filepath, fileApis) -> {
                List<String> testFilepathList = filterTestFilepath(allTargetSourceCodeFilepathList, filepath);
                for (String testFilepath : testFilepathList) {
                    // ???????????????????????????????????????
                    if (tmpTestFilepathSets.contains(testFilepath)) {
                        continue;
                    }
                    tmpTestFilepathSets.add(testFilepath);

                    Map<String, List<Integer>> testMethodInvokeApiMap = getTestMethodInvocationMap(testFilepath, fileApis);
                    // ??????api????????????????????????????????????api?????????????????????????????????
                    if (!testFilepathList.isEmpty() && !testMethodInvokeApiMap.keySet().isEmpty()) {
                        translateTests.add(TranslateTest.builder()
                                .taskId(taskParameter.getTaskId())
                                .testFilepath(testFilepath)
                                .testMethodApiInvocation(JsonUtil.objectToJson(testMethodInvokeApiMap))
                                .build());
                    }
                }
            });

            batchSave(translateTests);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ???????????????
     * ??????ut???????????????ut?????????????????????test method?????????????????????????????????
     */
    public void translateCode() {
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<TranslateTest> translateTests = selectByTaskId(taskParameter.getTaskId());
        MappingRuleLoader.load();

        // ?????????test?????????????????????????????????
        Log.info("??????[" + translateTests.size() + "]???????????????????????????????????????");
//        translateTests.forEach(this::doTranslate);
        // todo test
        doTranslate(translateTests.get(0));
    }

    private void doTranslate(TranslateTest translateTest) {
        // todo test
        String filep = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/example/android/case1/A.java";
//        String filep = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/example/android/test/ValueAnimatorTests.java";
        translateTest.setTestFilepath(filep);

        Log.info("????????????[" + translateTest.getTestFilepath() + "]???????????????????????????");

        fillTestCodeContext(translateTest);

        // ??????typeName
        printTypeNameMapMessage();

        // ?????????????????????test method
        filterNeedTranslateTestMethod(translateTest);

        // ????????????
        translate();

        // ????????????
        TranslateCodeGenerator.doGenerate();
    }

    private static void fillTestCodeContext(TranslateTest translateTest) {
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromFileName(translateTest.getTestFilepath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
        ParseTree parseTree = parser.compilationUnit();

        TestCodeContext.init();
        TestCodeVisitor testCodeVisitor = new TestCodeVisitor();
        testCodeVisitor.visit(parseTree);

        Log.info("??????????????????????????????" + TestCodeContext.className);
    }

    private static void filterNeedTranslateTestMethod(TranslateTest translateTest) {
        Map<String, List<Integer>> map = JsonUtil.jsonToPojo(translateTest.getTestMethodApiInvocation(), Map.class);
        List<String> migrateTestMethods = map == null ? Lists.newArrayList() : Lists.newArrayList(map.keySet());
        // ???????????????????????????test code
//        TestCodeFilter.filterMethodDeclarationCtxList(migrateTestMethods);
        // todo test code
        TestCodeFilter.filterMethodDeclarationCtxList(null);
    }

    private void translate() {
        Log.info("????????????????????????");

        TranslateCodeCollector.init();
        TranslateCodeCollector.className = TestCodeContext.className;

        // ?????????
        ClassDeclarationTranslate classDeclarationTranslate = new ClassDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.classDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = classDeclarationTranslate.translateClassDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.formatMisMatchCodes(TranslateHint.misMatchCodes);

            TranslateCodeCollector.classDeclarationTranslateCodes.add(translateCode);
        }

        // ??????????????????
        FieldDeclarationTranslate fieldDeclarationTranslate = new FieldDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.fieldDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = fieldDeclarationTranslate.translateFieldDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.formatMisMatchCodes(TranslateHint.misMatchCodes);

            TranslateCodeCollector.fieldDeclarationTranslateCodes.add(translateCode);
        }

        // ?????????????????????bs
        TranslateCodeCollector.blockStatementTranslateCodes = Lists.newArrayList();

        // ????????????
        MethodDeclarationTranslate methodDeclarationTranslate = new MethodDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.MethodTranslateCode.methodStartLine = parserRuleContext.getStart().getLine();
            TranslateCodeCollector.MethodTranslateCode.methodEndLine = parserRuleContext.getStop().getLine();

            methodDeclarationTranslate.translateMethodDeclaration(parserRuleContext);
            // methodHeader??????
            // method blockStatement??????
            TranslateCodeCollector.MethodTranslateCode methodTranslateCode = new TranslateCodeCollector.MethodTranslateCode();
            methodTranslateCode.methodHeaderTranslateCode = TranslateCodeCollector.methodHeaderTranslateCode;
            // ??????????????????blockStatement???????????????????????????blockStatement???
            methodTranslateCode.blockStatementTranslateCodes = filterRepeatSubBlockStatement(TranslateCodeCollector.blockStatementTranslateCodes);
            TranslateCodeCollector.methodDeclarationTranslateCodes.add(methodTranslateCode);

            TranslateCodeCollector.MethodTranslateCode.clearMethod();
        }

        // ??????????????????
        TranslateCodeCollector.isFullTranslate = false; // ?????????
        PartMigrationProcessor partMigrationProcessor = new PartMigrationProcessor();
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            TranslateCodeCollector.PartMigrationMethodTranslateCode partMigrationMethodTranslateCode = new TranslateCodeCollector.PartMigrationMethodTranslateCode();
            partMigrationMethodTranslateCode.translateCode = partMigrationProcessor.doPartMigrationTranslate(parserRuleContext);
            TranslateCodeCollector.partMigrationMethodTranslateCodes.add(partMigrationMethodTranslateCode);
        }

        Log.info("??????????????????");
    }

    /**
     * bs??????????????????????????????bs?????????
     *
     * @param blockStatementTranslateCodes
     * @return
     */
    private List<TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode> filterRepeatSubBlockStatement(
            List<TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode> blockStatementTranslateCodes) {

        // 1. ???bs???blockStatement????????????token????????????????????????
        List<TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode> sortedBlockStatementTranslateCodes =
                blockStatementTranslateCodes.stream()
                        .sorted(Comparator.comparing(TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode::getTokenStartIndex))
                        .collect(Collectors.toList());

        Set<Integer> removedBlockStatementTokenStartIndexSet = Sets.newHashSet();
        for (int i = 0; i < sortedBlockStatementTranslateCodes.size(); i++) {
            TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode blockStatementTranslateCode = sortedBlockStatementTranslateCodes.get(i);
            if (removedBlockStatementTokenStartIndexSet.contains(blockStatementTranslateCode.getTokenStartIndex())) {
                continue;
            }
            for (int j = i + 1; j < sortedBlockStatementTranslateCodes.size(); j++) {
                TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode subBlockStatementTranslateCode = sortedBlockStatementTranslateCodes.get(j);
                int bsStartIndex = blockStatementTranslateCode.getTokenStartIndex();
                int bsEndIndex = blockStatementTranslateCode.getTokenStopIndex();
                int subBsStartIndex = subBlockStatementTranslateCode.getTokenStartIndex();
                int subBsEndIndex = subBlockStatementTranslateCode.getTokenStopIndex();

                if (subBsStartIndex > bsStartIndex && subBsEndIndex < bsEndIndex) {
                    removedBlockStatementTokenStartIndexSet.add(subBsStartIndex);
                }
            }
        }

        return blockStatementTranslateCodes.stream()
                .filter(x -> !removedBlockStatementTokenStartIndexSet.contains(x.getTokenStartIndex()))
                .collect(Collectors.toList());
    }

    private Map<String, List<Integer>> getTestMethodInvocationMap(String testFilepath, List<ApiBasic> fileApis) {

        // antlr????????????testFile???????????????testMethod??????????????????testMethod????????????????????????map?????????
        // caller???test?????????callee???test??????????????????????????????
        // ??????????????????test?????????????????????test????????????test??????????????????????????????
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


    /**
     * ????????????Test?????????????????????test??????
     * AClass????????????????????????TestAClass ?????? AClassTest
     * <p>
     * ??????????????????????????????????????????test?????????????????????????????????????????????????????????
     * <p>
     * AnimatorSet??????????????????AnimatorSetActivityTest???????????????????????????
     * ???????????????????????????????????????????????????  ???
     * *???????????????+??????+*???????????????+Test????????????????????????????????????????????????
     *
     * @param filepath ??????api?????????????????????
     */
    private List<String> filterTestFilepath(List<String> allTargetSourceCodeFilepathList, String filepath) {
        String className = getClassNameByFilepath(filepath);
        Pattern pattern = Pattern.compile("(.*)(" + className + ")(.*)");

        return allTargetSourceCodeFilepathList.stream()
                .filter(sourceFilepath -> {
                    String testClassFileName = getClassNameByFilepath(sourceFilepath);

                    // ????????????test???????????????????????????????????????
                    String testFileName = testClassFileName.toLowerCase();
                    boolean isTestFile = testFileName.startsWith("test") || testFileName.endsWith("test")
                            || testFileName.startsWith("tests") || testFileName.endsWith("tests");
                    if (!isTestFile) {
                        return false;
                    }

                    // ????????????
                    Matcher matcher = pattern.matcher(testClassFileName);
                    return matcher.find();
                }).collect(Collectors.toList());
    }

    private String getClassNameByFilepath(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return StringUtils.EMPTY;
        }
        //todo ??????????????????????????????system
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
                .filter(x -> x.getCallee() != null && x.getCallee().size() > 0)
                .collect(Collectors.toMap(ApiInvocationVisitor.MethodInvocation::getCaller,
                        ApiInvocationVisitor.MethodInvocation::getCallee, (x, y) -> x));
    }


    private static void printTypeNameMapMessage() {
        // typename
        Log.info("TypeName Map message:");
        TestCodeContext.typeNameMap.forEach((key, value) -> {
            Log.info("typeName:" + key + ", typeValue:" + value);
        });
    }

    /**
     * CRUD
     **/
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
        List<TranslateTest> list = Lists.newArrayList();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            TranslateTestDao mapper = session.getMapper(TranslateTestDao.class);
            list = mapper.selectByTaskId(taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


}
