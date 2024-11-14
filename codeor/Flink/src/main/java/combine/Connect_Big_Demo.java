package combine;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.*;

/***
 * 要求 :
 *      <br>将每条Key对应的值匹配上
 *      <br>实现相互匹配的效果 两条流，不一定谁的数据先来
 *          <br>1 每条流 有数据来，存到一个变量中
 *              <br>hashmap
 *              <br>-> key = id 第一个字段值
 *              <br>-> value = List<数据>
 *          <br>每条流有数据来的时候，除了存变量中 不知道对方是否有匹配的数据 要取另一条流存的变量中 查找是否有匹配上的
 */
public class Connect_Big_Demo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<Tuple2<Integer, String>> Data1 = Env.fromElements(
            new Tuple2<>(1, "a1"),
            new Tuple2<>(1, "a2"),
            new Tuple2<>(2, "b"),
            new Tuple2<>(3, "c")
        );

        DataStreamSource<Tuple3<Integer, String, Integer>> Data2 = Env.fromElements(
            new Tuple3<>(1, "aa1", 1),
            new Tuple3<>(1, "aa2", 1),
            new Tuple3<>(2, "bb", 1),
            new Tuple3<>(3, "cc", 1)
        );

        //在合流后必须进行KeyBy,不然数据散乱在各个线程 无法交互
        ConnectedStreams<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>> connect = Data1.connect(Data2).keyBy(f -> f.f0,v -> v.f0);

        connect.process(new CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>() {
            /**
             * 用于存放流1的数据
             */
            Map<Integer,List<Tuple2<Integer,String>>> Value1 = new HashMap<>();

            /**
             * 用于存放流2的数据
             */
            Map<Integer,List<Tuple3<Integer, String, Integer>>> Value2 = new HashMap<>();

            /**
             * @param V1 传入的值
             * @param context 上下文
             * @param collector 发往下游
             * @throws Exception x
             */
            @Override
            public void processElement1(Tuple2<Integer, String> V1, CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>.Context context, Collector<String> collector) throws Exception
            {
                //containsKey如果包含 返回True
                //如果不包含某个键 那么添加
                if(!Value1.containsKey(V1.f0))
                {
                    List<Tuple2<Integer,String>> v1list = new ArrayList<>();
                    v1list.add(V1);
                    Value1.put(V1.f0,v1list);
                }
                //否则就是包含 直接往键值List塞这个V1
                else
                {
                    //返回的是引用类型
                    List<Tuple2<Integer, String>> v1list2 = Value1.get(V1.f0);
                    v1list2.add(V1);
                }

                //遍历流2的Map进行匹配
                if(Value2.containsKey(V1.f0))
                {
                    for (Tuple3<Integer, String, Integer> V2List : Value2.get(V1.f0))
                    {
                        collector.collect("流1 Tuple1:\t" + V1 + "\t<===========>\t" + "Tuple2:\t"  + V2List);
                    }
                }

            }

            @Override
            public void processElement2(Tuple3<Integer, String, Integer> v2, CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>.Context context, Collector<String> collector) throws Exception
            {
                //先判断Map是否存在该键
                if(!Value2.containsKey(v2.f0))
                {
                    //不存在直接创建
                    List<Tuple3<Integer, String, Integer>> list = new ArrayList<>();
                    list.add(v2);
                    Value2.put(v2.f0,list);
                }
                else
                {
                    //存在直接放入
                    Value2.get(v2.f0).add(v2);
                }
                //遍历流1
                if(Value1.containsKey(v2.f0))
                {
                    for (Tuple2<Integer, String> listv2 : Value1.get(v2.f0))
                    {
                        collector.collect("流2 Tuple1:\t" + v2 + "\t<===========>\t" + "Tuple2:\t"  + listv2);
                    }
                }
            }
        }).print();


        Env.execute();


    }
}
