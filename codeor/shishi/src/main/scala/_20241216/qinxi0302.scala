package _20241216

import org.apache.curator.utils.ZookeeperFactory
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

object qinxi0302 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)

        envt.executeSql(
            """
              |create table kf(
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
              |  'connector'='kafka',
              |  'topic'='fact_order_detail',
              |  'properties.bootstrap.servers'='192.168.45.13:9092',
              |  'properties.group.id'='awff',
              |  'scan.startup.mode'='earliest-offset',
              |  'format'='json'
              |)
              |""".stripMargin)

        envt.executeSql(
            """
              |create table htb(
              | row_key string,
              | info row <
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
              | >) with (
              |     'connector'='hbase-2.2',
              |     'table-name'='ods:order_detail',
              |     'zookeeper.quorum'='192.168.45.13:2181'
              |)
              |""".stripMargin)

        envt.from("kf")
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
                        $"modified_time"
                    ) as "info")
                .executeInsert("htb")
    }

}
