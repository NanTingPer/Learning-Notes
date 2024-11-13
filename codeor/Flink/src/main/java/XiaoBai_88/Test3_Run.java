package XiaoBai_88;

import FlinkSQL.TabelEnv;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.client.program.StreamContextEnvironment;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;


public class Test3_Run
{
    public static void main(String[] args) throws Exception
    {
        /*
        StreamExecutionEnvironment Env = StreamContextEnvironment.getExecutionEnvironment();
        StreamTableEnvironment TableEnv = StreamTableEnvironment.create(Env);

        //TODO 创建一个KafkaSource
        KafkaSource<String> kafkaSource = KafkaSource
            .<String>builder()
                .setTopics("fact_order_detail")
                .setBootstrapServers("192.168.45.13:9092")
                .setGroupId("kafkaMaster")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        //TODO 读取数据
        DataStreamSource<String> kafkaData = Env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafkaMaster");
        //TODO 除杂
        SingleOutputStreamOperator<Row> FinAll = kafkaData
            .map(f ->
            {
                return f.replaceAll("[{}\"]", "");
            })
            .map(f ->
            {
                String[] split = f.split(",");
                ArrayList<String> list = new ArrayList<>();
                for (String s : split)
                {
                    String[] split1 = s.split(":");
                    list.add(split1[1]);
                }
                return Row.of(list.toArray());
            });

        TableEnv.fromDataStream(FinAll).execute().print();
        */

/*
        //TODO 使用SQL创建kafka表
        TableResult tableResult = TableEnv.executeSql("create table kafkaTable(" +
            "order_id bigint," +
            "order_sn string," +
            "customer_id string," +
            "shipping_user string," +
            "province string," +
            "city string," +
            "address string," +
            "order_source bigint," +
            "payment_method bigint," +
            "order_money double," +
            "district_money double," +
            "shipping_money double," +
            "payment_money double," +
            "shipping_comp_name string," +
            "shipping_sn string," +
            "create_time timestamp," +
            "shipping_time timestamp," +
            "pay_time timestamp," +
            "receive_time timestamp," +
            "order_status string," +
            "order_point bigint," +
            "invoice_title string," +
            "modified_time timestamp" +
            ") with (" +
            "'connector'= 'kafka'," +
            "'topic'='fact_order_master'," +
            "'properties.bootstrap.server'='192.168.45.13:9092'," +
            "'properties.group.id'='group-test'," +
            "'format'='json'," +
            "'scan.startup.mode'='earliest-offset'" +
            ")"
        );
        CloseableIterator<Row> collect = tableResult.collect();
        while (collect.hasNext())
        {
            System.out.println(collect.next());
        }
*/
        //TODO 查表
//        TableEnv.sqlQuery("select * from kafkaTable").execute().print();

//        TableEnv.from("kafkaTable").select(new SqlCallExpression("*")).execute().print();


        // 设置流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置并行度
        env.setParallelism(1);
        // 启用检查点
        env.enableCheckpointing(5000);

        // 表执行环境
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);
        // 创建源表
        String source_kafka_table_create =
              "CREATE TEMPORARY TABLE order_master_kafka (" +
              "   order_id BIGINT," +
              "   order_sn STRING," +
              "   customer_id BIGINT," +
              "   shipping_user STRING," +
              "   province STRING," +
              "   city STRING," +
              "   address STRING," +
              "   order_source INTEGER," +
              "   payment_method INTEGER," +
              "   order_money DOUBLE," +
              "   district_money DOUBLE," +
              "   shipping_money DOUBLE," +
              "   payment_money DOUBLE," +
              "   shipping_comp_name STRING," +
              "   shipping_sn STRING," +
              "   create_time STRING," +
              "   shipping_time STRING," +
              "   pay_time STRING," +
              "   receive_time STRING," +
              "   order_status STRING," +
              "   order_point INTEGER," +
              "   invoice_title STRING," +
              "   modified_time STRING" +
              ") WITH ('connector'='kafka'," +
              "        'topic'='fact_order_master'," +
              "        'properties.bootstrap.servers'='192.168.45.13:9092'," +
              "        'properties.group.id'='group-test'," +
              "        'format'='json'," +
              "        'scan.startup.mode'='earliest-offset'" +
              "  )";
    /* scan.startup.mode的几个值：
       (1) group-offsets: 默认选项是group-offset，表示从ZK/kafka代理中最后提交的偏移量中消费
       (2) earliest-offset: start from the earliest offset possible.
       (3) latest-offset: start from the latest offset.
       (4) timestamp: start from user-supplied timestamp for each partition.
       (5) specific-offsets: start from user-supplied specific offsets for each partition.
     */
        tableEnvironment.executeSql("DROP TABLE IF EXISTS order_master_kafka");

        //TODO 执行SQL
            tableEnvironment
            .executeSql(source_kafka_table_create);

            tableEnvironment.sqlQuery("select * from order_master_kafka").execute().print();
//        tableEnvironment.sqlQuery("select * from order_master_kafka").execute().print();

//        env.execute();
//        env.execute();
    }
}
