package Test01

import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

object 实时数据清洗0302 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envtable = StreamTableEnvironment.create(env)

        envtable.executeSql(
            """
              |create table kas(
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
              | 'properties.group.id'='wafg',
              | 'properties.bootstrap.servers'='192.168.45.10:9092',
              | 'scan.startup.mode'='earliest-offset',
              | 'format'='json'
              |)
              |""".stripMargin)

        envtable.executeSql(
            """
              |create table hbaset(
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
              |     'table-name'='ods:order_detail',
              |     'zookeeper.quorum'='192.168.45.10:2181'
              | )
              |
              |
              |
              |""".stripMargin)


        envtable.from("kas")
                .select(concat(randInteger(10).cast(DataTypes.STRING()),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS")) as "row_key",
                    row($"order_detail_id",
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
                        $"modified_time") as "info").executeInsert("hbaset")
    }

}
