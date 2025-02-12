package OldJobTest

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessAllWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

import java.util

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
            |  product_name STRING,
            |  modified_time STRING,
            |  PRIMARY KEY (product_id) NOT ENFORCED
            |) WITH (
            |   'connector'='jdbc',
            |   'username'='root',
            |   'password'='123456',
            |   'url'='jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false',
            |   'table-name'='product_info'
            |)
        """.stripMargin
        envtable.executeSql(source_jdbc_table_create)
        val productDimTable = envtable.sqlQuery("SELECT product_id, product_name FROM product_info_source")
        envtable.createTemporaryView("product_dim_table", productDimTable)
        envtable.sqlQuery("select * from product_dim_table limit 100").execute().print()



        //product_id 商品ID
        //customer_id 用户ID
        //modified_time 202411093
        //{"log_id":"71118181231727","product_id":2051,"customer_id":12874,"gen_order":0,"order_sn":"0","modified_time":"20241109113426"}

        val DataStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "awf")

            .map(f => {
                val product_id = f.split(",")(1).replaceAll("[\" ]", "").split(":")(1)
                val customer_id = f.split(",")(2).replaceAll("[\" ]", "").split(":")(1)
                Tuple2(product_id.toInt, customer_id.toInt)
            }).keyBy(f => f._1)
            .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(20)))
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
//        envtable.createTemporaryView("table3",DataStream)
        val table = envtable.fromDataStream(DataStream)
        envtable.createTemporaryView("table2",table)
//        envtable.sqlQuery("select * from table2 limit 10").execute().print()

//        val table = envtable.fromDataStream(DataStream)
//        table

        val table1 =
            """
              |CREATE TEMPORARY TABLE product_pv_uv_hbase (
              |   row_key STRING,
              |   info ROW<product_id BIGINT, product_name STRING ,uv BIGINT, pv BIGINT>
              |) WITH ('connector' = 'hbase-2.2',
              |        'table-name' = 'ads:online_uv_pv',
              |        'zookeeper.quorum' = '192.168.45.13:2181'
              |)
              |""".stripMargin
        envtable.executeSql(table1)
        envtable.sqlQuery(
            """
              |SELECT tb2._1 as row_key ,tb2._2 as info
              |FROM
              |    table2 AS tb2
              |JOIN
              |    product_dim_table AS pdt ON tb2._2 = pdt.product_id
            """.stripMargin).execute().print()
//            .executeInsert("product_pv_uv_hbase")


//        envtable.executeSql("select * from product_pv_uv_hbase")
    }
}
