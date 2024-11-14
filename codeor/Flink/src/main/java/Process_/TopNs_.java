package Process_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO 使用KeyBy计算
public class TopNs_
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        Env.setParallelism(1);

        //TODO 获取数据流
        Env.socketTextStream("192.168.45.13",7777)
                //TODO 将数据转换为 WaterSensor类型
               .map(new WaterSensorMapFunction())
                //TODO 设置水位线字段
               .assignTimestampsAndWatermarks(
                   WatermarkStrategy
                   .<WaterSensor>forMonotonousTimestamps()
                   .withTimestampAssigner((ws,l) -> ws.getTs() * 1000L))
                //TODO 按照水位线进行Key 因为我们要统计水位线出现的次数
               .keyBy(f -> f.getVc())
                //TODO 设置滑动窗口，因为5秒要大于一次
               .window(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
                //TODO 进行AGG聚合统计个数
               .aggregate(
                   new MyAggFun(),
                   new MyProcessWindow())
                //TODO 将统计出来的数量 按照窗口分组 因为要窗口隔离
               .keyBy(f -> f.f2)
                //TODO 最终输出前指定名次
               .process(new MyKeyProcess(2))
               .print();
        Env.execute();
    }


    // TODO 计数 输出计数结果
    public static class MyAggFun implements AggregateFunction<WaterSensor,Integer,Integer>
    {

        //TODO 初始化
        public Integer createAccumulator()
        {
            return 0;
        }

        //TODO 累加逻辑
        public Integer add(WaterSensor waterSensor, Integer integer)
        {
            return integer+1;
        }

        //TODO 返回
        public Integer getResult(Integer integer)
        {
            return integer;
        }

        public Integer merge(Integer integer, Integer acc1)
        {
            return 0;
        }
    }

    /**
     * 第一个泛型 输入的类型 是Agg的输出<br>
     * 第二个泛型 输出的类型 Tuple3 => vc,数量,窗口结束时间<br>
     * 第三个泛型 Key 的类型 vc => Int<br>
     * 第四个泛型 Win 的类型 TimeWindow
     */
    public static class MyProcessWindow extends ProcessWindowFunction<Integer, Tuple3<Integer,Integer,Long>, Integer, TimeWindow>{
        public void process(Integer integer, ProcessWindowFunction<Integer, Tuple3<Integer, Integer, Long>, Integer, TimeWindow>.Context context, Iterable<Integer> elements, Collector<Tuple3<Integer, Integer, Long>> out) throws Exception
        {
            // TODO 取得数据 只有一条数据 直接获取即可
            Integer next = elements.iterator().next();
            long endTime = context.window().getEnd();
            // TODO 将聚合的结果进行包装后交给下一步
            // TODO 第一个是水位 第二个是个数 第三个是属于的窗口
            out.collect(new Tuple3<>(integer,next,endTime));
        }
    }

    /**
     * 第一个泛型 Key的类型<br>
     * 第二个泛型 输入的类型<br>
     * 第三个泛型 输出的类型
     */
    public static class MyKeyProcess extends KeyedProcessFunction<Long,Tuple3<Integer,Integer,Long>,String>{
        //TODO 用来存储要取前几名
        private int ForSum;
        private MyKeyProcess(){};
        public MyKeyProcess(int sum)
        {
            ForSum = sum;
        }

        private Map<Long, List<Tuple3<Integer,Integer,Long>>> e = new HashMap<>();
        public void processElement(Tuple3<Integer, Integer, Long> value, KeyedProcessFunction<Long, Tuple3<Integer, Integer, Long>, String>.Context ctx, Collector<String> out) throws Exception
        {
            //TODO 值过来 判断在不在Map里面
            Long WinTag = value.f2;
            if(e.containsKey(WinTag)){
                //TODO 在里面
                e.get(WinTag).add(value);
            }else {
                //TODO 不在里面
                ArrayList<Tuple3<Integer,Integer,Long>> list = new ArrayList<>();
                list.add(value);
                e.put(WinTag, list);
            }

            //TODO 注册一个事件时间计时器 触发时间为End+1
            ctx.timerService().registerEventTimeTimer(WinTag + 1);
        }

        @Override
        public void onTimer(long timestamp, KeyedProcessFunction<Long, Tuple3<Integer, Integer, Long>, String>.OnTimerContext ctx, Collector<String> out) throws Exception
        {
            super.onTimer(timestamp, ctx, out);
            //TODO 取出Key 获取数据 然后取得Map内的数据
            Long WinTag = ctx.getCurrentKey();
            List<Tuple3<Integer, Integer, Long>> Data = e.get(WinTag);

            //TODO 对数据进行排序
            Data.sort((o1,o2) -> o2.f1 - o1.f1);

            //TODO 输出数据
            StringBuilder stb = new StringBuilder();
            stb.append("====================\n");
            for(int i =0;i < Math.min(ForSum,Data.size());i++)
            {
                Tuple3<Integer, Integer, Long> eq = Data.get(i);
                stb.append("第" + (i+1) + "名:\t" + eq.f0 + "," + eq.f1 + "\n");
            }
            stb.append("====================\n");
            out.collect(stb.toString());
            e.clear();
        }
    }
}
