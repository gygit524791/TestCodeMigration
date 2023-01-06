package com.test.migration.service.translate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class TranslateHint {
    public static final String BS_HINT_TAG = "-BSHINT-";

    public static final String MIS_MATCH_TIPS = "迁移失效：以下安卓组件代码在鸿蒙中无法找到相匹配的映射";
    public static List<String> misMatchCodes;

    /**
     * 在translate之前，先调用init
     */
    public static void init() {
        misMatchCodes = Lists.newArrayList();
    }

    public static String misMatchCodesToString() {
        if (misMatchCodes == null || misMatchCodes.size() == 0) {
            return "";
        }
        return Joiner.on(",").join(misMatchCodes);
    }
}



