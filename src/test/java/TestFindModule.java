import utils.GetFoldFileNames;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestFindModule {

    private static void testFindModule(String afilepath, String hfilepath, String type) throws IOException {
        GetFoldFileNames getFoldFileNames = new GetFoldFileNames();
        List<String> readfile = getFoldFileNames.readfile(afilepath);

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

        if(type.equals("form")){
            return;
        }
        List<String> moduleFiles = files.stream()
                .filter(x -> !x.contains("test"))
                .collect(Collectors.toList());
        List<String> testFiles = files.stream()
                .filter(x -> x.contains("test"))
                .collect(Collectors.toList());

        if(moduleFiles.size()>0 && testFiles.size()>0){
            System.out.println(type+"---------start---------");
            System.out.println("harmony:");
            System.out.println(hfilepath+"/"+type);
            System.out.println("android:");
            moduleFiles.forEach(System.out::println);
            System.out.println("====test====");
            testFiles.forEach(System.out::println);
            System.out.println(type+"---------end---------");
            System.out.println();
        }

    }

    public static void main(String[] args) {
        String afilepath = "/Users/gaoyi/IdeaProjects/staticCodeAnalysis/doc/android/source/platform_frameworks_base";

        String hfilepath = "/Users/gaoyi/IdeaProjects/staticCodeAnalysis/doc/harmony/source/arkui_ace_engine-master/frameworks/core/";
        File file = new File(hfilepath);
        if(file.isDirectory()){
            Arrays.stream(Objects.requireNonNull(file.list()))
                    .filter(x->!x.contains("."))
                    .filter(x->!x.contains("test"))
                    .forEach(type-> {
                        try {
                            testFindModule(afilepath,hfilepath, type);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }


    }


}
