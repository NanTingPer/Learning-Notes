package SparkSQL;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.*;

public class SQL_UDAF_2024_10_26_18
{
    public static void main(String[] args)
    {
        SparkSession sparkSession = SparkSession
            .builder()
            .master("local[*]")
            .appName("SQLUDAF")
            .getOrCreate();
        //链接本地数据
        Dataset<Row> json = sparkSession.read().json("src/main/java/SparkSQL/Json.json");
        //视图化
        json.createOrReplaceTempView("json");
        //注册UDAF
        sparkSession.udf().register("AvgAge",functions.udaf(new SQLUDAF_AvgAge(),Encoders.LONG()));

        //计算平均值
        sparkSession.sql("select AvgAge(age) from json").show();

        sparkSession.close();
    }
}
