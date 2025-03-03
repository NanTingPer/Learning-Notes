package Test04

import org.apache.spark.sql.SparkSession

object MySQlToHive {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Util.GetSpark
        Util.GetMySQLData(spark, "shtd_store", "user_info")

    }
}
