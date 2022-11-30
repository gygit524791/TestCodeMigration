package com.test.migration.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslateTest {
    private Integer id;
    private Integer taskId;
    private Integer apiId;
    private String testFilepath;
    private String testClassName;
    private String testMethodName;
    private String translateTestMethod;
}
