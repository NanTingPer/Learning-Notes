package OldJobTest

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

import java.text.SimpleDateFormat
import scala.util.Random

object 实时清洗4_3_3 {
        def main(args: Array[String]): Unit = {
                val env = StreamExecutionEnvironment.getExecutionEnvironment
                env.setParallelism(1)
                val envtable = StreamTableEnvironment.create(env)
                envtable.executeSql(
                        """
                          |create table kafkaData(
                          |     log_id string,
                          |     customer_id int,
                          |     login_time string,
                          |     login_ip string,
                          |     login_type int
                          |) with (
                          |     'connector'='kafka',
                          |     'topic'='dim_customer_login_log',
                          |     'properties.group.id'='kafka666',
                          |     'scan.startup.mode'='earliest-offset',
                          |     'properties.bootstrap.servers'='192.168.45.10:9092',
                          |     'format'='json'
                          |)
                          |""".stripMargin)

                envtable.executeSql(
                        """
                          |create table HbaseT(
                          |     row_key string,
                          |     info row<
                          |             log_id string,
                          |             customer_id bigint,
                          |             login_time string,
                          |             login_ip string,
                          |             login_type bigint>,
                          |     primary key(row_key) not enforced)
                          |     with(
                          |             'connector'='hbase-2.2',
                          |             'table-name'='log',
                          |             'zookeeper.quorum'='192.168.45.10:2181'
                          |     )
                          |""".stripMargin)

//                使用随机数(0-9)+用户id+登陆时间)
                val Rand = new Random()
                envtable.from("kafkaData")
                        .select(concat(Rand.nextInt(10).cast(DataTypes.STRING()),"+",$"customer_id".cast(DataTypes.STRING()),"+",$"login_time".cast(DataTypes.STRING())) as "row_key",
                                row(
                                        $"log_id",
                                        $"customer_id",
                                        $"login_time",
                                        $"login_ip",
                                        $"login_type"
                                ) as "info")
                        .executeInsert("HbaseT")
        }
}
