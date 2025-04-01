package 特征工程

import org.apache.spark.sql.SparkSession

import java.util.Properties

object 相似用户 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .appName("hive")
            .master("local[*]")
            .enableHiveSupport()
            .getOrCreate()

//        val conf = new Properties(); conf.put("")
//        spark.read.jdbc("jdbc:mysql://192.168.45.13/shtd_store", "sku_info", conf)
    }
}
