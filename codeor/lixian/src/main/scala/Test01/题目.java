//TODO 抽取db 库中table1 的增量数据进入Hive 的ods 库中表table1。
//TODO 根据ods.table1 表中modified_time 作为增量字段，
//TODO 只将新增的数据抽入，
//TODO 字段名称、类型不变，
//TODO 同时添加静态分区，
//TODO 分区字段为etl_date，
//TODO 类型为String，
//TODO 且值为当前比赛日的前一天日期（分区字段格式为yyyyMMdd）。
//TODO 使用hive cli 执行show partitions ods.table1 命令，
//TODO 将执行结果截图粘贴至客户端桌面【Release\模块B 提交结果.docx】中对应的任务序号下；
