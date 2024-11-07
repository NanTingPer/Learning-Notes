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
