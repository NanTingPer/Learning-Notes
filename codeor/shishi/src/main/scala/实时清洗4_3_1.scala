import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

import java.text.SimpleDateFormat
import java.util.{Date, Random}

object 实时清洗4_3_1 {
        def main(args: Array[String]): Unit = {
                val env = StreamExecutionEnvironment.getExecutionEnvironment
                val envtable = StreamTableEnvironment.create(env)

//                KafkaSource.builder().setStartingOffsets(OffsetsInitializer.earliest())

                System.setProperty("HADOOP_USER_NAME","root")

                //TODO 读Kafka
                envtable.executeSql(
                        """
                          |CREATE TEMPORARY TABLE order_master_kafka (
                          |   order_id BIGINT,
                          |   order_sn STRING,
                          |   customer_id BIGINT,
                          |   shipping_user STRING,
                          |   province STRING,
                          |   city STRING,
                          |   address STRING,
                          |   order_source INTEGER,
                          |   payment_method INTEGER,
                          |   order_money DOUBLE,
                          |   district_money DOUBLE,
                          |   shipping_money DOUBLE,
                          |   payment_money DOUBLE,
                          |   shipping_comp_name STRING,
                          |   shipping_sn STRING,
                          |   create_time STRING,
                          |   shipping_time STRING,
                          |   pay_time STRING,
                          |   receive_time STRING,
                          |   order_status STRING,
                          |   order_point INTEGER,
                          |   invoice_title STRING,
                          |   modified_time STRING
                          |) WITH ('connector'='kafka',
                          |        'topic'='fact_order_master',
                          |        'properties.bootstrap.servers'='192.168.45.10:9092',
                          |        'properties.group.id'='group-test',
                          |        'format'='json',
                          |        'scan.startup.mode'='earliest-offset'
                          |  )
                          |""".stripMargin)
                //TODO 写Hbase
                envtable.executeSql(
                        """
                          |create temporary table order_master_hbase(
                          |     row_key string,
                          |     cf row<
                          |   order_id BIGINT,
                          |   order_sn STRING,
                          |   customer_id BIGINT,
                          |   shipping_user STRING,
                          |   province STRING,
                          |   city STRING,
                          |   address STRING,
                          |   order_source INTEGER,
                          |   payment_method INTEGER,
                          |   order_money DOUBLE,
                          |   district_money DOUBLE,
                          |   shipping_money DOUBLE,
                          |   payment_money DOUBLE,
                          |   shipping_comp_name STRING,
                          |   shipping_sn STRING,
                          |   create_time STRING,
                          |   shipping_time STRING,
                          |   pay_time STRING,
                          |   receive_time STRING,
                          |   order_status STRING,
                          |   order_point INTEGER,
                          |   invoice_title STRING,
                          |   modified_time STRING>,
                          |     primary key(row_key) not enforced
                          |) with (
                          |     'connector'='hbase-2.2',
                          |     'table-name'='order_master_hbase',
                          |     'zookeeper.quorum'='192.168.45.10:2181'
                          |)
                          |""".stripMargin)


                val rand = new Random()
                val dfm = new SimpleDateFormat("yyyyMMddHHmmssSSS")

                envtable
                        // 加载源表到Table中
                        .from("order_master_kafka")
                        // 增加row_key列(rowkey使用随机数（0-9）+yyyyMMddHHmmssSSS)
                        //concat(randInteger(10).cast(DataTypes.STRING()),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS")) as "row_key"
                        .select(concat(rand.nextInt(10).cast(DataTypes.STRING()),dfm.format(new Date(System.currentTimeMillis())))  as "row_key",
                                row($"order_id",
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
                                        $"modified_time") as "cf")
                        // 将表写入HBase表
                        .executeInsert("order_master_hbase")

                // 将表写入HBase表

        }
}
