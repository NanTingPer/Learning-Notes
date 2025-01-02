package _20241206

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

object Tob1032 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)
        envt.executeSql(
            """
              |create table kt(
              | order_detail_id bigint,
              | order_sn string,
              | product_id bigint,
              | product_name string,
              | product_cnt bigint,
              | product_price double,
              | average_cost double,
              | weight double,
              | fee_money double,
              | w_id bigint,
              | create_time string,
              | modified_time string
              |) with (
              | 'connector'='kafka',
              | 'topic'='fact_order_detail',
              | 'properties.group.id'='kafka666',
              | 'properties.bootstrap.servers'='192.168.45.13:9092',
              | 'scan.startup.mode'='earliest-offset',
              | 'format'='json'
              |)
              |
              |""".stripMargin)

        envt.executeSql(
            """
              |create table ht(
              | row_key string,
              | info row<
              |     order_detail_id bigint,
              |     order_sn string,
              |     product_id bigint,
              |     product_name string,
              |     product_cnt bigint,
              |     product_price double,
              |     average_cost double,
              |     weight double,
              |     fee_money double,
              |     w_id bigint,
              |     create_time string,
              |     modified_time string
              | >) with (
              |     'connector'='hbase-2.2',
              |     'table-name'='order_detail',
              |     'zookeeper.quorum'='192.168.45.13:2181'
              | )
              |""".stripMargin)

        envt.from("kt")
                .select(concat(randInteger(10).cast(DataTypes.STRING()),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS")) as "row_key",
                    row(
                        $"order_detail_id",
                        $"order_sn",
                        $"product_id",
                        $"product_name",
                        $"product_cnt",
                        $"product_price",
                        $"average_cost",
                        $"weight",
                        $"fee_money",
                        $"w_id",
                        $"create_time",
                        $"modified_time",
                    ) as "info").executeInsert("ht")


    }

}
