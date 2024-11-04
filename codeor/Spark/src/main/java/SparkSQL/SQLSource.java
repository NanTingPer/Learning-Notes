package SparkSQL;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

public class SQLSource
{
    public static void main(String[] args)
    {
        SparkSession sparkSession = SparkSession
            .builder()
            .master("local[*]")
            .appName("CSVapp")
            .getOrCreate();

        Dataset<Row> csv = sparkSession
            .read()
           .option("header", true)//有表头
           .option("sep", ",")//分割符
           .csv("C:\\LiMGren\\codeor\\Spark\\src\\main\\java\\SparkSQL\\csvSource.csv");
        csv.show();
        csv.write()
            .option("header",true)
            .mode(SaveMode.Append)
            .csv("csvSource");

        sparkSession.close();

    }
}
