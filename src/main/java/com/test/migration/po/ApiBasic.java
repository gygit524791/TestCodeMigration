package com.test.migration.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiBasic {

    private Integer id;

    private Integer taskId;

    private String filepath;

    private String className;

    private String apiName;

    /**
     * 0：鸿蒙 1：安卓
     */
    private Integer type;

    /**
     * 方法单词序列，用逗号分割
     */
    private String methodWordSequence;

    /**
     * 词干序列，用逗号分割
     */
    private String tokenSequence;

    private String tokenVector;
}
