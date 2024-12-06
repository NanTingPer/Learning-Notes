package Test02

import org.apache.curator.utils.ZookeeperFactory
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

object Tobj133 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)
        envt.executeSql(
            """
              |create table kt(
              | log_id bigint,
              | order_sn string,
              | product_id string,
              | customer_id string,
              | gen_order bigint,
              | modified_time double
              |) with (
              | 'connector'='kafka',
              | 'topic'='log_product_browse',
              | 'scan.startup.mode'='earliest-offset',
              | 'properties.group.id'='kafkao3',
              | 'properties.bootstrap.servers'='192.168.45.13:9092',
              | 'format'='json'
              |)
              |
              |""".stripMargin)

        envt.executeSql(
            """
              |create table ht(
              | row_key string,
              | info row<
              |     log_id bigint,
              |     order_sn string,
              |     product_id string,
              |     customer_id string,
              |     gen_order bigint,
              |     modified_time double
              | >) with (
              |     'connector'='hbase-2.2',
              |     'table-name'='ods:product_browse',
              |     'zookeeper.quorum'='192.168.45.13:2181'
              | )
              |
              |""".stripMargin)

        envt.from("kt")
                .select(concat($"log_id".cast(DataTypes.STRING()).substring(0,1),"2024",$"log_id".cast(DataTypes.STRING()).substring(1)) as "row_key",
                    row(
                        $"log_id",
                        $"order_sn",
                        $"product_id",
                        $"customer_id",
                        $"gen_order",
                        $"modified_time") as "info"
                ).executeInsert("ht")
    }
}
