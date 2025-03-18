//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types._
//import org.apache.spark.sql._
//import java.util.Properties
//
//object CalculationJob03{
//
//  def main(args: Array[String]): Unit = {
//
//    // 创建SparkSession实例
//    val spark = SparkSession.builder()
//      .master("local[*]")
//      .appName("Data Calculation")
//    // 兼容Hive Parquet存储格式
//  .config("spark.sql.parquet.writeLegacyFormat", true)
//      .enableHiveSupport()
//      .getOrCreate()
//
//    // 1）读取数据源
//    // 读取DWD中的用户登录日志表数据到DataFrame中，并注册到临时视图中
//    spark.table("ss2024_ds_dwd.log_customer_login").createOrReplaceTempView("log_data")
//
//    // 2) 获取2022-08-10 这周的周日时间
//    spark.sql(
//      """
//        |select
//        |   customer_id,
//        |   date(login_time) log_date,
//        |   next_day("2022-08-10", "Sunday") date_end
//        |from
//        |   log_data
//        |""".stripMargin).createOrReplaceTempView("first_data")
//
//    // 3）筛选出最近三周的数据
//    // 获取两周前的起始日期，筛选符合要求的数据，并计算每个登录日期所属周的周日日期
//    spark.sql(
//        """
//          |select
//          |   *
//          |from
//          |   (select
//          |      customer_id,
//          |      log_date,
//          |      if(dayofweek(log_date)=1,log_date,next_day(log_date,"sunday")) log_date_week_end,
//          |      date_sub(date_end,20) date_begin,
//          |      date_end
//          |    from
//          |      first_data
//          |   )t1
//          |where
//          |   log_date <= date_end
//          |   and
//          |   log_date >= date_begin
//          |""".stripMargin)
//      // 按user_id和log_date_week_end去重
//      .dropDuplicates("customer_id","log_date_week_end")
//      // 注册为临时表
//      .createOrReplaceTempView("second_data")
//
//    // 4）然后，统计最近三周连续登录3次的用户数：
//    val result = spark.sql(
//      """
//        |select
//        |   end_date,
//        |   count(distinct customer_id) active_total,
//        |   date_range
//        |from
//        |   (select
//        |         "2022-08-10" end_date,
//        |         customer_id,
//        |         concat(date_begin,"_",date_end) date_range,
//        |         count(*) loging_week_cnt
//        |    from
//        |         second_data
//        |    group by
//        |         end_date,
//        |         customer_id,
//        |         date_range
//        |   ) t1
//        |where
//        |   loging_week_cnt=3
//        |group by
//        |   end_date,
//        |   date_range
//        |""".stripMargin)
//
//    // 5）将求出的数据存入clickhouse。
//    // 然后执行以下代码，将计算结果写入ClickHouse的ds_result库的continuous_3week表中
//    // 使用官方clickhouse驱动程序和连接信息
//    val ckUrl = "jdbc:clickhouse://192.168.47.137:8123/ds_result"  // 数据库连接url
//    val ckDriver = "com.clickhouse.jdbc.ClickHouseDriver" // 驱动程序
//    val ckUser = "default"
//    val ckPassword = ""
//    val ckTable = "ds_result.continuous_3week"
//
//    val props = new Properties
//    props.put("driver", ckDriver)
//    props.put("user", ckUser)
//    props.put("password", ckPassword)
//
//    // 写入clickhouse
//    result.write.mode("append").jdbc(ckUrl,ckTable,props)
//  }
//}