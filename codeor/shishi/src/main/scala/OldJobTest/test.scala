package OldJobTest


import com.google.gson.JsonParser
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

import java.text.SimpleDateFormat
import java.util.Date

object test {
  private case class GMV(product_id: Int, customer_id: Int)
  def main(args: Array[String]): Unit = {
    // 创建流执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 设置全局并行度为1，意味着所有操作都将顺序执行。
    env.setParallelism(1)
    // 流表执行环境
    val tableEnvironment = StreamTableEnvironment.create(env)

    // 创建关联维表（来自MySQL中的product_info表）
    val sourceJdbcTableCreate =
      """
        |CREATE TABLE product_info_source (
        |  product_id BIGINT,
        |  product_core STRING,
        |  product_name STRING,
        |  bar_code STRING,
        |  brand_id BIGINT,
        |  one_category_id INT,
        |  two_category_id INT,
        |  three_category_id INT,
        |  supplier_id BIGINT,
        |  price DOUBLE,
        |  average_cost DOUBLE,
        |  publish_status INT,
        |  audit_status INT,
        |  weight DOUBLE,
        |  length DOUBLE,
        |  height DOUBLE,
        |  width DOUBLE,
        |  color_type INT,
        |  production_date STRING,
        |  shelf_life BIGINT,
        |  descript STRING,
        |  indate STRING,
        |  modified_time STRING,
        |  PRIMARY KEY (product_id) NOT ENFORCED
        |) WITH (
        |  'connector' = 'jdbc',
        |  'username' = 'root',
        |  'password' = '123456',
        |  'url' = 'jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false&serverTimezone=Asia/Shanghai&useUnicode=true',
        |  'table-name' = 'product_info'
        |)
        |""".stripMargin
    tableEnvironment.executeSql("DROP TABLE IF EXISTS product_info_source")
    tableEnvironment.executeSql(sourceJdbcTableCreate)

    // 只取product_id和product_name两个字段，并注册为一个临时视图
    val productDimTable = tableEnvironment.sqlQuery("SELECT product_id, product_name FROM product_info_source")
    tableEnvironment.createTemporaryView("product_dim_table", productDimTable)
    tableEnvironment.sqlQuery("select * from product_dim_table limit 100").execute().print()

    val source = KafkaSource.builder()
      .setTopics("log_product_browse")
      .setBootstrapServers("192.168.45.13:9092")
      .setValueOnlyDeserializer(new SimpleStringSchema())
      .setGroupId("group-test")
      .build()

    val dataStream = env
      // 指定Kafka数据源
      .fromSource(source, WatermarkStrategy.noWatermarks(), "kafka")
      .map(line => {
        val jsonObj = JsonParser.parseString(line).getAsJsonObject
        GMV(jsonObj.get("product_id").getAsInt, jsonObj.get("customer_id").getAsInt)
      })
      .keyBy(_.product_id) // 按product_id分组
      .window(TumblingProcessingTimeWindows.of(Time.seconds(30)))
      .process(new ProcessWindowFunction[GMV, (String, Int, Long, Long), Int, TimeWindow] {
        override def process(key: Int, context: Context, elements: Iterable[GMV], out: Collector[(String, Int, Long, Long)]): Unit = {
          var pvCount = 0L
          val uvSet = scala.collection.mutable.Set[Int]()

          for (element <- elements) {
            pvCount += 1
            uvSet.add(element.customer_id)
          }
          val uvCount = uvSet.size.toLong
          // 获取当前时间并格式化
          val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          val currentTime = dateFormat.format(new Date())

          // 生成 row_key
          val rowKey = s"$currentTime-$key"

          // 输出结果
          out.collect((rowKey, key, pvCount, uvCount))
        }
      })
//    dataStream.print()
val table = tableEnvironment.fromDataStream(dataStream)
    tableEnvironment.createTemporaryView("table2", table)
//    // 创建Sink表
val product_pv_uv_hbase_create =
  """
    |CREATE TEMPORARY TABLE product_pv_uv_hbase (
    |   row_key STRING,
    |   info ROW<product_id BIGINT,uv BIGINT, pv BIGINT>,
    |   PRIMARY KEY (row_key) NOT ENFORCED
    |) WITH ('connector' = 'hbase-2.2',
    |        'table-name' = 'ads:online_uv_pv',
    |        'zookeeper.quorum' = '192.168.45.13:2181'
    |)
    |""".stripMargin
    tableEnvironment.executeSql("DROP TABLE IF EXISTS product_pv_uv_hbase")
    tableEnvironment.executeSql(product_pv_uv_hbase_create)

    // ETL管道
    tableEnvironment.sqlQuery(
      """
        |SELECT _1 AS row_key,
        |ROW(_2,_3,_4) AS info
        |FROM table2
        |""".stripMargin)
  .executeInsert("product_pv_uv_hbase") // 将表写入HBase表
//    tableEnvironment.executeSql("select * from product_browse_sink").print()
    // 执行程序

  }
}