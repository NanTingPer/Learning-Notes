# Kafka => 消息队列

# 提前准备

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
   1. kafka-topic.sh --zookeeper master:2021 --create --topic dome --xx 1 --xx 1
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


<!--stackedit_data:
eyJoaXN0b3J5IjpbLTE0OTE2NzExNDJdfQ==
-->