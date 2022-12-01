package com.test.migration.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskParameter {

    private Integer taskId;
    /**
     * 用于mapping的source
     */
    private String sourceFilepath;
    /**
     * 用于mapping的target
     */
    private String targetFilepath;
    /**
     * 用于解析test文件，所有sourceCode
     */
    private String targetSourceCodeFilepath;

    /**
     * python 命令路径配置
     */
    private String pythonBinPath;

    /**
     * token序列语料库文件路径
     */
    private String corpusFilepath;
    /**
     * token向量字典文件路径
     */
    private String wordVecModelFilepath;

//    private Double similarityThreshold;

    /**
     * sqllite db文件的路径地址
     */
    private String dbFilepath;


    /**
     * python
     */
    private String pythonCppExtractor;
    private String pythonWordVec;
    private String pythonCalcTokenVec;
    private String pythonCalcSimilarity;

}
