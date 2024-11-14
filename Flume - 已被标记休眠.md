# Flume - 已被标记休眠

Flume是Cloudera提供的一个高可用的 高可靠 分布式的**海量日志采集、聚合和传输的系统**



1. Source -> 数据源
2. Sink -> 写入模块
3. Shannel -> 数据管
4. Event -> 一条数据就是一个Event对象
   1. 内容放在Body



- 使用Flume监听一个端口 收集该端口数据打印到控制台

```shell
# 各个过程命名
#a1 -n 后面的那个名称
a1.sources = r1
a1.sinks = k1
a1.channels = c1

#source的配置
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

#sink的配置
a1.sink.k1.type = logger

#channel的配置
a1.channels.c1.type=momory
a1.channels.c1.capacity = 1000 //管道容量 Event对象数量
a1.channels.c1.transactionCapacity = 100 //单个Event大小

#关联
#一个sink只能有一个管道
a1.sources.r1channels = c1
a1.sinks.k1.channel = c1
```

```sh
bin/flume-ng agent -c 配置文件目录 -f 配置文件 -n 名称
```



# Flume 和 Kafka的对接

- ##### Flume -> KafkaSource => 把Kafka当作数据源

  - ##### KafkaSource对于flume来讲是一个source的角色，对于kafka来讲，是一个消费者的角色

    - ##### kafka是消费者

    

- ##### Flume -> KafkaSink => 把数据写到Kafka

  - ##### kafkaSink对于flume来讲是一个sink的角色，对于kafka来讲，是一个生产者的角色

    - ##### kafka是生产者



- ##### Flume KafkaChannel

  - ##### 拿Kafka做缓冲

  - ##### 到这一步数据直接进入Kafka了

  

  - ##### 作为一个基本的channel来使用

    - ##### xxxSource -> kafkachannel -> xxxsink

    

  - ##### 支持往kafka中写入数据

    - ##### xxxSource - > kafkachannel

    

  - ##### 支持从kafka中读取数据

    - ##### kafkachannel -> xxxsink



# Flume -> Kafka

> ### KafkaSink

```sh
#名称配置
a1.sources = r1
a1.channels = c1
a1.sinks = k1

#Source配置
a1.source.r1.type = netcat
a1.source.r1.bind = 0.0.0.0
#端口
a1.source.r1.port = 6666

#Channel 配置
a1.channels.c1.type = memory
a1.channels.c1.capacity = 10000
a1.channels.c1.transactionCapacity = 100

#如果需要拦截器那么这里要加这些
a1.sources.r1.interceptors = i1;
a1.sources.r1.interceptors.i1.type = 全类名$类名


#Sink Kafka作为消费者(数据写入Kafka)
a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
#Kafka服务器
a1.sinks.k1.kafka.bootstrap.server = hadoopxx:9092,...
#目标topic
a1.sinks.k1.kafka.topic = TopicName
#批次大小
a1.sinks.k1.flumeBatchSize = 100;
#默认false 是否使用flume的格式 含有头
a1.sinks.k1.useFlumeEventFormat = false
#安全级别
a1.sinks.k1.kafka.producer.acks = -1;

#绑定
a1.soucers.r1.channels = c1
a1.sinks.k1.channel = c1
```

> ### 运行

```sh
flume-ng agent -c /opt/moudle/conf -f kafka.conf -n a1
```

> ### 启动kafka消费者

```sh
kafka-console-consumer.sh --topic topicName --bootstrap-server hostName:9092
```





# Kafka -> Flume

> #### Flume去Kafka里面读数据

- ##### KafkaSource => Kafka数据源

```sh
#名称配置
a1.sources = r1
a1.sinks = k1
a1.channels = c1

#配置source
a1.sources.r1.type = org.apache.flume.kafka.KafkaSource
a1.sources.r1.kafka.topic = t1,t2
a1.sources.r1.kafka.bootstrap.sercer = hostName:9092,...
a1.sources.r1.kafka.consumer.group.id = flume # 默认flume
a1.sources.r1.batchSize = 100; #默认1000
a1.sources.r1.useFlumeEventFormat = fasle #默认fasle

#配置Channels
a1.channels.c1.type = memory #内存模式
a1.channels.c1.capacity = 1000 #默认100
a1.channels.c1.transactionCapacity = 100 #默认100

#Sink
a1.sinks.k1.type = logger

#配置联合 使用哪个管道
a1.sources.r1.channels = c1
a1.sinks.k1.channels = c1

```



> ### Kafka数据源

```sh
#名称配置
a1.sinks = k1
a1.channels = c1

#channels
a1.channels.c1.type = org.apache.flume.kafka.KafkaChannel
a1.channels.c1.kafka.topic = topicName,...
a1.channels.c1.kafka.bootstrap.server = hostName:9092,...
a1.channels.c1.kafka.consumer.group.id = flume #默认flume
a1.channels.c1.kafka.consumer.auto.offset.reset = latest #默认latest
a1.channels.c1.parseAsFlumeEvent = false; #默认true 是否以Event读取

#sink
a1.sinks.k1.type = logger

#管道
a1.sinks.ks.channels = c1
```



> ### Kafka管道

```sh
a1.sources = r1
a1.channels = c1

a1.sources.r1.type = netcat
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 6666

a1.channels.c1.type = org.apache.flume.channel.kafka.KafkaChannel
a1.channels.c1.kafka.bootstrap.server = hostName:9092,...
a1.channels.c1.kafka.topic = topicName
a1.channels.c1.parseAsFlumeEvent = false

a1.sources.r1.channels = c1
```



```SH
a1.sources = s1
a1.sinks = k1
a1.channels = c1

#采集数据生成器 25001端口的 socket数据
a1.sources.s1.type = netcat
a1.sources.s1.bind = 0.0.0.0
a1.sources.s1.port = 25001

#sink到Kafka
a1.sinks.k1.typr = org.apche.flume.kafka.KafkaSink
a1.sinks.k1.kafka.topic = ods_mall_log
a1.sinks.k1.kafka.bootstrap.server = master:9092

#a1.sinks.k1.batchSize = 2
a1.sources.channels = c1
a1.sinks.channels = c1
```

