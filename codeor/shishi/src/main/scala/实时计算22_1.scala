import com.google.gson.JsonParser
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

import java.text.SimpleDateFormat
import java.util.{Date, Random}
import java.{lang, util}

object 实时计算22_1 {
        def StringToTuple(str : String) : PCID = {
                val jsonStr = JsonParser.parseString(str).getAsJsonObject
                val pID = jsonStr.get("product_id").getAsString
                val cID = jsonStr.get("customer_id").getAsString
                val MTi = jsonStr.get("modified_time").getAsString
                new PCID(pID,cID,MTi)
        }

        def main(args: Array[String]): Unit = {
                val env = StreamExecutionEnvironment.getExecutionEnvironment
                val tableEnv = StreamTableEnvironment.create(env)
                env.setParallelism(1)
                val ks = KafkaSource.builder()
                        .setBootstrapServers("192.168.45.10:9092")
                        .setTopics("log_product_browse")
                        .setGroupId("kafkafkakf")
                        .setValueOnlyDeserializer(new SimpleStringSchema())
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .build()
                //TODO 商品ID 用户ID
                //product_browse 9789 | 6749 | 1 | 2024112870294178 | '20241121175450 ')
                //TODO UV 不重复用户访问量
                //TODO PV  全部用户访问量
                val stream = env.fromSource(ks, WatermarkStrategy.noWatermarks(), "fawfawg")
                        .map(StringToTuple(_)).keyBy(_.product_id)
                        .window(TumblingProcessingTimeWindows.of(Time.minutes(1)))
                        .process(new ProcessWindowFunction[PCID, (Long, Long, Long , String), String, TimeWindow] {

                                override def process(key: String, context: Context, elements: Iterable[PCID], out: Collector[(Long, Long, Long, String)]): Unit = {
                                        val UV = new util.TreeSet[String]
                                        var PV = 0
                                        val iterator = elements.iterator
                                        var spID = key
                                        var ModTime : Long = Long.MaxValue;
                                        while (iterator.hasNext) {
                                                val next = iterator.next()
                                                PV += 1;
                                                UV.add(next.customer_id)
                                                if(next.modified_time.toLong < ModTime) ModTime = next.modified_time.toLong;
                                        }
                                        out.collect((spID.toLong, UV.size(), PV,ModTime.toString))
                                }
                        })


                //TODO 创建MySQL表 用于获取 product_name
                tableEnv.executeSql(
                        """
                          |create table mysql(
                          |     product_id bigint,
                          |     product_name String
                          |) with (
                          |     'connector'='jdbc',
                          |     'username'='root',
                          |     'password'='123456',
                          |     'url'='jdbc:mysql://192.168.45.10:3306/ds_db01?useSSL=false',
                          |     'table-name'='product_info'
                          |)
                          |
                        """.stripMargin)

                val tablerun = tableEnv.fromDataStream(stream)
                tableEnv.createTemporaryView("table2",tablerun)

                //TODO 创建HBase目标表 用于往HBase插入数据
                tableEnv.executeSql(
                        """
                          |create table hbaseT(
                          |     row_key bigint,
                          |     info row <
                          |     product_id bigint,
                          |     product_name string,
                          |     uv bigint,
                          |     pv bigint,
                          |     modified_time string>
                          |) with (
                          |     'connector'='hbase-2.2',
                          |     'table-name'='ods:TempTable',
                          |     'zookeeper.quorum'='192.168.45.10:2181'
                          |)
                          |""".stripMargin)


                //TODO 先创建临时表 然后从临时表去查
                //TODO 临时表 => 来源于流表的JOIN
                tableEnv.sqlQuery(
                        """
                          |with TempTa as (
                          |select
                          |     tb2._1, my.product_name, tb2._2, tb2._3 ,tb2._4
                          |from
                          |     table2 as tb2
                          |join
                          |     mysql as my
                          |on
                          |     tb2._1 = my.product_id
                          |)
                          |select _1 as row_key,
                          |row(
                          |     _1, product_name, _2, _3, _4
                          |) as info
                          |from TempTa
                          |""".stripMargin)
                        .executeInsert("hbaseT")

                tableEnv.executeSql("select * from hbaseT").print()
                tableEnv.sqlQuery("select * from hbaseT")
                        .select(concat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Date(System.currentTimeMillis())),
                                "-",
                                $"product_id").as("row_key"))


//                val streamTable = tableEnv.fromDataStream(stream)
//                tableEnv.createTemporaryView("stream",streamTable)
                //TODO ST
/*                tableEnv.createTemporaryView("stream",stream)


                tableEnv.executeSql("select * from stream").print()
                tableEnv.executeSql(
                        """
                          |create table mysql(
                          |     product_id bigint,
                          |     product_name String
                          |) with (
                          |     'connector'='jdbc',
                          |     'username'='root',
                          |     'password'='123456',
                          |     'url'='jdbc:mysql://192.168.45.10:3306/ds_db01?useSSL=false',
                          |     'table-name'='product_info'
                          |)
                          |
                        """.stripMargin)

                tableEnv.executeSql(
                        """
                          | create table run1(
                          | product_id string
                          | ) with (
                          | 'connector'='print'
                          | )
                          |""".stripMargin)

                tableEnv.sqlQuery(
                        """
                          |with run as(
                          |select
                          |     str._1, my.product_name, str._2, str._3, str._4
                          |from
                          |     stream as str
                          |join
                          |     mysql as my
                          |on
                          |     str._1 = my.product_id
                          |)
                          |select str._1 from run
                        """.stripMargin)
                        .executeInsert("run1")
                tableEnv.executeSql("select * from run1 limit 50").print()
*/
                //TODO END

//                          |create table run(
//                          |     product_id String,
//                          |     product_name String,
//                          |     PV String,
//                          |     UV String,
//                          |     modified_time String
//                          |)
//                          |insert into run (product_id, product_name, PV, UV, modified_time) select _1, product_name, _2, _3, _4 from runs
//                        stripMargin).print()

//                tableEnv.sqlQuery("select * from StrOnSQL").execute().print()







//                        .process(new [(String,String)]ProcessWindowFunction[(String,String),
//                                                (String,String,String),
//                                                String,
//                                                TimeWindow] {
//                                override def process(
//                                                     key: String,
//                                                     context: ProcessWindowFunction[(String, String), (String, String, String),
//                                                     String,
//                                                     TimeWindow]#Context,
//                                                     iterable: lang.Iterable[(String, String)],
//                                                     collector: Collector[(String, String, String)]): Unit ={
//
//                                }
//                        })
//                        .print()


//                        .process(new ProcessWindowFunction[(String,String),(String,String,String),String,TimeWindow] {
//                                override def process(key: String, context: ProcessWindowFunction[(String, String), (String, String, String), String, TimeWindow]#Context, iterable: lang.Iterable[(String, String)], collector: Collector[(String, String, String)]): Unit = {
//                                        val UV = new util.TreeSet[String]
//                                        var PV = 0
//                                        val iterator = iterable.iterator
//                                        var spID = key
//                                        while(iterator.hasNext)
//                                        {
//                                                val next = iterator.next()
//                                                PV += 1;
//                                                UV.add(next._2)
//                                        }
//                                        collector.collect((spID,UV.size()+"",PV+""))
//                                }})

//                                new ProcessWindowFunction[(String,String),(String,String,String),TimeWindow] {
//                                override def process(context: Context, elements: Iterable[(String, String)], out: Collector[(String,String, String)]): Unit = {
//                                        val UV = new util.TreeSet[String]
//                                        var PV = 0
//                                        val iterator = elements.iterator
//                                        var spID = ""
//                                        while(iterator.hasNext)
//                                        {
//                                                val next = iterator.next()
//                                                PV += 1;
//                                                UV.add(next._2)
//                                                spID = next._1;
//                                        }
//                                        out.collect((spID,UV.size()+"",PV+""))
//                                }



        }

//        class MyWindowFunction extends  ProcessWindowFunction[PCID,Tuple3[String,String,String],String,TimeWindow] {
//                override def process(key: String, context: ProcessWindowFunction[(String, String), Tuple3[String,String,String], String, TimeWindow]#Context, iterable: lang.Iterable[(String, String)], collector: Collector[Tuple3[String,String,String]]): Unit = {
//                        //TODO UV => 用户不重复访问数量
//                        //TODO PV => 含用户重复访问数量
//                        var UV = new util.TreeSet[String]
//                        var PV = 0
//                        val iter = iterable.iterator()
//                        while(iter.hasNext){
//                                val value = iter.next()
//                                PV +=1;
//                                UV.add(value._2)
//                        }
//                        collector.collect((key,UV.size()+"",PV+""))
//                }
//        }



        case class PCID(product_id : String,
                        customer_id : String,
                        modified_time : String)
}
