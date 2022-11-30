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
    private String targetApiIds;
    private String translateCode;
}
