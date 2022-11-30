package com.test.migration.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMapping {
    private Integer id;
    private Integer taskId;
    private Integer sourceApiId;
    private Integer targetApiId;
    private String targetApiTestIds;
}
