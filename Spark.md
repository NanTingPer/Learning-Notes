

clear清屏

CTRL + D复制当前行内容到下一行

ALT拉行统一编辑

> #### Lambda表达式原则

- ##### 方法明确有返回值 可以省略return

- ##### 可以换行表示代码逻辑 ;可以省略

- ##### 如果逻辑只有一行，大括号可以省略

- ##### 参数只有一个 小括号可以省略

- ##### 参数在逻辑中只使用了一次(需要有对象来实现功能)

- IN -> OUT 

  - IN : 传入的值
  - OUT : 返回的值

# 1.0概述

> ##### 分布式计算的核心 : 切分数据 减少数据规模
>
> 分布式集群中心化基础架构 : 主从架构(Master - Slave(奴隶))
>
> #### 基于内存的快速、通用、可扩展的大数据分析计算引擎

- ##### 内置模块

  - ##### Spark SQL 结构化数据

  - ##### Spark Streaming 实时计算

  - ##### Spark Mlib 机器学习

  - ##### Spark GraghX 图计算

# 1.1部署 Spark

> #### 将软件安装到什么位置
>
> ##### 部署Spark指的是Spark的程序逻辑在谁提供的资源中执行

- ##### 如果资源是当前单节点提供的，那么就称为单机模式 Local

- ##### 如果资源是当前多节点提供的，那么就称为分布式模式

  - 如果资源是由Yarn提供的，那么就称之为Yarn部署环境
  - 如果资源是由Spark提供的，那么就称之为Spark部署环境(Standalone)

- #### 解压缩

  - tar -zxvf sparkxxxxx.tgz
    - 解压
  - mv sparkxxxx/ spark-local
    - 重命名
  - mv spark-local /opt/module/
    - 移动

# 1.2Local模式

> ### 上面的解压缩操作就等于部署好了Local模式

- 运行官方提供的测试jar包
- --class 主类全类名
- --master local[*] 环境
- 10 运行次数
  - spark-submit --class org.apache.spark.examples.SparkPi --master local[*] ./jarbag位置 10

# 1.3Yarn模式

- 进入Spark的conf文件夹，将配置文件 spark-env.sh.template的后缀改为sh
  - mv spark-env.sh.template spark-env.sh
  - ////
  - cp spark-env.sh spark-env.sh
- 进入spark-env.sh进行编辑
- 添加内内容
  - 指定yarn配置文件路径
    - YARN_CONF_DIR=/opt/module/hadoop/etc/hadoop
- 启动Hadoop集群
- 执行examples
  - spark-submit --class org.apache.spark.examples.SparkPi --master yarn ../examples/jars/spark-examples_2.12-3.1.1.jar

## 1.3.1 三个进程

- ##### SparkSubmit 提交进程

- ##### YarnCoarseGrainedExecutorBackend 干活进程

- ##### ExecutorLauncher 管理进程(客户端模式)

- ##### ApplicationMaster (集群模式)

## 1.3.2 Yarn历史服务 history

> ##### spark-yarn/conf/spark-defaults.conf

- 添加内容
  - spark.eventLog.enabled	true
  - spark.eventLog.dir	hdfs://xxx:8082/directory
  - spark.yarn.historyServer.address=xxx:18080
  - spark.history.ui.port=18080

> ##### spark-yarn/conf/spark-env.sh

- 添加内容
  - exprot SPARK_HISTORY_OPTS="
  - -Dspark.history.ui.port=18080
  - -Dspark.history.fs.logDirectory=hdfs://xxx:8020/directory
  - -Dspark.history.retainedApplications=30"

```sh
export SPARK_HISTORY_OPTS="
-Dspark.history.ui.port=18080 
-Dspark.history.fs.logDirectory=hdfs://xxxx:端口/directory 
-Dspark.history.retainedApplications=30"
```

- 重启Spark历史服务
  - sbin/stop-history-server.sh
  - sbin/start-history-server.sh

## 1.3.3执行流程

> ##### RM资源申请
>
> NM nodeMaster

1. ##### 业务代码 -> subm

2. ##### submit -> context环境 ->Core -> SparkSubmit(JVM进程)(客户端) -> HDFS(运行环境全部上传)

3. ##### HDFS -> Yarn(Yarn下载文件)

4. ##### submit(Yarn)->RM(Yarn)

5. ##### RM->Driver(Yarn) NM

   1. ##### Driver(程序)

6. ##### RM->Executor(Yarn) NM

   1. ##### Executor(执行人) (slave)

7. ##### HDFS -> Driver -> Executor

> ### 将Driver放入集群内部 -> 集群模式

- #### Spark有yarn-client和yarn-cluster两种模式，主要区别在于 Driver程序的运行节点(默认client)

  - ##### client : Driver程序运行在客户端

  - ##### cluster : Driver程序运行在由ResourceManager启动的APPMaster

```sh
spark-submit 
--class org.apache.spark.examples.SparkPi 
--deploy-mode cluster
--master yarn ../examples/jars/spark-examples_2.12-3.1.1.jar
```

## 1.3.4模式对比

| 模式       | Spark安装机器数 | 需要启动的进程 | 属于   |
| ---------- | --------------- | -------------- | ------ |
| Local      | 1               | 无             | Spark  |
| Standalone | 3               | Master和Worke  | Spark  |
| Yarn       | 1               | Yarn和HDFS     | Hadoop |



# 2.0 SparkCore

## 2.1RDD概述

> #### RDD 弹性分布式数据集 是Spark中最基本的数据抽象
>
> ##### 分布式计算模型
>
> 它一定是个对象
>
> 它一定能完成对数据的一些操作(对数据结构做操作)
>
> 它一定封装了大量的方法和属性(计算逻辑)
>
> - 但是逻辑不能太复杂
> - 复杂的逻辑可以使用大量的简易的逻辑 组合在一起实现复杂的功能
>
> 它一定需要适合进行分布式处理(减小数据规模 并行计算)
>
> - RDD的操作类似字符串
>   - 字符串的功能是单点操作，功能一旦调用，就会马上执行
>   - RDD的功能是分布式操作，功能调用，但不会马上执行



- ##### 数据模型

  - #####  对事物属性的抽象，包含数据结构

- ##### 数据结构

  - ##### 采用特殊的结构组织和管理数据

    - ##### 数组

## 2.2 逻辑

1. ##### 数据 -> Driver(Master)

2. ##### Driver(内) -> RDD(封装逻辑) -> 拆分数据

3. ##### 打包数据Task(任务) [数据切片,逻辑] -> Slave



## 2.3数据处理的IO操作

> ## 多个RDD的功能如何组合在一起实现复杂逻辑

- IO : Input , Output

- ##### InputStream => 单个读取后直接执行操作 效率低

- ##### BufferdInputStream(new FileInputStream)

  - ##### 缓冲流包裹文件字节输入流

  - ##### Bufferd(缓冲区)本身无法读取文件

  - ##### 数据流入缓冲区，先不执行操作

  - ##### 缓冲区满了，执行操作

  > ### 字节流转字符流

- BufferedReader(new InputStreamReader(new FileInputStream))
- InputStreamReader => 转换流
  - 数据通过FileInputStream读取
  - 放入InputStreamReader缓冲区(转换) 3字节后转换为字符（Java编码使用）
  - 转换后数据放入字符缓冲流
  - 满后执行操作

> # 组合方式
>
> ##### BufferedReader(new InputStreamReader(new FileInputStream))
>
> ##### 设计模式 => 装饰者设计模式 : 组合功能 / 套娃
>
> ##### 将多个功能 装成了一个对象
>
> - 选择性的使用功能

> # RDD的处理方法和JavaIO流完全一样 
>
> # 也采用装饰者设计模式来实现功能的组合

# 3.0RDD编程

## 3.1创建RDD

> ### 方法

1. ##### 从集合中创建

2. ##### 从外部存储创建

3. ##### 从其他RDD创建

> ### 操作

1. 创建新的项目 => Maven项目
2. 添加依赖(对 spark-core)

```xml
<dependencies>
	<dependency>
		<groupId>org.apache.spark</>
		<artifactId>spark-core_版本(2.12)</> //scala语言版本
		<version>版本(3.3.1)</> //Spark版本
	</>
</>
```

> ### 代码实现

> ##### 构建Spark环境

```java
public class Spark_RDD_env
{
    public static void main(String[] args)
    {
        //创建Spark配置文件
        SparkConf conf = new SparkConf();
        //设置模式
        conf.setMaster("local");
        //设置程序名称
        conf.setAppName("Spark");
        //创建Spark运行环境
        JavaSparkContext sc = new JavaSparkContext(conf);
        sc.close();
    }
}
```

> ##### 构建RDD

```java
//利用Spark环境 对接内存数据源 并构建RDD
//parallelize => 并行
//内存中list必须存在
JavaRDD<String> javaRDD = sc.parallelize(list);
//收集数据
List<String> cool = javaRDD.collect();
//使用forEach结合Lamba表达式输出
cool.forEach(a -> System.out.println(a));
sc.close();
```

> 从文本文件中构建

```java
//读取文本文件构建rdd
JavaRDD<String> rdd = sc.textFile("src/main/java/Spark_RDD/Spark.txt");
//收集数据
List<String> data = rdd.collect();
//使用方法引用输出
data.forEach(System.out::println);
```

# 3.1RDD的理解

> - #### RDD数据模型存在泛型 只操作数据 不存，因为它是数据集
>
> - #### RDD类似于数据管道 用于对接(流转)数据
>
> - ##### RDD的组合
>
>   - ##### 两根管子对接
>   
> - 对于Max操作 ，先取到每个区的Max 再去每个区的最大取MAX

# 3.2RDD的文件分区

> Partition(分区)
>
> saveAsTextFile保存为文本文件
>
> 

- local环境中，分区数量和环境vcpu数量有关，但不推荐
- parallelize方法可以传递2个参数
  - 第一个参数表示对接的数据源集合
  - 第二个参数表示切片(分区)的数量
- 三种配置方式
  - 从配置对象中获取默认值 "spark.default.parallelism"
  - 使用方法参数
  - 使用配置参数

> File的分区
> 基于Hadoop的分区规则

- Hadoop切片规则
  - totalsize : 3byte => 文件总字节大小
  - goalsize : totalsize / min-part-num =>  文件总字节大小 / 最小分区数量 => 单个分区大小
  - part-num : 分区数量 => 文件总字节大小 / 单个分区大小
  - 如果part-num得到了余数，余数如果大于单个分区大小的10%那么建新区

- 在textFile的第二参数指定的是最小分区数



# 3.3RDD的内存数据源分配

> #### Spark分区数据的存储基本原则 : 平均分

- ##### (0 until 4) => [0,1,2,3]  取0到3不含4,这个4是指定的分区数

- ##### i * length / numSlices, ((i+1)*length)/numSlices

  - ##### length => 数据的长度

  - ##### numSlices => 切片的数量

- ##### (start,end) =>

  - ##### 开始点和结束点

# 3.4RDD的磁盘数据源分配

> #### Spark不支持文件操作，文件的操作都是由Hadoop完成的
>
> - Hadoop进行文件切片数量的计算核心文件数据存储计算规则不一样。
>   - 分区数量计算的时候，考虑的是尽可能的平均 => 
>     - 按字节来算
>   - 分区数据的存储是考虑业务数据的完整性 => 
>     - 不能按照字节来算 按照行来取
>     - 读取数据时，还需要考虑数据偏移量，偏移量从0开始
>     - 相同的偏移量不能重复读取

- ## 例

> 文件中有7字节
>
> 文件内容 => @回车
>
> {
>
> 1@@ => 012
>
> 2@@ => 345
>
> 3	=> 6
>
> }
>
> 第一个分区 [数据起始位,终止数据位]  数据3字节 把第二行的数据读取到了，直接整行读
>
> 【3byte】 =>[0,2] => 【1,2】
>
> 第二个分区 [数据起始位]  345已经被读过了，直接读下一个
>
> 【3byte】 =>[3,5] => 【3】
>
> 第三个分区 [数据起始位]（大于10%）全部被读完了 空
>
> 【1byte】 =>[6,6] => 【】

- # 结

> #### 在磁盘的数据读取上 Spark自己没有数据分配策略 使用的是hadoop的数据分配策略
>
> hadoop需要保证业务完整性 所以单个分区的内容即使溢出Spark的计算值，也要保证将整行数据放入
>
> 而被使用过的数据偏移量不能被重复使用，所以下一个数据分区读取的数据开始位就是上个块所读到的结束位
>
> 为什么会出现空分区=>
>
> ​	因为数据都被读取完了



> ### 在使用Spark读取磁盘数据时，需要分行，不然会出现数据倾斜
>
> #### 每一行 一个业务数据



# 3.5RDD功能

> ### String.trim只能去掉半角 不能去掉全角
>
> RDD方法两大类
>
> - RDD有很多的方法可以将数据向下流转，称之为转换
>   - Transformation 转换算子(转换方法)
>   - 算子 : 认知心理学 operate (操作,方法)
>     - 问题(初始) ->  算子 ->  问题(中间)  -> 算子  -> 问题(状态)
> - RRD有很多的方法可以控制数据流转，称之为行动
>   - Action 行动算子(行动方法)
>
> 学习重点 : 
>
> 1. 名字
> 2. 输入 IN
> 3. 返回 OUT

- #### parallelize => 把集合用作数据源

> ## RDD方法处理数据的分类
>
> - 单值 ->  1, "abc" , new User() , new ArrayList() ,(Key,Value)
> - 键值 -> KV => (Key,Value) 不当整体  Entry(键值对)

- ## KV对

> #### 元组 => 将无关数据封装在一起，形成一个整体，称之为元素的组合
>
> #### 如果元组中的数据只有2个，称之为对偶元组，也称为键值对

## 3.5.1 map方法

> #### 翻译 v. 使变换 => 给一个类型 得到另一个类型
>
> #### 			=> 给一个数据 得到另一个数据
>
> 在数据处理过程中 一般不会改变原始数据
>
> 作用 : 传入A转换为B，不限制A和B的关系

### 3.5.1.1map方法 - 原理

> #### 一个RDD只做一件事 => 第一个RDD对接数据源
>
> ##### 新RDD内数据的分区数量和旧的RDD的数量保持一致

> ##### 数据流转过程中，数据所在分区会如何变化
>
> - ##### 默认情况下，数据流转所在的分区编号不变
>
> ##### 数据流转的顺序
>
> - 与kafka一样，单分区内有序，多分区无序
> - 一个数据把一个RDD跑完后，不会等待其他数据也把这个RDD跑完，而是直接流往下一个RDD



- parallelize("1,2,3,4",2) .map(num->num*2);



## 3.5.2 filter过滤

> ##### RDD可以根据指定的过滤规则对数据源中的数据进行筛选过滤
>
> ##### 如果满足规则，保留数据，不满足规则，直接丢弃
>
> 在执行过程中可能会产生数据倾斜



## 3.5.3flatmap 扁平化映射

> #### 数据扁平化映射
>
> #### 将整体数据拆分为个体来使用

```java
//对接内存数据(并行)
sc.parallelize(Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(5, 6, 7, 8)))
    //进行扁平化
        .flatMap((FlatMapFunction<List<Integer>, Integer>) integers ->
        {
            //进行映射
            List<Integer> list = new ArrayList<>();
            integers.forEach(unm -> list.add(unm + 2));
            return list.iterator();
        })
        .collect()
        .forEach(System.out::println);
```

> #### 拆分文本字符 空格隔开

```java
//创建JavaSpark运行环境
JavaSparkContext sc = new JavaSparkContext(conf);
//对磁盘文件进行对接
sc.textFile("src/main/java/Spark_RDD/Spark.txt")
    //扁平化映射
        .flatMap((FlatMapFunction<String, String>) s ->
        {
            //扁平化
            ArrayList<String> arr = new ArrayList<>();
            for (String string : s.split(" "))
            {
                arr.add(string);
            }
            return arr.iterator();
        })
        .collect()
        .forEach(System.out::println);
sc.close();
```



## 3.5.4groupBy分组

> #### 返回组对应的名称
>
> 组名称一样的就将数据存入组
>
> 默认情况下，数据处理后，所在分区不会发生改变，但是groupBy除外
>
> Spark在数据处理中，要求同一个组的数据必须在同一个分区中
>
> 所以分组操作会将数据分区打乱重新组合，这个操作在Spark中称为Shuffle

> #### 有第二个参数 可以改变分区数 	
>
> ### 返回值是一个KV类型



```java
sc.textFile("src/main/java/Spark_RDD/Spark.txt")
        .flatMap((FlatMapFunction<String, String>) s ->
        {
            ArrayList<String> list = new ArrayList<>();
            for (String string : s.split(" "))
            {
                list.add(string);
            }
            return list.iterator();
        })
        .groupBy((Function<String, String>) s -> s.substring(0, 1))
        .collect()
        .forEach(System.out::println);
sc.close();
```

> 输出

```tex
(w,[w2g])
(爱,[爱无关给])
(a,[a2wf, awfawg鸟我哈, aw, aw2g, aw2g, a2])
(挖,[挖坟爱无关])
(f,[f2aw])
(该,[该模块我])
(阿,[阿文])
(g,[g2a])
```

```java
sc.textFile("src/main/java/Spark_RDD/Spark.txt")
        .flatMap((FlatMapFunction<String, String>) s ->
        {
            ArrayList<String> list = new ArrayList<>();
            for (String string : s.split(" "))
            {
                list.add(string);
            }
            return list.iterator();
        })
        .groupBy((Function<String, String>) s -> s.substring(0, 1))
        .map((Function<Tuple2<String, Iterable<String>>, Tuple2<String, Integer>>) stringIterableTuple2 ->
            {
                int a =0;
                Iterator<String> iterator = stringIterableTuple2._2.iterator();
                while (iterator.hasNext())
                {
                    iterator.next();
                    a++;	
                }
                return new Tuple2<>(stringIterableTuple2._1, a);
            })
    	.collect()
        .forEach(System.out::println);
sc.close();
```

> 输出

```
(w,1)
(爱,1)
(a,6)
(挖,1)
(f,1)
(该,1)
(阿,1)
(g,1)
```

## Shuffle

> RDD对象不能保存数据
>
> 当前groupBy操作会将数据保存到磁盘文件中，保证数据全部分组后执行后续操作
>
> Shuffle操作一定会落盘
>
> Shuffle操作有可能会导致资源浪费
>
> - 原本3个分区，但是分组后有一个空区
>
> Spark中含有Shuffle操作的方法都有改变分区的能力
>
> RDD的分区和Tast之间有关系 (一个RDD就是一个Tast)
>
> Shuffle会将流程一分为二 一部分写盘 一部分读盘
>
> - 写磁盘没完成，不允许读磁盘

## 3.5.5 Distinct 去重

> 分布式去重 => 采用了分组  + shuffle 的处理方式

## 3.5.6 sortBy排序

> sortBy 方法 : 按照指定的排序规则对数据进行排序
>
> sortBy方法可以传递三个参数
>
> - 第一个参数表示排序规则 :
>   - Spark会为每一个数据增加一个标记，然后按照标记对数据进行排序
> - 第二个参数表示排序的方式 : 升序 true 降序 false
> - 第三个表示分区数量(shuffle)
>
> 



# 3.6KV类型的处理

## 3.6.1初识 KV类型

```java
//创建元组
Tuple2<String,Integer> t1 = new Tuple2<>("a",1);
Tuple2<String,Integer> t2 = new Tuple2<>("b",2);
Tuple2<String,Integer> t3 = new Tuple2<>("c",3);
//创建元组集合
List<Tuple2<String,Integer>> listTuple = new ArrayList<>();
listTuple.add(t1);
listTuple.add(t2);
listTuple.add(t3);
//构建RDD
JavaSparkContext jsc = new JavaSparkContext(conf);
jsc.parallelizePairs(listTuple)
        //只对值做操作
        .mapValues(tul1 ->tul1 * 2)
        .collect()
        .forEach(tuple ->{
            System.out.print(tuple._1());
            System.out.println(tuple._2());
        });
//释放资源
jsc.close();
```

> #### 输出

```json
a2
b4
c6
```



## 3.6.2单值类型转换KV

> #### maptoPair

```java
//构建Spark运行环境
JavaSparkContext jsc = new JavaSparkContext(conf);
//获取RDD
jsc.parallelize(Arrays.asList(1,2,3,4,5,7))
    //转换为KV类型
        .mapToPair(num ->{
            //可自定义返回
            return new Tuple2<>(num,num * 2);
        })
    //对值乘以2
        .mapValues(num -> num * 2)
    //捕获数据
        .collect()
    //遍历输出
        .forEach(System.out::println);
//释放资源
jsc.close();
```

> ### groupBy

```java
//构建Spark运行环境
JavaSparkContext jsc = new JavaSparkContext(conf);
List<Integer> intList = Arrays.asList(1,2,3,4,5,7);
//获取RDD
jsc.parallelize(intList)
    //奇数偶分组
   .groupBy(num -> num % 2)
    //区内求和
   .mapValues(num ->{
       Iterator<Integer> iterator = num.iterator();
       int numsub = 0;
       while (iterator.hasNext())
       {
           numsub += iterator.next();
       }
       return numsub;
   })
  .collect().forEach(System.out::println);
//释放资源
jsc.close();
```

```json
(0,6)
(1,16)
```

## 3.6.3WordCount

```java
JavaSparkContext sc = new JavaSparkContext(conf);
//获取文件源
sc.textFile("src/main/java/Spark_RDD/KVClass/Word.txt")
  //切分
        .flatMap(strs -> Arrays.asList(strs.split(" ")).iterator())
    //分组
        .groupBy(words -> words)
    //计数
        .mapValues(strnum ->{
            int count = 0;
            Iterator<String> iterator = strnum.iterator();
            while (iterator.hasNext())
            {
                iterator.next();
                count ++;
            }
            return count;
        })
    //取出
        .collect()
        .forEach(System.out::println);
sc.close();
```

## 3.6.4groupByKey

> #### 强制按照Key分组
>
> 
>
> #### 按照K对V进行分组 只是把V放一块，而不是把数据放一块

```java
//创建元组并行数据源
sc.parallelizePairs(Arrays.asList(
        new Tuple2<String, Integer>("a", 1),
        new Tuple2<String, Integer>("b", 2),
        new Tuple2<String, Integer>("b", 3),
        new Tuple2<String, Integer>("a", 4)
)).groupByKey().collect().forEach(System.out::println);
```

> 输出

```json
(a,[1, 4])
(b,[2, 3])
```

## 3.6.5GroupByKey WordCount

```java
//创建配置文件
SparkConf conf = new SparkConf();
conf.setMaster("local[*]");
conf.setAppName("WordCount_groupByKey");
//创建JavaSpark环境
JavaSparkContext sc = new JavaSparkContext(conf);
//创建元组并行数据源
sc.parallelizePairs(Arrays.asList(
        new Tuple2<String, Integer>("a", 1),
        new Tuple2<String, Integer>("b", 2),
        new Tuple2<String, Integer>("b", 3),
        new Tuple2<String, Integer>("a", 4)
))
    //按照Key进行分组
    .groupByKey()
    //求组元素数量
    .mapValues(value ->
    {
        int a = 0;
        Iterator<Integer> iterator = value.iterator();
        while (iterator.hasNext())
        {
            iterator.next();
            a ++;
        }
        return a;
    }).collect()
    .forEach(System.out::println);
sc.close();
```

> #### 输出

```json
(a,2)
(b,2)
```



## 3.6.6 reduceByKey(聚合 减少)

> 计算的基本思想 => 永远都是两两相加

```java
//创建元组并行数据源
sc.parallelizePairs(Arrays.asList(
        new Tuple2<String, Integer>("a", 1),
        new Tuple2<String, Integer>("b", 2),
        new Tuple2<String, Integer>("b", 3),
        new Tuple2<String, Integer>("a", 4)
)).reduceByKey(new Function2<Integer, Integer, Integer>()
{
    @Override
    public Integer call(Integer integer, Integer integer2) throws Exception
    {
        return integer + integer2;
    }
}).collect()
  .forEach(System.out::println);
sc.close();
```

> 简化

```java
//创建元组并行数据源
sc.parallelizePairs(Arrays.asList(
        new Tuple2<String, Integer>("a", 1),
        new Tuple2<String, Integer>("b", 2),
        new Tuple2<String, Integer>("b", 3),
        new Tuple2<String, Integer>("a", 4)
)).reduceByKey((num1,num2) -> num1 + num2).collect()
  .forEach(System.out::println);
sc.close();
```

> 
>
> num1是上一个聚合后的V num2是下一个等待被聚合的V



## 3.6.7 sortByKey

> 直接按照Key排序



# 3.7优化shuffle

> 1. 增加磁盘读写缓冲区
> 2. 在不影响最终结果的情况下，磁盘的读写数据越少，性能越高



> reduceByKey => 方法可以在shuffle之前，分区内进行预先聚合，减少落盘的数据量
>
> 如果遇见需要分组聚合的功能，优先采用reduceByKey
>
> - 预聚合 combine
>   - 先在同区内将K一样的进行聚合



# 3.8分区更改 coalesce

> ## coalesce(分区数量,启动shuffle);
>
> 默认没有shuffle功能，数据不会被打乱重新组合，所以如果要扩大分区是无法实现的。
>
> 可以设定第二个shuffle参数，如果谁当为true那么可以扩大分区

> ### 重分区(扩大用)
>
> #### repartition
>
> repartition就是将coalesce的shuffle设置为了true



# 3.9 行动算子Action

> collect方法就行行动算子
>
> ​	RDD的行动算子就会触发作业Job的执行

> 如何区分行动算子和转换算子
>
> #### 	方法返回RDD的 就是转换算子

# 4.0Collect

> 把Executor端的数据拉取回Driver端(按插入顺序拉回)
>
> 方法的计算都在Executor端

> ##### Spark在编写代码时，调用转换算子，并不会真正执行，因为只是在Driver端组合功能
>
> 主方法(main)也称为driver方法，当前运行的main线程，也称之为Driver线程
>
> 转换算子中的逻辑代买就是在Executor端执行的。并不会在Driver端调用和执行
>
> RDD封装的逻辑其实就是转换算子中的逻辑

> collect方法可能会导致多个Executor的大量数据拉取到Driver端，导致内存溢出

## 4.0.1 count 返回结果数量

> 返回结果数量



## 4.0.2 first 获取结果的第一个

> 获取结果的第一个



## 4.0.3 take 从结果中获取前N个

> 从结果中获取前N个



## 4.0.4 countByKey 将结果按照Key计算数量

> 将结果按照Key计算数量



## 4.0.5 saveAsTextFile 保存

> 保存为分区文件



## 4.0.6 saveAsObjectFile 保存

> 序列化成文件保存(用来保存对象)



## 4.0.7 foreachPartition 遍历

> 按分区进行遍历
>
> ### 执行效率高 但是依托于内存大小



# 5.0序列化

> 为什么要序列化
>
> ​	对象在Driver端创建，在Executor使用到了这个对象
>
> ​	运行过程中，就需要将Driver中的对象通过网络传递到Executor端，否则无法使用
>
> ​	要跨端使用的对象就必须要实现可序列化接口，否则无法传递
>
> #### 所谓序列号就是把对象变成字节数组



RDD算子的逻辑代码是在Executor端执行的，其他的代码都是Driver端执行

例如 : map

​	方法内的内容才是Executor端执行的，其他都是在Driver端



# 6.0RDD依赖关系

> RDD.toDebugString => 获取血缘
>
> RDD.rdd().dependencies => 获取依赖
>
> Scala中直接.dependencies



> 如果A依赖了B 那么A就依赖B
>
> #### RDD依赖:Spark中相邻的2个RDD之间存在的依赖关系
>
> #### RDD依赖:新的RDD间接依赖（血缘关系）相邻外更老的RDD
>
> 
>
> ##### Spark中的每一个RDD都保存了依赖关系和血缘关系
>
> Spark中RDD不存储数据，最终写入错误后重试需要询问来源，重新将源数据流转
>
> #### 为了分布式流转



- 在Driver端准备计算逻辑(RDD的关系) -> 
- 由Spark对关系进行判断决定任务的数量和关系 ->
- 计算逻辑是在Executor端执行



## 6.1 窄依赖(独生)

> #### OneToOneDependency

- ##### RDD的依赖关系本质上部署RDD对象的关系

  - ##### 说的是RDD对象中分区数据的关系

- ##### 如果计算上游的RDD(前面的)的一个分区的数据被下游RDD的一个分区所独享

- ##### 不进行Shuffle就是窄依赖

- ##### 各个分区数据没有散布不叫窄依赖





## 6.2 宽依赖(多生)

> #### ShuffleDependency

- ##### 计算中上游的RDD的一个分区的数据被下游RDD的多个分区所共享

- ##### 下游存在Shuffle操作 就是宽依赖





## 作业 阶段 任务

- #### Job -> 作业

  - ##### 行动算子执行时，会触发作业的执行(ActiveJob)

- #### Stage -> 阶段

  - ##### 一个Job中RDD的计算流程，默认就是一个完整的阶段

  - ##### 但是如果计算流程中存在shuffle

  - ##### 流程就会一分为二

  - ##### 分开的每一段就称为一个Stage(阶段)

  - ##### 前一个阶段不执行完成，后一个阶段就不允许执行

- #### Task -> 任务

  - ##### 每个Executor执行的计算单元

  - #### 任务的数量就是每个阶段分区数量之和

  

- ##### 如果存在shuffle那么阶段就会一分为二

  - ##### ShuffleMapStage 分开(shuffle之前)

  - ##### ResultStage 整体



- ##### submitStage从下级往上级提交（递归）

  - ##### 如果没有上级，那么就提交



- ###### ~~rdd.partitions.length(当前rdd几个分区)~~

- ShuffleMapTask 写盘 (被shuffle切分的上一个阶段)

- ResultTask 读盘

























































DAG => 有向无环图

dagScheduler => 有向无环图调度器











































































































































































































































































- 

- <!--~~<u>*1.0 Spark环境搭建*</u>~~-->

- ## <!--~~<u>*1.1准备工作 Local-本地模式*</u>~~-->

- - ##### <!--~~<u>*保证有JDK(java1.8)*</u>~~-->

  - ##### <!--~~<u>*Windows(开发环境下)需要Scala Liunx不需要*</u>~~-->

  - ##### <!--~~<u>*Spark安装包*</u>~~-->

- ### <!--~~<u>*1.2环境搭建*</u>~~-->

- - <!--~~<u>*解压 Spark*</u>~~-->
  - <!--~~<u>*更改名称 Spark*</u>~~-->
  - <!--~~<u>*运行 bash bin/spark-shell.sh*</u>~~-->

- 

- 

- 

- 

- 

- # <!--<u>~~*环境搭建*~~</u>-->

- ## <!--<u>~~*本地环境搭建*~~</u>-->

- - #### <!--<u>~~*准备工作*~~</u>-->

    - <!--<u>~~*安装JDK*~~</u>-->
    - <!--<u>~~*Scala安装包 -- Windows*~~</u>-->
    - <!--<u>~~*Spark安装包*~~</u>-->

- <!--<u>~~**~~</u>-->	

- - #### <!--<u>~~*操作*~~</u>-->

    - <!--<u>~~*将安装包上传到node1*~~</u>-->
    - <!--<u>~~*解压spark安装包*~~</u>-->

    ##### <!--<u>~~*更改文件权限*~~</u>-->

    <!--<u>~~*chown -R root 路径*~~</u>-->

    <!--<u>~~*chgrp -R root 路径*~~</u>-->

    - <!--<u>~~*更改长链接为软链*~~</u>-->

    <!--<u>~~*ln -s 长名 软连*~~</u>-->

  - #### <!--<u>~~*解压操作*~~</u>-->

    - <!--<u>~~*tar -zxvf 压缩包 -C 目录*~~</u>-->

  - #### <!--<u>~~*文件名称更改*~~</u>-->

    - <!--<u>~~*mv 文件名 更改后*~~</u>-->

- 

- - ### <!--<u>~~*文件提交与运行*~~</u>-->

    - <!--<u>~~*--master 谁来提供资源*~~</u>-->
    - <!--<u>~~*bin/spark-submit --class 类名 --master 运行环境(local本机) jar包路径*~~</u>--> 

- 

- - ### <!--<u>~~*Yarn环境配置*~~</u>-->

    - <!--<u>~~*/opt/module/spark-yarn/conf*~~</u>-->
    - <!--<u>~~*在spark-env.sh最后面添加 YARN_CONF_DIR=/opt/module/hadoop/etc/hadoop*~~</u>-->

- # <!--<u>~~*Spark代码开发*~~</u>-->

- - ## <!--<u>~~*工程创建 - Manve*~~</u>-->