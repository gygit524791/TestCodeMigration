package com.test.migration.entity.po;

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
    private String testFilepath;

    /**
     * json:
     * Map<String, List<Integer>>
     *     key: test_method_name
     *     value: api_ids
     */
    private String testMethodApiInvocation;
    private String translateCode;
}
