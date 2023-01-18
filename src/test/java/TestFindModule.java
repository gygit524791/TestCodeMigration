import com.google.common.base.Joiner;
import utils.GetFoldFileNames;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestFindModule {

    private static void testFindModule(String afilepath, String hfilepath, String type) throws IOException {
        List<String> readfile = GetFoldFileNames.readfile(afilepath);

        List<String> files = readfile.stream()
                .map(f -> {
                    String[] split = f.split("/");
                    List<String> collect = Arrays.stream(split).collect(Collectors.toList());
                    StringBuilder s = new StringBuilder();
                    for (int i = 0; i < collect.size() - 1; i++) {
                        s.append(collect.get(i) + "/");
                    }
                    return s.toString();
                })
                .distinct()
                .filter(file -> file.contains(type))
                .collect(Collectors.toList());

        if (type.equals("form")) {
            return;
        }
        List<String> moduleFiles = files.stream()
                .filter(x -> !x.contains("test"))
                .collect(Collectors.toList());
        List<String> testFiles = files.stream()
                .filter(x -> x.contains("test"))
                .collect(Collectors.toList());

//        if (moduleFiles.size() > 0 && testFiles.size() > 0) {
//            System.out.println(type + "---------start---------");
//            System.out.println("harmony:");
//            System.out.println(hfilepath + "/" + type);
//            System.out.println("android:");
//            moduleFiles.forEach(System.out::println);
//            System.out.println("====test====");
//            testFiles.forEach(System.out::println);
//            System.out.println(type + "---------end---------");
//            System.out.println();
//        }

        if (moduleFiles.size() > 0 && testFiles.size() > 0) {
            System.out.println(type + "---------start---------");
            System.out.println("harmony:");
            System.out.println(hfilepath + "/" + type);
            System.out.println("android:");
            String ads = Joiner.on(",").join(moduleFiles);
            System.out.println(ads);

            System.out.println("====test====");
            String tests = Joiner.on(",").join(testFiles);
            System.out.println(tests);
            System.out.println(type + "---------end---------");
            System.out.println();
        }

    }


    /***
     *  TODO 提供跑数两种方式
     *
     *  1. 分别指定两个系统的大模块目录（比如android 的 platform_frameworks_base 和 arkui_ace_engine-master/frameworks/core）
     *  自行按子模块进行切分，然后每个子模块进行跑数
     *
     *  问题：数据量很大，跑的很慢，不利于快速出结果和分析
     *
     *  2. 分别指定两个系统子模块的目录（比如anmiation）
     *  需要多次手工来完成每个模块的跑数
     *
     *  1和2同时存在优先选取2，中间抽出一层来处理这个逻辑
     *
     *  存在的问题：1是批量处理（apimapping，translate），2是单次处理，不太好统一。
     *
     *  testFiles：按目前的逻辑自动找，不需要人工指定
     */
    public static void main(String[] args) {
        String afilepath = "/Users/gaoyi/IdeaProjects/staticCodeAnalysis/doc/android/source/platform_frameworks_base";

        String hfilepath = "/Users/gaoyi/IdeaProjects/staticCodeAnalysis/doc/harmony/source/arkui_ace_engine-master/frameworks/core";
        File file = new File(hfilepath);
        if (file.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(file.list()))
                    .filter(x -> !x.contains("."))
                    .filter(x -> !x.contains("test"))
                    .forEach(type -> {
                        try {
                            testFindModule(afilepath, hfilepath, type);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }


    }


}
