package com.test.migration.service.translate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MappingRule {

    /**
     * 简化映射规则
     * mActivityRule.runOnUiThread在迁移过程中去掉，只保留body
     * methodInvocation
     * typeName,identifier
     *
     */
    public static Set<String> simplyMethodInvocationTypeNameMapping = Sets.newHashSet();
    static {
        simplyMethodInvocationTypeNameMapping.add("mActivityRule");
    }

    /**
     *
     */
    public static Map<String, Map<String, String>> commonTypeNameMethodInvocationMapping = Maps.newHashMap();
    static {
        // typename.identifier => repalceIdentifier
        //Thread sleep Sleep
        commonTypeNameMethodInvocationMapping.put("Thread",  ImmutableMap.of("sleep","Sleep"));
    }

//    public static Map<String, Map<String, String>> typeNameMethodInvocationMapping = Maps.newHashMap();
//    static {
//        typeNameMethodInvocationMapping.put("Thread",  ImmutableMap.of("sleep","Sleep"));
//    }


    public static Map<String, List<String>> simplyMethodInvocationIdentifierMapping = Maps.newHashMap();
    static {
        simplyMethodInvocationIdentifierMapping.put("mActivityRule", Arrays.asList("runOnUiThread"));
    }

    public static Map<String, String> basicMapping = Maps.newHashMap();
    static {
        basicMapping.put("String", "std::string");
    }


    public static Map<String, String> testToolMethodNameMapping = Maps.newHashMap();
    static {
        testToolMethodNameMapping.put("assertTrue", "ASSERT_TRUE");
        testToolMethodNameMapping.put("assertFalse", "ASSERT_FALSE");
        testToolMethodNameMapping.put("assertEquals", "ASSERT_EQ");
        testToolMethodNameMapping.put("assertNotEquals", "ASSERT_NE");
//        testToolMethodNameMapping.put("assertNotNull", "ASSERT_NE(NULL, )");
        testToolMethodNameMapping.put("assertNotSame", "ASSERT_NE");
//        testToolMethodNameMapping.put("assertNull", "ASSERT_EQ(NULL,)");
        testToolMethodNameMapping.put("assertSame", "ASSERT_EQ");
//        testToolMethodNameMapping.put("assertThat", "");
//        testToolMethodNameMapping.put("", "EXPECT_EQ");
    }


    public static Map<String, String> methodNameMapping = Maps.newHashMap();
    static {
        methodNameMapping.put("isPaused", "IsPending");
        methodNameMapping.put("start", "Play");
        methodNameMapping.put("isStarted", "IsRunning");
        methodNameMapping.put("isRunning", "IsRunning");
        methodNameMapping.put("pause", "Pause");
    }

}
