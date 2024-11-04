package SparkSQL;

import org.apache.hadoop.hive.ql.exec.spark.session.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataType$;
import org.apache.spark.sql.types.StringType$;

public class SQLudf2024_10_2618
{
    public static void main(String[] args)
    {
        SparkSession sqLudf = SparkSession
            .builder()
            .master("local")
            .appName("SQLudf")
            .getOrCreate();

        Dataset<Row> json = sqLudf.read().json("src/main/java/SparkSQL/Json.json");
        json.createOrReplaceTempView("json");

        sqLudf.udf().register("addName", new UDF1<String, String>() {

            @Override
            public String call(String str) throws Exception
            {
                return "Name : " + str;
            }
        }, StringType$.MODULE$);


        String sqlstr ="select addName(name) from json";
        Dataset<Row> sql = sqLudf.sql(sqlstr);

        sql.show();

        sqLudf.close();

    }
}
