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
import org.jetbrains.annotations.NotNull;
import utils.GetFoldFileNames;
import utils.JsonUtil;
import utils.MyBatisUtil;
import utils.TaskParameterReader;

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
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        List<ApiMapping> apiMappings = apiMappingService.selectByTaskId(taskParameter.getTaskId());
        List<Integer> targetApiIds = apiMappings.stream()
                .map(ApiMapping::getTargetApiId)
                .collect(Collectors.toList());
        if (targetApiIds.size() == 0) {
            return;
        }

        List<ApiBasic> apiBasics = apiBasicService.selectByIds(targetApiIds);

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
        // 以文件为代码进行转换
        translateTests.forEach(translateTest -> {
//            String translateCode = translateFile(translateTest);
            translateTest.setTranslateCode("translateCode");
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

//        Map<String, List<String>> testMethodApiInvocationMap = JsonUtil.jsonToPojo(translateTest.getTestMethodApiInvocation(), Map.class);
//        Map<String, ParserRuleContext> parserRuleContextMap = Maps.newHashMap();
//        for (String testMethodName : testMethodApiInvocationMap.keySet()) {
//            parserRuleContextMap.put(testMethodName, null);
//        }
//
        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
        ParseTree parseTree = parser.compilationUnit();

        TestCodeContext.init();
        TestCodeVisitor testCodeVisitor = new TestCodeVisitor();
        testCodeVisitor.visit(parseTree);
        TestCodeContext.filter();

        TestCodeContext.ClassMemberDeclaration.classes.forEach(System.out::println);
        TestCodeContext.ClassMemberDeclaration.fields.forEach(System.out::println);


        testCodeVisitor.getTypeNameMap().put("mActivityRule", "ActivityTestRule<AnimatorSetActivity>");
        ReplaceRuleService.typeNameMap = testCodeVisitor.getTypeNameMap();

        System.out.println(TestCodeContext.className);

        System.out.println("====translate FieldDeclaration====");
        FieldDeclarationTranslate fieldDeclarationTranslate = new FieldDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.fieldDeclarationCtxList) {
            System.out.println(fieldDeclarationTranslate.translateFieldDeclaration(parserRuleContext));
            System.out.println("---------------------------");
        }

        System.out.println("====translate MethodDeclaration====");
        MethodDeclarationTranslate methodDeclarationTranslate = new MethodDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            System.out.println(methodDeclarationTranslate.translateMethodDeclaration(parserRuleContext));
            System.out.println("---------------------------");
        }

        System.out.println("====translate ClassDeclaration====");
        ClassDeclarationTranslate classDeclarationTranslate = new ClassDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.classDeclarationCtxList) {
            System.out.println(classDeclarationTranslate.translateClassDeclaration(parserRuleContext));
            System.out.println("---------------------------");
        }

        System.out.println("====translate TestMethodDeclaration====");
        for (ParserRuleContext parserRuleContext : TestCodeContext.testMethodDeclarationCtxList) {
            System.out.println(methodDeclarationTranslate.translateMethodDeclaration(parserRuleContext));
            System.out.println("---------------------------");
        }

//        System.out.println("==== MISMATCH HINT=======");
//        if (TranslateHint.MisMatchInfo.codes.size() > 0) {
//            System.out.println(TranslateHint.MisMatchInfo.MIS_MATCH_TIPS);
//            TranslateHint.MisMatchInfo.codes.forEach(System.out::println);
//        }

//        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
//        ParseTree parseTree = parser.compilationUnit();
//        TestCodeVisitor testCodeVisitor = new TestCodeVisitor();
//        testCodeVisitor.setParserRuleContextMap(parserRuleContextMap);
//        testCodeVisitor.visit(parseTree);

        // 代码转换的前置条件：初始化replace的typeNameMap
        // todo demo 后面做成自动化
//        testCodeVisitor.getTypeNameMap().put("mActivityRule","ActivityTestRule<AnimatorSetActivity>");
//        ReplaceRuleService.typeNameMap = testCodeVisitor.getTypeNameMap();

        // 代码转换
//        MethodDeclarationTranslate translate = new MethodDeclarationTranslate();
//        List<String> translateCodes = Lists.newArrayList();
//        testCodeVisitor.parserRuleContextMap.forEach((k, v) -> {
//            translateCodes.add(translate.translateMethodDeclaration(v));
//        });

//        return JsonUtil.objectToJson(translateCodes);

        return null;
    }

    @NotNull
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
     * 但是这个规则不一定找出所有的test，比如AClass对应的测试类命名是其它风格
     * <p>
     * 补充规则：AnimatorSet对应的测试类AnimatorSetActivityTest目前是匹配不出来的，为了匹配到这种命名，使用  ：
     * *（通配符）+类名+*（通配符）+Test（前后缀）的方式来匹配测试类文件
     *
     * @param allTargetSourceCodeFilepathList
     * @param filepath 目标api所在的文件路径
     * @return
     */
    @NotNull
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
