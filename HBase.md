# HBase

# 1.0 定义

> ##### HBase是一种分布式、可扩展、支持海量数据存储的NoSQL(非关系型)数据库
>
> 能实现HDFS上的增删改查
>
> 一个行键、列族、列修饰符、数据和时间戳组合起来叫做一个单元格（ **Cell**）。这里的行键、列族、列修饰符和时间戳其实可以看作是定位属性（类似坐标），最终确定了一个数据。

- ### 数据模型

  - ##### 逻辑上HBase的数据模型同关系型数据库很类似，数据存储在一张表中，有行有列。

  - ##### 但HBase的底层存储结构是K-V，HBase更像是一个multi-dimensional map 不支持随机读写



- ### HBase 逻辑结构

  - ##### 你创建的任何一个表都必须指定一个Row Key 必须是唯一的 (行键)

    - ##### 如果再插入这个Row Key的值 就是覆盖

    - ##### Row Key按照字典序排序 按位比较

    - ##### 一个Row Key对应多个列族

    

  - ##### 一个列族就是一个文件夹

    - ##### 对列进行分组 就是列族

      

  - ##### 数据过大时会进行分组 Region

    - ##### 横向切分和纵向切分

      - ##### 纵向切分分出来就是列族

      - ##### 横向切分出来就是Region(表的切片内容)

      

  - ##### store不存放元数据信息

    - ##### 只存储Row Key和数据内容



- ### HBase物理存储结构

  - ##### 在实际存储是不存personal_info行跟name_city_phone行的(元数据)

  |           | personal_info |      |        |
  | --------- | ------------- | ---- | ------ |
  | RowKey    | name          | city | phone  |
  | row_key1  | 张三          | 上海 | 131*** |
  | row_key11 | 李四          | 北京 | 132*** |

- ### 实际存储

  - ##### Type = 用什么方法

  - ##### TimeStamp = 操作时间 时间戳(重要) 

    - ##### 时间不同步 删个东西没删掉 改个东西没改成功

    - ##### 做了修改不是当时就修改了 查询的时候返回最大时间戳

    - ##### 不同版本(version)的数据根据timestamp进行分区
    
    - **Column Family**（常译为 列族/列簇）
  
  | Row Key  | Column Family | Column Qualifier | TimeStamp | Type | Value  |
  | -------- | ------------- | ---------------- | --------- | ---- | ------ |
  | row_key1 | personal_info | name             | t1        | Put  | 张三   |
  | row_key1 | personal_info | city             | t2        | Put  | 上海   |
  | row_key1 | personal_info | phone            | t3        | Put  | 131*** |
  | row_key1 | personal_info | phone            | t4        | Put  | 139*** |



# 1.1 数据模型

- ###### Name Space 命名空间

  - ###### 命名空间，类似于关系型数据库的database概念，每个命名空间下有多个表。Habse有两个自带的命名空间，分别是hbase和default,hbase中存放的是HBase内置的表，default表是用户默认使用的命名空间。

- ###### Region 表切片

  - ###### 类似于关系型数据库的表的概念。HBase定义表时只需要声明列族就可以。不需要声明具体的列。

  - ###### 往HBase写入数据时，字段可以动态 按需指定。

  - ###### HBase能轻松应对字段变更的场景

  - ###### 可以手动切分，新表只有一个Region

  - ###### 一张表可以有多个Region



- ###### Row

  - ###### HBase表中的每行数据都由一个RowKey和多个Column(列)组成，数据是按照RowKey的字典顺序存储的

  - ###### 查询数据时只能根据RowKey进行检索 所有RowKey十分重要



- ###### Column

  - ###### HBase 中的每个列都由Column Family(列族) 和 Column Qualifier(列限定符) 进行限定，例如 info : name, info : age



- ###### Time Stamp

  - ###### 用户标识数据的不同版本(version),每条数据写入时，如果不指定时间戳，系统会为其加上 其值为写入HBase的时间



- ###### Cell

  - ###### 由{rowkey,column Family:column Qualifier,time Stamp} 唯一确定的单元。

  - ###### cell中的数据没有类型，全部都是字节码形式存贮。

  - ###### row_key + 列族 + 列 + 时间戳 => 唯一确定数据 => 一个单元格 



# 1.3 HBase基本架构

- ###### Region Server

  - ###### Region

    - ###### store

      - ###### StoreFile

- ###### Region Server的作用 管理Region的增删改查 DML

  - ###### Data 数据

    - ###### put => 又是增又是改

    - ###### get => 取

    - ###### delete => 删

  - ###### Region

    - ###### splitRegion 切分

    - ###### CompactRegin 合并

- ###### Master的作用 监控集群

- ##### 管理表的 DDL 增删改查

  - ###### Table : create ,delete alter

  - ###### RegionServer : 分配regions 到每个RegionServer 监控每个RegionServer的状态



# 1.4 HBase安装部署

- #### 启动HDFS

- #### 启动Zk

- #### 上传HBase.tar 并解压 更名为更短的名称

- #### cd /conf

  - ##### env => 环境

    ```sh
    export JAVA_HOME=/opt/module/jdk
    
    #注释两行
    export HBASE_MASTER_OPTS=
    export HBASE_REGIONSERVER_OPTS=
    
    #放开
    #告诉HBAS是否管理自己的Zookeeper实例
    #HBAS自带zk 如果不改，在site配置了Zk, 他会更改id
    export HBAS_MANAGES_ZK=false
    ```

    

  - ##### site => 配置

    ```xml
    <!-- 文件存储位置 -->
    <property>
        <name>hbase.rootdir</name>
        <value>hdfs://hadoop1:9000/HBase</value>
    </property>
    
    <!-- 部署模式 默认false 单机模式 true 集群模式 -->
    <property>
        <name>hbase.cluster.distributed</name>
        <value>true</value>
    </property>
    
    <!-- 服务端口 -->
    <property>
        <name>hbase.master.port</name>
        <value>16000</value>
    </property>
    
    <!-- zookeeper集群 -->
    <property>
        <name>hbase.zookeeper.quorm</name>
        <value>hadoop1,hadoop2,hadoop3</value>
    </property>
    
    <!-- 工作目录 -->
    <property>
        <name>hbase.zookeeper.property.dataDir</name>
        <value>/opt/module/xxxx</value>
    </property>
    ```

    

  - ##### regionservers => 集群脚本

    ```json
    hadoop1
    hadoop2
    hadoop3
    ```

- #### 分发



# 1.5 启动

- /bin
- hbase-daemon.sh => 单独启动
- hbase-daemons.sh => 群启

```sh
hbase-daemon.sh start master #启动master
hbase-daemon.sh start regionserver #启动regionserver

stop-hbase.sh #单关
stop-hbases.sh #群关
```



# 1.6 HBaseCLI操作

```sh
bin/hbase shell
```

- ##### 按住CTRL按backspace才是往前面删 不然delete才是 

- > 变为*后 ‘’分开敲两次

- ##### 常用DDL (表 命令后面加个_namespace 就是对命名空间操作)

  - ##### create 增  创建表,必须指定一个列族

    ```hive
    create '表名','列族','列族2'...
    ```

  - ##### 在命名空间内创建表

    ```hive
    create '命名空间:表名','列族'...
    ```

  - ##### 创建命名空间

    ```hive
    create_namespace '名称'
    ```

    

  - ##### describe 查单表 

    ```hive
    describe '表名' --可以用TAB提示
    ```

    

  - ##### drop 删 表必须下线(disable '表名')

    ```hive
    drop '表名'
    ```

  - ##### 删除命名空间

    ```hive
    drop_namespace '名称'
    ```

  

  - ##### list 查

  - ##### alter 改

    - 更改指定表的 列族的版本保留数

    ```hive
    alter '表名',{NAME=>'info',VERSIONS=>3}
    ```

    

- 常用DML

  - ##### delete 删

    ```hive
    delete '表名','RowKey','列族名:列名'
    delete '表名','RowKey','列族名:列名',时间戳
    ```

    

  - ##### deleteall 多删  truncate '表名' => 删表

    ```hive
    deleteall '表名','RowKey'
    ```

    

  - ##### get 查

    ```hive
    get '表名','RowKey'
    get '表名','RowKey','列族名'
    get '表名','RowKey','列族名:列名'
    ```

    

  - ##### put 改 增

    ```HIVE
    put '名称空间:表名','RowKey','列族名:列名','值'
    put '名称空间:表名','RowKey','列族名:列名','值',时间戳
    ```

    

  - ##### scan 查 默认的def名称空间

    ```hive
    scan '名称空间:表名'
    scan '名称空间:表名',{STOPRIW=>'RowKey'} --以RowKey结尾 开区间
    scan '名称空间:表名',{STARTROW=>'RowKey'} --以RowKey开头 RowKey是闭区间
    ```



- 其他

  - ##### compact 合并

  - ##### flush 刷写

  - ##### major_compact 合并

  - ##### split 切分



# 1.7 HBase详细架构

- ##### 底层依赖于HDFS

- ##### 上层依赖Zookerp HMaster对zk有交互

- HRegionServer 维护Region

  - 内有HLog 预写入日志 -> 与HDFSClient 实时交互

- HRegion

  - Store(列族).....
    - MemStore
    - StoreFile
      - HFile -> 与HDFSClient条件交互

# 1.8 写数据流程

- put : stu/RowKey/CF/Column : V
- client -> zookeeper 获取mata表机器位置 设返回hadoop1
- client -> hadoop1 请求meta 返回meta,获取RS
- client -> 缓存元数据 Matacache
- client -> hadoop2 发送put请求
- Rs -> 预写入日志 -> MemStore
- Rs -> client ack

1. Client先访问zookeeper,获取hbase:mata表位于哪个RegionServer
2. 访问对应的RegionServer，获取hbase:meta表，根据请求的namespace:table/rowkey,查询出目标数据位于哪个RegionServer中的哪个Region中。并将该table的region信息以及meta表的位置信息缓存在客户端的metacache方便下次访问。
3. 与目标RegionServer进行通讯
4. 将数据顺序写入(追加)到WAL
5. 将数据写入对应的MemStore，数据会在MemStore进行排序
6. 向客户端发送ack
7. 等达到MemStore的刷写时机后，将数据刷写到HFile



# 1.9 合并

- Minor compaction
  - 只选取小的、相邻的HFile将他们合并成一个更大的Hfile。
  - 不会删除旧的数据，以及被deletes的数据
- Major compaction
  - 将一个Store下的所有Hfile合并成一个大文件，并执行物理删除操作。
  - 物理删除旧的数据
