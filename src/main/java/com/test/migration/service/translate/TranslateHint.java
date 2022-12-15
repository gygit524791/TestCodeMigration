package com.test.migration.service.translate;

import com.google.common.collect.Lists;

import java.util.List;

public class TranslateHint {
    public static class MisMatchInfo {
        public static final String MIS_MATCH_TIPS = "迁移失效：以下安卓组件代码在鸿蒙中无法找到相匹配的映射";
        public static List<String> codes = Lists.newArrayList();
    }
}
