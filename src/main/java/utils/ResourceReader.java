package utils;

import com.test.migration.entity.TaskParameter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceReader {

    private static TaskParameter taskParameter;

    @SneakyThrows
    public static TaskParameter getTaskParameter() {
        if (taskParameter == null) {
            InputStream in = ClassLoader.getSystemResourceAsStream("task.properties");
            Properties p = new Properties();
            p.load(in);
            return TaskParameter.builder()
                    .taskId(Integer.parseInt(p.getProperty("taskId")))
                    .pythonBinPath("")
                    .build();
        }

        return taskParameter;
    }

}
