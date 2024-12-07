package Test01

import org.apache.spark.sql.SparkSession

object SQL01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("dawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .enableHiveSupport()
                .getOrCreate()

        val data = spark.sql("select * from dwd.fact_order_master")
        data.createGlobalTempView("data")

        spark.sql("select * from data where substr(create_time,0,3) = '2022'").show
    }
}
