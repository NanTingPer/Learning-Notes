package gsj2

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.hudi.HoodieSparkSessionExtension
import org.apache.spark.serializer.KryoSerializer

// --conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
// --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
// --jars


object CQ1 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hive")
            .master("local[*]")
            .getOrCreate()
    }
}
