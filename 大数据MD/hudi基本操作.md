# `hudi`基本操作

## 进入`SparkSQL`

```sh
spark-sql \
--conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
--conf 'spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension' \
--jars hudi.jar
```

完整示例，这边只是将`hudi`的`jar`包放到了这个目录下
```sh
spark-sql --master yarn \
--conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
--conf 'spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension' \
--jars /opt/module/spark-3.1.1-yarn/jars/hudi-spark3.1-bundle_2.12-0.12.0.jar

```

对于`spark-shell` 把 `spark-sql` 换掉就行了
```sh
spark-shell \
--master yarn \
--conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
--conf 'spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension' \
--jars /opt/module/spark-3.1.1-yarn/jars/hudi-spark3.1-bundle_2.12-0.12.0.jar
```





## 写/读`hudi`

获取`Spark会话`，与进入`SparkShell`一样，需要两个`conf`
```scala
val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("chouqu01")
    .enableHiveSupport()
    .config("spark.serializer", "org.apache.spark.serializer.KyreSerializer")
    .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
    .getOrCreate()
```

### 数据读取

```cs
spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")
```

### 数据写入

```cs
table
    .write
    .format("hudi")
    .options(getQuickstartWriteConfigs) //org.apache.hudi.QuickstartUtils 一堆基础配置
    
    //org.apache.hudi.DataSourceWriteOptions
    .option(PRECOMBINE_FIELD.key,"dwd_modify_time") //预聚合字段
    .option(PARTITIONPATH_FIELD.key, "etl_date")	//分区字段
    .option(RECORDKEY_FIELD.key, "id")			//key
    
    //org.apache.hudi.config.HoodieWriteConfig
    .option(HoodieWriteConfig.TBL_NAME.key,"fact_order_detail")	//表名称
    
    .mode(SaveMode.Overwrite)
    .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
```



## 基本命令

> 这些命令是在`Spark-sql`中执行的

### 创建表

```sql
create table dws_ds_hudi.province_consumption_day_aggr(
	uuid string,
	province_id int,
	province_name string,
	region_id int,
	region_name string,
	total_amount double,
	total_count int,
	sequence int,
	year int,
	month int) 
using hudi #如报有多个hudi,需要指定，那就在这里指定
tblproperties(
	type='mor',#"mor / cow"
	primaryKey='uuid',#key 这是必须的 其他可选
	preCombineField='total_count')#预聚合字段
partitioned by(year,month)#分区字段
```

