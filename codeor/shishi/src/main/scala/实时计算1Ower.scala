import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeInformation, Types}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.jdbc.table.JdbcTableSource
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessAllWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util
import javax.xml.crypto.{AlgorithmMethod, KeySelector, KeySelectorResult, XMLCryptoContext}
import javax.xml.crypto.dsig.keyinfo.KeyInfo

object 实时计算1Ower {
    def main(args: Array[String]): Unit = {
        //使用Flink 消费kafka 中log_product_browse 主题的数据，
        // 统计商品的UV（浏览用户量）和PV（商品浏览量），
        // 将结果写入HBase 中的表ads:online_uv_pv 中。
        // 使用Hive cli(没写错)查询ads.pv_uv_result
        // 表按照product_id 和pv 进行降序排序，查询出10 条数据，
        // 将结果截图粘贴至客户端桌面【Release\模块C 提交结果.docx】中对应的任务序号下；
        System.setProperty("HADOOP_USER_NAME", "root")
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envtable = StreamTableEnvironment.create(env);
        env.setParallelism(1)
        val kafkaSource = KafkaSource.builder()
            .setTopics("log_product_browse")
            .setBootstrapServers("192.168.45.13:9092")
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setGroupId("waf")
            .build();

        var source_jdbc_table_create =
        """
            |  CREATE TABLE product_info_source (
            |  product_id BIGINT,
            |  product_core STRING,
            |  product_name STRING,
            |  bar_code STRING,
            |  brand_id BIGINT,
            |  one_category_id INTEGER,
            |  two_category_id INTEGER,
            |  three_category_id INTEGER,
            |  supplier_id BIGINT,
            |  price DOUBLE,
            |  average_cost DOUBLE,
            |  publish_status INTEGER,
            |  audit_status INTEGER,
            |  weight DOUBLE,
            |  length DOUBLE,
            |  height DOUBLE,
            |  width DOUBLE,
            |  color_type INTEGER,
            |  production_date STRING,
            |  shelf_life BIGINT,
            |  descript STRING,
            |  indate STRING,
            |  modified_time STRING,
            |  PRIMARY KEY (product_id) NOT ENFORCED
            |) WITH (
            |   'connector'='jdbc',
            |   'username'='root',
            |   'password'='123456',
            |   'url'='jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false&serverTimezone=Asia/Shanghai&useUnicode=true',
            |   'table-name'='product_info'
            |)
        """.stripMargin
        envtable.executeSql(source_jdbc_table_create)
        envtable.executeSql("select * from product_info_source limit 10").print()



        //product_id 商品ID
        //customer_id 用户ID
        //modified_time 202411093
        //{"log_id":"71118181231727","product_id":2051,"customer_id":12874,"gen_order":0,"order_sn":"0","modified_time":"20241109113426"}
/*
        val DataStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "awf")

            .map(f => {
                val product_id = f.split(",")(1).replaceAll("[\" ]", "").split(":")(1)
                val customer_id = f.split(",")(2).replaceAll("[\" ]", "").split(":")(1)
                Tuple2(product_id.toInt, customer_id.toInt)
            }).keyBy(f => f._1)
            .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(60)))
            .process(new ProcessAllWindowFunction[(Int,Int),(String,Long, Long, Long),TimeWindow] {
                //TODO _1 = product_id
                //TODO _2 = customer_id 不要重复
                override def process(context: Context, values : Iterable[(Int, Int)], out: Collector[(String, Long, Long, Long)]): Unit = {
                    var AllMap = new util.HashMap[Int,(util.TreeSet[Int],Int)]()
                    values.foreach(f => {
                        if(AllMap.containsKey(f._1)){
                            //TODO 存在
                            val tuple = AllMap.get(f._1)
                            val value = tuple._1
                            value.add(f._2)
                            AllMap.put(f._1,(value,tuple._2 + 1))
                        }else{
                            //TODO 不存在
                            var treeSet = new util.TreeSet[Int]()
                            treeSet.add(f._2)
                            AllMap.put(f._1,(treeSet,1))
                        }
                    })

                    var timeStr = "2024-11-19 15-59-44"
                    AllMap.forEach((k,v) =>{
                        var timess = s"${timeStr}-${k}"
                        out.collect(timess,k,v._1.size(),v._2);
                    })
                }
            })

        // _1,_2,_3,_4
        envtable.createTemporaryView("table2",DataStream)
//        val table = envtable.fromDataStream(DataStream)
//        table

        val table1 =
            """
              |CREATE TEMPORARY TABLE product_pv_uv_hbase (
              |   row_key STRING,
              |   info ROW<product_id BIGINT,uv BIGINT, pv BIGINT>
              |) WITH ('connector' = 'hbase-2.2',
              |        'table-name' = 'ads:online_uv_pv',
              |        'zookeeper.quorum' = '192.168.45.13:2181'
              |)
              |""".stripMargin
        envtable.executeSql(table1)
        envtable.sqlQuery(
            """
              |select _1 as row_key,
              |ROW(_2, _3, _4) as info
              |from table2
            """.stripMargin)
            .executeInsert("product_pv_uv_hbase")
        env.execute()
*/
    }
}
