import com.test.migration.antlr.java.Java8Lexer;
import com.test.migration.antlr.java.Java8Parser;
import com.test.migration.service.translate.*;
import com.test.migration.service.translate.bnf.declaration.ClassDeclarationTranslate;
import com.test.migration.service.translate.bnf.declaration.FieldDeclarationTranslate;
import com.test.migration.service.translate.bnf.common.method.MethodDeclarationTranslate;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.testng.annotations.Test;

import java.io.IOException;

public class TranslateTest {

    @Test
    public void test1() {
        MappingRuleLoader.load();

//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/translate/source/ValueAnimatorTests.java";
        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/translate/source/ADemo.java";
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromFileName(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

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



        System.out.println("==== MISMATCH HINT=======");
        if (TranslateHint.MisMatchInfo.codes.size() > 0) {
            System.out.println(TranslateHint.MisMatchInfo.MIS_MATCH_TIPS);
            TranslateHint.MisMatchInfo.codes.forEach(System.out::println);
        }
        // 代码转换
//        ClassDeclarationTranslate translate = new ClassDeclarationTranslate();

    }

}
