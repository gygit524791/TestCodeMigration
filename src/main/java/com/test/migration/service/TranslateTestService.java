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
            // filepath：所有目标api，以所在文件为单位（key）进行处理，避免文件多次解析
            // allTargetSourceCodeFilepathList：该模块对应的测试方法所在文件路径
            Set<String> tmpTestFilepathSets = Sets.newHashSet();
            fileApiBasicMap.forEach((filepath, fileApis) -> {
                List<String> testFilepathList = filterTestFilepath(allTargetSourceCodeFilepathList, filepath);
                for (String testFilepath : testFilepathList) {
                    // 避免同一个测试文件重复解析
                    if (tmpTestFilepathSets.contains(testFilepath)) {
                        continue;
                    }
                    tmpTestFilepathSets.add(testFilepath);

                    Map<String, List<Integer>> testMethodInvokeApiMap = getTestMethodInvocationMap(testFilepath, fileApis);
                    // 存在api映射关系，但是找不到目标api合适的测试类，直接跳过
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
     * 代码转换：
     * 候选ut文件，按照ut中需要做迁移的test method，以文件为单位进行转换
     */
    public void translateCode() {
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<TranslateTest> translateTests = selectByTaskId(taskParameter.getTaskId());
        MappingRuleLoader.load();

        // 以每个test文件为单位进行代码转换
        Log.info("总计[" + translateTests.size() + "]个测试文件需要处理代码迁移");
        translateTests.forEach(this::doTranslate);
    }

    private void doTranslate(TranslateTest translateTest) {
        // todo test
        String filep = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/example/android/case1/A.java";
//        String filep = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/example/android/test/ValueAnimatorTests.java";
        translateTest.setTestFilepath(filep);

        Log.info("开始处理[" + translateTest.getTestFilepath() + "]测试文件的测试迁移");

        fillTestCodeContext(translateTest);

        // 打印typeName
        printTypeNameMapMessage();

        // 过滤需要转换的test method
        filterNeedTranslateTestMethod(translateTest);

        // 代码转换
        translate();

        // 代码生成
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

        Log.info("当前处理的测试类名：" + TestCodeContext.className);
    }

    private static void filterNeedTranslateTestMethod(TranslateTest translateTest) {
        Map<String, List<Integer>> map = JsonUtil.jsonToPojo(translateTest.getTestMethodApiInvocation(), Map.class);
        List<String> migrateTestMethods = map == null ? Lists.newArrayList() : Lists.newArrayList(map.keySet());
        // 过滤掉不需要转换的test code
//        TestCodeFilter.filterMethodDeclarationCtxList(migrateTestMethods);
        // todo test code
        TestCodeFilter.filterMethodDeclarationCtxList(null);
    }

    private void translate() {
        Log.info("开始执行代码转换");

        TranslateCodeCollector.init();
        TranslateCodeCollector.className = TestCodeContext.className;

        // 类迁移
        ClassDeclarationTranslate classDeclarationTranslate = new ClassDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.classDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = classDeclarationTranslate.translateClassDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.formatMisMatchCodes(TranslateHint.misMatchCodes);

            TranslateCodeCollector.classDeclarationTranslateCodes.add(translateCode);
        }

        // 成员变量迁移
        FieldDeclarationTranslate fieldDeclarationTranslate = new FieldDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.fieldDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = fieldDeclarationTranslate.translateFieldDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.formatMisMatchCodes(TranslateHint.misMatchCodes);

            TranslateCodeCollector.fieldDeclarationTranslateCodes.add(translateCode);
        }

        // 方法迁移
        MethodDeclarationTranslate methodDeclarationTranslate = new MethodDeclarationTranslate();
        Log.info("methodDeclarationTranslate: " + TestCodeContext.methodDeclarationCtxList.size());

        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.MethodTranslateCode.methodStartLine = parserRuleContext.getStart().getLine();
            TranslateCodeCollector.MethodTranslateCode.methodEndLine = parserRuleContext.getStop().getLine();
            Log.info("当前方法开始行和结束行1：(" + TranslateCodeCollector.MethodTranslateCode.methodStartLine + ":" + TranslateCodeCollector.MethodTranslateCode.methodEndLine + ")");

            methodDeclarationTranslate.translateMethodDeclaration(parserRuleContext);
            // methodHeader信息
            // method blockStatement信息
            TranslateCodeCollector.MethodTranslateCode methodTranslateCode = new TranslateCodeCollector.MethodTranslateCode();
            methodTranslateCode.methodHeaderTranslateCode = TranslateCodeCollector.methodHeaderTranslateCode;
            methodTranslateCode.blockStatementTranslateCodes = TranslateCodeCollector.blockStatementTranslateCodes;
            TranslateCodeCollector.methodDeclarationTranslateCodes.add(methodTranslateCode);
            Log.info("当前方法开始行和结束行2：(" + TranslateCodeCollector.MethodTranslateCode.methodStartLine + ":" + TranslateCodeCollector.MethodTranslateCode.methodEndLine + ")");

            TranslateCodeCollector.MethodTranslateCode.clearMethod();
        }

        // 方法部分迁移
//        PartMigrationProcessor partMigrationProcessor = new PartMigrationProcessor();
//        TranslateCodeCollector.isFullTranslate = false; // 。。。
//        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
//            TranslateCodeCollector.PartMigrationMethodTranslateCode partMigrationMethodTranslateCode = new TranslateCodeCollector.PartMigrationMethodTranslateCode();
//            partMigrationMethodTranslateCode.translateCode = partMigrationProcessor.doPartMigrationTranslate(parserRuleContext);
//            TranslateCodeCollector.partMigrationMethodTranslateCodes.add(partMigrationMethodTranslateCode);
//        }

        Log.info("代码转换完成");
    }

    private Map<String, List<Integer>> getTestMethodInvocationMap(String testFilepath, List<ApiBasic> fileApis) {

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


    /**
     * 通过常规Test命名规则来获取test类：
     * AClass对应的测试类为：TestAClass 或者 AClassTest
     * <p>
     * 但是这个规则不一定找出所有的test，测试文件名不一定按照规范来设定，比如
     * <p>
     * AnimatorSet对应的测试类AnimatorSetActivityTest目前是匹配不出来的
     * 补充规则：为了匹配到这种命名，使用  ：
     * *（通配符）+类名+*（通配符）+Test（前后缀）的方式来匹配测试类文件
     *
     * @param filepath 目标api所在的文件路径
     */
    private List<String> filterTestFilepath(List<String> allTargetSourceCodeFilepathList, String filepath) {
        String className = getClassNameByFilepath(filepath);
        Pattern pattern = Pattern.compile("(.*)(" + className + ")(.*)");

        return allTargetSourceCodeFilepathList.stream()
                .filter(sourceFilepath -> {
                    String testClassFileName = getClassNameByFilepath(sourceFilepath);

                    // 必须带有test前后缀，否则视为非测试文件
                    String testFileName = testClassFileName.toLowerCase();
                    boolean isTestFile = testFileName.startsWith("test") || testFileName.endsWith("test")
                            || testFileName.startsWith("tests") || testFileName.endsWith("tests");
                    if (!isTestFile) {
                        return false;
                    }

                    // 正则匹配
                    Matcher matcher = pattern.matcher(testClassFileName);
                    return matcher.find();
                }).collect(Collectors.toList());
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
