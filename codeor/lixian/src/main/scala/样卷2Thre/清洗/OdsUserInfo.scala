package 样卷2Thre.清洗

import org.apache.hudi.DataSourceWriteOptions
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
import org.apache.spark.sql.functions.{col, lit, when}
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.sql.Timestamp
import java.time.LocalDate
import java.util.Properties

object OdsUserInfo {
  private val MysqlTableName:String = "base_province"
  private val HudiTableName:String = "base_province"
  def main(args: Array[String]): Unit = {
    val warehouse: String = s"hdfs:///user/hive/warehouse"
    val sparkSession: SparkSession = SparkSession.builder()
      .config("spark.sql.warehouse.dir", warehouse)
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
      .appName(HudiTableName)
      .enableHiveSupport() // 连接hive
      .getOrCreate()
    // 设置spark job日志输出等级
    sparkSession.sparkContext.setLogLevel("ERROR")
    // 获取昨天的日期(yyyyMMdd)
    val yd: String = LocalDate.now().minusDays(1).toString.replaceAll("-", "")
    // 获取user_info 增量字段的最大值
    val max_increase_time: Timestamp = sparkSession.sql(
      s"""
        |from ods_ds_hudi.${HudiTableName} select if(max(`create_time`) > max(`operate_time`),max(`create_time`),max(`operate_time`)) as max_increase_time
        |""".stripMargin).collect()(0).getTimestamp(0)
    // 获取ods层表列的顺序
    val columns: Array[String] = sparkSession.sql(s"from ods_ds_hudi.${HudiTableName} select * limit 1")
      .drop("_hoodie_commit_time", "_hoodie_commit_seqno", "_hoodie_record_key", "_hoodie_partition_path", "_hoodie_file_name") //TODO 删除hudi 隐藏字段
      .columns
    println(s"========================ods_ds_hudi.${HudiTableName}增量字段最大值:${max_increase_time}========================")
    // 读取mysql数据
   val jdbcUrl:String =  s"jdbc:mysql://master:3306/shtd_store?useUnicode=true&characterEncoding=utf-8"
    val properties: Properties = new Properties()
    properties.put("user", "root") //用户名
    properties.put("password","123456") // 密码
    properties.put("driver", "com.mysql.jdbc.Driver") // 驱动名称
    sparkSession.read.jdbc(jdbcUrl,MysqlTableName,properties)
      .where(col("create_time") > max_increase_time || col("operate_time")>max_increase_time)
      .withColumn("operate_time", when(col("operate_time").isNull, col("create_time")).otherwise(col("operate_time")))
      .withColumn("etl_date",lit(yd)) //TODO 添加分区字段以及值
      .select(columns.map(col): _*) // 按顺序获取数据 防止顺序错位
      .write.format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(TBL_NAME.key(),"user_info") //todo 设置hudi 表名
      .option(DataSourceWriteOptions.RECORDKEY_FIELD.key(),"id") //todo 设置primaryKey
      .option(DataSourceWriteOptions.PRECOMBINE_FIELD.key(),"operate_time") //todo 设置preCombineField
      .option(DataSourceWriteOptions.PARTITIONPATH_FIELD.key(),"etl_date") //todo 设置分区字段
      .option(DataSourceWriteOptions.HIVE_STYLE_PARTITIONING.key(),"true") //todo 设置为hive风格的分区,分区将为key=value格式
      .option(DataSourceWriteOptions.SQL_ENABLE_BULK_INSERT.key(),"true") //todo 启用BULK INSERT模式
      .mode(SaveMode.Append)
      .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
    // 查看表分区数据
    sparkSession.sql("show partitions ods_ds_hudi.user_info;").show(false)
    // 关闭SparkSession
    sparkSession.stop()
  }

}
