package _20241220

import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object jisuan03 {

    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir", "hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode", "nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val endDate = "2022-08-10"
        val dateBegin = spark.sql(s"""
        SELECT date_sub(next_day(to_date('$endDate', 'yyyy-MM-dd'), 'Sunday'), 20) as date_begin
        """).head()

        val config = new Properties()
        config.put("user","default")
        config.put("password","123456")

        val result = spark.sql(s"""
        WITH log_data AS (
          SELECT
            customer_id,
            date(login_time) as log_date
          FROM dwd.log_customer_login
        ),
        first_data AS (
          SELECT
            customer_id,
            log_date,
            CASE WHEN dayofweek(log_date) = 1 THEN log_date ELSE next_day(log_date, 'Sunday') END as log_date_week_end,
            date_sub(next_day(to_date('$endDate', 'yyyy-MM-dd'), 'Sunday'), 20) as date_begin,
            next_day(to_date('$endDate', 'yyyy-MM-dd'), 'Sunday') as date_end
          FROM log_data
        ),
        second_data AS (
          SELECT DISTINCT
            customer_id,
            log_date_week_end,
            date_begin,
            date_end
          FROM first_data
          WHERE log_date BETWEEN date_begin AND date_end
        ),
        customer_login_counts AS (
          SELECT
            customer_id,
            date_begin || '_' || date_end as date_range,
            COUNT(*) as loging_week_cnt
          FROM second_data
          GROUP BY customer_id, date_begin, date_end
        )
        SELECT
          '$endDate' as end_date,
          COUNT(DISTINCT customer_id) as active_total,
          date_range
        FROM customer_login_counts
        WHERE loging_week_cnt = 3
        GROUP BY date_range
""")                .write.mode(SaveMode.Append).jdbc("jdbc:clickhouse://192.168.45.20:8123/ds_result","continuous_3week",config)


    }
}
