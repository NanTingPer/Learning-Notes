import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions.Window

object CleaningJob01 {
  def main(arg:Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME","root")
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("CLEAN Job")
      .config("hive.exec.dynamic.partition","true")
      .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
      .config("hive.exec.dynamic.partition.mode","nonstrict")
      .config("spark.sql.sources.partitionOverwriteMode","dynamic")
      .config("spark.sql.parquet.writeLegacyFormat","true")
      .config("spark.serializer","org.apache.spark.serializer.KryoSerializer")
      .enableHiveSupport()
      .getOrCreate()
    //获取老用户的信息
    val dim_customer_inf = spark.sql("select * from dwd.dim_customer_inf where etl_date='20231225'")
    //col 返回给定单元格数据
    val columns = dim_customer_inf.columns.map(col(_))
    val new_customer_inf = spark
      .sql("select * from ods.customer_inf where etl_date='20231225'")
      //lit => 将常量转换为列
      .withColumn("dwd_insert_user",lit("user1"))
      //date_trunc => 将日期截取为制定格式
      .withColumn("dwd_insert_time",date_trunc("second",current_timestamp()))
      .withColumn("dwd_modify_user",lit("user1"))
      .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))
      .select(columns: _*)
    val union_df = dim_customer_inf.union(new_customer_inf)
    val w1 = Window.partitionBy("customer_id").orderBy(col("modified_time").desc)
    val w2 = Window.partitionBy("customer_id")
    val union_end_df = union_df
      .withColumn("row_number",row_number().over(w1))
      .withColumn("dwd_insert_time",min("dwd_insert_time").over(w2))
      .withColumn("dwd_modify_time",max("dwd_modify_time").over(w2))
    val merge_df = union_end_df.where("row_number=1").drop("row_number")
    merge_df
      .write
      .mode("overwrite")
      .saveAsTable("tmp_dim_customer_inf")
    spark
      .table("tmp_dim_customer_inf")
      .write
      .mode("overwrite")
      .insertInto("dwd.dim_customer_inf")
    spark.sql("drop table tmp_dim_customer_inf")
  }
}