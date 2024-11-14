package SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SQL2024_10_26_1537_JSON
{
    public static void main(String[] args)
    {
        SparkSession sc = SparkSession
            .builder()
            .master("local")
            .appName("SparkSQL")
            .getOrCreate();


        Dataset<Row> json = sc.read().json("src/main/java/SparkSQL/Json.json");
        json.createOrReplaceTempView("items");

        String sql = "select * from items";
        Dataset<Row> sql1 = sc.sql(sql);
        sql1.show();

        sc.close();
    }
}
