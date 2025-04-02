---
title: "大数据 OneHot编码"
date: 2025-04-02T15:41:00+08:00
draft: false
tags: ["大数据"]
---

# 特征工程 `OneHot`编码

## 需求

现有表一表`order`，其中字段为

```sql
id int,
name string,
sku_id int,
spu_id int
```

有另外一表`sku_info`，其中字段为

```sql
id int,
name string
```

现在要对`order`表中 `sku_id`进行`OneHot`处理



## 开始

使用`Spark` 相关库 `Scala 2.12.X`进行

### 1. 创建Spark上下文

```scala
val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("dawf")
    .enableHiveSupport()
    .getOrCreate()
```

### 2.读取表数据

```scala
val conf = new Properties()
conf.put("user","root")
conf.put("password","123456")
val order = spark.read.jdbc("jdbc:mysql://192.168.xx.xx/xx?useSSL=false", "order", conf)
val sku_info = spark.read.jdbc("jdbc:mysql://192.168.xx.xx/xx?useSSL=false", "sku_info", conf)
```

### 3.提取全部`sku_id`

| 函数     | 解释             |
| -------- | ---------------- |
| distinct | 去重             |
| collect  | 将数据收集       |
| map      | 将`Row`转为`Any` |

```scala
val allSku_id = sku_info.select("id").distinct().collect().map(f => f.get(0))
```

### 4.创建动态表

```scala
var order_var = order.select("id", "sku_id")
```

### 5.遍历`sku_id`完成`OneHot`

```cs
val udf1 = udf((d1 : Double, d2 : Double) => if(d1 == d2) "1.0" else "0.0")
allSku_id.foreach(f => order_var = order_var.withColumn(s"sku_id#${f}", udf1(col("sku_id"), lit(f))))
```

