# Kafka => 消息队列

# 提前准备

zookeeper.connect=集群主机1:2181,集群主机2:2181......



免密码ssh

​	ssh-keygen

​	ssh-copy-id root@192.168.45.11

1. 先解压zookeeper重命名为zookeeper
2. 拷贝conf下的zoo_sample.cfg 重命名为zoo.cfg
3. 编辑文件_zoo.cfg
   1. dataDir改为持久化目录
4. 启动zookeeper
   1. 进入bin目录 
      1. ./zkServer.sh start 启动
5. 解压kafka并重命名
6. 进入kafka目录
   1. 修改config目录下的server.properties
      1. broker.id=0 //每台改+1
      2. listeners=PLAINTEXT://IP:9092  (就在id下面一点)   
      3. zoop那边的IP也改成本机
      4. 保存
7. 进入bin目录 启动 &表示后台进程
   1. zoop-server-start.sh ../config/zoop.pro &
   2. kafka-server-start.sh ../config/server.properties &
8. 创建topic 
   1. kafka-topic.sh --zookeeper master:2181 --create --topic dome --xx 1 --xx 1
9. 链接
   1. kafkaxxx --sever master:9092 --topic dome

副本数必须小于等于已有kafka集群数量



# 1.Kafka简介

## 1.1 消息队列

- 消息队列 -> 用于存放消息的组件
- 可以放入消息，也可以获取消息
- 做为零时的存储
- 消息队列中间件: 消息队列的组件


# 集群搭建

1. ##### *~~下载kafka~~*

2. ##### *~~上传到Liunx~~*

- ### *~~我使用的是wsl所以直接cp了~~*

  - #### *~~cp kafak /mnt~~*



- ### *~~解压~~*

  - #### *~~tar -xvzf kafka~~*



- ### *~~修改server.properties~~*

  - #### *~~进入解压后的目录~~*

  - ##### *~~cd /config~~*

  - ##### *~~vim server.properties~~*

- #### *~~添加内容/修改内容~~*

  - *~~broker.id=0~~*
  - *~~//这个是数据存放位置~~*
  - *~~log.dors=路径~~*

## *~~<u>1.集群规划</u>~~*

| *~~<u>hadoop102</u>~~* | *~~<u>hadoop103</u>~~* | *~~<u>hadoop104</u>~~* |
| ---------------------- | ---------------------- | ---------------------- |
| *~~<u>zk</u>~~*        | *~~<u>zk</u>~~*        | *~~<u>zk</u>~~*        |
| *~~<u>kafka</u>~~*     | *~~<u>kafka</u>~~*     | *~~<u>kafka</u>~~*     |

1. #### *~~<u>上传kafka文件</u>~~*

2. #### *~~<u>然后解压 => tar -zxvf kafka.tgz</u>~~*

## *~~<u>2.Kafka配置文件</u>~~*

- #### *~~<u>唯一标识 server.properties</u>~~*

  - #### *~~<u>broker.id=0</u>~~*

- #### *~~<u>数据存储 log.dirs=/opt/module/kafka/datas</u>~~*

- #### *~~<u>Zookeeper</u>~~*

  - #### *~~<u>zookeeper.connect=</u>~~*

    - ##### *~~<u>集群名称:端口(2181),集群名称:端口(2181)/kafka(目录)</u>~~*

## 3.设置主机绑定用于分发内容

- ### vim /etc/hostname

  - ##### IP master

  - ##### IP slave1

  - ##### IP slave2

## 4.内容分发

- ##### scp /module/kafka root@slave1:/opt/module

- ##### scp /module/kafka root@slave2:/opt/module

  - 各个修改id值 分别为 1 ,2

## 5.环境变量配置

- ##### cd /etc/profile.h

- ##### touch my_path.sh

  - ##### export PATH=$PATH:/etc/module/kafka/bin

  - ##### export PATH=$PATH:/etc/module/java/bin

- #### 关闭时候 需要先关kafka在关zoo

## 6.Kafka命令行操作

| bin/kafka-topics.sh(主题)                          |                               |
| -------------------------------------------------- | ----------------------------- |
| --bootstrap-server <String :server toconnect to>   | 连接KafkaBroker主机名和端口号 |
| --topic <String : topic>                           | 操作的topic名称               |
| --create                                           | 创建主题                      |
| --delete                                           | 删除主题                      |
| --alter                                            | 修改主题                      |
| --list                                             | 查看所有主题                  |
| --describe                                         | 查看主题的详细信息            |
| --partitions <Integer:#of partitions>              | 设置分区数                    |
| --replication-factor<Integer : replication factor> | 设置分区副本                  |
| --config<String : name=value>                      | 更新系统默认的配置            |

发送流程 => 生产者产生数据 => 分区器发送到队列 => 达到设定值 => 发送给sender线程 => 发送到Kafka集群 => 发送成功消息到Sender => Sender删除缓存请求

### 6.1连接

--bootstrap-server IP:9092

### 6.2创建主题

1. bash 使用bashshell运行
2. kafka-topics.sh 运行指定脚本
3. --bootstrap-server 连接指定服务器
4. --topic name 要操作哪个主题
5. --create 创建主题
6. --partitions 1 指定分区
7. --replication-factor 3 指定备份数量

```shell
bash kafka-topics.sh --bootstrap-server master:9092 --topic Dome --create --partitions 1 --replication-factor 3
```

### 6.4Kafka点对点模型和发布订阅模型

- 消息生成者产生消息发送到queue中，然后消息消费者从queue中取出并消费
- 消息被消费后，queue中不再存储，所以消息消费者不可能消费到已经被消费的消息。
  - Queue支持存在多个消费者，对于一个消息而言，只会有一个消费者可以消费。
- 发布订阅
  - 消息生产者(发布)将消息发布到topic中，同时有多个消息消费者(订阅)消费该消息。
  - 和点对点方式不同，发布到topic的消息会被所有订阅者消费

- 生产者 -> MQ

- MQ -> 消费者A

- MQ -> 消费者B

- MQ -> 消费者C



- 异步

- 生产者线程只负责生产数据到缓冲区 
- Send线程复制发送数据到kafka

# 7.异步发送代码实现

> - ### 普通异步发送 Send()

```java
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Conset
{
    public static void main(String[] args)
    {
        Properties props = new Properties();
        //连接集群
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.45.10:9092");
        //指定序列化类型
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //创建Kafka生产者对象
        KafkaProducer<String, String> Kafkapri = new KafkaProducer<>(props);

        //发送数据
        for (int i = 0; i < 10; i++){
            System.out.println("第" + i + "次");
            Kafkapri.send(new ProducerRecord<>("dome","eeee"));
        }
        Kafkapri.close();
    }
}
```

> - ### 带回调异步发送

```java
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Test1
{
    public static void main(String[] args)
    {
        //创建Kafka的配置文件
        Properties prodCofig = new Properties();
        prodCofig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.45.10:9092");
        //键值序列化
        prodCofig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prodCofig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //创建通信
        KafkaProducer<String,String> Proud =  new KafkaProducer<>(prodCofig);
        for(int i = 0 ;i < 10 ;i++)
        {
            Proud.send(new ProducerRecord<>("dome",Integer.toString(i)), new Callback()
            {
                @Override
                //回调函数
                public void onCompletion(RecordMetadata recordMetadata, Exception e)
                {
                    //e返回的错误信息 没有则空
                    if(e == null){
                        //recordMetadata 返回的分区信息
                        System.out.println(recordMetadata.topic());
                    }
                }
            });
        }
        Proud.close();
    }
}
```

> ### 8.同步代码发送
>
> CTRL+P

在发送的最后面调用get方法



# 7.1Topics代码创建实现

```java
public class KafkaTopic
{
    public static void main(String[] args) throws Exception
    {
        //创建配置文件 键值对
        Map<String, Object> config = new HashMap<>();
        //添加链接配置
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.45.13:9092");
        //创建管理员用户
        Admin admin = Admin.create(config);
        //创建新的主题
        String TopicName = "Test2";
        int Partitions = 1;
        short Replicas = 1;
        NewTopic newTopic = new NewTopic(TopicName, Partitions, Replicas);
        //创建主题 topics
        CreateTopicsResult topics = admin.createTopics(Arrays.asList(newTopic));
        //关闭链接
        admin.close();
    }
}
```



# 7.2拦截器代码创建实现

生产者数据 => 拦截器... => 生产者数据

```java
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

public class Interceptor_
{
    public static void main(String[] args)
    {
        Map<String,Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.45.13:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //指定拦截器
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,Value_Interceptor.class.getName());

        //创建生产者
        KafkaProducer<String,String> kafka = new KafkaProducer<>(config);
        
        //指定topic和要发送的消息
        ProducerRecord<String,String> prss = new ProducerRecord<String,String>("Demo","verl");

        //发送
        kafka.send(prss, new Callback()
        {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e)
            {
                System.out.println(recordMetadata.topic());
            }
        });

        kafka.close();
    }
}
```

### 拦截器实现

```java
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class Value_Interceptor implements ProducerInterceptor<String, String>
{

    @Override
    //拦截
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> pr)
    {
        //在值的后面加个awf
        return new ProducerRecord<>(pr.topic(), pr.key(), pr.value() + "awf");
    }

    @Override
    //消息发送完后服务器响应 调用
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e)
    {

    }

    @Override
    //关闭后
    public void close()
    {

    }

    @Override
    //配置文件处理
    public void configure(Map<String, ?> map)
    {

    }
}
```



# 8.指定分区

```java
//目标topic名称,目标分区,key给空"",生产的值
public ProducerRecord(String topic, Integer partition, K key, V value) {
    this(topic, partition, (Long)null, key, value, (Iterable)null);
}
```

## 8.1自定义分区

- ### 步骤

  1. 创建一个类 实现 Partitioner 接口
  2. 重写partition方法 => 返回值就是指定的分区
  3. 将这个类put到配置

Proeter这个就是实现的类，需要全类名

```java
config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "Proeter");
```

```java
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class Proeter implements Partitioner
{
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster)
    {
        //第四个行参是指定值
        int num = 0;
        if(o1.toString().equals("2")){
            num = 1;
        }else{
            num = 0;
        }
        return num;
    }
}
```

- #### 我这边没有指定多个分区，如果程序卡住无法退出 那么就说明正确； 

## 8.2文件含义

- ##### log 数据文件 20位长度的文件夹 文件名就是偏移量

- ##### index 偏移量的索引文件

- ##### timeindex 时间索引量

##### 

# 9.生产效率 自定义发送

1. 缓冲区大小
   - ProducerConfig.BUFFER_MEMORY_CONFIG,单位字节
2. 批次大小
   - ProducerConfig.BATCH_SIZE_CONFIG,单位字节
3. 发送延迟 linger.ms
   - ProducerConfig.LINGER_MS_CONFIG,单位毫秒
4. 压缩方式
   - ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy就行了"

# 10.数据可靠性 acke => 1

- ### ACK级别 = -1

- ### 分区副本大于等于2

- ### ISR里应答的最小副本大于等于2

> ### 设置acks值 

```java
put(ProducerConfig.ACKS_CONFIG,"1");
```

> ### 重试次数

`put(Config.RETRIES_CONFIG,"3");`

- ### 至多一次 => 只发送一次 数据不重复

- ### 至少一次 => 发送多次 数据可能重复

  - #### 为什么重复

  - ##### kafka的应答数据在发送给生产者的时候，应答数据丢失，生产者长时间没收到，于是又发了一条

- ### 消息重试产生消息乱序

  - ##### 本来3条数据，123，发送的时候23成功了，1没成功，1就会重发，这时候的顺序就变成了 231




# 11.幂等性和事务 精确一次

- #### 幂等性 => Producer(生产者)不论向Broker发送多少次重复的数据,Broker端都只会持久化一条。

- #### 判断标准 => 具有 <PID,Partition,SeqNumber> 相同主键的消息提交时,Broker只会持久化一条。

  - #### PID每次重启一次都会变 只能保证在单分区单会话内不重复

- #### 开启 => enable.idempotence = true; 默认开启

  - ##### ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG

- ##### 操作要求：

  - ##### ACKS = -1

  - ##### 开启重试机制(默认就行)

  - ##### 在途请求小于等于5






### 11.1事务

> ### 开启事务 必须开启幂等性
>
> ##### 生产者在使用事务功能之前，必须先自定义一个唯一的transactional.id。
>
> 生产者ID不改变 幂等性就不会失效 => 原理
>
> 只能跨对话，不能跨分区

### 11.2使用:

- #### 指定事务ID

`put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"id");`

```java
生产者.initTransactions(); //初始化

try{
    生产者.beginTransactions();//启动
    
    数据发送内容
    
	生产者.commitTransactions();//提交
}
catch(Ex e){
	生产者.abortTransactions();//终止事务
}finall{
    生产者.close(); //无论如何都关闭
}
```

### 11.3流程

1. ##### 创建生产者对象

2. ##### 发送事务请求 查找事务所在节点(->Broker)

3. ##### 初始化事务ID(事务管理者 管理器)(Broker)

4. ##### 返回事务ID(ID % 50)(Broker ->)

5. ##### 初始化事务

6. ##### 开启事务

7. ##### 将事务分区信息发送给事务管理器(->TC ADD_PARTITIONS_TO_TXN)

8. ##### 生产数据(->Broker)

9. ##### 提交保存成功数据分区信息(Broker -> TC)

10. ##### 返回保存成功数据(Broker -> 生产者)

11. ##### 结束事务(生产者 -> TC _ END_TXN)

12. ##### 修改事务状态为PrepareCommit(预先准备提交)(TC-> _transaction_state(事务状态))

13. ##### 写入事务标记(TC -> Broker _ WRITE_TXN_MARKERS)

14. ##### 修改事务状态为CompleteCommit

15. ##### Send只是将数据放入缓冲区



# <u>~~Zookeeper中存储的Kafka信息~~</u>

1. ##### <u>~~/kafka/broker/ids [0,1,2] 记录有哪些服务器~~</u>

2. ##### <u>~~/kafka/brokers/topic/first/partitions/0/state 记录谁是Leader 有哪些服务器可用~~</u>



这些信息是有的,在新版本中看法 =>

- 打开kafka的bin目录 有一个zoop-shell 的.sh文件
- 运行 zoop-shell master:2181
- ls /查看目录
- 查看broker目录
  - ls /broker
- 查看broker目录下的ids
  - ls /broker/ids

# 12.负载均衡 (服役)

- ## 创建一个文件

  - ##### touch 自定义名称.json

  - ##### 编辑内容

  ```json
  {
  	"topic":[
  		{"topic" : "topicName"}
  	],
  	"version" : 1
  }
  ```

  - 执行命令

  ```shell
  bin/kafka-reassign-partitions.sh --bootstrap-server master:9092 --topics-to-move-json-file 脚本名称.json --broker-list "0,1,2,3(要均衡的机器)" --generate
  ```

  - 创建文件 存放具体计划 内容为上面的命令输出的内容
  - 执行 / 验证

  ```shell
  bin/kafka-reassign-partitions.sh --bootstrap-server master:9092 --reassignment-json-file 脚本名称.json --execute
  --verify =>验证
  ```

# 2.0

- ### 两个线程

  - ##### main线程

    - ##### KafkaProducer

    - ##### send(,)

    - ##### 拦截器

    - ##### 序列化器(java序列化太重)

    - ##### 分区器

  - ##### sender 发送数据到kafka

    - ##### 存储5个请求

    - ##### selector 打通链路

    - ##### 收到回复 删除对应请求

- ### 异步发送API

  - ##### 配置

    - ##### bootstrap-server

    - ##### key vue 序列号

  - ##### 创建生产者

    - ##### KafkaProducer

  - ##### 发送数据

    - ##### send(,new Callback)

  - ##### 关闭资源

- ### 分区

  - ##### 好处

    - ##### 存储

    - ##### 计算

  - ##### 默认分区规则

    - ##### 指定分区

    - ##### 按照key的hashcode值取模分区数

    - ##### 无指定 无key 粘性分区

  - ##### 自定义分区

    - ##### 定义类 实现partitioner接口

- ### 吞吐量

  - ##### 批次大小 默认16k

  - ##### linger.ms 传输延迟 默认0ms

  - ##### 压缩

  - ##### 缓冲区大小 默认32m

- ### 传输可靠性

  - ##### acks => 0,1,-1

    - ##### 0 生产者发生 kafka不回应

    - ##### 1 kafka回应

    - ##### -1 完全可靠

      - ##### 副本大于等于2

      - ##### isr 大于等于2

- ### 数据重复

  - ##### 幂等性

    - ##### <pid,分区号,序列号>

      - ##### pid => 每次重启分配一个

  - ##### 事务

  - ##### 事务API

    - ##### 初始化

    - ##### 启动

    - ##### 提交

    - ##### 终止
  
- ### 数据乱序

  - ##### 无幂等性 => inflight = 1

  - ##### 有幂等性 => inflight <= 5

  - ##### 排序

    - ##### 单分区有序

# 3.0Kafka副本

## 基本信息

- #### 作用 : 提高数据可靠性

- #### 副本数量默认一个 太多会增加磁盘存储空间，增加网络上数据传输，降低效率

- #### 副本 : Leader 和Follower 生产者数据只会发给Leader,Follower找Leader进行同步

- #### 分区部分统称AR(Assigned Repllicas)

  - ##### 按照Replicas上位

  - ##### AR = ISR + OSR

    - ##### ISR => 保持同步的Follower集合 包含Leader

      - ##### 超时会被踢出(30s) => replica.lag.time.max.ms 并重新选举Leader

    - ##### OSR 表示Follower与Leader副本同步时，延迟过多的副本。

# 3.1Follower故障处理

- ##### LEO(Log End Offset) => 每个副本的最后一个offset LEO其实就是最新的offset + 1

- ##### HW(High Watermark)  => 所有副本中最小的LEO。

> ISR [0, 1, 2]
>
> ##### Leader
>
> ​			  	 			      HW			  LE0

broker0 => 0	1	2	3	4	5	6	7

> ##### Follower
>
> ​								HW	LEO

broker1 => 0	1	2	3	4	5

> ##### Follower
>
> ​								LEO（HW）

broker2 => 0	1	2	3	4



### Follower故障

- ##### Follower发送故障后会被临时踢出ISR

- ##### 这个期间Leader和Follower继续接收数据

- ##### 该Follower恢复后，Follower会读取本地记录的HW，并将 高于LEO的数据截掉，从HW开始同步

- ##### 该Follower追上Leader后 重新加入ISR

# 3.2分区副本分配

- ### (自动)尽量错开 不在乎服务器配置差异

| 1    | 2    | 3    |
| ---- | ---- | ---- |
| 2    | 3    | 1    |
| 3    | 1    | 2    |

## 3.2.1手动分配

- ##### 按照服役给出的脚本进行修改



## 3.2.2Leader Partition自动平衡

- ##### auto.leader.rebalance.enable => true

  - ##### 自动平衡Leader Partition

- ##### leader.imbalance.per.broker.percentage

  - ##### 不平衡Leader比例，超过指定值就会触发自动再平衡

- ##### leader.imbalance.check.interval.seconds

  - ##### 间隔时间 默认300秒