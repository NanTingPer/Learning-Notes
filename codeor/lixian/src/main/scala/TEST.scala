import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import java.util.Properties

object  main_ {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SessionUtils.getSession
        import spark.implicits._

        val order_detail: DataFrame = MysqlUtils.read(spark, "ds_db01", "order_detail")
        val order_info: DataFrame = MysqlUtils.read(spark, "ds_db01", "order_info")

        //    根据order_id连接起来，查出每个用户购买的商品并去重
        val data: Dataset[Row] = order_detail.join(order_info, order_detail("order_id") === order_info("id"))
            .select("user_id", "sku_id")
            .distinct()

        //    6708用户所购买的商品
        val user6708_skuids: Array[Int] = data.filter(col("user_id") === 6708).select("sku_id").map((_: Row) (0).toString.toInt).collect()

        val user_ids: String = data.withColumn("cos", when(col("sku_id").isin(user6708_skuids: _*), 1.0).otherwise(0.0))
            .groupBy("user_id")
            .agg(sum("cos").as("same"))
            .filter(col("user_id") !== 6708)
            .orderBy(desc("same"), asc("user_id"))
            .limit(10)
            .map((_: Row) (0).toString)
            .collect()
            .mkString(",")

        val str = user_ids.map(_(0).toString)
            .collect()
            .mkString(",")

        println("-------------------相同种类前10的id结果展示为：--------------------")
        println(str)


        SessionUtils.close(spark)
    }
}
object SessionUtils {

    def getSession: SparkSession = {
        System.setProperty("HADOOP_USER_NAME","root")
        Logger.getLogger("org").setLevel(Level.OFF)
        val sparkSession: SparkSession = SparkSession.builder()
            .master("local[*]")
            .appName("Exam1_1")
            .config("spark.hadoop.dfs.client.use.datanode.hostname", "true")
            .config("hive.metastore.uris", "thrift://bigdata1:9083")
            .config("hive.exec.dynamic.partition.mode", "nonstrict")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.legacy.avro.datetimeRebaseModeInWrite", "LEGACY")
            .config("spark.sql.legacy.parquet.datetimeRebaseModeInRead", "LEGACY")
            //      .config("dfs.client.use.datanode.hostname", "true")
            //      .config("spark.sql.parquet.writeLegacyFormat", "true")
            //      .config("spark.hadoop.hive.metastore.schema.verification", "false")
            //      .config("spark.hadoop.datanucleus.autoCreateSchema", "true")
            //      .config("spark.hadoop.datanucleus.autoCreateTables", "true")
            .enableHiveSupport()
            .getOrCreate()

        sparkSession
    }


    def close(sparkSession:SparkSession): Unit ={
        if(sparkSession != null){
            sparkSession.close()
        }
    }

}

object MysqlUtils {

    var p:Properties = new Properties() {
        {
            setProperty("user", "root")
            setProperty("password", "123456")
        }
    }


    def read(sparkSession: SparkSession,db:String,tb:String): DataFrame = {
        sparkSession.read.jdbc(s"jdbc:mysql://bigdata1:3306/${db}?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF-8",tb,p)
    }

    def write(db: String, tb: String,dataFrame: DataFrame): Unit = {
        dataFrame.write.jdbc(s"jdbc:mysql://bigdata1:3306/${db}?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF-8", tb, p)
    }




}
