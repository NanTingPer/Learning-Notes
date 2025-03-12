package Test07
import org.apache.spark.sql.functions.desc
import org.apache.spark.sql.{SaveMode, SparkSession}

object demo {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val warehouse: String = s"hdfs:///user/hive/warehouse"
        val sparkSession: SparkSession = SparkSession.builder()
            .master("local[*]")
            .config("spark.sql.warehouse.dir", warehouse) //todo 指定hive仓库地址
//            .config("hive.metastore.uris", "thrift://master:9083") //todo 指定metastore地址
            .config("spark.sql.shuffle.partitions", 10) //todo 设置shuffle的分区数
            .appName("UserContinueOrder") //设置程序名称
            .enableHiveSupport() // 连接hive
            .getOrCreate()
        //todo 获取 dim_user_info 最大分区
        val dim_user_info_max_etldate: String = sparkSession.sql(
            """
              |from dwd.dim_user_info select max(etl_date)
              |""".stripMargin).collect()(0).get(0).toString
        //todo 设置spark job日志输出等级
        sparkSession.sparkContext.setLogLevel("ERROR")
        //todo 初始化mysql驱动
        sparkSession.sql(
                s"""
                   |from (from (from dwd.fact_order_info
                   |                     join dwd.dim_user_info
                   |                          on (dim_user_info.id = user_id
                   |               )
                   |            select dim_user_info.id                                                        as `userid`,
                   |                   name                                                                    as `username`,
                   |                   date_format(cast(fact_order_info.create_time as timestamp), "yyyyMMdd") as `day`,
                   |                   sum(`final_total_amount`)                                               as `sumDayAmount`,
                   |                   count(*)                                                                as `countDayOrder`
                   |            group by dim_user_info.id, name,
                   |                     date_format(cast(fact_order_info.create_time as timestamp), "yyyyMMdd")) as v1
                   |      select userid,
                   |             username,
                   |             `day`,
                   |             sumDayAmount,
                   |             countDayOrder,
                   |             lead(`day`) over (partition by userid,username order by day)           as `leadDay`,
                   |             lead(`sumDayAmount`) over (partition by userid,username order by day)  as `lastSumDayAmount`,
                   |             lead(`countDayOrder`) over (partition by userid,username order by day) as `lastCountOrder`) as v2
                   |select userid,
                   |       username,
                   |       concat_ws("_", day, leadDay)                      as `day`,
                   |       (sumDayAmount + lastSumDayAmount)                 as `totalconsumption`,
                   |       (countDayOrder+ lastCountOrder) as `totalorder`
                   |where datediff(from_unixtime(unix_timestamp(leadDay, "yyyyMMdd")), from_unixtime(unix_timestamp(day, "yyyyMMdd"))) = 1
                   |  and lastsumDayAmount > sumDayAmount;
                   |""".stripMargin)
            .orderBy(desc("totalorder"),desc("totalconsumption"),desc("userid"))
            .limit(5)
            .show(false)
    }
}
