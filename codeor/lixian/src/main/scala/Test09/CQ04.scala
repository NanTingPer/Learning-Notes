package Test09

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions.lit

import java.util.Properties

object CQ04 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val spark = Utile.GetSpark
        val sqlTable = "fact_order_detail"
        val sqlDatabase = "ds_realtime_db"
        val hivedatabtable = "ods.order_detail"
        val conf = new Properties()
        conf.put("password","123456")
        conf.put("user","root")
        val mysql = spark.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${sqlDatabase}?useSSL=false", sqlTable, conf)
        mysql
            .withColumn("etl_date", lit("20250321"))
            .write
            .mode(SaveMode.Overwrite)
            .partitionBy("etl_date")
            .format("hive")
            .saveAsTable(hivedatabtable)
    }
}
