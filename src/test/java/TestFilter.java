import com.google.common.collect.Lists;
import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.*;
import com.test.migration.service.translate.bnf.common.method.MethodDeclarationTranslate;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestFilter {
    public static void main(String[] args) {
        MappingRuleLoader.load();
        CharStream inputStream = null;
        try {
            String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/migration/case/PhysicsAnimationLayoutTest.java";
            inputStream = CharStreams.fromFileName(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TranslateCodeCollector.init();
        Java8Parser parser = new Java8Parser(new CommonTokenStream(new Java8Lexer(inputStream)));
        ParseTree parseTree = parser.compilationUnit();
        String json = "{\"testArePropertiesAnimating\":[291],\"testCancelAllAnimations\":[304]}";
        TestCodeContext.init();
        TestCodeVisitor testCodeVisitor = new TestCodeVisitor();
        testCodeVisitor.visit(parseTree);
        Map<String, List<Integer>> map = JsonUtil.jsonToPojo(json, Map.class);
        List<String> migrateTestMethods = map == null ? Lists.newArrayList() : Lists.newArrayList(map.keySet());

        TestCodeFilter.filterMethodDeclarationCtxList(migrateTestMethods);

        System.out.println("====translate MethodDeclaration====");
        MethodDeclarationTranslate methodDeclarationTranslate = new MethodDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            System.out.println(methodDeclarationTranslate.translateMethodDeclaration(parserRuleContext));
            System.out.println("---------------------------");
        }


    }
}
