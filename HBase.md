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



# 2.0 数据的删除时机

- flush 的时候不会删除删除标记
- 在compact的时候才会删除 删除标记
- 涉及版本保留会保留版本



# 2.1 数据切分

> 会产生数据倾斜
>
> - 官方不建议使用多个列族
>   - 因为数据流量不同，会产生很多小文件
> - 如果使用多个列族 分配好数据即可



# 2.2 API的使用

- #### 添加依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-client</artifactId>
        <version>2.4.11</version>
    </dependency>
</dependencies>
```

- 创建连接
  - HBase的客户端连接由ConnectionFactory类来创建，用户使用完成之后需要手动关闭连接。
  - 连接是重量级的，推荐一个进程使用一个连接，对HBase的命令通过连接中的两个属性Admin和Table来实现
- 单线程创建连接

```JAVA
//创建连接配置文件 该配置字段可以在 hbase的site配置下找到
Configuration config = new Configuration();
config.set("hbase.zookeeper.quorum","192.168.45.13");
//创建hbase操作对象
Connection cf = ConnectionFactory.createConnection(config);
//如果打印不为空则创建成功
System.out.println(cf);
```



## 2.2.1 多线程连接 单例模式

```java
//创建静态字段
public static Connection connection = null;
//使用静态代码快创建单例对象
static {
    try
    {
        connection = ConnectionFactory.createConnection();
    }catch (Exception e){};
}
//创建公共方法用来关闭单例对象
public static void closeConnection() throws IOException
{
    if(connection!=null)
    {
        connection.close();
    }
}
public static void main(String[] args) throws IOException
{
    Connection Myconnection = connection;
    System.out.println(Myconnection);
    //由于是引用变量，所有hash值是相等的 是同一个对象
	System.out.println(connection.hashCode()==Myconnection.hashCode());
}
```



## 2.2.2 创建命名空间

```java
/***
 * 创建命名空间
 * @param NameSoace 命名空间的名字
 */
//构建一个方法来创建命名空间 可以增加代码复用
public static void creaceNameSpace(String NameSoace) throws IOException
{
    //得到一个轻量的Admin连接
    Admin admin = connection.getAdmin();
    //得到一个Builder => 用于取得NamepaceDescriptor实例
    NamespaceDescriptor.Builder newNameSpace = NamespaceDescriptor.create(NameSoace);
    //添加说明 
    newNameSpace.addConfiguration("说明", "这是新表");
    //创建
    admin.createNamespace(newNameSpace.build());
    //admin.createNamespace(NamespaceDescriptor.create("NewNameSpace").addConfiguration("说明","这是新表").build());
    //记得必须关闭
    admin.close();
}
public static Connection connection = null;
static {
    try
    {
        connection = ConnectionFactory.createConnection();
    }catch (Exception e){};
}
public static void closeConnection() throws IOException
{
    if(connection!=null)
    {
        connection.close();
    }
}
public static void main(String[] args) throws IOException
{
    Connection Myconnection = connection;
    //System.out.println(Myconnection);
    //System.out.println(connection.hashCode() == Myconnection.hashCode());
    //创建名称空间
    creaceNameSpace("NewNameSpace");
    System.out.println("执行完毕");
    Myconnection.close();
}
```



## 2.2.3 异常处理

> 创建命名空间出现的问题 都属于本方法自身的问题 不应该抛出

```java
Admin admin = connection.getAdmin();
NamespaceDescriptor.Builder newNameSpace = NamespaceDescriptor.create(NameSoace);
newNameSpace.addConfiguration("说明", "这是新表");
//在这里加上 try catch
try
{
    admin.createNamespace(newNameSpace.build());
}catch (IOException e){
    System.out.println("命名空间已经存在");
    e.printStackTrace();
}
```



## 2.2.4 判断表格是否存在

```java
/**
 * 判断表格是否存在
 * @param NameSpace 命名空间的名称
 * @param tableName 表的名称
 * @return
 */
public static boolean isTableExists(String NameSpace,String tableName) throws IOException{
    //1,获取admin
    Admin admin = connection.getAdmin();
    boolean run = false;
    //2,判断
    try
    {
        run = admin.tableExists(TableName.valueOf(NameSpace, tableName));
    }catch (IOException e){e.printStackTrace();}
    finally
    {
        //3,关闭流
        admin.close();
    }
    //返回
    return run;
}
//查看表是否存在
System.out.println(isTableExists("123", "123"));
```



## 2.2.5 创建表格

- 需要 命名空间的名称,表格的名称,列族的名称
  - 列族可以多个 String...



- 获取Admin

- 调用 createTable创建表

  - 创建表格描述 建造者
  - TableDescriptorBuilder.newBuilder(TableName.valueOf(名称空间,表名));
  - 
  - 创建列族描述 建造者 传入列族名称
  - ColumFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(字符串))

  - 

  - 添加版本参数(可空)
  - ColumFamilyDescriptorBuilder.setMaxVersions(版本条数)

  - 

  - 添加列族描述实例

  - 表格建造者.setColumnFamily(列族建造者.build());

  - 表格建造者.setColumnFamily() -> 一条一条加 多条可以循环添加

  - admin.createTable(表格建造者.build());

```java
public static void creaceTable(String nameSpace,String tableName,String... column) throws IOException
{
    if(column.length ==0)
    {
        System.out.println("必须有一个列族");
        return;
    }
    
    //1,得到admin
    Admin admin = connection.getAdmin();
    //2,创建表格建造者
    TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(nameSpace,tableName));
    //3,往表格建造者添加列族信息
    for(String columnstr : column)
    {
        //3.1 创建列族建造者
        ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = 	ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnstr));
        columnFamilyDescriptorBuilder.setMaxVersions(5);//设置保留5个版本
        //3.2 添加表格的列族信息
        tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());
    }
    //创建表格
    try{
    admin.createTable(tableDescriptorBuilder.build());
    }catch (IOException e)
    {
        System.out.println("表格已经存在");
        e.printStackTrace();
    }finally {
        admin.close();
    }
}
//创建表格
creaceTable("NewNameSpace","NewTable","info");
```



# 2.2.6 修改表格信息

- 获取表格描述
  - admin.getDescriptor()
    - TableName.valueOf(namespace,tableName)
- 获取表格描述生成器
  - TableDescriptorBuilder.newBuilder(admin.getDescriptor)
- 获取 列族描述 信息
  - admin.getDescriptor().getColumnFamily(Bytes.toBytes(列族名))
- 创建 列族描述建造者
  - ColumnFamilyDescriptorBuilder.newBuilder(上面获取到的列族描述)
- 对上面的列族描述建造者进行 setmaxver(xx);
- 使用 上面的表格描述生成器对新的列族描述建造者进行修改生效
  - modifyColumnFamily(列族描述建造者.build());

```java
/**
 * 修改表格的版本信息
 * @param nameSpace 名称空间
 * @param tableName 表的名称
 * @param column 列族名称
 * @param ver 要保留的版本数量
 * @throws IOException 正常抛出
 */
public static void RevTableDescr(String nameSpace,String tableName,String column,int ver) throws IOException
{ 
            //判断表格是否存在
        if(!isTableExists(nameSpace,tableName)){
            System.out.println("表格不存在");
            return;
        }
    //1，获取Admin
     Admin admin = connection.getAdmin();
     //2，获取表格的描述
         //获取原表的 描述信息
         TableDescriptor descriptor = admin.getDescriptor(TableName.valueOf(nameSpace,tableName));
         //3,创建表格建造者 使用原表的信息
         TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(descriptor);
         //获取旧的表格列族描述
         ColumnFamilyDescriptor columnFamily = descriptor.getColumnFamily(Bytes.toBytes(column));
         //创建列族描述建造者 传入原表的列族描述信息
         ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(columnFamily);
         //修改版本信息
         columnFamilyDescriptorBuilder.setMaxVersions(ver);
         //将修改后的数据载入表格建造者
         tableDescriptorBuilder.modifyColumnFamily(columnFamilyDescriptorBuilder.build());
         //修改
         admin.modifyTable(tableDescriptorBuilder.build());
     admin.close();
}

//修改表格的版本信息
RevTableDescr("NewNameSpace","NewTable","info",7);
```



# 2.2.7 删除表格

```java
/**
 * 删除指定表格
 * @param nameSpace 命名空间
 * @param tableName 表名
 * @return True表示删除成功
 * @throws IOException 正常抛出
 */
public static boolean DeleteTable(String nameSpace,String tableName) throws IOException
{
    if(!isTableExists(nameSpace,tableName)) {
        System.out.println("表格不存在");
        return false;
    }
    //获取admin
    Admin admin = connection.getAdmin();
    try
    {
        //获取需要删除的表格
        TableName TABLE = TableName.valueOf(nameSpace, tableName);
        //禁用并删除
        admin.disableTable(TABLE);
        admin.deleteTable(TABLE);
    }catch (IOException e){
        e.printStackTrace();
        return false;
    }finally
    {
        admin.close();
    }
    return true;
}

//删除表格
System.out.println(DeleteTable("NewNameSpace", "NewTable"));
```





# 2.2.8 添加数据Put

```java
/**
 * 添加内容
 * @param nameSpase 命名空间
 * @param tableName 表名
 * @param RowKey RowKey
 * @param RowColumn 列族名称
 * @param RowName_ 列名
 * @param content 内容
 */
public static void putCall(String nameSpase,String tableName,String RowKey,String RowColumn,String RowName_,  String content) throws IOException
{
    Table table = null;
    //获取要操作的表格
    try
    {
        table = connection.getTable(TableName.valueOf(nameSpase, tableName));
    }catch (IOException e){
        System.out.println("表格未找到");
        return;
    }
    //创建Put对象
    Put put = new Put(Bytes.toBytes(RowKey));
    put.addColumn(Bytes.toBytes(RowColumn),Bytes.toBytes(RowName_),Bytes.toBytes(content));
    //put
    table.put(put);
    table.close();
}

putCall("NewNameSpace","NewTable","1001","info","姓名","lisi");
connection.close();
```



# 2.2.9 获取数据 Get

```java
/**
 * Get数据
 * @param nameSpase 命名空间
 * @param tableName 表名
 * @param RowKey 行键
 * @param RowColumn 行族
 * @param RowName 行名
 * @throws IOException 正常抛出
 */
public static void getCall(String nameSpase,String tableName,String RowKey,String RowColumn,String RowNam
{
    //得到表格
    Table table = connection.getTable(TableName.valueOf(nameSpase,tableName));
    //创建Get对象
    Get get = new Get(Bytes.toBytes(RowKey));
    //添加查询范围
    get.addColumn(Bytes.toBytes(RowColumn),Bytes.toBytes(RowName));
    //得到数据
    Result result = table.get(get);
    //取出数据
    Cell[] cells = result.rawCells();
    //循环输出
    for (Cell cell : cells)
    {
        //底层字节 需要特殊处理
        System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
    }
    table.close();
}
//获取
getCall("NewNameSpace","NewTable","1002","info","姓名");
connection.close();
```



# 2.3.0 扫描数据 scan

```JAVA
/**
 * 扫描数据
 * @param nameSpace  表命名空间
 * @param tableName 表名称
 * @param startRow 起始行号
 * @param stopRow 结束行号
 * @throws IOException 正常抛出
 */
public static void ScanContent(String nameSpace,String tableName, String startRow,String stopRow) throws IOException
{
    //1.得到表
    Table table = connection.getTable(TableName.valueOf(nameSpace,tableName));
    //2.创建Scan对象
    Scan scan = new Scan();
    //起始位
    scan.withStartRow(Bytes.toBytes(startRow));
    //结束位
    scan.withStopRow(Bytes.toBytes(stopRow));
    //得到数据
    ResultScanner scanner = table.getScanner(scan);
    //遍历数据
    for (Result result : scanner)
    {
        Cell[] cells = result.rawCells();
        for (Cell cell : cells)
        {
            System.out.println(new java.lang.String(CellUtil.cloneValue(cell), StandardCharsets.UTF_8));
        }
    }
    table.close();
}
//扫描
ScanContent("ods","order_master","020241031163235115","020241031163240658");
connection.close();
```



# 2.3.1 过滤数据

```java
/**
 * 扫描数据 并过滤
 * @param nameSpace  表命名空间
 * @param tableName 表名称
 * @param startRow 起始行号
 * @param stopRow 结束行号
 * @param RowFamily 列族
 * @param RowName 列名
 * @param Value 值
 * @throws IOException 正常抛出
 */
public static void FilterContent(String nameSpace,String tableName, String startRow,
                                 String stopRow,String RowFamily,
                                 String RowName,String Value) throws IOException
{
    //1.得到表
    Table table = connection.getTable(TableName.valueOf(nameSpace,tableName));
    //2.创建Scan对象
    Scan scan = new Scan();
    //起始位
    scan.withStartRow(Bytes.toBytes(startRow));
    //结束位
    scan.withStopRow(Bytes.toBytes(stopRow));
    //创建过滤器
    FilterList filterList = new FilterList();
    //只保留值的过滤(列)
    //列族,列名,方式(EQUAL = 等于),具体值
    //整行 => SingleColumnValueFilter 使用方式一样
    ColumnValueFilter columnValueFilter = new ColumnValueFilter(
        Bytes.toBytes(RowFamily),  //列族名称
        Bytes.toBytes(RowName),  //列明
        CompareOperator.EQUAL,  //等于
        Bytes.toBytes(Value)); //具体值
    filterList.addFilter(columnValueFilter);
    //添加过滤器
    scan.setFilter(filterList);
    //得到数据
    ResultScanner scanner = table.getScanner(scan);
    //遍历数据
    for (Result result : scanner)
    {
        Cell[] cells = result.rawCells();
        for (Cell cell : cells)
        {
            System.out.println(new java.lang.String(CellUtil.cloneValue(cell), StandardCharsets.UTF_8));
        }
    }
    table.close();
}

//过滤
FilterContent("NewNameSpace","NewTable","1000","1003","info","姓名","王五");
connection.close();

```



# 2.3.2 删除某一列数据 delete

```java
/**
 * 删除一列内容
 * @param nameSpace 名称空间
 * @param tableName 表名
 * @param RowFamily 列族
 * @param RowKey RowKey
 * @param RowName 列名
 * @throws IOException 正常抛出
 */
public static void deltetContentRow(String nameSpace,String tableName, String RowFamily,
                                    String RowKey,String RowName) throws IOException
{
    //获取表 table
    Table table1 = connection.getTable(TableName.valueOf(nameSpace, tableName));
    //创建删除对象 传入 RowKey
    Delete delete = new Delete(Bytes.toBytes(RowKey));
    //添加删除内容 传入 列族  列名
    delete.addColumn(Bytes.toBytes(RowFamily),Bytes.toBytes(RowName));
    //删除
    table1.delete(delete);
    table1.close();
}
//删除
deltetContentRow("NewNameSpace","NewTable","info","1005","666");
connection.close();
```



# 2.3.3 HBase对接Hive

- #### 在HBase的site配置下添加如下内容

```xml
<property>
    <name>hive.zookeeper.quorum</name>
    <value>bigdata1,bigdata2,bigdata3</value>
</property>
<property>
    <name>hive.zookeeper.client.port</name>
    <value>2181</value>
</property>
```

- 创建关联表 插入数据到Hive表的同时能影响HBase表
- 在Hive中创建表同时关联HBase

```hive
CREATE TABLE 表名(
	empno int,
    job string,
    mgr string
)STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
with SERDEPROPERTIES("hbase.colums.mapping"=":key,info:empno,info:job,info:mgr")
tblproperties("hbase.table.name"="表名")；
```

- 关联表只能使用insert into 进行插入数据



- ### Hive表关联HBase进行数据操作

- #### Hive创建外部表

```hive
CREATE EXTERNAL TABLE 表名(
	字段名 类型名,...
)
stored by
'org.apache.hadoop.hiv.hbase.HBaseStorageHandler'
with serdeproperties("hbase.columns.mapping" =
                    ":key,info:字段...")
tblproperties ("hbase.table.name" = "HBase内的表名称")
```

