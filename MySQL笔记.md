P20

https://www.bilibili.com/video/BV1Cm421373b?spm_id_from=333.788.videopod.episodes&vd_source=6cfabdd9118b8397a529eb6df87378b6

# MySQL

with 临时表

# 1.0 概念

- 数据定义语句 => 创建和修改盛放数据的容器 DDL
- 数据操纵语句 => 表中添加、修改、删除数据 DML
- 数据查询语句 => 表中数据多条件查询 DQL

# 1.1 基本操作

- ## 连接MySQL服务

  - mysql -u用户名 -p密码 -h地址 -P 端口 库名

- ## 退出连接

  - exit;

# 1.2 数据定义语言 DDL

> 数据定义语言
>
> ​	DDL用于定义和管理数据库的结构，包括库 表 索引 视图 等数据库对象的创建 修改 和删除
>
> ​	DDL不涉及对数据的操作，而是关注数据库的结构和元数据(容器)

- ### DLL关键字

1. CREATE => 用于创建数据库、表、索引、视图等
2. ALTER => 用于修改数据对象的结构，如修改表结构、添加列、删除列等。
3. DROP => 用于删除数据库对象，如删除表、删除索引等

# 1.2.1 SQL命名规范

> ## 规定

- 数据库名，表名不得超过30个字符，变量名长度限制29个
- 必须只能包含A-Z，a-z,_共63个字符，不能数字开头
- 数据库名，表名，字段名等对象中间不能包含空格。

> ## 规范

- 注释应该清晰、简洁地解释SQL语句的意图 功能和影响
- 库 表 列名应该使用小写字母、并使用下划线_或 //不是特别建议驼峰命名法
- 库 表 字段名应该简洁明了，具有描述性，反映其所存储的数据的含义
- 库名应与对应的程序名一致
- 表名最好是遵循“业务名称_表”的作用 例如 : alipay_task
- 列明应该遵循"表实体_属性"的作用 例如 product_name

# 1.2.2 库管理 创建

> CREATE create 创建
>
> DATABASE database 数据库

- 创建库必须指定库名，可能指定字符集或者排序方式

1. 创建数据库 使用默认的字符集和排序方式

   ```sql
   CREATE DATABASE 数据库名;
   create database 数据库名;
   ```

   

2. 判断并创建默认字符集库(推荐)

   ```sql
   CREATE DATABASE IF NOT EXISTS 数据库名;
   create database if not exists 数据库名;
   ```

   

3. 创建指定字符集库或排序方式

   ```sql
   CREATE DATABASE 数据库名 CHARACTER SET 字符集;(不建议)
   create database 数据库名 character set 字符集;
   
   CREATE DATABASE 数据库名 COLLATE 排序规则;
   create database 数据库名 collate 排序规则;
   //排序规则 utf8mb4_0900_as_cs => 大小写敏感
   ```

4. 创建指定字符集和排序规则库

   ```sql
   create database 数据库名 character set 字符集 collate 排序规则
   
   ```



# 1.2.3 库管理 查看和使用

> 对数据库进行操做时 必须use选择

1. 查看当前所有库

   ```sql
   SHOW DATABASES;
   show database;
   ```

   

2. 查看当前使用库

   ```sql
   SELECT DATABASE();
   select database();
   ```

   

3. 查看指定库下所有表

   ```sql
   SHOW TABLES FROM 数据库名;
   show tables from 数据库名;
   ```

   

4. 查看创建库的信息

   ```sql
   SHOW CREATE DATABASE 数据库名;
   show create database 数据库名;
   ```

   

5. 切换库/选择库

   ```sql
   USE 数据库名;
   use 数据库名;
   ```

   

# 1.2.4 库管理 修改库

1. 修改库编码字符集

   ```sql
   ALTER DATABASE 数据库名 CHARACTER SET 字符集; #修改字符集 gbk utf8
   ALTER DATABASE 数据库名 COLLATE 排序方式
   ALTER DATABASE 数据库名 CHARACTER SET 字符集 COLLATE 排序方式;
   alter database 数据库名 character set 字符集;
   alter database 数据库名 collate 排序方式;
   alter database 数据库名 character set 字符集 collate 排序方式;
   ```

2. 删除数据库

   - 直接删除

   ```sql
   DROP DATABASE 数据库名称;
   drop database 数据库名称;
   ```

   - 判断并删除

   ```sql
   DROP DATABASE IF EXISTS 数据库名称;
   drop database if exists 数据库名称;
   ```

   

# 1.2.5 库管理实战练习

> 场景1 假设你正在为一个多语言的博客平台设计数据库。你需要创建一个名为blog_platform 的数据库，支持多语言的文章和评论。由于博客平台可能包含来自不同语言的用户，你决定使用utf8mb4字符集，排序方式选择默认值，以支持广泛的Unicode字符

> 场景2 产看数据库字符集和排序规则

> 场景3 假设在后续发展中，你决定将排序方式修改为 utf8mb4_0900_as_cs 以实现大小敏感比较

> 场景4 查看修改后数据字符集和排序规则

> 场景5 项目惨遭放弃，需要删除项目库，并且跑路

- 步骤

1. 创建数据库

```sql
create database blog_platform character set utf8mb4_0900_as_cs;
CREATE DATABASE blog_platform character set utf8mb4_0900_as_cs;

create database if not exists blog_platform character set utf8mb4;
CREATE DATABASE IF NOT EXISTS blog_platform CHARACTER SET utf8mb4;
```

1. 查看数据库的字符集和排序规则

```sql
show create database blog_platform;
SHOW CREATE DATABASE blog_platform;


show variables like 'character_set_database';
SHOW VARIABLES LIKE 'character_set_database';

SHOW VARIABLES LIKE 'collation_databse';
show variables like 'collation_databse';
```

1. 修改排序方式

```sql
alter collate utf8mb4_0900_as_cs;

ALTER DATABASE blog_platform COLLATE utf8mb4_0900_as_cs;
alter database _____________ COLLATE __________________;
```

1. 查看修改后的信息

```sql
show create database blog_platform;
SHOW CREATE DATABASE blog_platform;
```

1. 删库

```sql
DROP blog_platform;
DROP DATABASE IF EXISTS blog_platform;
drop blog_platform;
drop database if exists blog_platform;
```

# 1.2.6 库管理 表管理

> 创建库VS创建表
>
> - 创建库，相当于创建一个excel表格文件，我们只需要指定库名和字符集即可！
> - 创建表，相当于创建一个表格内容页，我们不仅需要指定表名和字符集，还需要指定表中的列名和列类型，甚至还会加入一些约束，例如 手机号必须填写

# 1.2.6.1 创建表

> 核心要素
>
> 1. 指定表名
> 2. 指定列名
> 3. 指定类列类型
> 4. 指定列约束[可选]
> 5. 指定表配置[可选]

```sql
CREATE TABLE 表名(
	列名 类型 [列可选约束] [COMMENT '列可选注释'],
)[表可选约束 表符号]
```

```sql
CREATE TABLE IF NOT EXISTS 表名(
	列名 类型 [列可选约束] [COMMENT '列可选注释'],
)[表可选约束 表符号]
```

```sql
select * from SQLTable where (get_time > '${MaxTimeStr}' and get_time != 'NULL') or 
(used_time > '${MaxTimeStr}' and used_time != 'NULL') or (pay_time > '${MaxTimeStr}' and pay_time != 'NULL')
```



# 9.0 数据插入insert into

> ### 创建表

```sql
create table student(
	id int,
    name String
)
```



> ### 插入数据

```sql
insert into student (id,name) value (1,'wd'),(2,'qd');
```

```sql
insert into student value (1,'wd'),(4,'qe')
```



# 9.1 数据删除 delete

> 不带条件 全部删除

```sql
delete from student where 条件;
```



# 9.2 数据更新 update

> 基础语法
>
> update 表名 set 要更改的列名 = '更改后的值' where 进行判断的字段

```sql
update student set name = 'xxx' where id = x;
```



# 9.3 分组聚合 group by

> ### 按条件分组 统计每个组的人数

- ### 执行顺序 -> 先分组 再聚合

```sql
select gender from student group by gender;
```



# 9.4 排序分页 order by

> ### ASC升序 DESC降序

- 将年龄大于20岁的按照年龄进行升学排序
- order by 默认是asc升序

```sql
select * from studnet where age > 20 order by age;
```



## 9.4.1 LIMIT 展示条目

```sql
select 列|聚合函数|* from 表
where ...
group by ...
order by ... [asc|desc]
limit n[,m]
```

> ### 执行顺序

from -> where -> group by -> 聚合 -> select -> order by -> limit



# 9.5 JOIN 多表查询

> from 多表
>
> inner join
>
> outer join

> ### from多表

```sql
select 表1.人名,
		表2.班级名称 
from 表1,表2 
where 表1.id = 表2.id
```



> ### 内关联 inner join
>
> inner join 关联表名 ...
>
> on 字段 = 字段 (条件)

- 内关联只是求两个表的交集，如果有一个表的一条数据
  - 被关联的字段为空，那么这条数据就不会显示
- 必须双向匹配

```sql
select * from student
inner join class 
on student.id = class.id;
```



> ## 外关联

```sql
select ... 
from 表1 [as 别名] ... 
(left | right) [outer] join 表2 [as 别名2] ... 
on 连接条件
```

left左外关联 => from的表的数据全部都要显示

right右外关联 => 以join的表的数据为主





# 9.6 窗口函数

> ### MySQL 8

- #### 排序类

1. rank,dense_rank,row_number

```sql
--按班级分组后打上序号 不考虑并列
select *,row_number() over (partition by cid order by 分数) from SQLTable

--按照班级分组后做跳跃排名 考虑并列
select *,rank() over (partition by cid order by 分数) from SQLTable

--按照班级分组后作连续排名 考虑并列
select *,dense_rank() over (partition by cid order by 分数) from SQLTable


```

