package SparkSQL;

import org.apache.spark.sql.SparkSession;

public class SQL2024_10_26
{
    public static void main(String[] args)
    {
        SparkSession sc = SparkSession
            .builder()
            .master("local[*]")
            .appName("SQL2024_10_26")
            .getOrCreate();

        sc.close();
    }
}
