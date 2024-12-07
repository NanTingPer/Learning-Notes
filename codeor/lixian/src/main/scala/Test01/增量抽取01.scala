package Test01

import org.apache.spark.sql.SparkSession

import java.sql.Timestamp
import java.util.Properties
//TODO 需要注意的是 Spark的Hive配置在resources的hive-site.xml
//TODO 所以代码中基本不需要配置
object 增量抽取01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("666")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .enableHiveSupport()
                .getOrCreate()

        //TODO 获取hive存量最大值
        //TODO first() 获取第一行
        //TODO getAs[T] 将数据转换为T类型 (0) 是获取第一列的值
        val rows = spark.sql("select max(modified_time) from ods.order_master").first().getAs[Timestamp](0)
        println(rows)

//        val pro = new Properties()
//        pro.put("user","root")
//        pro.put("password","123456")
//
//
//        val data = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/xxxx?useSSL=false", "test", pro)
//        data.createOrReplaceGlobalTempView("mysql")
//
//        spark.read.
    }

}
