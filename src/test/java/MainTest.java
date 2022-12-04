import com.test.migration.entity.TaskParameter;
import utils.CallPythonUtil;
import utils.TaskParameterReader;

import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        extracted();
        extracted();
//        test();
    }

    private static void extracted() {
        TaskParameter taskParameter = TaskParameterReader.getTaskParameter();
        String[] pyArgs = new String[]{
                taskParameter.getPythonBinPath(),
                taskParameter.getPythonWordVec(),
                String.valueOf(taskParameter.getTaskId()),
                taskParameter.getDbFilepath()
        };
        List<String> call = CallPythonUtil.call(pyArgs);
        call.forEach(System.out::println);
    }

//    static void test(){
//        Jedis jedis = new Jedis("127.0.0.1", 6379);
//        jedis.set("1","2");
//        System.out.println(jedis.get("1"));
//        System.out.println(jedis.get("2"));
//    }
}
