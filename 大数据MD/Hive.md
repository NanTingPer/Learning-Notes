# Hive

# 1.0分区表 和 分桶表

# 1.1分区表

> Hive中的分区表就是把一张大表的数据按照业务需要分散的存储到多个目录，每个目录就是一个分区，可以使用where语句对分区进行查询

## 1.1.1 分区表 读写 创建

> 创建分区表
>
> 相当于给表添加一个字段
>
> 可以查询 也可以 做过滤条件

```hive
create table 表名
(
	字段 int;
	dad char;
)
	partitioned by (day string) //分区字段

```

> 写数据 load

```hive
load data local inpath '文件路径'
into table 表名
partition(day='20220401'); //day是分区字段的值
							//导入到这个分区
```

将0401分区的数据复制到0402分区

```hive
//写入  覆盖 		表		分区
insert overwrite table 表名 partition(day='20240402')
select 
	deptno,
	dname,
	loc
form 表名
where day='20220401'
```

- 为表添加分区列

```hive
alter table 表名 add partition(day='2024'month='12');
```





# 2.0 重新

> #### 元数据管理
>
> #### SQL解析器

元数据都记录在MySQL的hive库下

##### 在命令行查看内容时，行内容紧凑在一起，是因为默认分隔符是"\001"是一种特殊字符 文本编辑器内显示为 SOH

```sql
create table if not exists 表.... row format delimited fields terminated by '格式字符';

row format => 行格式化
delimited fields => 列格式化
terminated by => 以什么为间隔
```



- DBS => 有哪些库
- TBLS => 有哪些表

> #### hive的数据库本质在hdfs上就是一个文件夹.db

- ##### 指定创建的数据库的存储位置

```sql
create database 库名 location '路径';
```

```sql
desc database 库名;//查看位置
```

- 删库

```sql
drop database 库名;//空库
drop database 库名 cascade;//全删
```

- 创建库

```sql
create database if not exists 库名 location 路径;
```

- 修改路径

```sql
alter database 库名 set location 路径名;
```

- 插入数据

```sql
insert into 表 values(列1值,列2值...),(列1值,列2值);
```

- 查看表的数据信息

```sql
desc formatted 表名;
```



# 2.1 SQL语法

> [] => 可选
> | => 或
>
> ... => 未完
>
> () => 必选

- 

# 2.2 HIVE的数据类型

> ## 标准SQL所没有的字段

- external => 外部表
- partitioned by => 分区表
- clustered by => 分桶表
- stored as => 存储格式
- location => 存储位置

> ### 数据类型

| BOOLEAN            | true/false     | true |
| ------------------ | -------------- | ---- |
| TINYINT            | -128 - 127     | 1Y   |
| SMALLINT           | -32768 - 32767 | 1S   |
| **INT(int)**       |                | 1    |
| BIGINT(long)       |                | 1L   |
| FLOAT(float)       |                |      |
| **DOUBLE(double)** |                |      |
| **STRING(String)** | 字符串，变长   |      |

> 创建基础表
>
> 3列

```sql
create table Student(
	id int comment 'ID',
    name string comment '名字',
    age int comment '年龄'
);
```

# 2.3 *部表

> 自定义列分隔符

- 内部表 (`create table 表名`)  持久化使用
  - 内部表又称为**管理表**，内部表数据存储的位置由hive.metastore.warehouse.dir参数决定
  - 删除内部表会**直接删除元数据(metadata)及存储数据**，因此内部表不适合其他工具共享数据
- 外部表(`create externai table 表名...location....`  ) 临时使用
  - 指定关键字 externai
  - 被external关键字修饰的就是外部表，也叫关联表
  - 外部表指表数据可以在任何位置，通过location关键字指定。数据存储的不同也代表了这个表在理念上并不是Hive内部管理的，而是可以随意临时链接到外部数据上的。
  - 删除外部表的时候，仅仅只删除元数据(表的信息) 不删除数据本身



# 2.4 外部表

> 外部表的表和数据是独立的
>
> 可以先创建外部表再导入数据
>
> 可以先导入数据再创建外部表
>
> 
>
> 创建外部表必须指定分隔符

```sql
create table if not exists 表.... row format delimited fields terminated by '格式字符';

row format => 行格式化
delimited fields => 列格式化
terminated by => 以什么为间隔
```

> 示例

```sql
create external table 表名(字段...) row format delimited fields terminated by '分隔符' location '路径';
```



# 2.5 内外部表转换

> #### 查看表的格式数据 `desc formatted 表名`

Table Type => MANAGED_TABLE 内部表

Table Type => EXTERNAL TABLE 外部表

> #### 修改
>
> ###### `tblproperties => 属性`

```sql
alter table 表名 set tblproperties('EXTERNAL'='TRUE'); //成为外部表
alter table 表名 set tblproperties('EXTERNAL'='FALSE'); //成为内部表
```

# 2.6 数据导入

> 加载 / 导入
>
> `LOAD DATA [LOCAL] INPATH '路径' [OVERWRITE] INTO TABLE 表名`
>
> - LOAD DATA => 加载数据
> - [LOCAL] => 如果在本地需要写 不在本地可不写
> - INPATH 'XXX' => 数据路径
> - [OVERWRITE] => 写入模式 写这个表示覆盖 不写则不覆盖
> - INTO TABLE 表名 =>  将数据写入哪个内部表

> #### 创建内部表

```sql
create table Student(
	name string comment '名字',
	age int comment '年龄',
	tag string comment '标签'
)row format delimited fields terminated by '\t';
```

> #### 加载外部数据

```sql
load data local inpath '/root/Student.txt' into table Student;
```

> #### 加载外部数据 HDFS
>
> HDFS的文件会被 移动
>
> fs -mv

```sql
load data inpath '/usr/Student.txt' into table Studnet;
```

> #### 插入数据
>
> 将表名2的数据插入到表名

```sql
insert into 表名 select * from 表名2;
```



# 2.7 数据导出

> ##### 语法` insert overwrite [local] directory 'path' select xxx from xxx;`

> 将查询结果导出到本地，使用默认分隔符
>
> 将表Student的数据 覆盖导入到 本地的 /opt/Student文件

```sql
insert overwrite local directory '/opt/Student' select * from Studnet;
```



> 将查询的结果导出到本地，指定列分隔符
>
> 将Student的数据 覆盖写入到本地的 /opt/Student文件 分隔符为\t

```sql
insert overwiter local directory '/opt/Student' row format delimited fields terminated by '\t' select * from Student;
```



> 将查询结果导出到HDFS
>
> 将Student的数据 覆盖写入到HDFS 的/opt/Student文件下 分隔符为\t

```sql
insert overwiter directory '/opt/Student' row format delimited fields terminated by '\t' select * from Student;
```



> 使用bin/hive 进行导出

> bin/hive -e "SQL语句" > 文件
>
> 将SQL查询的数据通过 '>' 重定向符号 覆盖写入到右边指定的文件

```shell
bash /bin/hive -e "select * from Student;" > /opt/Student.txt
```

> bin/hive -f SQL脚本文件 > 文件
>
> 将sql脚本 sqllang 的查询结果 通过 '>' 重定向符号 覆盖写入到指定的文件

```shell
bash /bin/hive -f sqllang.sql > /opt/Student.txt
```



- 数据导入
- 方式1
  - `load data [local] inpath 'path' [overwrite] into table tableName`
  - 如果数据在hdfs 那么源文件会消失，本质上使用mv移动
  - 加载不会走MR 小文件加载速度快
- 方式2
  - 从其他表加载数据
  - `insert into|overwrite table tableName select ...;`



- 数据导出
- 方式1
  - 通过SQL查询语句向文件写入数据
  - `insert [overwrite] [local] directory '文件路径' select ...`
- 方式2
  - 使用hive自带bash执行命令
  - bin/hive -f sqlscript.sql > 文件路径
  - bin/hive -e sql语句 > 文件路径

# 2.8 分区表

> ## 概念

- 每一个分区，就是一个文件夹
  - 将一个大的表格分割成了很多个文件夹



> #### 层级

- 数据库
  - 表1
    - 分区1
    - 分区2
    - ....
  - 表2

- ##### 如果没有分区 那么一个表就一个文件夹

- ##### 如果有分区了 那么一个表内就很多个文件夹

> #### 多层级分区

- 数据库
  - 表1
    - 分区1
      - 分区的分区1
    - 分区2
      - 分区的分区1
    - ....
  - ....



> ##### 例

- ##### 按年份分区 -> 按月份分区

## 2.8.1 基本语法

```sql
create table 表名(...) partitioned by (分区列 列类型,....) [row format delimited fields terminated by ''];
```

- ##### partitioned by

  - ##### 从左往右 一个一个层级

> ##### 例
>
> 创建一个叫Student的表
>
> 添加一个分区字段
>
> 以\t为分隔符

```sql
create tbale Student(
	name string comment'姓名',
	age int comment '年龄'
) partitioned by(month string) 
row format delimited 
fields terminated by '\t';
```

> #### 加载数据到分区表
>
> 文件路径下的数据载入到 表名的 month的202005分区下

```sql
load data local inpath '文件路径' into table 表名 partition(month='202005')
```



# 2.9 分桶表

> 分桶和分区一样，也是通过改变表的存储模式，从而完成对表优化的一种调优方式
>
> 但是分区不同，分区是将表拆分到**不同的子文件夹**中进行存储，而分桶是将表拆分到**固定数量的不同文件**中进行存储。
>
> 一个是按 文件夹分 一个是按 文件



> ### 创建分桶表

- 开启分桶的自动优化(自动匹配reduce task数量和桶数量一致)
  - set hive.enforce.bucketing=true;
- 创建分桶表
  - clustered by(分桶字段)
  - into 数量 buckets 分桶数量


```hive
create table Studnet(
	name string comment '名字',
    age int
)clustered by(age)
into 3 buckets
row formate delimited fields terminated by '\t';
```

> #### 往分桶表内插入数据
>
> 分桶表内无法直接通过load data加载数据
>
> 只能通过insert select

1. 创建一个临时表 外部表内部表都可以，通过load data往这个表注入数据

```hive
create table Student_Temp(name (
    string comment '名字'
	age int    
)row format delimited fields terminated by '\t';
```
2. 往临时表注入数据

```hive
load data local inpath '数据目录' into table Student_Temp;
```

3. 使用insert select 往 分桶表插入数据

- 分桶字段必须与最终需要插入数据的表的分桶字段一样

```hive
insert overwrite table Student select * from Student_Temp cluster by(c_id);
```



## 2.9.1 分桶列

> 只有分区没有分桶就无所谓每个分区的数据量
>
> 一旦有了分桶设置，比如分桶数量为3，那么，表内文件或分区内数据文件的数量就限定为3，当数据插入的时候，需要以分为3，进入三个桶文件内。

> 数据的三份划分基于 ***分桶列的值进行hash取摸*** 来决定
>
> load data ***不会触发MR(MapReduce),也就是没有计算过程(无法执行Hash算法)***,只是简单的移动数据，所有无法进行分桶表的数据插入。

> 相同的值得到的Hash只不管怎么计算都是同一个值 取余得到其所分配的分区

- 对单值过滤有着高性能 基于分桶列
- 双表 JOIN高性能 基于分桶列
- group by 分组高性能 基于分桶列

# 3.0 修改表

- 重命名表

```hive
alter table oldTable rename to newTable;
```



- 修改表属性值

```hive
alter table tableName set tblproperties('属性字段'='属性值');
```



- 表分区 添加 只加文件夹 数据需要自己导入

```hive
alter table tableName add partition(分区字段1='分区值'...);
```



- 修改分区值(修改元数据记录，HDFS的实体文件夹不会改名，单是在元数据记录中改名了)

```hive
alter table tableName partition(分区字段1='分区值'...) rename to partition(分区字段1='分区值'...);
```



- 删除分区(只是删除元数据 分区数据还在)

```hive
alter table tableName drop partition (分区字段='分区值');
```



- 添加列

```hive
alter table tableName add columns (列1 类型,列2 类型);
```



- 修改列名 类型必须匹配

```hive
alter table tableName change oldName newName 类型; 
```



- 删除表

```hive
drop table tableName;
```



- 清空表数据 外部表无法清空

```hive
truncate table tableName;
```



# 3.1 复杂类型操作(Array Map)

> 创建含有数据类型列的表
>
> colloction items terminated by ',' -> 指定数组元素分隔符

```hive
create table ArrayTable(
	name string,
    city array<string>)
row format delimited fields terminated by '\t'
collection items terminated by ',';

```

- 取元素

```hive
select name,city[0] from ArrayTable;
```

- 查个数

```hive
select name,size(city) from ArrayTable;
```

- 过滤信息 查看是否包含 '数据'   size只有hive有 MySQL没有

```hive
select * from ArrayTable where array_contains(city,'数据');
```



> ### Map

```hive
create table tableName(
	id int,
    name string,
    sex map<string,string>,
)
row format delimited fields terminated by ','
collection items terminated by '#', <!-- 各个值之间的间隔符 -->
map keys terminated by ':'; <!-- 键值对之间的间隔符 -->
```

> ### 查看键对应的值

```hive
select sex['男'] from tableName;
```

> #### 取出map的全部key 返回array

```hive
select map_keys(sex) from tableName;
```

> #### 查看map kv对数

```hive
select size(sex) from tableName;
```

> 查看指定的数据是否包含在map中

```hive
select * from tableName where array_contains(map_keys(sex),'男'); 
select * from tableName where array_contains(map_values(sex),'*');
```



# 3.2 复杂类型struct

> #### 相当于一个列上分成两个子列，实际上还是一列

- 创建表

```hive
create table Student(
	id int,info struct<name:string,age:int>
)row format delimited fields terminated by '#'
collection items terminated by ':';
```

- collection items terminated by => 数据之间的分隔符

# 3.3 数据查询

## 3.3.1 构建查询数据库

- 创建itcast库

```hive
create database itcast;
```

- 创建orders表

```hive
CREATE TABLE itcast.orders(
	orderId bigint COMMENT'订单id',
	orderNo string COMMENT'订单编号',
	shopId bigint COMMENT'门店id',
	userId bigint COMMENT'用户id',
	orderstatus tinyint COMMENT '订单状态 3:用户拒收 2:未付款的订单 1:用户取消 0:待发货 1:配送中 2:用户确认收货',
	goodsMoney double COMMENT'商品金额',
	deliverMoney double COMMENT'运费',
	totalMoney double COMMENT'订单金额(包括运费)',
	realTotalMoney double COMMENT'实际订单金额(折扣后金额)',
	payType tinyint COMMENT '支付方式,0:未知;1:支付宝，2:微信;3、现金;4、其他',
	isPay tinyint COMMENT'是否支付 0:未支付 1:已支付',
	userName string COMMENT '收件人姓名',
	userAddress string COMMENT '收件人地址',
	userPhone string COMMENT '收件人电话',
	createTime timestamp COMMENT '下单时间',
	payTime timestamp COMMENT '支付时间',
	totalPayFee int COMMENT '总支付金额'
)ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';

```

- 构建数据

```hive
load data local inpath '/opt/itheima_orders.txt' into table itcast.orders
```

- 创建用户表

```hive
CREATE TABLE itcast.users(
	userId int,
	loginName string,
	loginSecret int,
	loginPwd string,
	userSex tinyint,
	userName string,
	trueName string,
	brithday date,
	userphoto string,
	userQQ string,
	userPhone string,
	userScore int,
	userTotalScore int,
	userFrom tinyint,
	userMoney double,
	lockMoney double,
	createTime timestamp,
	payPwd string,
	rechargeMoney double
)ROW FORMAT DELIMITED FIELDS TERMINATED BY'\t';
```

- 载入数据

```hive
load data local inpath '/opt/itheima_users.txt' into table itcast.users
```

## 3.3.2 查询

- 查询全表

```hive
select * from itcast.orders;
```



- 查询单列信息

```hive
select userid from itcast.orders;
```



- 查询表有多少条数据

```hive
select count(*) from itcast.orders;
```



- 过滤广东省的订单 %是匹配任意字符

```hive
select * from itcast.orders where like '%广东%'
```



- 找出广东省单笔营业额最大的订单

```hive
select * from itcast.orders where useraddress like '%广东%'
order by totalmoney desc limit 1;
```



- 统计未支付、已支付各自的人数

```hive
select ispay,count(*) from itcast.orders group by ispay;
```



- 在已付款的订单中，统计每个用户最高的一笔消费金额

```hive
select userid,MAX(totalmoney) --显示字段
from itcast.orders --来自
where ispay = 1 --条件
group by userid; --分组
```



- 统计每一个用户的平均订单消费金额

```hive
select userid,avg(totalmoney)
from itcast.orders 
group by userid;
```



- 统计每个用户的平均订单消费额 并过滤大于10000的数据

```hive
select userid,avg(totalmoney) as avg_money
from itcast.orders
group by userid
having avg_money > 10000;
```



## 3.3.3 join

> 订单表和用户表JOIN 找出永固username

```sql
select 订单表.用户id,用户表.用户名称 from订单表 left join 用户表 on 订单表.id = 用户表.id
```



# 3.4 正则匹配RLIKE

| 字符 | 匹配                                                         | 示例                      |
| ---- | ------------------------------------------------------------ | ------------------------- |
| .    | 任意单个字符                                                 | jav.匹配java              |
| []   | []中的任意一个字符                                           | java匹配[abc]va           |
| -    | []内表示字符范围                                             | java匹配[a-z]av[z-g]      |
| ^    | 在[]内的开头，匹配除了[]内的字符外的任意一个字符             | java匹配"[^b-f va         |
| \|   | 或                                                           | x\|y匹配x或y              |
| \    | 将下一个字符标记为特殊字符、文本、反向引用或者八进制转义符   | "(匹配(                   |
| $    | 匹配输入字符串结尾的位置。如果设置了RegExp对象的Multiline属性，$还会与 \n 或 \r 之前的位置匹配 | ;$匹配位于一行及外围的;号 |
| +    | 一次或多次匹配钱买你的字符                                   | zo+ 匹配zo或zoo           |
| ?    | 零次或一次匹配前面的字符                                     | zo?匹配z或zo              |
| \s   | 空白字符                                                     |                           |
| \S   | 非空白字符                                                   |                           |



# 3.5 UNION联合

> UNION用于将多个SELECT语句的结果组合成单个结果集。
>
> 每个select语句返回的列的数量和名称必须相同。否则，将引发架构错误。
>
> 默认去重，加上ALL不去重

```sql
select ...
	union [ALL]
select ...
```



# 3.6 随机采样 TABLESAMPLE

> 大数据体系下，在真正的环境中，很容易出现很大的表，比如体积达到TB级别。
>
> 对这种表的一个简单的select*都会非常慢，哪怕limit10想要看10条数据也会走MR流程

> hive提供的快速抽样的语法，可以快速从大表中随机抽取一些数据供用户查看。

```hive
select ... from 表明 tabLesample(bucket x out of y on(colname | rand()))
```

- y表示将数据随机划分成y份(y个桶)
- x表示从y里面随机抽取x份数据作为取样
- colname 表示随机的依据基于某个列的值，基于这个列的值 进行Hash取模 分成y份 (写列名)
  - 条件不变 每次查询结果一致
- rand()表示随机的依据基于整行，完全随机
  - 条件不变 每次查询结果也不一致



> ### 语法二 基于数据块抽样

```sql
select ... from 表1 tablesample (num ROWS | num PERCENT | num(K|M|G))
```

- num ROWS 表示抽样num条数据
- num PERCENT 表示抽样num百分比比例的数据
- num(K|M|G)表示抽取num大小的数据,单位可以是KMG表示KB MB GB

1. 使用这种语法抽样，条件不变的话，每次抽样的结果一致
2. 无法做到随机抽样，只是按照数据顺序从前向后取



# 3.7 虚拟列

1. INPUT_FILE_NAME 显示数据行所在的具体文件
2. BLOCK_OFFSET_INSIDE_FILE 显示数据行所在文件的偏移量
3. ROW_OFFSET_INSIDE_BLOCK 显示数据所在HDFS块的偏移量
   1. 此虚拟列需要设置 SET hive.exec.rowoffset=true 才能使用



# 3.8 函数01

> #### 内建函数(内置函数)

- #### 查看当前可用的函数

```hive
show functions;
```

- 查看函数的使用方式

```hive
describe function extended 函数名称;
```



> 类型转换函数
>
> ### cast(数据 as 类型)
>
> 将给定数据转换为给定类型，如果转换失败返回null
>
> 对于cast(expr as boolean)对于非空字符串都会返回true



> ### Date Functions 日期函数

| timestamp | current_timestamp()                                          | 返回当前时间戳 |
| --------- | ------------------------------------------------------------ | -------------- |
| date      | current_date                                                 | 返回当前日期   |
| date      | to_date(string timestamp)                                    | 时间戳转日期   |
| int       | 需要传入参数为 string date<br>year年<br>quarter季度<br>month月<br>day日<br>hour时<br>minute分<br>second秒 |                |



# 3.9 综合案例

> 什么叫ETL
>
> 从表查询数据进行过滤和转换，并写入到新表
>
> ETL:
>
> - E Extract 抽取
> - T Transform 转换
> - L Load 加载
>
> 从A抽取数据(E) 进行数据转换(T) 将数据加载到B(L)

- 创建数据库 db_msg

```hive
create database db_msg;
```

- 创建表

```hive
create table db_msg.tb_msg_source(
    msg_time string comment "消息发送时间",
    sender_name string comment "发送人昵称",
    sender_account string comment "发送人账号",
    sender_sex string comment "发送人性别",
    sender_ip string comment "发送人ip地址",
    sender_os string comment "发送人操作系统",
    sender_phonetype string comment "发送人手机型号",
    sender_network string comment "发送人网络类型",
    sender_gps string comment "发送人的GPS定位",
    receiver_name string comment "接收人昵称",
    receiver_ip string comment "接收人IP",
    receiver_account string comment "接收人账号",
    receiver_os string comment "接收人操作系统",
    receiver_phonetype string comment "接收人手机型号",
    receiver_network string comment "接收人网络类型",
    receiver_gps string comment "接收人的GPS定位",
    receiver_sex string comment "接收人性别",
    msg_type string comment "消息类型",
    distance string comment "双方距离",
    message string comment "消息内容"
);
```



- 加载数据

```hive
load data local inpath '/opt/chat_data-5W.csv' INTO table db_msg.tb_msg_source;
```



- 查看数据

```hive
select * from db_msg.tb_msg_source tablesample(100 rows);
select count(*) from db_msg.tb_msg_source;
```



- 过滤 GPS 为空的数据

```hive
select * from db_msg.tb_msg_source where lenght(sender_gps) > 0 
```

- 构建天和小时

```hive
date(msg_time).hour(msg_time)
```

- 提取经纬度

```hive
split(sender_gps,',')[0] --经度
split(sender_gps,',')[1] --纬度
```



- 构建新表数据

```hive
SELECT 
	*,
	date(msg_time) as msg_date,
	hour(msg_time) as msg_hour,
	split(sender_gps,',')[0] as GPS_X,
	split(sender_gps,',')[1] as GPX_Y
FROM db_msg.tb_msg_source
WHERE Length(sender_gps) > 0
```



- 创建新表

```hive
create table db_msg.tb_msg_etl(
    msg_time string comment "消息发送时间",
    sender_name string comment "发送人昵称",
    sender_account string comment "发送人账号",
    sender_sex string comment "发送人性别",
    sender_ip string comment "发送人ip地址",
    sender_os string comment "发送人操作系统",
    sender_phonetype string comment "发送人手机型号",
    sender_network string comment "发送人网络类型",
    sender_gps string comment "发送人的GPS定位",
    receiver_name string comment "接收人昵称",
    receiver_ip string comment "接收人IP",
    receiver_account string comment "接收人账号",
    receiver_os string comment "接收人操作系统",
    receiver_phonetype string comment "接收人手机型号",
    receiver_network string comment "接收人网络类型",
    receiver_gps string comment "接收人的GPS定位",
    receiver_sex string comment "接收人性别",
    msg_type string comment "消息类型",
    distance string comment "双方距离",
    message string comment "消息内容",
    msg_day string comment "消息日",
    msg_hour string comment "消息小时",
    sender_lng double comment "经度",
    sender_lat double comment "纬度"
);
```



- 数据插入新表

```hive
insert INTO table db_msg.tb_msg_etl
SELECT 
	*,
	date(msg_time) as msg_date,
	hour(msg_time) as msg_hour,
	split(sender_gps,',')[0] as GPS_X,
	split(sender_gps,',')[1] as GPX_Y
FROM db_msg.tb_msg_source
WHERE Length(sender_gps) > 0;
```





# ~~<u>*4.0 数据增量导入*</u>~~

- ~~<u>*先查询分区是否存在*</u>~~

```hive
show partitions 表名 partition(分区字段=值...);
```

- ~~<u>*创建新的分区(增量)*</u>~~

```hive
alter table 表名 add partition(分区字段=值...);
```

- 



# 4.0 全量与增量的概念

- ### 全量

> 指的分区下面的所有数据 比如 2024 10月下面有1000w个数据，全部同步
>
> 一个分区存放全部数据

- ### 增量

> 指的是分区只记录当天的一个数据，比如10月7日有10w条数据
>
> 如果有历史分区 比如2022 10月七日到 2024 10月七日 加起来有1000w条数据
>
> 多个分区 存放全部数据



# 4.1 DWD层概述

> ### 从ODS层拉取数据
>
> #### 明细颗粒度事实层

- 格式内容问题产生的问题
  - 不同数据源采集而来的数据内容和格式定义不一致
  - 时间 日期格式不一致清晰 根据实际情况，把时间/日期数据库转换成统一的表示方式
  - 数据类型不符
- 逻辑错误清洗
  - 数据重复
  - 数据不完全相同 但从业务角度看待数据是同一个数据
  - 矛盾内容修正
- 缺失值的清洗
  - 数据值缺失
  - 造成原因 有些对象的某些属性或某些属性不可用 信息被遗漏，或者实时性高还未来得及做出判断
  - 数据填充用一定的值去填充空的值，从而使信息表完备化
- 不符合业务要求的数据(或挖掘需求分析)
