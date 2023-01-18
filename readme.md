## 项目结构说明
git :https://github.com/gygit524791/TestCodeMigration
### antlr
解析java文件，用于生成抽象语法树，获取类信息等 
### dao
持久化，使用mybatis中间件存储一些中间结果
### entity
实体：测试迁移任务参数，api基础信息，api映射信息，代码翻译信息
### service:业务逻辑
    extract：抽取api基础信息
    invocation:获取函数调用链
    preprocess:代码预处理：驼峰/下划线命名处理，提取词干，消除stop words等
    translate:代码转换引擎
        bnf：代码语法规则节点处理
        MappingRuleLoader:加载mapping规则
        ReplaceRuleService：替换规则

### resource
    mappingRule：生成和补充的映射规则
    task.properties:测试迁移任务参数项


## 操作步骤
1. 修改task.properties任务参数
2. 执行ApiMappingMain计算api mapping
3. 执行TranslateMain转换测试代码