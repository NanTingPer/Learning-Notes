package _20241206

import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

object Tob1031 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)
        envt.executeSql(
            """
              |create table kt(
              | order_id bigint,
              | order_sn string,
              | customer_id bigint,
              | shipping_user string,
              | province string,
              | city string,
              | address string,
              | order_source bigint,
              | payment_method bigint,
              | order_money double,
              | district_money double,
              | shipping_money double,
              | payment_money double,
              | shipping_comp_name string,
              | shipping_sn string,
              | create_time string,
              | shipping_time string,
              | pay_time string,
              | receive_time string,
              | order_status string,
              | order_point bigint,
              | invoice_title string,
              | modified_time string
              |) with (
              | 'connector'='kafka',
              | 'topic'='fact_order_master',
              | 'properties.group.id'='kafka666',
              | 'properties.bootstrap.servers'='192.168.45.13:9092',
              | 'scan.startup.mode'='earliest-offset',
              | 'format'='json'
              |)
              |""".stripMargin)

        envt.executeSql(
            """
              |create table ht(
              | row_key string,
              | info row<
              |     order_id bigint,
              |     order_sn string,
              |     customer_id bigint,
              |     shipping_user string,
              |     province string,
              |     city string,
              |     address string,
              |     order_source bigint,
              |     payment_method bigint,
              |     order_money double,
              |     district_money double,
              |     shipping_money double,
              |     payment_money double,
              |     shipping_comp_name string,
              |     shipping_sn string,
              |     create_time string,
              |     shipping_time string,
              |     pay_time string,
              |     receive_time string,
              |     order_status string,
              |     order_point bigint,
              |     invoice_title string,
              |     modified_time string
              | >) with (
              |     'connector'='hbase-2.2',
              |     'table-name'='order_master',
              |     'zookeeper.quorum'='192.168.45.13:2181'
              | )
              |
              |""".stripMargin)

        envt.from("kt")
                .select(concat(randInteger(10).cast(DataTypes.STRING()),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS")) as "row_key",
                    row(
                        $"order_id",
                        $"order_sn",
                        $"customer_id",
                        $"shipping_user",
                        $"province",
                        $"city",
                        $"address",
                        $"order_source",
                        $"payment_method",
                        $"order_money",
                        $"district_money",
                        $"shipping_money",
                        $"payment_money",
                        $"shipping_comp_name",
                        $"shipping_sn",
                        $"create_time",
                        $"shipping_time",
                        $"pay_time",
                        $"receive_time",
                        $"order_status",
                        $"order_point",
                        $"invoice_title",
                        $"modified_time"
                    ) as "info").executeInsert("ht")

    }
}
