import com.google.common.collect.Sets;
import com.test.migration.entity.po.ApiBasic;
import com.test.migration.entity.po.ApiMapping;
import com.test.migration.entity.po.TranslateTest;
import com.test.migration.service.ApiBasicService;
import com.test.migration.service.ApiMappingService;
import com.test.migration.service.TranslateTestService;
import utils.FileReadUtil;
import utils.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApiNum {
    public static void main(String[] args) throws IOException {
//        TranslateTestService translateTestService = new TranslateTestService();
//        List<TranslateTest> translateTests = translateTestService.selectByTaskId(1);
//       Set<String> testNumSet = Sets.newHashSet();
//       Set<Integer> apiNumSet = Sets.newHashSet();
//        for (TranslateTest translateTest:
//        translateTests) {
//            Map<String, List<Integer>> map = JsonUtil.jsonToPojo(translateTest.getTestMethodApiInvocation(),Map.class);
//            map.forEach((key,value)->{
//                testNumSet.add(key);
//                apiNumSet.addAll(value);
//
//            });
//        }
//
//        System.out.println(testNumSet.size());
//        System.out.println(apiNumSet.size());


        List<String> strings = FileReadUtil.readFile("/Users/gaoyi/IdeaProjects/TestMigrationV2/demo/android/animation");


    }

}
