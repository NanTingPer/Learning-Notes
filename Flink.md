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



## 1.0 窗口的类型

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



> #### 如果窗口没有按照Key分组，那么并行度强行为1
>
> #### 如果按照Key分组，一种Key就是一个窗口

## 1.1 窗口API

> 1. 需要先指定窗口分配器 如何开窗
> 2. 指定窗口函数 窗口内的计算逻辑

```java
        SingleOutputStreamOperator<WaterSensor> socketData = Env.socketTextStream("192.168.45.13", 7777)
                                                         .map(new WaterSensorMapFunction());
        KeyedStream<WaterSensor, String> Keyby = socketData.keyBy(f -> f.getId());

        //会话窗口 超时时间为10秒
        socketData.windowAll(ProcessingTimeSessionWindows.withGap(Time.seconds(10)));

        //滚动窗口 窗口长度10秒
        socketData.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        //滑动窗口 窗口长度10秒，间距5秒会出一个新的窗口
        socketData.windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10),Time.seconds(5)));

        //计数窗口与计数滑动窗口
        socketData.countWindowAll(10);
        socketData.countWindowAll(10,5);

        //全局窗口 需要自定义
        socketData.windowAll(GlobalWindows.create());
```



> ## 聚合

```java
package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class reduceWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> sockData =
            Env.socketTextStream("192.168.45.13", 7777)
                .map(new WaterSensorMapFunction());

        WindowedStream<WaterSensor, String, TimeWindow> window =
            //滚动窗口 10秒
            sockData.keyBy(f -> f.getId())
                    .window(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        //只有达到窗口设定的时间，才会进行输出，而不是计算一条输出一条
        SingleOutputStreamOperator<WaterSensor> reduce = window.reduce(new ReduceFunction<WaterSensor>()
        {
            /**
             * 对数据进行聚合
             * @param waterSensor 先前的数据
             * @param t1 传入的数据
             * @return 计算后的数据
             * @throws Exception e
             */
            @Override
            public WaterSensor reduce(WaterSensor waterSensor, WaterSensor t1) throws Exception
            {
                return new WaterSensor(waterSensor.getId(), t1.getTs() + waterSensor.getTs(), t1.getVc() + waterSensor.getVc());
            }
        });
        reduce.print();
        Env.execute();
    }
}
```



## 1.2 聚合函数 AggregateFunction

> #### 输入 聚合 输出
>
> #### 3种状态的数据类型可以不同

```java
package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class AggWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<WaterSensor> sockData =
            Env.socketTextStream("192.168.45.13", 7777)
               .map(new WaterSensorMapFunction());
        WindowedStream<WaterSensor, String, TimeWindow> window =
            //滚动窗口 10秒
            sockData.keyBy(f -> f.getId())
                    .window(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        SingleOutputStreamOperator<String> aggregate = window.aggregate(new AggregateFunction<WaterSensor, Integer, String>()
        {

            /**
             * 初始化累加器
             * @return 初始值
             */
            @Override
            public Integer createAccumulator()
            {
                System.out.println("初始化器被调用了");
                return 0;
            }

            /**
             * 聚合逻辑
             * @param waterSensor 传入值
             * @param integer 累加器的值
             * @return 聚合后的值
             */
            @Override
            public Integer add(WaterSensor waterSensor, Integer integer)
            {
                System.out.println("聚合被调用了");
                return waterSensor.getVc() + integer;
            }

            /**
             * 最终返回值
             * @param integer 累加器的值
             * @return 最终值
             */
            @Override
            public String getResult(Integer integer)
            {
                System.out.println("值被获取了");
                return integer.toString();
            }

            /**
             * 会话窗口才会用到的
             * @param integer
             * @param acc1
             * @return
             */
            @Override
            public Integer merge(Integer integer, Integer acc1)
            {
                return 0;
            }
        });

        aggregate.print();

        Env.execute();
    }
}
```



## 1.3 全窗口函数

```java
package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class processWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> socketData =
            Env.socketTextStream("192.168.45.13", 7777)
            .map(new WaterSensorMapFunction());


        KeyedStream<WaterSensor, String> KeyData = socketData.keyBy(f -> f.getId());

        KeyData.window(TumblingProcessingTimeWindows.of(Time.seconds(10))).process(
            /**
             * WaterSensor 输入的类型
             * String 输出的类型
             * String Key的类型
             * TimeWindow 窗口的类型
             */
            new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {

                /**
                 * 只有在窗口到时的时候才会触发一次
                 * @param s Key
                 * @param context 上下文
                 * @param iterable 窗口内的所有数据
                 * @param collector 发送器
                 * @throws Exception e
                 */
                @Override
                public void process(String s, ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception
                {
                    //context.window可以获取窗口上下文
                    long startTime = context.window().getStart();
                    long endTime = context.window().getEnd();
                    //时间转换
                    String start = DateFormatUtils.format(startTime, "HH:mm:ss");
                    String end = DateFormatUtils.format(endTime, "HH:mm:ss");

                    System.out.println("===================窗口起始:" + start + " ===================");
                    System.out.println("===================窗口Key:" + s + " 内容===================");
                    Iterator<WaterSensor> iterator = iterable.iterator();
                    while (iterator.hasNext()){
                        System.out.println(iterator.next());
                    }
                    System.out.println("===================窗口结束:" + end + " ===================");
                }
            }
        ).print();

        Env.execute();

    }
}
```

```java
===================窗口起始:10:19:50 ===================
===================窗口Key:s1 内容===================
WaterSensor{id='s1', ts=0, vc=0}
WaterSensor{id='s1', ts=2, vc=2}
WaterSensor{id='s1', ts=2, vc=3}
WaterSensor{id='s1', ts=23, vc=2}
===================窗口结束:10:20:00 ===================
```



## 1.4 双函数聚合

```java
package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class AggManyWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> socketData =
            Env.socketTextStream("192.168.45.13", 7777)
               .map(new WaterSensorMapFunction());

        socketData.keyBy(f->f.getId())
                  .window(ProcessingTimeSessionWindows
                  .withGap(Time.seconds(10)))
                  .aggregate(new MyAggFunction(),new MyProcessFunction())
                  .print();

        Env.execute();
    }

    /**
     * 1,传入的值的类型<br>
     * 2,过程钟值的类型<br>
     * 3,最终返回的类型<br>
     */
    public static class MyAggFunction implements AggregateFunction<WaterSensor,Integer,String>{

        public Integer createAccumulator()
        {
            System.out.println("初始化聚合值");
            return 0;
        }

        public Integer add(WaterSensor waterSensor, Integer integer)
        {
            System.out.println("触发聚合逻辑");
            //返回总vc值
            return integer + waterSensor.getVc();
        }

        public String getResult(Integer integer)
        {
            System.out.println("取得聚合结果");
            return integer.toString();
        }

        public Integer merge(Integer integer, Integer acc1)
        {
            return 0;
        }
    }


    /**
     * 由于是双函数调用<br>
     * 这个函数最终被传入的值是Agg的返回值<br>
     *<br>
     * 1，输入类型<br>
     * 2，输出类型<br>
     * 3，Key 类型<br>
     * 4，窗口类型<br>
     */
    public static class MyProcessFunction extends ProcessWindowFunction<String,String,String, TimeWindow>{

        public void process(String s, ProcessWindowFunction<String, String, String, TimeWindow>.Context context, Iterable<String> iterable, Collector<String> collector) throws Exception
        {
            //获取上下文 获取窗口的起始结束时间
            TimeWindow window = context.window();
            String startTime = DateFormatUtils.format(window.getStart(), "HH:mm:ss");
            String endTime = DateFormatUtils.format(window.getEnd(), "HH:mm:ss");

            System.out.println("窗口"+s+"开始时间: " + startTime);
            System.out.println("窗口"+s+"结束时间: " + endTime);
            System.out.println("窗口"+s+"最终结果: " + iterable.toString());

            collector.collect(iterable.toString() + "是Process哦");
        }
    }
}
```



- ### 输入s1,2,3

> #### 最终结果由Process接管

```java
初始化聚合值
触发聚合逻辑
取得聚合结果
窗口s1开始时间: 11:12:06
窗口s1结束时间: 11:12:16
窗口s1最终结果: [3]
24> [3]是Process哦
```



## 1.5 会话窗口

> .window(ProcessingTimeSessionWindows)
>
> .withDynamicGap => 动态时长，另一个是指定时长

```java
package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SessionWindowTimeGapExtractor;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class SessionWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<WaterSensor> socketData = Env.socketTextStream("192.168.45.13", 7777)
                                                                .map(new WaterSensorMapFunction());
        KeyedStream<WaterSensor, String> Keyby = socketData.keyBy(f -> f.getId());

        Keyby
            //使用会话窗口
            .window(ProcessingTimeSessionWindows
                //动态会话时常 单位是ms毫秒
            .withDynamicGap(new SessionWindowTimeGapExtractor<WaterSensor>()
            {
                @Override
                public long extract(WaterSensor waterSensor)
                {
                    return waterSensor.getVc() * 1000L;
                }
            }))
            .process(new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>()
                     {
                         public void process(String s, ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception
                         {
                             //context.window可以获取窗口上下文
                             long startTime = context
                                 .window()
                                 .getStart();
                             long endTime = context
                                 .window()
                                 .getEnd();
                             //时间转换
                             String start = DateFormatUtils.format(startTime, "HH:mm:ss");
                             String end = DateFormatUtils.format(endTime, "HH:mm:ss");

                             System.out.println("===================窗口起始:" + start + " ===================");
                             System.out.println("===================窗口Key:" + s + " 内容===================");
                             Iterator<WaterSensor> iterator = iterable.iterator();
                             while (iterator.hasNext())
                             {
                                 System.out.println(iterator.next());
                             }
                             System.out.println("===================窗口结束:" + end + " ===================");

                         }
                     }
            ).print();

        Env.execute();
    }
}
```

> 输入
>
> s1,3,4
>
> s1,2,3

> 输出
>
> ===================窗口起始:11:36:05 ===================
> ===================窗口Key:s1 内容===================
> WaterSensor{id='s1', ts=3, vc=4}
> WaterSensor{id='s1', ts=2, vc=3}
> ===================窗口结束:11:36:12 ===================

## 1.6 会话窗口

> 对于会话窗口而言
>
> 滑动步长就是每次触发计算的间隔
>
> 每隔步长，一定有一个窗口触发和输出
>
> （5，2）
>
> 1，2，3，4，5，6
>
> 2，3，4，5，6 => 到这个窗口的时候，他已经无法包含到1了

## 1.7 触发器 Trigger

> 用来触发计算和输出

```java
/**
	TODO 1,窗口什么时候触发 或者输出
		时间进展 >= 窗口的最大时间戳（end - 1ms）

/
```

## 1.8 移除器 Evictor

> ​	Evictor是个接口	
>
> ​	移除器主要用来定义移除某些数据的逻辑。基于WindowedStreamd调用.evictor()方法，就可以传入一个自定义的移除器
>
> ​	不同窗口类型都有各自的预实现的移除器



## 1.9 窗口的开始原理

- ### 时间

- #### 启动 => 向下取整，取窗口长度的整数倍

- #### 结束 => start + 窗口长度

- #### 窗口左闭右开 => 属于本窗口的 最大时间戳 = end - 1ms

- #### 窗口的生命周期

  - ##### 创建 ： 属于本窗口的第一条数据来的时候，new的 , 放入一个singleton单例的集合中

  - ##### 销毁(关窗) : 时间进展 >= 窗口的最大时间戳 (end - 1ms)  + 允许迟到的时间(默认0)

```java
long start = TimeWindow.getWindowsStartWithOffset(
	now,(globalOffset + staggerOffset) % size, size);
)
    
    
//timestamp 是当前时间(now)
//offset是调整窗口便宜
//windowSize 窗口长度
getWindowStartWithOffset(long timestamp,long offset,long windowSize){
    final long remainder = (timestamp - offset) % windowSize;
    //如果当前时间是13秒，偏移量是0
    //(13s -0) % 10 = 3
    if(remainder < 0){
		return timestamp - (remainder + windowSize);
    }else{
        //13-3=10s
        return timestamp - remainder;
    }
    
}
```



# 3.4 时间语义

> 事件时间 : 一个数据产生的时间(时间戳Timestamp)
>
> 处理时间 : 数据真正被处理的时刻
>
> 例子
>
> ​	一瓶牛奶的生产日期是一月一号,买了后你也不会直接去喝，而是二号才去喝
>
> ​	1.1就是事件时间，1.2就是处理时间

> 统计窗口中8-9点的区间活动,到底应该用什么时间呢。
>
> ​	事件时间

- 到底以哪种时间作为衡量标准，就是所谓的时间语义。



# 3.5 水位线

> ​	在窗口的处理过程中，可以基于数据的时间戳，自定义一个**“逻辑时钟”**。这个时钟的时间不会自动流逝；它的时间进展，就是靠着新到数据的时间戳来推动的。
>
> ​	这样的好处在于，计算的过程可以**完全不依赖处理时间（系统时间）**，不论什么时候进行统计处理，得到的结果都是正确的。而一般实时流处理的场景中，事件时间可以基本**与处理时间保持同步，只是略微有一点延迟**，同时保证了窗口计算的正确性。

> ​	在Flink中，用来衡量 **事件时间**进展的标记，就被称作**"水位线"(Watermark)**
>
> ​	具体是线上，水位线可以看作一条特殊的数据记录，它是插入到数据流中的一个标记点，主要内容就是一个时间戳，用来指示当前的事件时间。



> ### 有序流中的水位线
>
> ​	理想状态 -数据量小- ，数据应该按照**生成的先后顺序**进入流中，**每条数据产生一个水位线**；
>
> 来了一条数据 ，把当前数据的事件事件抽出来，就是水位线
>
> - 实际应用中，如果当前**数据量非常大**，且同时涌来的数据事件差非常小（比如几毫秒），往往对处理计算也没什么影响。所以**为了提高效率**，一般会**每隔一段时间生成一个水位线**。



> ### 乱序流中的水位线
>
> ​	在分布式系统中，数据在节点间传输，会因为网络传输延迟的不确定性，导致顺序发生改变，这就是所谓的“**乱序数据**”
>
> ​	乱序+数据量小：还是靠数据来驱动，每来一个数据就提取它的时间戳、插入一个水位线。在乱序的情况下，**先判断一下时间戳是否比之前的大，否则不生成新的水位线。**
>
> ​	只有数据的时间戳比当前时钟大，才能推动时钟前进，这时才插入水位线。

> ​	乱序+数据量大 : 周期性的生成水位线。只需保存之前所有数据中的最大时间戳，需要插入水位线时，就直接以它的时间戳生成新的水位线

> ​	乱序+迟到数据 : 为了让窗口能正确收集到迟到的数据，可以等上一段时间，比如2秒。用当前已有数据的最大时间戳减去2秒，就是要插入的水位线的时间戳。
>
> 水位线就代表了当前的事件时钟，而且可以在数据的时间戳基础上加上一些延迟保证数据不丢失。



> 水位线特性

- 水位线是插入到数据流中的一个标记，可以认为是一个特殊的数据
- 主要内容是一个时间戳，用来表示当前事件时间的进展
- 是基于数据的时间戳生成的
- 时间戳必须单调递增，以确保任务的事件时间时钟一直向前推进
- 可以通过设置延迟，保证正确处理乱序数据
- 一个水位线Watermark(t)，表示在当前流中事件事件已经达到了时间戳t，这代表t之前的所有数据都到齐了，之后流中不会出现时间戳t' ≤ t的数据
- 是保证结果正确性的核心机制，往往跟窗口结合，解决数据乱序



## 1.1 水位线的设置

> ​	完美的水位线“绝对正确”的，也就是一个水位线一旦出现，就表示这个时间之前的数据已经全部到齐、之后就再也不会出现了。**如果要保证绝对正确，就必须等足够长的时间，这会带来更高的延迟。**

- 使用WatermarkStrategy创建水位线策略
- .<WaterSensor>forMonotonousTimestamps() 指定水位线模式，并指定数据类型
-  .withTimestampAssigner 指定水位线数据块
- 数据源.assignTimestampsAndWatermarks(e) 应用水位线策略
-  TumblingEventTimeWindows 窗口使用事件时间窗口

> 乱序
>
> ```java
> //指定单调递增时间戳 改为 乱序便宜时间 传入一个java.time的Duration时间
> .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
> ```

```java
public class WaterSensorsMono
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        //并行度
        Env.setParallelism(1);


        SingleOutputStreamOperator<WaterSensor> socketData =
            Env.socketTextStream("192.168.45.13", 7777)
            .map(new WaterSensorMapFunction());

        //升序时间
        //创建水位线策略
        WatermarkStrategy<WaterSensor> e = WatermarkStrategy
            //指定单调递增时间戳
            .<WaterSensor>forMonotonousTimestamps()
            //设定时间戳获取方法
            .withTimestampAssigner(
                new SerializableTimestampAssigner<WaterSensor>()
                {
                    @Override
                    public long extractTimestamp(WaterSensor waterSensor, long l)
                    {
                        System.out.println(waterSensor);
                        //使用Ts作为时间戳 单位是ms毫秒
                        return waterSensor.getTs() * 1000L;
                    }
                }
            );

        //应用水位线策略
        SingleOutputStreamOperator<WaterSensor> TimeWater = socketData.assignTimestampsAndWatermarks(e);


        TimeWater.keyBy(f -> f.getId())
        //使用事件事件
            .window(TumblingEventTimeWindows.of(Time.seconds(10))).process(
            new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                @Override
                public void process(String s, ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception
                {
                    //context.window可以获取窗口上下文
                    long startTime = context.window().getStart();
                    long endTime = context.window().getEnd();
                    //时间转换
                    String start = DateFormatUtils.format(startTime, "HH:mm:ss");
                    String end = DateFormatUtils.format(endTime, "HH:mm:ss");

                    System.out.println("===================窗口起始:" + start + " ===================");
                    System.out.println("===================窗口Key:" + s + " 内容===================");
                    Iterator<WaterSensor> iterator = iterable.iterator();
                    while (iterator.hasNext()){
                        System.out.println(iterator.next());
                    }
                    System.out.println("===================窗口结束:" + end + " ===================");
                }
            }
        ).print();

        Env.execute();

    }
}
```



```java
//输入
s1,1,1
s1,2,2
s1,3,3
s1,5,6
s1,10,1
s1,20,1
    
//输出
WaterSensor{id='s1', ts=1, vc=1}
WaterSensor{id='s1', ts=2, vc=2}
WaterSensor{id='s1', ts=3, vc=3}
WaterSensor{id='s1', ts=5, vc=6}
WaterSensor{id='s1', ts=10, vc=1}
===================窗口起始:08:00:00 ===================
===================窗口Key:s1 内容===================
WaterSensor{id='s1', ts=1, vc=1}
WaterSensor{id='s1', ts=2, vc=2}
WaterSensor{id='s1', ts=3, vc=3}
WaterSensor{id='s1', ts=5, vc=6}
===================窗口结束:08:00:10 ===================
WaterSensor{id='s1', ts=20, vc=1}
===================窗口起始:08:00:10 ===================
===================窗口Key:s1 内容===================
WaterSensor{id='s1', ts=10, vc=1}
===================窗口结束:08:00:20 ===================
```



## 1.2 内置的水位线生成

> 内置Watermark的生成原理
>
> 1. 都是周期性生成的，默认200ms
> 2. 有序流 : watermark = 当前最大的事件时间 减去 1ms
> 3. 乱序流 : watermark = 当前最大的事件时间 减去 延迟时间 减去 1ms



## 1.3 自定义水位线生成

- 实现WatermarkGenerator接口
- 实现两个方法
  - onEvent 每条数据来都会调用一次，可以用来提取最大的事件时间保存下来
  - onPeriodicEmit 周期性调用 用来生成 watermark



```java
//自定义水位线
//创建水位线策略
WatermarkStrategy<WaterSensor> e = WatermarkStrategy
    //指定水位线策略
    .forGenerator(new WatermarkGeneratorSupplier<WaterSensor>() {
        @Override
        public WatermarkGenerator<WaterSensor> createWatermarkGenerator(Context context)
        {
            return new MyWatermarkStrategy<>();
        }
    })
```



```java
WaterSensor{id='s1', ts=2, vc=1}
WaterSensor{id='s1', ts=3, vc=1}
WaterSensor{id='s1', ts=5, vc=1}
WaterSensor{id='s1', ts=9, vc=1}
WaterSensor{id='s1', ts=7, vc=1}
WaterSensor{id='s1', ts=10, vc=1}
WaterSensor{id='s1', ts=11, vc=1}
WaterSensor{id='s1', ts=13, vc=1}
===================窗口起始:08:00:00 ===================
===================窗口Key:s1 内容===================
WaterSensor{id='s1', ts=2, vc=1}
WaterSensor{id='s1', ts=3, vc=1}
WaterSensor{id='s1', ts=5, vc=1}
WaterSensor{id='s1', ts=9, vc=1}
WaterSensor{id='s1', ts=7, vc=1}
===================窗口结束:08:00:10 ===================
```



> ## 水位线代码

```java
package functions;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

public class MyWatermarkStrategy<T> implements WatermarkGenerator<T>
{
    /**
     *timeInter = 乱序等待时间
     *new Time = 新的时间
     */
    private long timeInter;
    private long newTime;

    //设置空参构造，不传值就 给个3秒的延迟
    public MyWatermarkStrategy(){
        this.timeInter = 3000L;
        this.newTime = Long.MIN_VALUE + 3001L;
    }

    public MyWatermarkStrategy(long timeInter) {
        this.timeInter = timeInter;
        this.newTime = Long.MIN_VALUE + timeInter + 1;
    }

    /**
     * 每次有数据来就执行一次
     * @param t e
     * @param l 提取到的数据的事件时间
     * @param watermarkOutput e
     */
    @Override
    public void onEvent(T t, long l, WatermarkOutput watermarkOutput)
    {
        //获取最大的时间戳
        this.newTime = Math.max(l,newTime);
    }

    /**
     * 用来创建水位线
     * @param watermarkOutput e
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput watermarkOutput)
    {
        watermarkOutput.emitWatermark(new Watermark(this.newTime - this.timeInter - 1));
    }
}
```

> #### 在onEvent中同样提供了WatermarkOutput,可以在每条数据到来时就创建一个水位线



## 1.4 水位线的传递

> 取最小
>
> 广播给所有下游

- 有数据 1,2,5,7,8,10,12,13,14 也代表水位线，水位线偏移设置3，2个子任务 ，KeyBy这些数据走往同一个分区
-  1 -> 水位线更新为 -2
- 2 -> 水位线还是 -2 暂存水位线 -1
- 5 -> 水位线变为 -1 暂存水位线 2
- 7 -> 水位线变为 2 暂存水位线 4
- 8 -> 水位线变为 4 暂存水位线 5
- 10 -> 水位线 5 暂存7
- 12 -> 水位线 7 暂存 9
- 13 -> 水位线 9 暂存 10
- 14 -> 水位线 11 窗口结束



- 多线程
- 任务1 1 -> L最小值
- 任务2 5-> L最小值
- 任务2 7-> L最小值
- 无论任务2怎么发任务，水位线始终为最小的，而最小的就是任务1传过来的
- 不是暂存，是分别存储各个线程所对应的水位线
- 然后比较各个水位线值 取最小



> 核心
>
> 1，接收到上游多个，取最小
>
> 2，往下游多个发送，广播

```java
WatermarkStrategy.WithIdleness(Duration.ofSeconds(5)) //空闲等待5秒，不管上游其他人了 处理时间
```



## 1.5 迟到数据

- 对水位线设置了等待时间，等待时间已经过了还有却又出现了该窗口的数据就是迟到数据

- 在开窗后可以设置迟到

```java
.allowedLateness(Time.seconds(2))//推迟2秒关窗
```

- 迟到的数据来一条触发一次计算





## 1.6 迟到处理

> 侧输出流

- 在开窗后 `.sideOutputLateData(new OutputTag)`



> > 乱序与迟到的区别
> >
> > ​	乱序 : 数据的顺序乱了，出现时间小的比时间大的晚来
> >
> > ​	迟到 : 数据的时间戳 < 当前的watermark
>
> > 乱序、迟到数据的处理
> >
> > ​	如果开窗，设置窗口允许迟到
> >
> > ​	=> 推迟关窗时间，在关窗之前，迟到数据来了，还能被窗口计算，来一条迟到数据触发一次计算
> >
> > ​	=> 关窗后，迟到数据不会被计算
> >
> > 关窗后的迟到数据 放入侧输出流
>
> 如果watermark等待3s，窗口允许迟到2s，为什么不之间watermark等待5s或者窗口允许迟到5s
>
> => watermark等待时间不会设太大 ===> 影响计算的延迟
>
> ​	如果3s => 窗口第一次触发计算和输出 , 13s的数据来。 13-3 = 10s
>
> ​	如果5s => 窗口第一次触发计算和输出 , 15s的数据来。 15-5 = 10s
>
> => 窗口允许迟到，是对大部分迟到数据的处理，尽量让结果准确
>
> ​	如果只设置允许迟到5秒，就会导致频繁重新输出

> ### 设置经验
>
> 1. watermark等待时间，设置一个不算特别大的，一般是秒级，在乱序和延迟取舍
> 2. 设置一定的窗口允许迟到，只考虑大部分的迟到数据，极端小部分迟到很久的数据，不管
> 3. 极端小部分迟到很久的数据，放到侧输出流。获取到之后就可以做各种处理



# 3.6 Join

## 1.1 WindowJoin

> 1. 落在同一个时间窗口范围内才能匹配
>
> 2. 根据指定的key(where - equalTo)，来进行匹配关联
> 3. 只能拿到匹配上的数据



```java
package Join_;

public class WindowJoin_
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        Env.setParallelism(1);

        SingleOutputStreamOperator<Tuple2<String, Integer>> tuple2 = Env
            .fromElements(
                new Tuple2<>("a", 1),
                new Tuple2<>("a", 2),
                new Tuple2<>("c", 10),
                new Tuple2<>("b", 10),
                new Tuple2<>("c", 115)
            )
            .assignTimestampsAndWatermarks(WatermarkStrategy
                .<Tuple2<String, Integer>>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple2<String, Integer>>()
                {
                    @Override
                    public long extractTimestamp(Tuple2<String, Integer> stringIntegerTuple2, long l)
                    {
                        return stringIntegerTuple2.f1 * 1000L;
                    }
                }));


        //Tuple3数据源
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> tuple3 = Env
            .fromElements(
                new Tuple3<>("a", 1, 2),
                new Tuple3<>("b", 5, 2),
                new Tuple3<>("a", 6, 2),
                new Tuple3<>("c", 7, 2),
                new Tuple3<>("a", 10, 2),
                new Tuple3<>("a", 15, 2),
                new Tuple3<>("a", 23, 2)
            )
            //TODO 设置水位线逻辑
            .assignTimestampsAndWatermarks(WatermarkStrategy
                //TODO 升序水位线
                .<Tuple3<String, Integer, Integer>>forMonotonousTimestamps()
                //TODO 水位线设置水位线字段
                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, Integer, Integer>>()
                {
                    @Override
                    public long extractTimestamp(Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3, long l)
                    {
                        return stringIntegerIntegerTuple3.f1 * 1000L;
                    }
                }));

        tuple2
            // TODO 要join的另一条流
            .join(tuple3)
            //TODO join左边的键
            .where(k -> k.f0)
            //TODO join右边的键
            .equalTo(k -> k.f0)
            //TODO 开窗 JOIN只会在该窗口内匹配 非窗口的数据不匹配
            .window(TumblingEventTimeWindows.of(Time.seconds(10)))
            //TODO 进JOIN
            .apply(new JoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>()
            {
                // TODO 数据匹配后的join逻辑
                /**
                 * 关联上的数据
                 * @param stringIntegerTuple2 数据1
                 * @param stringIntegerIntegerTuple3 数据2
                 * @return 自定义返回
                 * @throws Exception e
                 */
                @Override
                public String join(Tuple2<String, Integer> stringIntegerTuple2, Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3) throws Exception
                {
                    return stringIntegerTuple2 + "\t \t \t" + stringIntegerIntegerTuple3;
                }
            }).print();

        Env.execute();
    }
}
```



```java
(a,1)	 	 	(a,1,2)
(a,1)	 	 	(a,6,2)
(a,2)	 	 	(a,1,2)
(a,2)	 	 	(a,6,2)
```



## 1.2 Interval Join(间隔)

> #### 只支持事件时间
>
> ##### 使用Interval Join必须先KeyBy
>
> key就是关联条件
>
> 

```java
// TODO 必须先KeyBy才能使用intervalJoin
KeyedStream<Tuple3<String, Integer, Integer>, String> tp3key = tuple3.keyBy(f -> f.f0);
KeyedStream<Tuple2<String, Integer>, String> tp2key = tuple2.keyBy(f -> f.f0);

// TODO 使用intervalJoin
tp2key.intervalJoin(tp3key)
            // TODO 使用between设置时间偏移量 以数据自身的事件时间为基准
            // TODO 匹配对方在设定时间内的数据
          .between(Time.seconds(-3),Time.seconds(3))
            // TODO 匹配后的数据
          .process(new ProcessJoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>() {
              /**
               * 匹配上后执行
               * @param stringIntegerTuple2 左流数据
               * @param stringIntegerIntegerTuple3 右流数据
               * @param context 上下文
               * @param collector coll
               * @throws Exception e
               */
              @Override
              public void processElement(Tuple2<String, Integer> stringIntegerTuple2, Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3, ProcessJoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>.Context context, Collector<String> collector) throws Exception
              {
                  collector.collect(stringIntegerTuple2 + "\t\t\t" + stringIntegerIntegerTuple3 );
              }
          }).print();
```



> 1. 只支持事件时间
>
> 2. 指定上界，下界的偏移，负号代表时间之前，正号代表时间之后
>
> 3. process中，只能处理join上的数据
>
> 4. 两条流关联后的watermark，以两条流中最小的为准
>
> 5. 如果当前数据的事件时间 < 当前的watermark，就是迟到数据，主流的process不处理
>
>    => between后，可以将左右流的迟到数据，分别放入侧输出流

> ### 迟到处理



```java
//创建两条侧输出流
OutputTag<Tuple2<String,Integer>> tag1 = new OutputTag<>("tag1", Types.TUPLE(Types.STRING,Types.INT));
OutputTag<Tuple3<String,Integer,Integer>> tag2 = new OutputTag<>("tag2", Types.TUPLE(Types.STRING,Types.INT,Types.INT));
```

> ### 在between后指定流向

```java
// TODO 使用between设置时间偏移量 以数据自身的事件时间为基准
// TODO 匹配对方在设定时间内的数据
.between(Time.seconds(-3), Time.seconds(3))
//左数据
.sideOutputLeftLateData(tag1)
//右数据
.sideOutputRightLateData(tag2)
```

> ### 获取并输出

```java
run.print("主流");
run.getSideOutput(tag1).printToErr("tup2迟到");
run.getSideOutput(tag2).printToErr("tup3迟到");
```



> tup2输入

```java
a1,1    
a1,3,1
a1,5,1
a1,2
a1,6
a1,1
```

> tup3输入

```java
a1,2,1
a1,7,1
a1,1,1
```

> 控制台输出

```java
主流> (a1,1)			(a1,2,1)
主流> (a1,3)			(a1,2,1)
主流> (a1,5)			(a1,2,1)
主流> (a1,2)			(a1,2,1)
主流> (a1,6)			(a1,7,1)
主流> (a1,5)			(a1,7,1)
tup3迟到> (a1,1,1)
tup2迟到> (a1,1)
```



> 其他更改

```java
.socketTextStream("192.168.45.13",7777)
 //输入数据转2元组
.map(f->{String[] e = f.split(",");return new Tuple2<String,Integer>(e[0],Integer.valueOf(e[1]));})
 //类型擦除
.returns(Types.TUPLE(Types.STRING,Types.INT))
```

```java
//Tuple3数据源
SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> tuple3 = Env
    .socketTextStream("192.168.45.13",8888)
    .map(f -> {String[] s =  f.split(",");return new Tuple3<String,Integer,Integer>(s[0],Integer.valueOf(s[1]),Integer.valueOf(s[2])); })
    .returns(Types.TUPLE(Types.STRING,Types.INT,Types.INT))
```



# 3.7 处理函数

> Process处理函数
>
> ### Process一次只处理一条数据
>
> - 提供了定时服务 TimerService
> - 可以访问流中的事件 event
> - 时间戳 timestamp
> - 水位线 watermark
> - 注册 "定时事件"



- ProcessFunction

  - 最基本的处理函数 基于DataStream直接调用.process()时作为参数传入

  

- KeyedProcessFunction

  - 对流按键分区后的处理函数，基于KeyedStream调用.process()时作为参数传入。要想使用定时器，比如KeyedStream。

  

- ProcessWindowFunction

  - 开窗之后的处理函数，是全窗口函数的代表。基于WindowedStream调用.process()时作为参数传入

  

- ProcessAllWindowFunction

  - 开窗之后的处理函数，基于AllWindowedStream调用.process()时作为参数传入

  

- CoProcessFunction

  - 合并(connect)两条流之后的处理函数

- ### ....



> ### 在什么场景下，就调用什么情况下的Process



## 1.0 按键分区处理函数

> #### KeyedProcessFunction
>
> 一个计时器只会被触发一次



## 1.1 事件时间计时器

- 这里设置的并行度1 不然默认CPU线程数并行度需要数据量

```java
a1,1,1
a2,1,1
a3,1,1
a3,6,6
```

```java
计时器被创建Key	a1
计时器被创建Key	a2
计时器被创建Key	a3
计时器被创建Key	a3
a1的计时器	5000
a3的计时器	5000
a2的计时器	5000
```



```java
StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

Env.setParallelism(1);

SingleOutputStreamOperator<WaterSensor> socketData = Env
    .socketTextStream("192.168.45.13", 7777)
    .map(new WaterSensorMapFunction())
    //设置水位线
    .assignTimestampsAndWatermarks(WatermarkStrategy
        .<WaterSensor>forMonotonousTimestamps()
        //使用TS为水位线
        .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
            @Override
            public long extractTimestamp(WaterSensor waterSensor, long l)
            {
                return waterSensor.getTs() * 1000L;
            }
        })
    );

//ID为Key分组
socketData.keyBy(f -> f.getId())
          /**
           * 第一个类型是Key的类型,第二个类型是数据的类型,第三个是输出的类型
           */
          // TODO 使用处理函数
              .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                  /**
                   * 来了数据触发
                   * @param waterSensor 来的数据
                   * @param context 上下文
                   * @param collector 数据采集器
                   * @throws Exception e
                   */
                  public void processElement(WaterSensor waterSensor, KeyedProcessFunction<String, WaterSensor, String>.Context context, Collector<String> collector) throws Exception
                  {
                        //使用上下文获取事件器
                      TimerService ServiceE = context.timerService();
                      //注册事件时间器 间隔5秒
                      ServiceE.registerEventTimeTimer(5000L);
                      System.out.println("计时器被创建Key\t" + waterSensor.getId());
                  }

                  /**
                   * 事件触发
                   * @param timestamp 触发计时器的时间戳
                   * @param ctx 上下文
                   * @param out 用于返回结果的收集器
                   * @throws Exception e
                   */
                  public void onTimer(long timestamp, KeyedProcessFunction<String, WaterSensor, String>.OnTimerContext ctx, Collector<String> out) throws Exception
                  {
                      // TODO 获取该计时器的Key
                      String currentKey = ctx.getCurrentKey();
                      System.out.println(currentKey + "的计时器\t" + timestamp);
                      super.onTimer(timestamp, ctx, out);
                  }
              }).print();

Env.execute();
```



## 1.2 处理时间计时器

> 处理时间 不同于事件时间，计时器被创建后 现实(5s)的时间后触发

```java
  //使用上下文获取事件器
  TimerService ServiceE = context.timerService();
  // TODO ServiceE.currentProcessingTime()获取系统时间 java自己也有
  // TODO + 5000L表示5秒后触发
  ServiceE.registerProcessingTimeTimer(ServiceE.currentProcessingTime() + 5000L);
  System.out.println("处理时间计时器被创建Key\t" + waterSensor.getId() +"\t" + ServiceE.currentProcessingTime());

// TODO 获取该计时器的Key
String currentKey = ctx.getCurrentKey();
System.out.println(currentKey + "计时器触发\t" + timestamp);
super.onTimer(timestamp, ctx, out);
```

```java
a1,1,

处理时间计时器被创建Key	a1	1731209959910
a1计时器触发	1731209964910
```



## 1.3 定时器总结

> 1. Keyed才有
>
> 2. 事件时间定时器，通过watermark来触发
>
>    watermark 大于等于(>=) 注册时间
>
>    注意 : watermark 等于(=) 当前最大事件时间 减(-) 等待时间 减(-) 1ms毫秒,因为 -1ms 所以会推迟一条数据
>
>    比如5s的定时器
>
>    如果 等待=3s , watermark = 8s - 3s - 1ms = 4999ms,不会触发5s的定时器
>
>    需要watermark = 9s - 3s -1ms = 5999ms 才能去触发5s的定时器 
>
> 3. 在process中获取当前watermark，显示的是上一次的watermark
>
>    => 因为process还没接收到这条数据对应生成的新watermark



> Process一次只处理一条数据
>
> 现在有数据 1,代码中的水位线是在map后生成的，map后便是Process
>
> 1 -> map 变为了  1 后面跟着水位线
>
> 水位线 1 -> Process -> 1(走)  Process当前水位线是Long.MIN
>
> 水位线 -> process -> Process更新水位线
>
> 也就是说，process方法的水位线需要数据处理完后，下一条数据才是水位线
>
> process水位线落后一条消息
>
> 所以调用processElement获取当前的watermark获取的是process的watermark 相当于是上一条数据的watermark , 只有下一条数据来了，调用了process才会去更新定时器的时间 ，**实际process的watermark已经到了水位线了，但是没有数据来驱动(雾)**



## 1.4 TopN AllWindow

```java
package Process_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.TreeMap;

// TODO 统计10秒内的水位线出现次数前2的，5秒输出一次
// TODO 使用滑动窗口 10,5
public class TopN_
{
    // TODO 窗口事件设置为了10秒 滑动时间设置为了5秒
    // TODO 使用WaterSensor的Ts作为了事件时间

    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setParallelism(1);

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
               .assignTimestampsAndWatermarks(WatermarkStrategy
               .<WaterSensor>forMonotonousTimestamps()
              //指定水位线
               .withTimestampAssigner((waterSensor, l) -> waterSensor.getTs() * 1000L))
            	//设置窗口步长
               .windowAll(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
            //数据处理
               .process(new ProcessAllWindowFunction<WaterSensor, String, TimeWindow>() {
                   @Override
                   public void process(ProcessAllWindowFunction<WaterSensor, String, TimeWindow>.Context context, Iterable<WaterSensor> elements, Collector<String> out) throws Exception
                   {
                       TreeMap<Long,Integer> tr = new TreeMap<>();
                       for (WaterSensor element : elements)
                       {
                           //TODO 判断是否为第一条数据
                           // TODO 如果不是第一条数据
                           Long vce = (long) element.vc;
                           if(tr.containsKey(vce))
                           {
                               tr.put(vce,tr.get(vce) + 1);
                           }
                           //TODO 否则就是第一次来
                           else
                           {
                               tr.put(vce,1);
                           }
                       }

                       // TODO 对数据进行排序
                       ArrayList<Tuple2<Long,Integer>> list = new ArrayList<>();

                       //TODO 载入数据
                       tr.forEach((k,v) -> {
                           list.add(new Tuple2<>(k,v));
                       });

                       //TODO 排序数据
                       list.sort((o1,o2) -> o2.f1 - o1.f1);

                       StringBuilder stb = new StringBuilder();

                       for(int i = 0;i < Math.min(list.size(),2);i++){
                           Tuple2<Long, Integer> tp2 = list.get(i);
                           stb.append("第" + i+1 + ": " +tp2.f0 + "\t" + tp2.f1 + "\n" + "================" + "\n");
                       }

                       out.collect(stb.toString());

                   }
               }).print();

        Env.execute();
    }
}
```



## 1.4 TopN_2

> 1. 按照vc做keyby 开窗，分别count
>
>    => 增量聚合，计算，count
>
>    => 全窗口，对计算结果进行count值进行封装 , 带上窗口标签
>
>    ​	=> 为了让同一个窗口时间范围的计算结果到一起去 
>
> 2. 对同一个窗口范围的count值进行处理： 排序 取前N个
>
>    => 按照windowEnd 做Keyby
>
>    => 使用process,来一条调用一次，需要先存起来，分开存在HashMap, key = 窗口标签，value = List
>
>    ​	=> 使用定时器，对存起来的结果进行排序、取前N个

```java
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
```



# 3.8 状态管理

> 在Flink中 算子任务可以分为 无状态和有状态两种
>
> ​	无状态的算子任务只需要观察每个独立事件，根据当前输入的数据之间转换输出结果。如map、filter、flatMap，计算时不依赖其他数据就属于无状态算子
>
> ​	有状态的算子，除了当前数据外，需要一些其他数据来得到计算结果。这个其他数据 就是状态 ,例如将数据存储起来，然后数据来的时候将存储的数据拿出来根据业务逻辑进行计算，然后更新存储的数据(更新状态)发送结果

- #### 在Flink编程中，通常使用托管状态，原始状态基本不会碰

> ##### 	状态的作用范围限定为当前算子的并行子任务,状态对于同一个任务是共享的。不同并行度之间的状态是相互独立的
>
> ##### 	如果按Key分组了，那么每组的状态是隔离的

> ##### 	即便是无状态的算子 也可以通过Rich Function(富函数)来定义状态



## 1.0 值状态

> ### ValueState



> 如果上个水位与当前水位相差10就报警

```JAVA
public class StateDemo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamContextEnvironment.getExecutionEnvironment();

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
               .assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                       @Override
                       public long extractTimestamp(WaterSensor waterSensor, long l)
                       {
                           return waterSensor.getTs() * 1000L;
                       }
                   }))
               .keyBy(f -> f.getId())
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   // TODO 定义一个值状态，值状态存储的类型是Integer
                   private ValueState<Integer> valueState;

                   // TODO 开始时触发
                   @Override
                   public void open(Configuration parameters) throws Exception
                   {
                       super.open(parameters);
                       //TODO 使用运行时上下文创建一个值状态,在Open里面初始化valueState
                       //TODO 因为可以避免重复初始化 也可以避免任务没有启动就已经初始化导致出错
                       valueState = getRuntimeContext().getState(new ValueStateDescriptor<>("value", Types.INT));
                   }

                   // TODO 逻辑
                   @Override
                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                        //TODO 因为使用的Integer包装类，所以默认值为null
                        int Vc = valueState.value() == null ? 0 : valueState.value();
                        //TODO 如果大于等于10就报警
                        if((Math.abs(Vc - value.getVc())>=10))
                            out.collect("传感器" + value.getId() + "\t" + Vc + "\t" + value.getVc()+"\t"+"报警！！！");
                        valueState.update(value.getVc());
                   }
               }).print();

        Env.execute();
    }
}
```



## 1.1 列表状态

| Iterable<T> get()      | 获取当前的列表状态，返回的是可迭代类型Iterable<T> |
| ---------------------- | ------------------------------------------------- |
| update(List<T> values) | 传入一个列表values 直接对状态进行覆盖             |
| add(T value)           | 在状态列表添加一个元素value                       |
| addAll(List<T> value)  | 向列表中添加多个元素，以列表values形式传入。      |



> ## 取各组水位线最高的前三

```java
package State_;
public class StateList
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
                   .assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forMonotonousTimestamps()
                       .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                           public long extractTimestamp(WaterSensor waterSensor, long l)
                           {
                               return waterSensor.getTs() * 1000L;
                           }
                       }))
                //TODO 按照键分组
               .keyBy(f -> f.getId())
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   @Override
                   public void open(Configuration parameters) throws Exception
                   {
                       //TODO 在Open方法初始化 只会初始化一次，能保证任务启动后初始化
                       super.open(parameters);
                       state = getRuntimeContext().getListState(new ListStateDescriptor<>("dawf", Types.INT));
                   }

                   private ListState<Integer> state;
                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                       //TODO 其返回的是可迭代类型
                       Iterable<Integer> integers = state.get();
                       //TODO 创建一个List用来存放值
                       ArrayList<Integer> list = new ArrayList<>();
                       for (Integer integer : integers)
                       {
                           list.add(integer);
                       }
                       list.add(value.getVc());
                       //TODO 排序
                       list.sort((o1,o2) -> o2 - o1);
                       //TODO 只取前三
                       if(list.size() > 3) list.remove(3);
                       //TODO 返回
                       out.collect("Key:" + value.getId() + "\t" + list.toString());

                       //TODO 覆盖
                       state.update(list);
                       list.clear();
                   }
               }).print();

        Env.execute();
    }
}
```



## 1.2 Map状态

> ### 统计各个传感器 各个水位线出现的次数

```java
package State_;
public class StateMap
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
                   .assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forMonotonousTimestamps()
                       .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                           public long extractTimestamp(WaterSensor waterSensor, long l)
                           {
                               return waterSensor.getTs() * 1000L;
                           }
                       }))
                //TODO 按照键分组
               .keyBy(f -> f.getId())
                //TODO 目标
                //TODO 统计每种水位出现的次数
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   //TODO 这里不初始化
                   //TODO 键是水位 值是次数
                   MapState<Integer,Integer> mapState;

                   public void open(Configuration parameters) throws Exception
                   {
                       super.open(parameters);
                       //TODO 在open初始化
                       mapState = getRuntimeContext().getMapState(new MapStateDescriptor<>("mapsta",Types.INT,Types.INT));
                   }

                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                       int vc = value.getVc();
                       //TODO 返回true存在
                        if(mapState.contains(value.getVc())) mapState.put(vc, mapState.get(vc) + 1);
                        //TODO 不存在
                        if(!mapState.contains(value.getVc())) mapState.put(vc, 1);

                        //TODO 遍历拼接返回
                       Iterator<Map.Entry<Integer, Integer>> iterator = mapState.iterator();
                       StringBuilder stb = new StringBuilder();
                       stb.append(value.getId() + "\n===========================\n");
//                       while (iterator.hasNext())
//                       {
//                           Map.Entry<Integer, Integer> next = iterator.next();
//                           stb.append(next.getKey() + "\t" + next.getValue() + "\n");
//                       }

                       //TODO 以水位线大小排序
                       List<Tuple2<Integer,Integer>> list = new ArrayList<>();
                       while (iterator.hasNext())
                       {
                           Map.Entry<Integer, Integer> next = iterator.next();
                           list.add(new Tuple2<>(next.getKey(),next.getValue()));
                       }
                       list.sort((o1,o2) -> o1.f0 - o2.f0);
                       for (Tuple2<Integer, Integer> e : list)
                       {
                           stb.append(e.f0 + "\t" + e.f1 + "\n");
                       }

                       out.collect(stb.toString());
                   }
               }).print();

        Env.execute();
    }
}
```



## 1.3 归约状态

> ## Reducing

> ### 统计水位线总值

```java
.process(new KeyedProcessFunction<String, WaterSensor, String>() {
    ReducingState<Integer> reducingState;
    @Override
    public void open(Configuration parameters) throws Exception
    {
        super.open(parameters);
        reducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<>(
            "Name",
            //TODO 可以简化为喇嘛大
            new ReduceFunction<Integer>() {
                //TODO 直接累加
                @Override
                public Integer reduce(Integer integer, Integer t1) throws Exception
                {
                    return integer + t1;
                }
            },
            Types.INT
        ));
    }
    @Override
    public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
    {
        //将水位线传过去
         reducingState.add(value.getVc());
         out.collect("Key:" + value.getId() + "\t 水位线: " + reducingState.get());
    }
}).print();
```



## 1.4 聚合状态

> ### AggregatingState

> 取水位均值

```java
//TODO 按照键分组
.keyBy(f -> f.getId())
.process(new KeyedProcessFunction<String, WaterSensor, String>() {
    //TODO 第一个泛型 输入类型，第二个泛型 输出类型
    AggregatingState<Integer,Integer> aggstate;
    @Override
    public void open(Configuration parameters) throws Exception
    {
        super.open(parameters);
        aggstate = getRuntimeContext().getAggregatingState(new AggregatingStateDescriptor<Integer, Tuple2<Integer,Integer>, Integer>(
            "NameAgg",
            new AggregateFunction<Integer, Tuple2<Integer,Integer>, Integer>() {
                //初始化
                @Override
                public Tuple2<Integer, Integer> createAccumulator()
                {
                    return new Tuple2<>(0,0);
                }
                //累加逻辑
                @Override
                public Tuple2<Integer, Integer> add(Integer Input, Tuple2<Integer, Integer> ACC)
                {
                    //TODO f0存放总量，f1存放次数
                    return Tuple2.of(ACC.f0 + Input,ACC.f1 + 1);
                }
                //最终返回
                @Override
                public Integer getResult(Tuple2<Integer, Integer> ACC)
                {
                    //TODO取平均值
                    return ACC.f0 / ACC.f1;
                }
                @Override
                public Tuple2<Integer, Integer> merge(Tuple2<Integer, Integer> integerIntegerTuple2, Tuple2<Integer, Integer> acc1)
                {
                    return null;
                }
            },
            Types.TUPLE(Types.INT,Types.INT)
            ));
    }
    @Override
    public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
    {
         aggstate.add(value.getVc());
         out.collect("Key:" + value.getId() + "\t" + "水位均值: " + aggstate.get());
    }
}).print();
```



## 1.5 状态生存时间 （TTL）
|StateTtlConfig	|  |
|---------------------------------------|--------------------|
|						|	|
| .newBuilder(Time)   | 创建TTL                          |
| .setUpdateType      | 设置模式                         |
| .setStateVisibility | 状态可见性<br>被标记了是否可访问 |
| .build              |                                  |
|                     |                                  |

```java
package State_;
public class StateTTL
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
                   .assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forMonotonousTimestamps()
                       .withTimestampAssigner((waterSensor, l) -> waterSensor.getTs() * 1000L))
               .keyBy(f -> f.getId())
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   ValueState<Integer> valueState;

                   @Override
                   public void open(Configuration parameters) throws Exception
                   {
                       StateTtlConfig.Builder TTLConfig =
                                StateTtlConfig
                               //TODO 创建TTL 传入过期时间
                               .newBuilder(Time.seconds(10))
                               //TODO 选择更新状态的模式
                               //TODO 在给定情况下，状态的TTL会被重置
                               .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                               //TODO 选择被标记清除的状态是否能被读取
                               //TODO 默认是这个 不能，另一个是可以 - ReturnExpiredIfNotCleanedUp
                               .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired);

                       ValueStateDescriptor<Integer> VSD =  new ValueStateDescriptor<>("ValueTTL",Types.INT);
                       //TODO 启用TTL 并给定TTL配置
                       VSD.enableTimeToLive(TTLConfig.build());
                       valueState = getRuntimeContext().getState(VSD);
                       super.open(parameters);
                   }

                   @Override
                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                       System.out.println("Key:" + value.getId() + "\t上一个水位:" + valueState.value());
                       valueState.update(value.getVc());
                   }
               }).print();

        Env.execute();
    }
}
```



# 3.9 算子状态

> #### 作用范围被限定为当前算子任务，算子状态跟数据的Key无关
>
> - ##### 不同Key的数据只要被分发到同一个并行子任务就会访问到同一个算子状态(Operator State)
>
> 算子状态的实际应用场景不如Keyed State多，一般用在Source或者Sink等外部系统连接



## 1.0 ListState

> ##### 与Keyed的不同，这个是一个子任务一个List，而Keyed的是一个Key一个List
>
> #### 在无状态算子中 注册状态，必须实现CheckpointedFunction接口

| snapshotState   | 将本地变量拷贝到算子状态中，持久化本地变量                   |
| --------------- | ------------------------------------------------------------ |
| initializeState | (down掉重启)初始化本地变量，把本地变量从状态中恢复，每个子任务调用一次 |

1. ##### 实现CheckpointedFunction接口

2. ##### 定义一个计数变量

3. ##### 定义一个状态 ListState

4. ##### 在snapshotState内将变量持久化进状态

   - ##### 情况算子状态 .clear();

   - ##### 将本地变量添加到算子状态中

5. 在initializeState
   - 获取算子状态
     - context.getOperatorStateStore().getListState



> 计算数据条数 每个子任务独立

```java
package State_;
public class OperMapState
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setParallelism(2);
        Env.socketTextStream("192.168.45.13",7777)
               .map(new MyMapFunction()).print();

        Env.execute();
    }

    public static class MyMapFunction implements MapFunction<String,Long>, CheckpointedFunction{

        public Long map(String s) throws Exception
        {
            return ++conunt;
        }

        Long conunt = 0L;
        ListState<Long> listState;
        /**
         * 将本地变量拷贝到状态 持久化
         * TODO 一个线程触发一次 只有在首次运行时触发
         * @param context 上下文
         * @throws Exception e
         */
        public void snapshotState(FunctionSnapshotContext context) throws Exception
        {
            System.out.println("snapshotState被调用");
            //TODO 清空算子状态
            listState.clear();
            //TODO 放入算子状态
            listState.add(conunt);
        }

        /**
         * 初始本地变量 在down重启后
         * @param context 上下文
         * @throws Exception e
         */
        public void initializeState(FunctionInitializationContext context) throws Exception
        {
            System.out.println("initializeState被调用");
            //TODO 使用上下文获取算子状态
            //TODO 由于我们情况了又放入 所以每个线程算子状态列表始终只有一个值
            //TODO 将状态值取出后累加

            //TODO 判断是否恢复成功 true false 直意
            if (context.isRestored())
            {
                Iterable<Long> le = context
                    .getOperatorStateStore()
                    .getListState(new ListStateDescriptor<Long>("LE", Types.LONG))
                    .get();
                le.forEach(f -> conunt += f);
            }
        }
    }
}
```

​     

> List与unionList的区别 :
>
> ​	并行度改变时，怎么重新分配状态
>
> - List
>   - 将各个子任务的列表状态收集起来，(分片)然后轮询发送状态
> - unionList
>   - 将各个子任务的列表状态收集起来，(完整)全部发给各个并行度



## 2.0 广播状态

> #### 所有流的分区任务都访问同一个状态



> ### 水位状态到达阈值进行报警，阈值可以动态修改

1. ##### 创建一个广播流

2. ##### Source流内的 broadcast方法传入一个 Map状态

3. ##### 将数据流和广播流合流 connect 使用数据流合流广播流

4. ##### 调用process

| processBroadcastElement | 广播后的配置流处理方法 |
| ----------------------- | ---------------------- |

5. 通过上下文获取广播状态，往里面写数据



```java
package State_;
public class BroadState
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> Data = Env
            .socketTextStream("192.168.45.13", 7777)
            .map(new WaterSensorMapFunction());

        //TODO 定义一个流 用来充当广播流
        DataStreamSource<String> Brod = Env.socketTextStream("192.168.45.13", 8888);
        //TODO 创建广播状态
        MapStateDescriptor<String, Integer> MapStateStream = new MapStateDescriptor<>("Broadcast", Types.STRING, Types.INT);
        //TODO 将流加入广播状态
        BroadcastStream<String> broadcast = Brod.broadcast(MapStateStream);
        //TODO 将需要共享广播状态的流与广播流进行合并
        BroadcastConnectedStream<WaterSensor, String> BrodAllStream = Data.connect(broadcast);

        //TODO 第一个泛型 第一条流的类型
        //TODO 第二个泛型 第二条流的类型
        //TODO 第三个泛型 输出的类型
        BrodAllStream.process(new BroadcastProcessFunction<WaterSensor, String, String>() {

            @Override
            public void processElement(WaterSensor value, BroadcastProcessFunction<WaterSensor, String, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception
            {
                //TODO 取得广播流数据
                Integer MAX = ctx
                    .getBroadcastState(MapStateStream)
                    .get("runErr");
                //TODO 判断广播流数据是否为空
                int IsExNULL = (MAX == null ? 0 : MAX);
                if(value.getVc() > IsExNULL) out.collect(value + "\t" + "水位过高 报警");
            }

            /**
             *广播流的数据处理
             * @param value 传入的值
             * @param ctx 上下文 可以用来更新广播状态
             * @param out 采集器
             * @throws Exception e
             */
            @Override
            public void processBroadcastElement(String value, BroadcastProcessFunction<WaterSensor, String, String>.Context ctx, Collector<String> out) throws Exception
            {
                //TODO 得到流
                BroadcastState<String, Integer> ErrStream = ctx.getBroadcastState(MapStateStream);
                //TODO 覆写数据
                ErrStream.put("runErr",Integer.valueOf(value));
            }
        }).print();

        Env.execute();
    }
}
```



## 3.0 状态后端

> ### State Backends
>
> 管理状态的存储 访问 以及维护
>
> 主要负责管理本地状态的存储方式和位置

### 1.1 哈希表状态后端

- #### HashMapStateBackend

  ​	是把状态存放在内存里。

  - 在内部会直接把状态当作对象 objects  报错在Taskmanager的JVM堆上(任务节点的堆内存)
  - 底层是一个哈希表HashMap

  

### 2.2 内嵌RocksDB

> 内嵌RocksDB状态后端,EmbeddedRocksDBStateBackend

- RocksDB是内嵌的key-value存储介质，可以把数据持久化到本地硬盘。默认存储在TaskManager的本地数据目录、
- RocksDB的状态数据被存储为序列化的字节数组
- 因为做了序列化 key的比较会按照字节进行 而不是直接调用hashCode 和eq
- 始终执行异步快照，不会因为保存检查点而阻塞数据的处理 增量式保存检查点



### 2.3

- ##### HashMap效率更快，存储上限更低

- ##### RocksDB安全性更高，存储上限更高



### 2.4 配置状态后端

> ##### Flink flink-conf.yaml
>
> state.backend.type : hashmap
>
> - 增量 / 全量 false => 全量
>
>   state.backend.incremental : false

> ### 代码中指定状态后端

```JAVA
env.setStateBackend(new HashMapStateBackend());
```



> ### 代码中设置 RocksDB

```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-statebackend-rocksdb</artifactId>
  <version>1.17.0</version>
</dependency>
```

```java
Env.setStateBackend(new HashMapStateBackend());
//TODO 可以传入一个true表示启用增量保存 不指定就是全量
Env.setStateBackend(new EmbeddedRocksDBStateBackend());
```



> All

- #### 代码中指定状态后端

  - 负责管理 本地状态
  - hashmap
    - 存在JVM的堆内存(任务节点) 读写快，缺点是存不了太多 受限于TaskManager的内存
  - rocksdb
    - 存在任务节点的rocksdb数据库，存到磁盘中 写需要序列化，读需要反序列化

- 配置方式

  - 配置文件 默认值 flink-conf.yaml
  - 代码中指定
  - 提交参数指定
    - flink run-application -t yarn-application
    - -p 3 (并行度)
    - -Dstate.backend.type=rocksdb   => 这里指定
    - -c 全类名
    - jar包



# 4.0 容错机制

## 1.1 检查点

> #### 检查点 Checkpoint

> ​	可以用存档读档的思路，将之前某个时间点所有的状态保存下来 这个存档就是检查点
>
> ​	与大盘故障重启的时候，可以从检查点中读取存档，恢复之前的状态
>
> ​	这个检查，其实是针对故障恢复效果而言的，故障恢复后继续处理的结果应该与发生故障前完全一致
>
> **需要检查结果的正确性**，所有checkpoint叫做**一致性检查点**



## 1.2 检查点的保存

> ​	应该在所有任务(算子)都恰好处理完一个相同的输入数据的时候，将它们的状态保存下来。

- 周期性的触发保存

  - 在Flink中，检查点的保存是周期性触发的，间隔时间可以进行设置。不建议一条一保存

- 保存的时间点

  - ### **应该在所有任务(算子)都恰好处理完一个相同的输入数据的时候，将它们的状态保存下来**

  - 一条数据被所有任务(算子)完整的处理完，状态得到了保存。

    - 不管中间状态 只管最终状态

  - **只需要让源(source)任务向数据源重新提交偏移量，请求重放数据就可以了**

    - **数据已经经过部分算子 但是没有到最终状态 直接丢弃状态**
    - **只需要让源申请保存的偏移量开始读取数据，就是重放**
    - 需要源任务可以把偏移量作为算子状态保存下来 而且外部数据源能够重置偏移量

- Checkpoint由JobManager发送指令触发检查点保存；Source任务中插入一个分界线，并将偏移量保存到远程的持久化存储中

  - 发送指令即 在数据流中插入了特殊的数据结构
  - 状态持久化后 Source会返回JobManager一个ack



- 算子一对多的时候 会将特殊结构进行广播给下游 **barrier(特殊结构)**

  - 下游只有等上游的特殊结构全部到达的时候才会进行状态持久化

  - 如果上游一个特殊结构已经到了，有一个还每到，而到了的那个又发来了一条数据，不会被计算

    - 特殊结构前面的数据才会被处理，后面的不处理，等结构对齐

  - ### 精准一次 数据不越位



- 如果是至少一次 那么后来的数据会直接计算 等到下一个Barrier到达时做状态的保存

  - 故障重启时，介于两个Barrier之间的分界线已经到达分区任务传过来的数据会再次计算(重复计算)

  - ### **至少一次** 数据越位



- 非对齐
  - 一个到了 直接跨过，然后存储这一批的数据 故障恢复 数据恢复到缓冲区
  - 将越过的数据 还有在Barrier前面的数据一并备份
  - IO压力更大 备份大小更大



- 触发检查点 JobManager向Source发送Barrier
- Barrier发送 向下游广播发送
- Barrier对齐
- 状态保存
- 处理缓存数据 然后正常继续处理



## 1.3 检查点算法

> 采用了基于Chandy-Lamport算法的分布式快照，可以不暂停整体流处理的前提下 将状态备份到检查点

​	借鉴水位线的设计，在数据流中插入了一个特殊的数据结构，专门用来表示触发检查点保存的时间点。



- 特殊结构经过谁，谁就保存状态



> 在多线程下，算子只有接收到上游的全部的特殊结构才会保存自身状态



## 1.4 All

1. Barrier对齐 ： 一个Task收到所有上游同一个编号的barrier之后，才会对自己的本地状态做备份
   - 精准一次 : 在barrier对齐过程中，barrier后面的数据 阻塞等待 不会越过barrier
   - 至少一次 : 在barrier对齐过程中，先到的barrier，后面的数据不阻塞 接着计算
2. 非Barrier对齐 ：一个Task收到第一个barrier时，就开始执行备份
   - 先到的barrier,将本地状态备份，后面的数据接着计算输出
   - 未到的barrier,前面的数据接着计算输出，同时保存到备份中
   - 最后一个barrier到时，Task备份结束



## 1.5 编码配置

- ### 如果使用HDFS存储需要导入依赖

- 加上<scope>provided</scope>

```xml
<dependency>
  <groupId>org.apache.hadoop</groupId>
  <artifactId>hadoop-client</artifactId>
  <version>3.3.4</version>
</dependency>
```



```java
StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
System.setProperty("HADOOP_USER_NAME","root");

//TODO 默认barrier对齐 第一个参数是设置多久一次ms , 第二个参数是模式 精准一次
Env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);

//TODO 获取配置文件
CheckpointConfig config = Env.getCheckpointConfig();

//TODO 设置保存路径
config.setCheckpointStorage("hdfs://192.168.45.13:8020/chk");

//TODO checkpoint的超时时间 默认10分组
config.setCheckpointTimeout(60000);

//TODO 同时运行中的checkpoint的最大数
config.setMaxConcurrentCheckpoints(2);

//TODO 最小等待间隔 上一轮checkpoint结束到下一轮checkpoint开始 之间的间隔 设置>0并发就会变成1
config.setMinPauseBetweenCheckpoints(1000);

//TODO 取消作业时 checkpoint的数据是否保留在外部系统  => 在取消时删除
//TODO 程序突然挂掉 无法删除
config.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);

//TODO 运行checkpoint最大失败次数 默认0,如果失败job（作业）就挂掉
config.setTolerableCheckpointFailureNumber(10);

//TODO 开启非对其检查点 必须精准一次才能
//TODO setMaxConcurrentCheckpoints必须为1
config.enableUnalignedCheckpoints();

//TODO 如果大于0 ，非对齐检查点启用时候，先使用对齐检查点
//TODO 如果对齐检查点超时 超过设定时间 切换为非对齐 Flink 16后
config.setAlignedCheckpointTimeout(Duration.ofSeconds(1));
```



## 1.6

>Flink1.15之后 checkpoint(changelog) 通用增量
>
>Map也支持增量



# 4.1 保存点

> ### Savepoint

- 版本管理和归档存储
- 更新Flink版本
- 更新应用程序
- 调整并行度
- 暂停应用程序



> ### 每个算子都能指定UID
>
> #### 每个算子都能指定一个name -> 别名



- 保存点的使用
  - 在命令行为运行的作业创建一个保存点镜像
  - jobId => 填充要做镜像保存的作业ID
  - targetDirectory是目标路径 可选，表示保存点存储的路径

```sh
bin/flink savepoint :jobId [:targetDirectory]
```

- - flink-conf.yaml中的state.savepoints.dir


```xml
state.savepoints.dir: hdfs:///flink/savepoints
```

- - 程序中

```java
env.setDefaultSavepointDir("Path");
```

- - 作业停止 推荐 Source必须实现StoppableFunction接口

```sh
bin/flink stop --savepointPath [:targetDirectory] :jobId
```

- - 立即停止

```sh
bin/flink cancel -s Path job-id -yid application-id
```







> 从保存点加载

```sh
bin/flink run -s :savepointPath [:runArgs]
```



> 如果是yarn还需要跟上 => -yid application-id

- 例

```sh
bin/flink run-application -d -t yarn-application -s 保持点完整路径 -c 全类名 ./jar包
```

> 从保存的checkpint 恢复需要指定到-id目录



- 依赖分析插件 maven helper
- 直接pom指定冲突依赖



# 4.2 状态一致性

- ##### 最多一次(At-Most-Once) =>不重复

- ##### 至少一次(At-Least-Once) =>会重复

- ##### 精确一次(Exactly-Once) => 不会重复



> 端到端一致性
>
> ​	要考虑输入端支不支持重放，也要考虑输出端支不支持重放

- 能否达到exactly-once级别 流处理器内部，数据源，外部存储都要有相应的保证机制
- 如 源只能保证至少一次 其他能保证精准一次 最终也只能至少一次



## 1.1 端到端精确一次

- 输入端 要求数据可重放
  - 如kafka 可重置读取数据偏移量
- Flink处理 开启checkpoint且精准一次
  - barrier对齐精准一次
  - 非barrier对齐精准一次
- 输出端 幂等 或 事务
  - 幂等
    - 利用mysql的主键upsert hbase的rowkey唯一
  - 事务 外部系统提供 事务 : 要么成功要么失败，失败就回滚
    - 两阶段提交写kafka
    - 两阶段提交写mysql XA事务



预写入日志只能保证至少一次，因为一批写，还要等待恢复，如果数据已经写入，确认信息故障，就会重读



## 1.2 两阶段提交

> ##### 用来解决分布式场景下的事务

1. 上一次检查点完成，barrier后续的数据开始进行预提交 (sink多个子任务都在往外写)

   - 节点的数据写入到外部系统临时文件（打上一个标签） 多个子任务一起

2. 当新的检查点完成时，各个节点进行正式提交

   - 检查点定期检查各个节点的数据时候都提交成功了
   - 如果有一个不成功 全部回滚
   - 如果都提交成功了 成为正式数据（撕掉标签）
   - - 当Sink任务收到JobManager发来的检查点完成的通知时，正式提交事务，写入的结果就真正可用了



- #### Sink任务必须能够在进程失败后恢复事务

- #### 提交事务必须是幂等操作。事务的宠物提交应该是无效的

- #### 外部系统必须提供事务支持，或者Sink任务必须能够模拟外部系统上的事务

   

- Kafka的事务超时时间必须大于checkpoint设置的时间

- checkpint必须为精准一次，不论是否Barrier



- Kafka数据源必须设置消费隔离级别为
  - ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_committed"





# 4.3 SQL

> bin/sql-client.sh [embedded] -s yarn-session

- 设置结果显示模式

```sh
#默认table 可以设置为tableau changelog
set sql-client.execution.result-mode=changelog;
```



- 设置执行环境

```sh
#默认streaming（流），也可以设置batch （批）
set execution.runtime-mode=streaming;
```



- 设置并行度

```sh
set parallelism.default=1;
```



- 设置状态生命周期 TTL

```sh
set table.exec.state.ttl=1000;
```



> ### SQL文件初始化

- /opt/mysql.sql

```sql
create database tables;
```

```sh
bin/sql-client.sh embedded -s yarn-session -i /opt/mysql.sql
```



## 1.1 动态表

> 查询一旦开始 就停不了了

每来一条数据，SQL都会重新执行一次

动态表转流，采用撤回追加，有新的数据来，将旧的数据删除，新的数据插入



- ### 更新插入upsrt

> 其实就是 update 和 insert 的合成词，对于更新插入流来说，INSERT插入和UPDATE更新操作，统一被编码为upsrt消息，而DELETE删除操作则被编码为delete消息



## 1.2 时间属性

> ### 事件事件

```sql
create table tableName(
	usee string,
    url string,
    ts timestamp(3), #精确到毫秒
    #将ts字段作为 水位线字段 允许迟到5秒
    WATERMARK FOR ts AS ts - INTERVAL '5' SECOND
)
```

- 时间戳类型必须是TIMESTAMP或者TIMESTAMP_LTZ类型。

- 一般得到的时间戳都是秒或者毫秒的BIGINT类型

- #### UNIX_TIMESTAMP得到秒的时间戳

```sql
ts BIGINT,
time_ltz as TO_TIMESTAMP_LTZ(ts,3),
```



> ### 处理事件

```sql
create table tableName(
	usee string,
    url string,
    ts as proctime(),
)
```



## 1.3 主键 parymary key

> ##### 主键只支持not enforce必须要加的

```sql
create table tableName(
	userid bingint,
    name string,
    PARYMARY KEY(userid) not enforce
) with (
	connector = kafka
    ...
)
```



## 1.4 like

> #### 直接从旧表字段创建新表

```SQL
create table NewTableName(corName ClassName) like oldTableName;
```



## 1.5 Kafka映射表

```sql
create table TableName(
	userid bingint,
    name string,
    ts timestamp(3) metadata from 'timestamp' #将kafka的时间戳获取成为字段 如果字段名与要获取的元数据名一致可以自动 不用from
) with (
	'connector' = 'kafka', #外部连接器
    'topic' = 'topicName', #主题
    'properties.bootstrap.servers' = 'hostname:port', #服务器
    'properties.group.id' = 'groupName', #组ID
    'scan.startup.mode' = 'earliest-offset', #模式
    'format' = 'csv' #格式
)
```



## 1.6 数据生成源表

```SQL
create table source(
	id int,
    ts bigint,
    vc int
)with(
    'connector' = 'datagen',
    'rows-per-second' = '1', #时间限制
    'fields.id.kind' = 'sequence', #自增
    'fields.id.start' = '1', #id字段起始
    'fields.id.end' = '100000', #id字段终止
    
    'fields.ts.kind' = 'sequence', 
    'fields.ts.start' = '1', 
    'fields.ts.end' = '100000', 
    
    'fields.vc.kind' = 'random', 
    'fields.vc.start' = '1', 
    'fields.vc.end' = '100',
);
```



## 1.7 with临时表

```sql
with TmepTableName(column_name[,...]) as (
	select * from source
)
select * from TmepTableName;
```



## 1.8 分组聚合Group by

```sql
select id,count(vc) from source group by id
```



## 1.9 去重distinct

```sql
select distinct 字段 from 表
```



# 4.4 SQL编码

> ##### 只要用到了DataStreamAPI就要.execute()

- #### 依赖

```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-table-api-java-bridge</artifactId>
    <version>1.17.0</version>
</dependency>

<!--  FlinkSQL -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-planner-loader</artifactId>
      <version>1.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-runtime</artifactId>
      <version>1.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-files</artifactId>
      <version>1.17.0</version>
    </dependency>
    <!-- Flink SQL Client -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-sql-client</artifactId>
      <version>1.17.0</version>
    </dependency>

```



```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>env</groupId>
  <artifactId>Flink</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>maven-plugin</packaging>

  <name>Flink Maven Plugin</name>

  <!-- FIXME change it to the project's website -->
  <url>http://maven.apache.org</url>




  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-streaming-java</artifactId>
<!--      <scope>provided</scope> &lt;!&ndash; 打包的时候不会打包进去 &ndash;&gt;-->
      <version>1.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-clients</artifactId>
<!--      <scope>provided</scope>-->
      <version>1.17.0</version>
    </dependency>
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-runtime-web</artifactId>
      <version>1.17.0</version>
    </dependency>
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-files</artifactId>
      <version>1.17.0</version>
    </dependency>
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-kafka</artifactId>
      <version>1.17.0</version>
    </dependency>
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-datagen</artifactId>
      <version>1.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-jdbc</artifactId>
      <version>3.1.2-1.17</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-statebackend-rocksdb</artifactId>
      <version>1.17.0</version>
    </dependency>

<!--    <dependency>-->
<!--      <groupId>org.apache.flink</groupId>-->
<!--      <artifactId>flink-table-planner_2.12</artifactId>-->
<!--      <version>1.17.0</version>-->
<!--    </dependency>-->


<!--  FlinkSQL -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-planner-loader</artifactId>
      <version>1.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-runtime</artifactId>
      <version>1.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-files</artifactId>
      <version>1.17.0</version>
    </dependency>
    <!-- Flink SQL Client -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-sql-client</artifactId>
      <version>1.17.0</version>
    </dependency>



    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-client</artifactId>
      <version>3.3.4</version>
      <exclusions>
        <exclusion>
          <artifactId>jsr305</artifactId>
          <groupId>com.google.code.findbugs</groupId>
        </exclusion>
      </exclusions>
    </dependency>

    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.27</version>
    </dependency>


      <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-table-api-java-bridge</artifactId>
        <version>1.17.0</version>
      </dependency>
      <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-table-api-java</artifactId>
        <version>1.17.0</version>
      </dependency>
      <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-streaming-java</artifactId>
        <version>1.17.0</version>
      </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-common</artifactId>
      <version>1.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-plugin-api</artifactId>
      <version>2.0</version>
    </dependency>
    <dependency>
      <groupId>org.apache.maven.plugin-tools</groupId>
      <artifactId>maven-plugin-annotations</artifactId>
      <version>3.2</version>
<!--      <scope>provided</scope>-->
    </dependency>
    <dependency>
      <groupId>org.codehaus.plexus</groupId>
      <artifactId>plexus-utils</artifactId>
      <version>3.0.8</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.8.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>




  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.6.0</version>
        <configuration>
          <!-- put your configurations here -->
        </configuration>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
            <configuration>
<!--              <minimizeJar>true</minimizeJar>-->
              <artifactSet>
                <excludes>
                  <exclude>com.google.code.findbugs:jsr305</exclude>
                  <exclude>org.slf4j:*</exclude>
                  <exclude>log4j:*</exclude>
                </excludes>
              </artifactSet>
              <filters>
                <filter>
                  <aftifact>*:*</aftifact>
                  <excludes>
                    <exclude>META-INF/*.SF</exclude>
                    <exclude>META-INF/*.DSA</exclude>
                    <exclude>META-INF/*.RSA</exclude>
                  </excludes>
                </filter>
              </filters>
              <transformers combine.children="append">
                <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer">
                </transformer>
              </transformers>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>

</project>
```



- ### 测试

```java
//TODO 1.创建FlinkEnv
StreamExecutionEnvironment flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment();

//TODO 2.使用FlinkEnv创建TableEnv
StreamTableEnvironment tableEnv = StreamTableEnvironment.create(flinkEnv);

//TODO 3.创建一个socket流
DataStreamSource<String> data = flinkEnv.socketTextStream("192.168.45.13", 7777);

//TODO 4.将socket流转换为表视图
//TODO 第一个参数是视图名，第二个是流
tableEnv.createTemporaryView("socket",data);

//TODO 调用TableEnv的sql查询
Table table = tableEnv.sqlQuery("select * from socket");

//TODO 将表转流打印
tableEnv.toChangelogStream(table).print();
flinkEnv.execute();
```



- 输入r,1 r,2 输出r,3

```java
public class WordCount
{
    public static void main(String[] args) throws Exception
    {
        //TODO 1.创建FlinkEnv
        StreamExecutionEnvironment flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        //TODO 2.使用FlinkEnv创建TableEnv
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(flinkEnv);


        //TODO 3.创建一个socket流
        DataStreamSource<String> data = flinkEnv.socketTextStream("192.168.45.13", 7777);

        //TODO 4.将socket流数据转换为2元组
        DataStream<Tuple2<String, Integer>> map = data.map(f ->
        {
            String[] split = f.split(",");
            return new Tuple2<>(split[0], Integer.valueOf(split[1]));
            //TODO 注意泛型擦除
        }).returns(Types.TUPLE(Types.STRING,Types.INT));
        //TODO 创建视图 指定列名
        tableEnv.createTemporaryView("mapTable",map,org.apache.flink.table.api.Expressions.$("Word"),org.apache.flink.table.api.Expressions.$("num"));


        //TODO 调用TableEnv的sql查询
        //TODO 分组求和
        Table table = tableEnv.sqlQuery("select Word,sum(num) from mapTable group by Word");

        //TODO 将表转流打印
        tableEnv.toChangelogStream(table).print();

        flinkEnv.execute();

    }
}
```



## 1.0 UDF函数

### 1.0 标量函数

- ##### 继承ScalarFunction => 标量函数

- 写eval方法，需要public 无重写

- ##### eval方法参数需要 @DataTypeHint(inputGroup = inputGroup.类型) 用来指定输入类型

  - 如果是Object就是ANY

```java
tableEnv.createTemporaryFunction("FunctionName",MyFunction.class);

//Table对象打印可以直接这样写
//就不用调用Env的execute()了
table.execute().print(); 
```



### 2.0 表函数

- ##### 继承 TableFunction<返回的类型>

- 手敲

```java

//如果是Row
@functionHint(output = @DataTypeHint("ROW<返回时的名称 类型(STRING),...>"))
public class MyFunction extends TableFunction<Row>
{
    public void eval(输入类型 cl)
    {
        collect(结果);
    }
}

//注册
tableEnv.createTemporaryFunction("FunctionName",MyFunction.class);
//lateral是交叉联结，使用表函数必须联结
//如果使用左右边关联需要(键 = 键)on true
tableEnv.sqlQuery("select 字段名1,返回结果的字段名,... from tabelName,lateral table(FunctionName(字段名1))");
tableEnv.sqlQuery("select 字段名1,返回结果的字段名,... from tabelName left join lateral table(FunctionName(字段名1)) on true");
```



# 4.5 JSON

> ### 使用Kafka的数据 JSON格式创建表

```sql
CREATE TABLE user_behavior (
  user_id BIGINT,
  item_id BIGINT,
  category_id BIGINT,
  behavior STRING,
  ts TIMESTAMP(3) -- 获取时间戳
) WITH (
 'connector' = 'kafka', --指定模式
 'topic' = 'user_behavior', --指定主题可以多选
 'properties.bootstrap.servers' = 'localhost:9092', --指定链接
 'properties.group.id' = 'testGroup', --消费者组
 'format' = 'json', --解析格式 JSON
 'json.fail-on-missing-field' = 'false', --(字段缺失)解析失败是否跳过 false报错
 'json.ignore-parse-errors' = 'true' --(单纯的异常)解析异常跳过还是报错 false报错
)
```





# 4.6 Flink流式导入HBase

> https://www.bilibili.com/video/BV1gk4y1z7EQ/?spm_id_from=333.337.search-card.all.click&vd_source=6cfabdd9118b8397a529eb6df87378b6

- 拷贝hbase shaderd的jar包到flink opt目录下

```sh
flink/flink-connectors/flink-sql-connector-hbase/target/flink-s
flink/build-target/opt
```



- 拷贝kafka shaderd的jar包到flink opt目录下

```sh
flink/flink-connectors/flink-sql-connector-kafka/tarfet/flink-s
flink/build-target/opt
```



- 拷贝json相关的jar包到 flink opt目录下

```sh
flink/flink-formats/flink-json/target/flink-json-SNAPSHOT.jar
flink/build-target/opt
```



- 启动Flink集群
- 启动Kafka集群



## 1.1 Kafka源表json的读取

```sql
create table 表名Kafka(
	urerName string,
    visitURL string,
    click_ts DECIMAL(38,18)
) with (
	'connector.type' = 'kafka',
    'topic' = '主题名称',
    'startup-mode' = 'earliest-offset', --读取方式
    'connector.properties.bootstrap.server' = '192.168.45.12:9092', --集群地址 bootstrap.server
    'format' = 'json',
    'json-schema' = '{
    	"type" : "object",
    	"properties": {
    		"userName":{
    			"type": "string"
    		},
    		"visitURL":{
    			"type": "string"
    		},
    		"click_ts":{
    			"type": "number"
    		}
    	}
    }'
)
```



- 启动Hbase集群

```sh
./bin/start-hbase.sh
```



## 1.2 Flink Hbase Sink表

```sql
create table 表名Hbase(
	url_address string,
    url_agg row<total_visit_count BIGINT,distinct_users BIGINT>
) with (
    'connector' = 'hbase',
    'table-name' = 'Hbase表名',
    'zookeeper.quorum' = '192.168.45.13:2181 ->?/hbase',
    ?-- zookeeper.znode.parent = '/hbase',
    [
        write.buffer-flush.max-size = '10mb',
        write.buffer-flush.max-rows = '1000',
        write.buffer-flush.interval = '2s',
    ]
);
```



- 通过SQL将Kafka的数据导入到 Hbase表

```sql
插入到，表明Hbase              click_ts键(Row_key)，行 urerName,visitURL
insert into 表名Hbase select click_ts,ROW(urerName,visitURL) from 表名Kafka;
```

