package Test01

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

import java.text.SimpleDateFormat
import java.util.Date

object 实时数据清洗0301 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envtable = StreamTableEnvironment.create(env)

        envtable.executeSql(
            """
              |create table kafkaSource(
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
              | 'properties.bootstrap.servers'='192.168.45.10:9092',
              | 'properties.group.id'='run_kafka',
              | 'format'='json',
              | 'scan.startup.mode'='earliest-offset'
              |)
              |""".stripMargin)

        envtable.executeSql(
            """
              |create table hbasetable(
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
              |     modified_time string>
              |) with (
              | 'connector'='hbase-2.2',
              | 'table-name'='ods:order_master',
              | 'zookeeper.quorum'='192.168.45.10:2181'
              |)
              |""".stripMargin)


        envtable.from("kafkaSource")
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
                        $"modified_time" as "info"))
                .executeInsert("hbasetable")
    }
}
