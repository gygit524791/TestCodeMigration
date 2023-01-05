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
    public void translateTest() {
        MappingRuleLoader.load();

        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/example/android/test/ValueAnimatorTests.java";
//        String filepath = "/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/translate/source/BDemo.java";
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
//        TestCodeContext.filter();

        testCodeVisitor.getTypeNameMap().put("mActivityRule", "ActivityTestRule<AnimatorSetActivity>");
        ReplaceRuleService.typeNameMap = testCodeVisitor.getTypeNameMap();

//        ReplaceRuleService.typeNameMap.forEach((k, v) -> {
//            System.out.println(k + ":" + v);
//        });
        translate();
//        print();
    }

    private static void translate() {
        TranslateCodeCollector.init();
        TranslateCodeCollector.className = TestCodeContext.className;

        FieldDeclarationTranslate fieldDeclarationTranslate = new FieldDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.fieldDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = fieldDeclarationTranslate.translateFieldDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.misMatchCodes;
            TranslateCodeCollector.fieldDeclarationTranslateCodes.add(translateCode);
        }

        MethodDeclarationTranslate methodDeclarationTranslate = new MethodDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.methodDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.MethodTranslateCode.methodStartLine = parserRuleContext.getStart().getLine();
            TranslateCodeCollector.MethodTranslateCode.methodEndLine = parserRuleContext.getStop().getLine();

            methodDeclarationTranslate.translateMethodDeclaration(parserRuleContext);
            // methodHeader信息
            // method blockStatement信息
            TranslateCodeCollector.MethodTranslateCode methodTranslateCode = new TranslateCodeCollector.MethodTranslateCode();
            methodTranslateCode.methodHeaderTranslateCode = TranslateCodeCollector.methodHeaderTranslateCode;
            methodTranslateCode.blockStatementTranslateCodes = TranslateCodeCollector.blockStatementTranslateCodes;
            TranslateCodeCollector.methodDeclarationTranslateCodes.add(methodTranslateCode);

            TranslateCodeCollector.MethodTranslateCode.clearMethodLine();
        }

        ClassDeclarationTranslate classDeclarationTranslate = new ClassDeclarationTranslate();
        for (ParserRuleContext parserRuleContext : TestCodeContext.classDeclarationCtxList) {
            TranslateHint.init();
            TranslateCodeCollector.TranslateCode translateCode = new TranslateCodeCollector.TranslateCode();
            translateCode.translateCode = classDeclarationTranslate.translateClassDeclaration(parserRuleContext);
            translateCode.misMatchCodes = TranslateHint.misMatchCodes;
            TranslateCodeCollector.classDeclarationTranslateCodes.add(translateCode);
        }
    }

    public static void print() {
        System.out.println("===fieldDeclarationTranslateCodes===");
        for (TranslateCodeCollector.TranslateCode translateCode : TranslateCodeCollector.fieldDeclarationTranslateCodes) {
            System.out.println(translateCode.translateCode);
            if (translateCode.misMatchCodes.size() > 0) {
                System.out.println("----hint start----");
                translateCode.misMatchCodes.forEach(System.out::println);
                System.out.println("----hint end----");
            }
        }

        System.out.println("===methodDeclarationTranslateCodes===");
        int i = 1;
        for (TranslateCodeCollector.MethodTranslateCode tc : TranslateCodeCollector.methodDeclarationTranslateCodes) {
            System.out.println();
            System.out.println();
            System.out.println("method[" + i + "]    start:");

            System.out.println("header: ");
            System.out.println(tc.methodHeaderTranslateCode.translateCode);
            if (tc.methodHeaderTranslateCode.misMatchCodes.size() > 0) {
                System.out.println("----hint start----");
                tc.methodHeaderTranslateCode.misMatchCodes.forEach(System.out::println);
                System.out.println("----hint end----");
            }

            System.out.println("blockStatement: ");
            for (TranslateCodeCollector.MethodTranslateCode.BlockStatementTranslateCode bs : tc.blockStatementTranslateCodes) {
                System.out.println(bs.translateCode);
                if (bs.misMatchCodes.size() > 0) {
                    System.out.println("----hint start----");
                    bs.misMatchCodes.forEach(System.out::println);
                    System.out.println("----hint end----");
                }
            }

            System.out.println("method[" + i + "]    end:");
            System.out.println();
            System.out.println();
            i++;
        }

        System.out.println("===classDeclarationTranslateCodes===");
        for (TranslateCodeCollector.TranslateCode translateCode : TranslateCodeCollector.classDeclarationTranslateCodes) {
            System.out.println(translateCode.translateCode);
            if (translateCode.misMatchCodes.size() > 0) {
                System.out.println("----hint start----");
                translateCode.misMatchCodes.forEach(System.out::println);
                System.out.println("----hint end----");
            }
        }
    }
}