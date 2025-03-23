package Test09

import org.apache.spark.sql.SparkSession

object Utile {
    def GetSpark: SparkSession = {
        SparkSession.builder().enableHiveSupport().master("local[*]").appName("dawf").config("hive.exec.dynamic.partition.mode","nonstrict").getOrCreate()
    }
}
