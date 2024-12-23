package Test01
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.lit

import java.util.Properties

object Main {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkSession.builder()
                .appName("Offline Data Collection")
                .master("local[*]")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        //    val maxTime = sparkSession.sql("SELECT max(modified_time) FROM ods.order_master").first().getAs[Timestamp](0)

        val properties = new Properties()
        properties.put("user", "root")
        properties.put("password", "123456")

        val df = sparkSession.read.jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false", "order_master", properties)
        df.createOrReplaceTempView("dataFrame")

        //    WHERE modified_time > ${maxTime.toString}
        sparkSession.sql(s"SELECT * FROM dataFrame")
                .withColumn("etl_data", lit("20241208"))
                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
                .partitionBy("elt_data")
                .saveAsTable("ods" + "." + "order_master")

        println("\t\t\t\t\t\t\t\tCompleted")
        println("-----------------------------------------------------------------------------------------")
        println("-----------------------------------------------------------------------------------------")
        println("-----------------------------------------------------------------------------------------")
        println("-----------------------------------------------------------------------------------------")

        sparkSession.close()
    }
}
