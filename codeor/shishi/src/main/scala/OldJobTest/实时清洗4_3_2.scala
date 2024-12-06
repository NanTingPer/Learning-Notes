package OldJobTest

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.text.SimpleDateFormat
import java.util.Random

object 实时清洗4_3_2 {
        def main(args: Array[String]): Unit = {
                val env = StreamExecutionEnvironment.getExecutionEnvironment
                val envtable = StreamTableEnvironment.create(env)
                System.setProperty("HADOOP_USER_NAME","root")
                envtable.executeSql(
                        """
                          |     create table kafkaData(
                          |     order_detail_id int,
                          |     order_sn string,
                          |     product_id int,
                          |     product_name string,
                          |     product_nct int,
                          |     product_price double,
                          |     average_cost double,
                          |     weight double,
                          |     fee_money double,
                          |     w_id int,
                          |     create_time string,
                          |     modified_time string
                          |     ) with (
                          |'connector'='kafka',
                          |     'topic'='fact_order_detail',
                          |     'properties.bootstrap.servers'='192.168.45.10:9092',
                          |     'properties.group.id'='group-test',
                          |     'format'='json',
                          |     'scan.startup.mode'= 'earliest-offset'  )
                          |""".stripMargin)

                val sql =
                        """
                          |     create table hbase(
                          |     row_key string,
                          |     info row<
                          |             order_detail_id bigint,
                          |             order_sn string,
                          |             product_id bigint,
                          |             product_name string,
                          |             product_nct bigint,
                          |             product_price double,
                          |             average_cost double,
                          |             weight double,
                          |             fee_money double,
                          |             w_id bigint,
                          |             create_time string,
                          |             modified_time string>,
                          |     primary key (row_key) not enforced
                          |     ) with (
                          |     'connector'='hbase-2.2',
                          |     'zookeeper.quorum'='192.168.45.10:2181',
                          |     'table-name'='ods:order_detail'
                          |     )
                          """.stripMargin

                envtable.executeSql(sql)

                val ram = new Random()
                val format = new SimpleDateFormat("yyyyMMddHHmmssSSS")

                envtable.from("kafkaData")
                        .select(concat(randInteger(10).cast(DataTypes.STRING()),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS")) as "row_key",
                              row(
                                        $"order_detail_id",
                                        $"order_sn",
                                        $"product_id",
                                        $"product_name",
                                        $"product_nct",
                                        $"product_price",
                                        $"average_cost",
                                        $"weight",
                                        $"fee_money",
                                        $"w_id",
                                        $"create_time",
                                        $"modified_time"
                                ) as "info").executeInsert("hbase")

        }
}
