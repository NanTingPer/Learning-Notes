# Flink

# 1.0 Flink是什么

> ##### Flink核心目标是 数据流上的有状态计算
>
> ##### Flink是一个框架分分布式处理引擎，用于对无界和有界数据流进行有状态计算。

# 2.0 Flink项目创建

- 创建一个Maven项目
- 添加依赖

```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-streaming-java</artifactId>
  <version>1.17.0</version>
</dependency>
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-clients</artifactId>
  <version>1.17.0</version>
</dependency>
```



# 2.1 WordCount

```java
package _001;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCount
{
    public static void main(String[] args) throws Exception
    {
        //创建运行环境
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        //使用文本文件形成DataStream 数据流
        DataStreamSource<String> WordFile = see.readTextFile("C:\\LiMGren\\codeor\\Flink\\src\\main\\java\\_001\\word.txt");

        //调用flatMap进行数据类型转换 转换为(string,1)
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordSplit = WordFile.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>()
        {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception
            {
                //使用空格分组
                String[] Word = s.split(" ");
                for (String string : Word)
                {
                    //调用采集器发送数据 最后会被变量收到
                    collector.collect(new Tuple2<String, Integer>(string, 1));
                }
            }
        });

        //按照Key进行分组
        KeyedStream<Tuple2<String, Integer>, String> All = wordSplit.keyBy(new KeySelector<Tuple2<String, Integer>, String>()
        {
            @Override
            public String getKey(Tuple2<String, Integer> stringIntegerTuple2) throws Exception
            {
                return stringIntegerTuple2.f0;
            }
        });

        //计算
        SingleOutputStreamOperator<Tuple2<String, Integer>> AllOut = All.sum(1);
        AllOut.print();

        //行动算子
        see.execute();

    }
}
```

> ### 输出

```java
7> (哈哈哈哈哈哈,1)
13> (你好,1)
4> (啊,1)
19> (哈哈哈,1)
13> (你好,2)
24> (早上,1)
18> (好,1)
6> (你您你你你你,1)
```

- ##### 有重复？ 这就是流式计算，来一个算一个，而不是一批一批的算

- ##### 且先前的计算结果没有借助外部系统，而是Flink自己存储，这就是有状态的计算

- ##### 前面的状态就是并行度，他在使用CPU的哪个线程 这个数字不会超过你CPU的线程数



> ### 喇嘛大表达式的类型擦除 - 无界流
>
> ##### 在指定IP的那一端开启一个Socket发送程序 再启动该Java线程

```java
public class WordCountLambd
{
    public static void main(String[] args) throws Exception
    {
        //获取运行环境
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        //创建Socket流
        DataStreamSource<String> socketStream = see.socketTextStream("192.168.45.13", 7777);
//        socketStream.flatMap((String str, DataStream.Collector<Tuple2<String,Integer>> a) ->{
//
//        })
        //进行类型转换
        socketStream.flatMap((String str,Collector<Tuple2<String,Integer>> col) ->{
            String[] split = str.split(" ");
            for (String s : split)
            {
                //发送给下游
                col.collect(new Tuple2<String,Integer>(s,1));
            }
            //解决类型擦除
            //手动指定返回类型
        }).returns(Types.TUPLE(Types.STRING,Types.INT))
            .keyBy(kv->kv.f0)
            .sum(1)
        .print();
        see.execute();
    }
}
```



# 2.2 Flink部署

## 2.2.1集群角色

- 客户端(Client) 代码由客户端获取并做转换，之后提交给JobManger
- JobManger 就是Flink集群里的"管事人" 对作业进行中央调度管理，而它获取到要执行的作业后 会进一步处理转换，然后分发任务给TaskManager
- TaskManager 就是干活人，数据的处理操作都是它们来做的



2.2.2集群启动

- 集群规划

| 节点服务器 | hadoop101                 | hadoop101   | hadoop101   |
| ---------- | ------------------------- | ----------- | ----------- |
| 角色       | JobManager<br>TaskManager | TaskManager | TaskManager |

- 下载安装包 https://flink.apache.org/downloads/#all-stable-releases
- 上传服务器 解压

```sh
tar -zxvf flink-1.17.0-bin-scala_2.12.tgz -C /opt/module/
```



> ## 配置 flink-conf

- 老大的位置

```xml
jobmanager.rpc.address: hadoop101
```

- 外面访问的

```xml
jobmanager.bind-host: 0.0.0.0
```



- 小弟

```xml
taskmanager.bind-host: 0.0.0.0
taskmanager.host: hadoop101
```



- web

```xml
rest.address: hadoop101
rest.bind-address: 0.0.0.0
```



> #### 配置 workers(小弟)

```xml
hadoop101
hadoop102
hadoop103
```



> #### 配置 master

```xml
hadoop101:8081
```



> #### 分发目录



> ### 进一步修改

- #### 小弟的conf文件配置

```xml
taskmanager.host: 小弟主机名
```

> ### 启动

```sh
bin/start-cluster.sh
```

访问 192.168.45.13:8081



## 2.2.2 提交作业

> 启动netcat
>
> nc -lk 7777

> - 未找到 (运行)运行配置 => Application => 包含Provided scope
> - 运行配置 => 左下角 => Java草稿 => 打勾将带有"provided"作用域的依赖项目添加到类路径
> - 所谓临时路径 就是要运行的类
> - 所谓主函数 去类里面右键 复制引用 将#main删掉

上传jar包

```sh
bin/flink run -m job地址(hadoop101:8081) -c 全类名 ./jar包名称.jar
```



## 2.2.3 模式介绍

- 会话模式 : 需要先启动一个集群，保持一个会话，在这个会话中通过客户端提交作业。集群启动时资源已经确定，所有提交的作业会竞争集群中的资源。
  - 适用于 单个规模小执行时间短的大量作业



- 单作业模式 : 会话模式因为资源共享会导致很多问题，所以为了更好的隔离资源，可以考虑为每个提交的作业启动一个集群，就是单作业模式 Per-Job
  - 作业完成集群就会关闭 资源也会释放
  - 这些特性使得单作业模式在生产环境运行更加稳定，所以是实际应用的首选模式
  - Flink无法直接这样部署 需要借助Yarn等



- 应用模式 : 前面两种模式下，应用代码都是在客户端上执行，然后由客户端提交给JobManager。
  - 这种方式客户端需要占用大量网络带宽 去下载依赖和把二进制数据发给JobManager 会加重客户端所在节点的资源消耗
  - 不需要客户端，直接把应用提交到JobManager上运行。由JobManager执行应用程序



# 2.2.4 运行模式

> ### Standalone 运行模式

- 独立模式 由Flink独自运行 一般只用于开发测试或者量小的场景下

- 将应用放到lib目录

```sh
bin/standalone-job.sh start --job-classname 全类名
bin/taskmanager.sh stop
bin/standalone-job.sh stop
```



> ### YARN 模式

> Flink会根据运行在JobManger上的作业所需要的Slot数量动态分配TaskManager资源



- ##### 配置HADOOP环境变量

```shell
export HADOOP_HOME=/opt/module/hadoop
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin

#Flink需要
#``飘号 说明执行了一个命令 => hadoop classpath
export HADOOP_CLASSPATH = `hadoop classpath`
#hadoop配置文件目录
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
```

> ##### 需要启动hdfs yarn
>
> ./yarn-session.sh

| -d   | 分离模式（不占用主控制台） |
| ---- | -------------------------- |
| -jm  | 指定内存                   |
| -nm  | 指定应用名称               |
| -qu  | 指定yarn队列               |
| -tm  | 指定小弟的内存             |

> #### 发布作业
>
> 能自动找到Yarn
>
> 未指定-m 会自己去/tmp/.yarn-properties 寻找

```sh
bin/flink run -c 全类名 lib/jar包
```

> 停止yarn-session

echo "stop" | ./bin/yarn-session.sh -id 应用ID





> ## 应用模式部署
>
> 一个main会启一个集群

```sh
bin/flink run-application -t yarn-application -c 全类名 包路径
```



Flink运行时架构 —— Standalone会话模式

- 提交job1 -> 1.1脚本启动执行 -> 客户端

- 客户端内

  - 1.2 解析参数 -> 1.3 封装提交参数 -> Actor通信系统 进行提交
  - 1.4 提交任务、取消或更新任务(Actor -> JobManager)
  
- JobManager内


    - JobManager的Actor通信系统 -> 分发器 -> 2 启动并提交应用 -> JobMastr(老大)


    - JobMastr根据作业情况 -> 3 请求slots(插槽) -> 资源管理器(JobManager内)
    - 资源管理器 -> 4 请求slots -> TaskManager内的通信系统(Actor)

- TaskManager -> 5 提供slots 给JobMaster -> JobMaster

- JobMaster -> 6 分发任务 -> TaskManager

- JobManager -> 7 状态更新 计算结果 -> 客户端



> ### TaskManager内部 小弟内部
>
> 其实是一个一个的插槽

- ### TaskManager(任务管理器)

  - TaskSlot
    - Task
  - TaskSlot
    - Task
  - TaskSlot
    - Task



> ## JobManager重要的3个东西

- ##### 分发器

  - 

- ##### JobMaster

  - ##### 一个Job对应一个JobMaster

  - 一个作业对应一个唯一的JobMaster

- ##### 资源管理器

  - 

- ##### 客户端解析参数 封装提交参数 最终通过 通信系统互相沟通



# 2.2.5 并行度

> 3个人打扫卫生，并行度就是3

- 把一个算子操作，"复制"多份到多个节点，数据来了之后就可以到其中任意一个执行。
- 一个子任务被拆成多个并行的"子任务(subtasks)"，再将它们分发到不同节点，就是真正实现了并行计算。
- 一个算子包含一个或者多个子任务(多少个子任务 并行度就是多少)



- 一个流程序的并行度 就是其所有算子中最大的并行度



## 2.2.5.1 并行度设置

- Socket 只能是1

> ### 算子后面跟上

```JAVA
.setParallelism(2); //2是设置的并行度
```



> ##### 本地WebUi 依赖 本地运行环境 用于测试使用
>
> ##### 在IDEA运行不指定，默认就是电脑线程数

```java
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-runtime-web</artifactId>
  <version>1.17.0</version>
</dependency>
```

```java
StreamExecutionEnvironment localEnvironmentWithWebUI = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
```



> ### 全局设置并行度

```java
see.setParallelism(2);
```



- ### 如果全局设置了，算子也设置了，那么算子优先

- ### 如果代码没指定提交没指定(-p) 那么按照conf内的配置

- 算子并行度 > 全局并行度 > 指定并行度 > 配置内并行度



# 2.2.6 算子链

- ##### 一对一 （One - to - One ,forwarding）

  - ##### 不需要重分区，也不调整数据的顺序

- ##### 重分区 (Redistributing)

  - ##### (KeyBy) 除了一对一以外 其他都是重分区

- ##### 合并算子链

  - 并行度相同的一对一算子操作，可以直接链接在一起形成一个"大"任务(task)
  - 原来的算子成为了真正任务里的一部分
  - 每个task会被一个线程执行

```java
//全局禁用算子链
see.disableOperatorChaining();

//单个算子禁用
算子A.disableOperatorChaining(); //算子A前后不与任何算子串在一起

//以当前开始 开始一个新的算子链
算子A.startNewChain(); //算子A前面不与任何算子串在一起，后面串
```



# 2.2.7 任务槽

- Flink中每个TaskManager都是一个JVM进程，它可以启动多个独立的线程，并行执行多个子任务(subtask)
- TaskManager的计算资源有限，并行的任务越多，每个线程的资源就会越少。
- 为了控制并发量，需要在TaskManager上对每个任务运行所占用的资源做出明确的划分，这就是**任务槽**
- 每个任务槽(task slot)就表示了TaskManager拥有计算资源的一个**固定大小**的子集。这些资源就是用来独立执行一个子任务的

> ------------------------------------------------------------------------------------------------------------------------

​	假如一个TaskManager有3个slot，那么它会将管理的内存平均分成3份，每个slot独自占据一份。

​	在slot上执行一个子任务时，相当于划定了一块内存“专款专用”，就不需要跟来自其他作业的任务取竞争内存资源了。

​	一个吃的多 一个吃的少，没吃完也不能分给吃的多的



## 2.2.7.1 任务槽的设置

> ### conf文件
>
> 隔离内存 不隔离CPU

```yaml
taskmanager.numberOfTaskSlots: 1 #设置的是每个TM的个数 不是总数
```



## 2.2.7.2 共享

- 在同一个作业中，不同任务节点的并行子任务，就可以放到同一个slot上执行(需要同一个作业)

```java
算子.slotSharingGroup("组名"); //为算子指定共享组 不指定就是default
```



# 2.2.8 槽和度的关系

​	**任务槽和并行度都跟程序的并行执行有关，但两者是完全不同的概念。任务槽是静态的概念**，是指TaskManager具有的并发执行能力，可以通过参数taskmanager.numberOfTaskSlots进行配置；而并行度是动态概念，也就是TaskManager运行程序时实际使用的并发能力，可以通过参数parallelism.default进行配置。

- 例子

```java
一个厕所 有3个坑位 表示这个厕所最多只能3个人去用,3是上限是静态的概念 就是slot
    
来了两个人,使用了两个坑位 一起在用 就是并行度
```

```txt
不同算子子任务可以共享一个槽

有一个任务，人为的设置其并行度为1，那么只占用一个slot(槽)

全局并行度设置为9并单独设置Sink并行度为1
假设有3个TM每个TM3个槽
那么每个槽都会有一个非Sink的线程
但是只有一个槽有Sink
```



> ## slot数量与并行度的关系

- slot是一种静态的概念，表示最大的并发上限
  - 并行度是一种动态的概念，表示实际运行占用了几个线程
- 要求 slot数量 大于等于 job并行度(算子最大并行度) job才能运行
  - 注意 : 如果是Yarn模式 动态申请
    - ->申请的TM数量 = job并行度 / 每个TM的slot数量，向上取整
  - 比如session 一开始 0 个TaskManager 0个slot
  - ->提交一个job 并发读10
    - 10/3  会申请4个TM 剩下2个槽

| taskmanager.numberOfTaskSlots: 1 | #设置的是每个TM的个数 不是总数 |
| -------------------------------- | ------------------------------ |



# 2.2.9 作业的提交流程

## 1.0 Standalone会话模式作业提交流程

- 1. 提交作业job1 -> 1.1 脚本启动执行 -> 客户端
- 客户端内
  - 1.2 解析参数 -> 1.3 生产逻辑流图 StreamGraph -> 1.4 生成作业流图JobGraph
  - -> 1.5封装提交参数 -> Actor通信系统
- 1.6 Actor通信系统(客户端) -> JobManager Actor通信系统 (1.6提交任务、取消或更新任务)
- JobManager内
  - Actor通信系统 -> 分发器(收到任务后通知分发器) 
  - 分发器 -> JobMaster(2.1启动并提交应用) JobMaster(2.2 形成执行图 ExecutionGraph)
    - 执行图是客户端作业流图JobGraph形成的
  - JobMaster -> 资源管理器 (3 请求slots)
  - 资源管理器 -> Actor通信系统(TaskManager的) (4 请求slots)
  - 工作节点TaskManager -> JobMaster(提供slots)
  - JobMaster根据执行图将任务分发出去 -> TaskManager
  - 6.2 生成物理流图
  - 9状态更新 计算结果 (JobManager -> 客户端)



- 逻辑流图由客户端解析代码形成
- 逻辑流图转换为作业流图会进行算子聚合 形成算子链



## 2.0 Yarn应用模式作业提交流程

- - run-application -> Yarn的ResourceManager

    - 输入run的时候就是向Yarn的ResourceManager提交请求了

  - Yarn 启动AM 往 NodeManager节点启动一个容器

    - 容器内运行**ApplicationMaster (JobManager)**

  - 2.1 启动分发器

  - 2.2 启动资源管理器

  - 2.3 分发器启动JobMaster

  - 3. 1 JobMaster生成逻辑流图StreamGraph(最原始的流图)

    - 因为Yarn运行模式将客户端的内容都省略交给 JobManager内的JobMaster来做了

  - 3.2 生成作业流图 JobGraph(JobMaster)

  - 3.3 生成执行流图 ExecutionGraph(JobMaster)

  - 3.4 注册 请求 slot (Job Master  ->  资源管理器(flink内的))

    - Flink的资源管理器是个中介
    - 资源管理器 : ResourceManager

  - 资源管理器(Flink) -> 资源管理器(Yarn的)   [申请资源]

  - 5 启动TaskManager -> 往NodeManager 启动容器

    - 容器内运行TaskManager进程

  - TaskManager启动后 往Flink的资源管理器注册slot

  - Flink的资源管理器分配slot

  - 工作节点提供slot(NodeManager内的TaskManager -> JobMaster)

  - JobMaster -> 分配任务 (Job Master -> NodeManager内的TaskManager) 按照执行流图分配

  - 生成物理流图



# 2.3 动手！

# 2.4 DataStreamAPI

> #### DataStreamAPI是Flink的核心层API。一个Flink程序,其实就是对DataStream的各种转换

- Environment(获取执行环境) -> Source(读取数据源) -> Transformation(转换操作) -> Sink(输出)  -> Execute(触发执行)



## 2.4.1 Env

1. 本地运行环境
2. 远程运行环境
3. 自动识别运行环境 直接用这个就行了
   1. 底层调用了getExecutionEnvorpnment
      1. 如果能获取到远程环境 直接使用返回远程环境
      2. 如果获取不到远程环境 使用了 createLocalEnvironment

```java
StreamExecutionEnvironment.getExecutionEnvironment(); //自动识别 远程集群还是本地环境
```

- ### Configuration(配置文件)

```java
//配置文件
Configuration conf = new Configuration();
//Flink封装了一个有关配置的类
//          RestOptions
conf.set(RestOptions.BIND_PORT,"8081");

//使用配置文件获取运行环境 未配置的配置项 按照默认值
StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
```



- ### 运行模式设置 -> 流还是批

- ##### 默认是流处理

1. BATCH => 批
2. AUTOMATIC => 自动
3. STREAMING => 流

```java
//流处理
Env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
//自动
Env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
//批处理
Env.setRuntimeMode(RuntimeExecutionMode.BATCH);
```



- #### 通过命令行设置 一般这样做 程序不写死

```sh
bin/flink run -Dexecution.runtime-mode=BATCH ...
```



## 2.4.2 Env.execute();

> 如果两套逻辑可以使用 executeAsync() 但是不建议

```java
//默认一个env.execute()触发一个flink job
//		一个main方法可以调用多个execute,但是没有意义,指定到第一个就会阻塞

// env.executeAsync() 异步触发 不阻塞
//		=> 一个main方法里excuteAsync()个数 = 生成的flink job数

// yarn-application集群 提交一次，集群里会有几个flinkjob?
// 		=> 取决于调用了几个excuteAsync() 对应就有几个job
//		=> 对应application集群内就有n个job
//		=> 对应jobmanager当中，会有n个JobMaster

//如果后面还有 execute 那么第一个必须是异步
Env.executeAsync();
Env.execute();
```



# 2.5 源算子(Source)

## 1.0 准备工作

为了方便练习 使用WaterSensor作为数据模型

- 所有属性的类型都是可以序列化的
- 有一个无参构造方法
- 类是公共的 public

| 字段名 | 数据类型 | 说明             |
| ------ | -------- | ---------------- |
| id     | String   | 水位传感器类型   |
| ts     | Long     | 传感器记录时间戳 |
| vc     | Integer  | 水位记录         |

> ## 创建JavaBeans 简单的Java对象

- Flink会把这样的类作为一种特殊的POJO数据类型来对待 方便数据的解析和序列化

```java
package SourceDome;

import java.util.Objects;

public class WaterSensor
{
    /**
     * 水位传感器类型
     */
    public String id;

    /**
     * 传感器记录时间戳
     */
    public Long ts;

    /**
     * 水位记录
     */
    public int vc;

    /**
     * 空参 一定要提供
     */
    public WaterSensor()
    {
    }

    /**
     * 构建一个水位传感器
     * @param id 水位传感器类型
     * @param ts 传感器记录时间戳
     * @param vc 水位记录
     */
    public WaterSensor(String id, Long ts, int vc)
    {
        this.id = id;
        this.ts = ts;
        this.vc = vc;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Long getTs()
    {
        return ts;
    }

    public void setTs(Long ts)
    {
        this.ts = ts;
    }

    public int getVc()
    {
        return vc;
    }

    public void setVc(int vc)
    {
        this.vc = vc;
    }

    @Override
    public String toString()
    {
        return "WaterSensor{" +
            "id='" + id + '\'' +
            ", ts=" + ts +
            ", vc=" + vc +
            '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterSensor that = (WaterSensor) o;
        return vc == that.vc && Objects.equals(id, that.id) && Objects.equals(ts, that.ts);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, ts, vc);
    }
}
```



## 1.1 集合/数组数据源

```java
//环境
StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
//获取
DataStreamSource<Integer> ArraysSource = Env.fromCollection(Arrays.asList(1, 1, 2, 34, 5, 64, 6));
//打印
ArraysSource.print();
Env.execute();
```



## 1.2 文件数据源

- #### 导入依赖

```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-connector-files</artifactId>
  <version>1.17.0</version>
</dependency>
```

- 如何知道参数到底要写什么 ? CTRL+H看实现
  - 点进构造器
  - 查看构造器参数
  - 点进构造器参数
  - 如果是接口 查看实现 实现就是参数要的东西

```java
public class FileSourceDome
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
//        FileSource.FileSourceBuilder<String> filesource = FileSource.forRecordStreamFormat(
//            new TextLineInputFormat(),
//            Path.fromLocalFile(
//                new File("C:\\LiMGren\\codeor\\Flink\\src\\main\\java\\_001\\word.txt")
//            )
//        );
        //创建文件 数据源
        FileSource<String> filesource = FileSource
            .forRecordStreamFormat(
                new TextLineInputFormat(),
                new Path("C:\\LiMGren\\codeor\\Flink\\src\\main\\java\\_001\\word.txt")
            ).build();
        //创建数据流
        DataStreamSource<String> FileSourceData = Env.fromSource(filesource, WatermarkStrategy.noWatermarks(), "666");
        //打印
        FileSourceData.print();
        //行动
        Env.execute();
    }
}
```



> ### 写法

```java
// env.fromSource(Source的实现类,Watermark,名字)
```



## 1.3 Kafka数据源

- #### 导入依赖

```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-connector-kafka</artifactId>
  <version>1.17.0</version>
</dependency>
```

> kafka消费者的参数
>
> ​	auto.reset.offsets
>
> ​		earliest : 如果有offset , 从offset继续消费 ；如果没有offset, 从最早消费
>
> ​		lates : 如果有offset ，从offset继续消费;如果没有offset,从最新消费
>
> Flink的消费者参数 offset消费策略 OffsetInitializer
>
> 默认 earliest
>
> ​		earliest : 一定从最早消费
>
> ​		lates : 一定从最新消费

```java
public class KafkaSourceDome
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        KafkaSource<String> Kafka =
            KafkaSource.<String>builder()
                       //设置消费主题
                       .setTopics("Kafka666")
                       //设置Kafka链接
                       .setBootstrapServers("192.168.45.13:9092")
                       //设置反序列化器
                       .setValueOnlyDeserializer(new SimpleStringSchema())
                       //设置便宜 从最新开始
                       .setStartingOffsets(OffsetsInitializer.latest())
                       //设置消费者组
                       .setGroupId("KafkaSource")
                       //得到这个Source
                       .build();
        Env.fromSource(Kafka, WatermarkStrategy.noWatermarks(),"Kafka666").print();
        Env.execute();
    }
}
```



## 1.4 数据生成器源

- #### 导入依赖 我这边没出来

```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-connector-datagen</artifactId>
  <version>1.17.0</version>
</dependency>
```



## 1.5 Flink支持的数据类型

> #### 在Flink中 其需要的类型是TypeInformation类型
>
> 标准POJO 否则会被当作黑盒，无法访问内部
>
> - 所有属性的类型都是可以序列化的
> - 有一个无参构造方法
> - 类是公共的 public

- 等效

```java
//                    .returns(Types.TUPLE(Types.STRING,Types.INT))
            .returns(new TypeHint<Tuple2<String, Integer>>() {})
```



# 2.6 基本转换算子

> #### 标准写法 新建 包存放公共算子
>
> 新建 functions包

## 1.0 Map(转换算子)

在functions包下创建MapFun类 实现MapFunction接口

```java
package functions;

import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;

public class MapFun implements MapFunction<WaterSensor,String>
{

    @Override
    public String map(WaterSensor waterSensor) throws Exception
    {
        return waterSensor.getId();
    }
}
```

> ### 使用

```java
package Transfrom;

import WaterSensors.WaterSensor;
import functions.MapFun;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Map_fun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd2", 2L, 3),
            new WaterSensor("sd3", 2L, 3)
        );

        Data.map(new MapFun()).print();

        Env.execute();
    }
}
```



## 1.1 Filter

> true保留 false丢弃

```java
package Transfrom;

import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Filter_Fun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd1", 343L, 3),
            new WaterSensor("sd2", 2L, 3),
            new WaterSensor("sd3", 2L, 3)
        );

        Data.filter(new FilterFunction<WaterSensor>() {
            @Override
            public boolean filter(WaterSensor waterSensor) throws Exception
            {
                return "sd1".equals(waterSensor.getId());
            }
        }).print();

        Env.execute();
    }
}
```

```java
16> WaterSensor{id='sd1', ts=2, vc=3}
17> WaterSensor{id='sd1', ts=343, vc=3}
```



## 1.2 flatMap

> 对于sd1的数据一进一出
>
> 对于sd2的数据一进多出
>
> 对于sd3的数据一进零出 类似过滤

```java
package Transfrom;

import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class flatMap_Fun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd1", 343L, 3),
            new WaterSensor("sd2", 2L, 3),
            new WaterSensor("sd3", 2L, 3)
        );
        //如果是sd1输出ts
        //如果是sd2输出ts和vc

        Data.flatMap(new FlatMapFunction<WaterSensor, String>() {
            @Override
            public void flatMap(WaterSensor waterSensor, Collector<String> collector) throws Exception
            {
                if("sd1".equals(waterSensor.getId())){
                    collector.collect(waterSensor.getId() + "\tts: " +  waterSensor.ts);
                }else if ("sd2".equals(waterSensor.getId())) {
                    collector.collect(waterSensor.getId() + "\tts: " +  waterSensor.ts);
                    collector.collect(waterSensor.getId() + "\tvc: " +  waterSensor.vc);

                }
            }
        }).print();

        Env.execute();
    }
}
```

```java
12> sd1	ts: 343
13> sd2	ts: 2
11> sd1	ts: 2
13> sd2	vc: 3
```



# 2.7 聚合算子

## 1.0 按键分区(KeyBy)

> #### 相同Key的数据发往同一个分区
>
> #### 按照Key分组	

> ##### 返回的是一个KeydStream 键控流
>
> Keyby不是转换算子，只是对数据进行重分区
>
> 转换算子都能设置并行度 而Keyby不是转换算子

> Keyby分区与分组的关系
>
> ​	keyby 是对数据分组 保证相同key的数据 在同一个分区
>
> ​	分区 : 一个子任务 可以理解为一个分区
>
> 相同组的数据在同一个分区
>
> 一个分区可以有多个组

```java
//学生、教室。学生分了小组 1、2、3组
//教师是物理上的资源 也就是并行度 子任务
//KeyBy就是 老师跟同学说你是第几组
//对于1组的同学 2组的同学 3组的同学都在同个教师
```

```java
package aggreagte;

import WaterSensors.WaterSensor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeyByFun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
//        Env.setParallelism(2);//设置并行度
        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd1", 3L, 7),
            new WaterSensor("sd2", 2L, 8),
            new WaterSensor("sd2", 6L, 5),
            new WaterSensor("sd3", 8L, 14),
            new WaterSensor("sd3", 9L, 5)
        );
        //按照 id 分组
        //KeySelector<输入的类型,分组字段的类型>
        Data.keyBy(new KeySelector<WaterSensor, String>() {
            @Override
            public String getKey(WaterSensor waterSensor) throws Exception
            {
                return waterSensor.getId();
            }
        }).print();
        Env.execute();
    }
}
```

```java
10> WaterSensor{id='sd1', ts=2, vc=3}
10> WaterSensor{id='sd1', ts=3, vc=7}
10> WaterSensor{id='sd2', ts=2, vc=8}
10> WaterSensor{id='sd2', ts=6, vc=5}
10> WaterSensor{id='sd3', ts=8, vc=14}
10> WaterSensor{id='sd3', ts=9, vc=5}
```



## 1.1 简单聚合算子

> > 只有经过Keyby这种处理后才会有这些方法
> >
> > sum / min / max / minBy / maxBy
>
> max\maxby的区别：同min
>
> ​	max 只会取比较字段的最大值，非比较字段保留第一次的值
>
> ​	maxby 取比较字段的最大值，同时非比较字段 取 最大值这条数据的值

```java
max("vc")
10> WaterSensor{id='sd1', ts=2, vc=3}
10> WaterSensor{id='sd1', ts=2, vc=7}
10> WaterSensor{id='sd2', ts=2, vc=8}
10> WaterSensor{id='sd2', ts=2, vc=8}
10> WaterSensor{id='sd3', ts=8, vc=14}
10> WaterSensor{id='sd3', ts=8, vc=14}
```

```java
DataStreamSource<WaterSensor> Data = Env.fromElements(
    new WaterSensor("sd1", 2L, 3),
    new WaterSensor("sd1", 3L, 7),
    new WaterSensor("sd2", 2L, 8),
    new WaterSensor("sd2", 6L, 5),
    new WaterSensor("sd3", 20L, 14),
    new WaterSensor("sd3", 18L, 5)
);
maxBy("vc")
    
10> WaterSensor{id='sd1', ts=2, vc=3}
10> WaterSensor{id='sd1', ts=3, vc=7}
10> WaterSensor{id='sd2', ts=2, vc=8}
10> WaterSensor{id='sd2', ts=2, vc=8}
10> WaterSensor{id='sd3', ts=20, vc=14}
10> WaterSensor{id='sd3', ts=20, vc=14}
```



## 1.2 reduce聚合

> 1. keyby之后调用
> 2. 输入类型 = 输出类型，类型不能变
> 3. 每个key的第一条数据来的时候，不会执行reduce方法，存起来 直接返回
>
> v1是之前计算的结果，v2是来的数据

```java
DataStreamSource<WaterSensor> Data = Env.fromElements(
    new WaterSensor("sd1", 2L, 3),
    new WaterSensor("sd1", 3L, 7),
    new WaterSensor("sd2", 2L, 8),
    new WaterSensor("sd2", 6L, 5),
    new WaterSensor("sd3", 20L, 14),
    new WaterSensor("sd3", 18L, 5)
);
.reduce(new ReduceFunction<WaterSensor>() {
  @Override
  public WaterSensor reduce(WaterSensor t1, WaterSensor t2) throws Exception
  {
      return new WaterSensor(t1.id,t1.ts + t2.ts,t1.vc + t2.vc);
  }
  }).print();

10> WaterSensor{id='sd1', ts=2, vc=3}
10> WaterSensor{id='sd1', ts=5, vc=10}
10> WaterSensor{id='sd2', ts=2, vc=8}
10> WaterSensor{id='sd2', ts=8, vc=13}
10> WaterSensor{id='sd3', ts=20, vc=14}
10> WaterSensor{id='sd3', ts=38, vc=19}
```



## 1.3 UDF用户自定义

> 直接new
>
> 定义一个类
>
> ##### 在实现自定义函数的时候可以选择定义一个构造器 在new的时候传入匹配的参数

# 2.8 富函数

> #### 内含生命周期管理办法
>
> 

> RichXXXFunction : 富函数
>
> 1. 多了生命周期管理方法
>
>    open -> 每个子任务开始时调用
>
>    close -> 每个子任务结束时调用
>
>    ->如果是Flink程序异常退出 close方法将不会被调用
>
>    ->如果正常调用cancel命令 可以close
>
> 2. 多了一个 运行时上下文
>
>    可以获取一些运行时的环境信息，比如 子任务编号、名称、其他...

```java
Env.setParallelism(2);
DataStreamSource<WaterSensor> Data = Env.fromElements(
    new WaterSensor("sd1", 2L, 3),
    new WaterSensor("sd1", 343L, 3),
    new WaterSensor("sd2", 2L, 3),
    new WaterSensor("sd3", 2L, 3)
);
//使用富函数
Data.filter(new RichFilterFunction<WaterSensor>() {
    @Override
    public boolean filter(WaterSensor waterSensor) throws Exception
    {
        return true;
    }
    @Override
    public void open(Configuration parameters) throws Exception
    {
        super.open(parameters);
        //getRuntimeContext 获取运行时上下文
        System.out.println(getRuntimeContext().getTaskNameWithSubtasks() + "调用了open");
    }
    @Override
    public void close() throws Exception
    {
        super.close();
        System.out.println(getRuntimeContext().getTaskNameWithSubtasks() + "调用了close");
    }
}).print();
```



- 可以看到 每个任务线程都会运行一次

```java
Filter -> Sink: Print to Std. Out (1/2)#0调用了open
Filter -> Sink: Print to Std. Out (2/2)#0调用了open
2> WaterSensor{id='sd1', ts=343, vc=3}
1> WaterSensor{id='sd1', ts=2, vc=3}
2> WaterSensor{id='sd3', ts=2, vc=3}
1> WaterSensor{id='sd2', ts=2, vc=3}
Filter -> Sink: Print to Std. Out (2/2)#0调用了close
Filter -> Sink: Print to Std. Out (1/2)#0调用了close
```



# 2.9 分区算子

> ##### shuffle随机分区 : ranom.nextInt(下游算子并行度)
>
> ##### rebalance全局轮询 : nextChannelToSendTo = (nextChannelToSendTo+1) % 下游算子并行度
>
> ##### 	如果是数据源倾斜的场景，source读进来后，调用rebalance,就可以解决 **数据源**的数据倾斜

> ##### rescale() 缩放局部轮询, 局部组队 比rebalance更高效
>
> ##### broadcast 广播 ： 发送给下游所有的子任务
>
> ##### global 全局 : 全部发往第一个子任务 return 0;



- ### 自定义分区器

1. 实现Partitioner接口
2. 重写partition方法 该方法返回要去的分区

```java
package SourceDome;
import org.apache.flink.api.common.functions.Partitioner;
public class 自定义分区器 implements Partitioner
{
    //需要注意的是 任务的起始编号是0
    @Override
    public int partition(Object o, int i)
    {
        if(o.hashCode() % i <= 0) return 0;
        if(o.hashCode() % i > i) return i;
        return o.hashCode() % i - 1;
    }
}
```

```java
package SourceDome;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
public class SocktSourceParititon
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        Env.setParallelism(5);
        DataStreamSource<String> Data = Env.socketTextStream("192.168.45.13", 7777);
        
        //使用自定义分区器
        Data.partitionCustom(
            new 自定义分区器(), k -> k
        ).print();
        Env.execute();
    }
}
```

- 由于我使用hash取余，所以相同的内容肯定在同一个分区

```java
1> 2
2> 666
1> 123
2> 666
1> 444
1> 444
4> 556465
1> 132
1> 123
1> 444
4> 44
```



# 3.0 分流

> Fliter可以简单实现 但是不推荐 效率低
>
> #### 当你发现当前的算子都不能满足你的要求时，可以使用process算子
>
> #### 	这个算子是底层算子，是最灵活的算子

- 先定义一个map,用于将输入数据进行转换，转换为先前自定义的一个类

```java
package functions;
import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;
public class WaterSensorMapFunction implements MapFunction<String, WaterSensor>
{
    @Override
    public WaterSensor map(String s) throws Exception
    {
        String[] split = s.split(",");
        String id;
        Long ts;
        int vc;
        id = split[0];
        try{
            ts = Long.valueOf(split[1]);
        }catch (Exception e){ ts = 0L;}
        try{
            vc = Integer.valueOf(split[2]);
        }catch (Exception e){ vc = 0;}
        return new WaterSensor(id,ts,vc);
    }
}
```

> ## 分流

## 9 process底层算子

```java
package OutStream;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.*;

public class ProcessOutStream
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> DataStream = Env.socketTextStream("192.168.45.13", 7777);

        //将输入的数据转换为WaterSensor
        SingleOutputStreamOperator<WaterSensor> map = DataStream.map(new WaterSensorMapFunction());

        //创建两个支流
        //第一个参数是支流名称
        //第二个参数是该流内的数据类型
        OutputTag<WaterSensor> s1 = new OutputTag<>("s1", Types.POJO(WaterSensor.class));
        OutputTag<WaterSensor> s2 = new OutputTag<>("s2", Types.POJO(WaterSensor.class));

        //分流
        SingleOutputStreamOperator<WaterSensor> Value = map.process(new ProcessFunction<WaterSensor, WaterSensor>()
        {

            //第一个参数是传入的数据
            //第二个参数是上下文
            //第三个参数是给下游发送消息的(主干道)
            @Override
            public void processElement(WaterSensor waterSensor, ProcessFunction<WaterSensor, WaterSensor>.Context context, Collector<WaterSensor> collector) throws Exception
            {
                //如果id是s1那么走支流s1
                if ("s1".equals(waterSensor.getId()))
                {
                    context.output(s1, waterSensor);
                }
                //如果id是s2那么走支流s2
                else if ("s2".equals(waterSensor.getId()))
                {
                    context.output(s2, waterSensor);
                }
                //其他走主干
                else
                {
                    collector.collect(waterSensor);
                }
            }
        });

        //输出主干道内容
        Value.print("主干道\t");
        //输出支流
        SideOutputDataStream<WaterSensor> s1Value = Value.getSideOutput(s1);
        s1Value.print("支s1\t");
        SideOutputDataStream<WaterSensor> s2Value = Value.getSideOutput(s2);
        s2Value.print("支s2\t");

        Env.execute();

    }
}
```

```java
支s1	:14> WaterSensor{id='s1', ts=3, vc=4}
支s2	:15> WaterSensor{id='s2', ts=5, vc=3}
主干道	:16> WaterSensor{id='s3', ts=6, vc=4}
主干道	:17> WaterSensor{id='s5', ts=0, vc=64}
```

![image-20241107211022916](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20241107211022916.png)



# 3.1 合流

## 1.0 联合(Union)

> #### 数据类型必须相同 最简单的合流操作

```java
public class Union_Demo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> Data1 = Env.fromElements(1, 2, 3);
        DataStreamSource<Integer> Data2 = Env.fromElements(11, 22, 33);
        DataStreamSource<String> Data3 = Env.fromElements("1", "2", "3");
        
        //合流方法1 多次union
        DataStream<Integer> union1 = Data1.union(Data2).union(Data3.map(f -> Integer.valueOf(f)));
        
        //合流方法2 传入可变长参
        DataStream<Integer> union2 = Data1.union(Data2, Data3.map(f -> Integer.valueOf(f)));
        union2.print();
        Env.execute();
    }
}
```

```java
4> 3
1> 11
2> 1
2> 22
3> 2
3> 33
11> 2
12> 3
10> 1
```



## 1.1 连接 (Connect)

> 使用Connect合流
>
> - 一次只能连接两条流
>
> - 流的数据可以不一样
> - 连接后可以调用map,flatmap,process来处理

```java
package combine;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class Connect_Demo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Integer> DataS1 =
            Env.socketTextStream("192.168.45.13", 7777)
                .map(f -> {
                    Integer b = 0;
                    try{
                    b = Integer.valueOf(f);}
                    catch (Exception e)
                    {
                        b = 0;
                    }
                    return b;
                });
        DataStreamSource<String> DataS2 = Env.socketTextStream("192.168.45.13", 8888);

        ConnectedStreams<Integer, String> DataCom = DataS1.connect(DataS2);

        SingleOutputStreamOperator<String> connMap = DataCom.map(new CoMapFunction<Integer, String, String>()
        {
            @Override
            public String map1(Integer integer) throws Exception
            {
                return "来源于数字流:" + integer;
            }

            @Override
            public String map2(String s) throws Exception
            {
                return "来源于字符流:" + s;
            }
        });

        connMap.print();

        Env.execute();

    }
}
```

```java
16> 来源于数字流:0
17> 来源于数字流:0
18> 来源于数字流:0
19> 来源于数字流:0
20> 来源于字符流:awdf
21> 来源于字符流:aw
22> 来源于字符流:g
23> 来源于字符流:aw
24> 来源于字符流:21
1> 来源于字符流:21
2> 来源于字符流:1
```



## 1.2 connect案例

```java
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
```

```java
17> 流1 Tuple1:	(1,a1)	<===========>	Tuple2:	(1,aa1,1)
22> 流1 Tuple1:	(3,c)	<===========>	Tuple2:	(3,cc,1)
17> 流2 Tuple1:	(1,aa2,1)	<===========>	Tuple2:	(1,a1)
24> 流1 Tuple1:	(2,b)	<===========>	Tuple2:	(2,bb,1)
17> 流1 Tuple1:	(1,a2)	<===========>	Tuple2:	(1,aa1,1)
17> 流1 Tuple1:	(1,a2)	<===========>	Tuple2:	(1,aa2,1)
```



# 3.2 输出算子

> Flink的DataStream API专门提供了向外部写入数据的方法:addSink。与addSource类似，addSink方法对应着一个"Sink" 算子,主要就是用来实现与外部系统连接、并将数据提交写入；Flink程序中所有对外的输出操作，一般都是利用Sink算子完成的。

## 1.1 File输出

> 必须设置Env.enableCheckpointing 否则文件会一直处于使用状态, .inprogress

```java
package OutTrans;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.CustomSinkOperatorUidHashes;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.RollingPolicy;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;
import java.time.ZoneId;

public class OutFile
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        //必须设置
        Env.enableCheckpointing(200, CheckpointingMode.EXACTLY_ONCE);

        KafkaSource<String> Source = KafkaSource
            .<String>builder()
            .setTopics("Kafka666")
            .setBootstrapServers("192.168.45.13:9092")
            .setGroupId("Kafka777")
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        DataStreamSource<String> KafkaSource = Env.fromSource(Source, WatermarkStrategy.noWatermarks(), "Kafwd66");

        FileSink<String> sink = FileSink
            //泛型方法
            .<String>forRowFormat(
                new Path("C:/temp"),
                new SimpleStringEncoder<>())
            //设置文件前缀和后缀
            .withOutputFileConfig(
                OutputFileConfig.builder()
                    //前缀
                    .withPartPrefix("run-")
                    //后缀
                    .withPartSuffix(".log")
                    .build())
            //按照目录分桶
            .withBucketAssigner(new DateTimeBucketAssigner<>("yyyy_MM_dd_HH", ZoneId.systemDefault()))
            //滚动策略
            .withRollingPolicy(
                DefaultRollingPolicy
                    .builder()
                    //设置多久一次 1分钟
                    .withRolloverInterval(Duration.ofMinutes(1))
                    //设置多大一次 1G
                    .withMaxPartSize(new MemorySize(1024*1024*1024))
                    .build())
            .build();

        KafkaSource.sinkTo(sink);

        Env.execute();
    }
}
```





## 1.2 输出到Kafka

```java
package OutTrans;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchemaBuilder;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class OutKafka
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        ///如果是精准一次必须设置
        ///设置了精准一次必须指定事务前缀
        ///必须指定事务超时时间
        Env.enableCheckpointing(500, CheckpointingMode.EXACTLY_ONCE);

        DataStreamSource<String> SocketSource = Env.socketTextStream("192.168.45.13", 7777);


        KafkaSink<String> SinkKafka = KafkaSink.<String>builder()
                   .setBootstrapServers("192.168.45.13:9092")
                   .setRecordSerializer(KafkaRecordSerializationSchema
                       .builder()
                       //设置Key序列化器
                       .setKeySerializationSchema(new SimpleStringSchema())
                       //设置Value序列化器
                       .setValueSerializationSchema(new SimpleStringSchema())
                       //设置要写入的Topic名称
                       .setTopic("Kafka666")
                       .build())
                   //精准一次
                   .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                   //设置事务ID前缀
                   .setTransactionalIdPrefix("Kafka")
                   //设置事务超时时间
                   //事务超时时间 小于15分 大于上面设置的
                   .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 10 * 60 * 1000 + "")
                   .build();

        DataStreamSink<String> run = SocketSource.sinkTo(SinkKafka);

        Env.execute();
    }
}
```



## 1.2 指定Kafka的Key

> 如果要指定写入kafka的Key
>
> 可以自定义反序列器：
>
> 1. 实现一个接口，重写序列化方法
> 2. 指定key,转成字节数组
> 3. 指定value 转成字节数组
> 4. 返回一个producerRecord对象，把key value放进去

```java
package OutTrans;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Bytes;

import javax.annotation.Nullable;

public class OutKafkaKey
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        ///如果是精准一次必须设置
        ///设置了精准一次必须指定事务前缀
        ///必须指定事务超时时间
        Env.enableCheckpointing(500, CheckpointingMode.EXACTLY_ONCE);

        DataStreamSource<String> SocketSource = Env.socketTextStream("192.168.45.13", 7777);


        KafkaSink<String> SinkKafka = KafkaSink.<String>builder()
                   .setBootstrapServers("192.168.45.13:9092")
                   .setRecordSerializer(new KafkaRecordSerializationSchema<String>() {
                       /**
                        *
                        * @param element 要被序列化的内容
                        * @param context context to possibly determine target partition
                        * @param timestamp timestamp
                        * @return 。
                        */
                       @Nullable
                       @Override
                       public ProducerRecord<byte[], byte[]> serialize(String element, KafkaSinkContext context, Long timestamp)
                       {
                           //取前3个字符做Key
                           StringBuffer sb = new StringBuffer();
                           sb.append( element.charAt(0));
                           sb.append( element.charAt(1));
                           sb.append( element.charAt(2));
                           byte[] key = sb.toString().getBytes();
                           byte[] value = element.getBytes();
                           //指定分区 key value
                           return new ProducerRecord<>("Kafka666",key,value);
                       }
                   })
                   //精准一次
                   .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                   //设置事务ID前缀
                   .setTransactionalIdPrefix("Kafka")
                   //设置事务超时时间
                   //事务超时时间 小于15分 大于上面设置的
                   .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 10 * 60 * 1000 + "")
                   .build();

        SocketSource.sinkTo(SinkKafka);

        Env.execute();
    }
}
```



## 1.3 MySQL JDBC

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>5.1.49</version>
</dependency>

<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-connector-jdbc</artifactId>
  <version>3.2.0-1.18</version>
</dependency>
```

> 创建数据库 test 内创建一个表 ws
>
> create table ws(
>
> id varchar(100) NOT NULL,
>
> ts bigint(20) default null,
>
> vc int(11) default null,
>
> primary key(id)
>
> )engine=InnoDB default charset=utf8

```java
package OutTrans;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OutJDBC
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        //Socket数据源
        SingleOutputStreamOperator<WaterSensor> socketSource = Env.socketTextStream("192.168.45.13", 7777)
                                                         .map(new WaterSensorMapFunction());

        SinkFunction<WaterSensor> jdbc = JdbcSink.sink("insert into ws values(?,?,?)", new JdbcStatementBuilder<WaterSensor>()
        {
            /**
             *
             * @param preparedStatement 占位符填充
             * @param waterSensor   传入的数据
             * @throws SQLException >
             */
            @Override
            public void accept(PreparedStatement preparedStatement, WaterSensor waterSensor) throws SQLException
            {
                //填充上面的? 索引从1开始
                preparedStatement.setString(1, waterSensor.getId());
                preparedStatement.setLong(2, waterSensor.getTs());
                preparedStatement.setInt(3, waterSensor.getVc());
            }
            //创建JDBC配置文件
        }, new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
            .withPassword("123456")
            .withUrl("jdbc:mysql://192.168.45.13:3306/test?useSSL=false")
            .withUsername("root")
            //超时时间
            .withConnectionCheckTimeoutSeconds(60)
            .build());

        //将数据写入Sink
        socketSource.addSink(jdbc);

        Env.execute();
    }
}
```



## 1.4 自定义Sink

> 1，定义一个类 实现SinkFunction接口 / RichSinkFunction
>
> ​	创建链接要在RichSinkFunction里面写 （open方法）
>
> ​	销毁链接 close写里面
>
> 2，重写invoke方法 
>
> ​	sink的核心逻辑就写里面



# 3.3 窗口

> ​	在批处理统计中，可以等待一批数据都到齐后，统一处理。但是在实时处理统计钟，是来一条就处理一条，那么怎么统计最近一段时间内的数据呢？ 引入窗口

> ​	窗口就是划定一段时间范围，也就是"**时间窗**";对在这范围内的数据进行处理，就是所谓的**窗口计算**。所以窗口和时间往往是分不开的。

> 将无限的数据，切成有限的数据块处理 就是所谓的窗口

- #### 例子

```java
// 有一个水龙头 源源不断的流水
// 我们将水用水桶装起来再统计
// 这一个一个水桶 就是窗口
```



- ### 正确理解

> ​	在Flink中，窗口其实**并不是一个"框"**，应该把窗口理**解成一个”桶“**。在Flink中，窗口可以把流切割成有限大小的**多个"存储桶"**（bucket）;每个数据都会分发到对应的桶中，**当到达窗口结束时间时，就对每个桶中收集的数据进行计算处理**

- ### tip

> ​	Flink中窗口并不是静态准备好的，而是**动态创建**——当有落在这个窗口区间范围内的数据达到时，才创建对应的窗口。另外，到达窗口结束时间时，窗口就会触发计算并关闭，事实上“触发计算”和“窗口关闭”两个行为也可以分开



## 3.3.1 窗口的类型

- 按照驱动类型分

  - 时间窗口(Time Window) => 每小时装一桶，过了就不装了
  - 计数窗口(Count Window) => 不管时间，装满了才换下一个

- 按照窗口分配数据的规则分类

  - 滚动窗口(Tumbling Windows) => 窗口之间没有重叠，也不会有间隔 首尾相接
    - 可以用来对每个时间段做聚合统计
  - 滑动窗口(Sliding Windows) => 大小是固定的 窗口之间并不是首尾相接的 而是可以"错开"一定位置
    - 0:1,	0.30:1.30,	1.00:2.00
  - 会话窗口(SessionWindows)
    - 基于"会话"(session)来对数据进行分组

  ```java
  //会话窗口
  //只要有数据来，数据不断 他们就是一个会话
  //如果最后一个数据过来后 设定的时间内没有数据过来 那么会话就断了
  //再来就开启新的会话
  ```

  - 全局窗口(GlobalWindows) 窗口没有结束的时候 默认页是不会触发计算的
