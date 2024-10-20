

>
在scala中 使用object 创建文件 其默认为一个单例对象
可直接调用其内部的方法 相当于Java中的静态类 无法创建实例



在复制代码块时候 使用按住ALT

# Scala中的For循环

- #### 定义

  ```scala
  for(i <- 0 to 10){
  
  }
  ```



# Scala中的break

- #### 导包

  ```scala
  import scala.util.control.Breaks._
  ```

- #### 使用

  ```
  breakable
  {
  	for(a <- 0 to 999)
  	{
  		if(a == 4) 
  		{
  			break;
  		} 
  		else 
  		{
  			printf(a);
  		}
  	}
  }
  ```




# Scala中的内插字符串

- ### 使用

  ```scala
  var a = 5;
  printf(s"a的值为${a}");
  ```

  不能这样判断

  var a = "ea";

  var b = "e";

  var c ="a";

  a.eq(s"${ea}")



# 函数的定义

```scala
var 名称 = (参数名 : 参数类型) =>{
	方法体
}
```



# 样例类 case class

一般用于保存数据	

case class 名称<(var xx : xx)>{}

## 内部默认方法:

​	apply : 创建样例类无需new
​	toString : 打印各属性值
​	equals : 直接使用==比较两个对象的属性值是否相等
​	copy : 克隆对象

# 样例对象 case object

​	使用场景 : 
​		用作枚举值
​		作为没有任何消息参数的传递

## 如何用作枚举值:

​	1,定义一个特质(trait)
​	2,定义样例对象
​	对象名称 : 枚举的具体值
​	3,继承特质
​	4,创建一个需要使用该枚举值的对象
​	class stu(var name : String , var sex : [trait name])

# 数组 - Array

###### 固定数组

Array[泛型] 与Java不同 在Scala中，指定类型都是[]而不是<>

- 格式: 定义一个Int类型 长度为10的数组

```scala
var arr1 : Array[Int] = Array[Int](10);
```

- 获取指定索引的元素

```scala
arr1(0);
```

- 获取数组的长度 返回Int值 使用时候需要-1 因为数组默认从索引0开始

```
arr1.size();
```



###### 变长数组

- 格式: 定义一个String类型的变长数组

```scala
var arr1 : ArrayBuffer[String] = new ArrayBuffer[String]();
```

- 用法

  - += : 添加一个元素

  ```scala
  var bufferArrayname : ArrayBuffer[String] = new ArrayBuffer[String]();
  bufferArrayname += "李四";
  bufferArrayname += "李四";
  print(bufferArrayname.toString());
  ```

  会打印两个李四

  

  - -= : 删除一个元素

  ```scala
  var bufferArrayname : ArrayBuffer[String] = new ArrayBuffer[String]();
  bufferArrayname += "李四";
  bufferArrayname -= "李四";
  print(bufferArrayname.toString());
  ```

  只会打印一个李四

  

  - ++= : 追加一个数组

  ```scala
  def main(args: Array[String]): Unit = {
      var bufferArrayname : ArrayBuffer[String] = new ArrayBuffer[String]();
      var Arrayname : Array[String] = new Array[String](3);
      Arrayname(0) = "王五";
      Arrayname(1) = "张三";
      Arrayname(2) = "老虎";
      bufferArrayname += "花猫";
      bufferArrayname ++= Arrayname;
      print(bufferArrayname.toString());
  }
  ```

  结果 : ArrayBuffer(花猫, 王五, 张三, 老虎)

  

  - --= : 删除多个指定的元素

  ```scala
  def main(args: Array[String]): Unit = {
    var bufferArrayname : ArrayBuffer[String] = new ArrayBuffer[String]();
    var Arrayname : Array[String] = new Array[String](3);
    Arrayname(0) = "王五";
    Arrayname(1) = "张三";
    Arrayname(2) = "老虎";
    bufferArrayname += "花猫";
    bufferArrayname --= Arrayname;
    print(bufferArrayname.toString());
  }
  ```

  结果 : ArrayBuffer(花猫)



###### 遍历数组

```scala
for(i <- 0 to arr.size()-1){
	print(arr(i) + "\n");
}
```



###### 自带的方法

sum() 求和

max() 求最大值

min() 求最小值

sorted() 排序 -> 返回新的数组(升续)

​	如何降序 -> 先排序 再反转

reverse() 反转 -> 返回新的数组

# 元组 - Tupl

###### 创建元组数组

```scala
var tupl1 : Array[(String,Int)] = new Array[(String, Int)](2);
var tupl : Array[Tuple2[String,Int]]  = new Array[Tuple2[String, Int](2);
```

###### 使用元组数组

- 添加元素

  ```scala
  tupl(0) = ("王五",14);
  ```

- 获取元素

  ```scala
  tupl(0)._1
  tupl(0)._2
  ```
  
  根据创建，得到的类型是不同的。



# 列表 - List

- 创建列表 Int类型

  ```scala
  var list : List[Int] = List(1,23,4,5,6);
  ```

- 创建列表 空

  ```
  var list : List[Int] = Nil;
  ```

- 创建可变列表

  ```
  var list : ListBuffer[Int] = new ListBuffer[Int]();
  //添加内容
  list += 1;
  print(list);
  ```



###### 常见操作

| 格式             | 功能                   |
| ---------------- | ---------------------- |
| list(index)      | 获取指定索引的值（-1） |
| list(index) = x; | 将x赋值给指定索引      |
| +=               | 添加                   |
| -=               | 删除                   |
| ++=              | 追加列表               |
| --=              | 删除多个元素           |
| toList           | 转为不可变             |
| toArray          | 转为数组               |

| isEmpty   | 判空                                               |
| --------- | -------------------------------------------------- |
| ++        | 拼接列表返回返回新的列表                           |
| head      | 返回第一个元素                                     |
| tail      | 返回除了第一个外的其他元素(可以用于删除第一个元素) |
| zip       | 合并列表（拉链）                                   |
| unzip     | 拆分列表 （拉开）                                  |
| union     | 获取并集元素                                       |
| intersect | 获取交集元素                                       |
| diff      | 获取差集元素                                       |
| mkString  | list.mkString(".") 使用” . “ 隔开数据输出          |
| distinct  | 去重                                               |

- 扁平化

  将嵌套List 即List[List[Int]] 转换为一个List

  ```
  var list : list[<T>] = list.flatten;
  ```

  

- 拉链

  将两个List组合起来，索引一 一对应，形成一个元组列表

  ```scala
  def main(args: Array[String]): Unit = {
    var list:List[String] = List("张三","李四");
    var listage : List[Int] = List(24,23);
    var listnameAndage : List[(String,Int)] =  list.zip(listage);
    print(listnameAndage(0).toString());
  }
  ```

  打印的结果为 (张三,24)

​	

- 拉开

  将拉链列表拉开为一个元组 其中 元组的数据对应原列表的类型

  拥有被拉链后相同格式的list也可以

  ```scala
  var Tuple : Tuple2[List[String],List[Int]] = listnameAndage.unzip;
  var listName : List[String] = Tuple._1;
  print(listName)
  ```

  得到的结果 : List(张三, 李四)

  

- 并集

  list1.union(list2) 将list1和list2合并在一起 且不去重

  ```scala
  def main(args: Array[String]): Unit = {
    var list1 : List[Int] = List(1,2,3,4);
    var list2 : List[Int] = List(1,2,3,4,5);
    list1 = list1.union(list2);
    print(list1);
  }
  ```

  输出为List(1, 2, 3, 4, 1, 2, 3, 4, 5)



- 交集

  list1.intersect(list2) 获取list1和list2共有的部分

  

- 差集

  list1.diff(list2)获取list1有list2没有的



- 删除元素

  返回true是不删除

  ```scala
  list4 = list4.filter((p:String) => {
      if(p.size<=3){
          false;
      }else{
          true;
      }
  })
  ```

  

# 集 - Set Or Map

### Set

Set的读取顺序随机 数据唯一

- #### 不可变Set集

  ```scala
  def main(args: Array[String]): Unit = {
    var setname : Set[Int]  = Set(1,2,3,4,5,7,8,6,5,4,3,2);
    print(setname)
  }
  ```

  输出结果为 Set(5, 1, 6, 2, 7, 3, 8, 4)

- #### 操作

  | 标识符 | 结果                      |
  | ------ | ------------------------- |
  | -=     | 删除一个元素 返回新的集   |
  | +=     | 添加一个元素 返回新的集   |
  | ++     | 添加一个元素组 返回新的集 |
  | --     | 删除一个元素组 返回新的集 |
  | size   | 获取长度                  |

  

- #### 可变Set集

  创建可变Set集

  ```scala
  var set: Set[Int] = Set[Int]();
  ```

  执行操作

  ```scala
  def main(args: Array[String]): Unit = {
    var set: Set[Int] = Set[Int]();
    set += 1;
    set += 2;
    set += 3;
    set += 1;
    print(set)
  }
  ```

  输出为 : Set(1, 2, 3)



### Map

- #### 可变Map

  ##### Map本质上并不是集 而是一个键值对的映射

  ##### Map的键是唯一的，但是值是可以非唯一的

  for((k,v) <- map) 遍历只适用于 Scala自己的Map 不适用 Java下的

  创建可变Map

  ```scala
  var map : Map[Int,String] = Map[Int,String]();
  ```

- #### 操作

  | 标识符             | 结果                  |
  | ------------------ | --------------------- |
  | getOrElse(key,<T>) | 查找key 不存在返回<T> |
  | += key -> Value    | 添加                  |

- #### Map的遍历

  ```scala
  for((key,value) <- map)
  {
  	print(key,value)
  }
  ```

  

​	

# 迭代器 - Iterator

## Iterator

##### 	任何集合都有迭代器，通过 集合.iterator获取	

##### 	不能对迭代器的数据进行任何操作

- #### 获取迭代器

  ```scala
  var map : Map[Int,String] = Map[Int,String]();
  var inter : Iterator[(Int,String)]  = map.iterator;
  ```

- #### 迭代器的两个方法

  | 标识符  | 结果                            |
  | ------- | ------------------------------- |
  | hasNext | 如果有下个值返回True反之        |
  | next()  | 返回当前值 ！不能对该值进行操作 |

- #### 使用迭代器 + While循环遍历

  ```scala
  while(inter.hasNext){
    print(inter.next());
  }
  ```

​	



## foreach

​	任何集合都有一个foreach

- #### 使用foreach遍历一个Map集合

  使用 : foreach(函数)

  该函数为无返回值(Unit)

  

  当 这个参数在函数体中只使用一次 且没有其他操作 那么可以直接使用 " _ "

  ```scala
  def main(args: Array[String]): Unit = {
    var map : Map[Int,String] = Map[Int,String]();
    map += 1 -> "王五";
    map += 2 -> "李四";
    map += 3 -> "老朱";
    map.foreach(( kn : (Int , String)) => {
      println(kn)
    })
      
    //简化
    map.foreach(println(_));
  }
  ```

  

# 函数式编程

TraversableOnce -> 将数据转换为List

## map

- #### map是处理行为 将数据类型进行转换

- #### 使用

  ##### 1,定义一个集合

  ```scala
  var list1 : List[Int] = List(1,2,3,4,5,6);
  ```

  ##### 2,调用map方法

  ```scala
  var list2 = list1.map((a : Int)=> { "*" * a;})
  ```

  ##### 其原始定义 B为返回值

  ```scala
  def map[B](f:A => B):TraversableOnce[B]
  ```

  

## 扁平化映射 - flatMap

- #### 传统操作

  先map(元素转为List)  每个元素都转为一个List 最后存入一个 List

  再扁平化(flatten)

  ```scala
  var listst : List[String] = List("老王 老朱 老侯 老民","劳心 劳烦 劳累");
  var list2 : List[Array[String]] = listst.map((name : String) => {
    name.split(" ");
  })
  var list3 : List[String] = list2.flatten;
  println(list3);
  ```

​	输出结果 : List(老王, 老朱, 老侯, 老民, 劳心, 劳烦, 劳累)

- #### 扁平化映射

  ```scala
  var list4 = listst.flatMap((f : String)=>{
    f.split(" ");
  })
  print(list4);
  ```

​	输出结果 : List(老王, 老朱, 老侯, 老民, 劳心, 劳烦, 劳累)



## 过滤 - filter

使用上面的listst

```scala
var listst : List[String] = List("老王 老朱 老侯 老民","劳心 劳烦 劳累 老贼");
var list2 = listst.flatMap((a : String) =>{
  a.split(" ");
})
```

进行过滤，只需要'老'开头的数据

```scala
var list3 = list2.filter((a : String) =>{
  a.charAt(0).equals('老');
})
print(list3);
```

输出结果为：List(老王, 老朱, 老侯, 老民, 老贼)



## 排序

### 升序 - sorted

```scala
var list1 : List[Int] = List(1,2,3,4,5,6,65,5,4,63,46,3,64,3,347,3,7); //定义 不可变数组
var list2 = list1.sorted; //排序
println("升序: " + list2); //输出
print("降序: " + list2.reverse); //反转输出
```

输出结果为:

​	升序: List(1, 2, 3, 3, 3, 3, 4, 4, 5, 5, 6, 7, 46, 63, 64, 65, 347)
​	降序: List(347, 65, 64, 63, 46, 7, 6, 5, 5, 4, 4, 3, 3, 3, 3, 2, 1)



### 指定字段排序 - stortby

原始 A = 原始数据类型，B = 需要比对的数据类型

```
def sortBy[B](f:(A) => B):List[A]
```



有一个List 对他的开头数字大小进行排序

```scala
var listst : List[String] = List("08老王 09老朱 03老侯 04老民 05劳心 07劳烦 04劳累");
```

```scala
var list2 : List[String] = listst.flatMap((a : String)=>{
  a.split(" ");
})	//进行扁平化映射
var list3 = list2.sortBy[Int]((a : String)=>{
  var stb : StringBuilder = new StringBuilder();
  stb += a.charAt(0);
  stb += a.charAt(1);
  stb.toInt;
})//比对的是最后一行语句产生的数值
print(list3);
```

输出结果 : List(03老侯, 04老民, 04劳累, 05劳心, 07劳烦, 08老王, 09老朱)



## 自定义排序 - sortWith

- ##### 原始

  ```scala
  def sortWith(f:(A,A) => Boolean) : List[A]
  ```

- ##### 说明

  | sortWith方法 | 说明                                                         |
  | ------------ | ------------------------------------------------------------ |
  | 参数         | 传入一个比较大小的函数对象<br />接收两个集合类型的元素参数<br>返回两个元素的大小，小于返回true，大于返回false |
  | 返回值       | 返回排序后的列表                                             |

- ##### 使用自定义排序将指定字段排序的内容完成一遍

  ```scala
  object sortwhit_ {
    def main(args: Array[String]): Unit = {
      var listst : List[String] = List("08老王 09老朱 03老侯 04老民 05劳心 07劳烦 04劳累");
      var list2 : List[String] = listst.flatMap((a : String) => {
        a.split(" ");
      })
      var list3 : List[String] = list2.sortWith((a,b)=>{
          //去除空格
        var a1 = a.trim;
        var b1 = b.trim;
          
          //变长字符串 减轻内存占用
        var stba : StringBuilder = new StringBuilder();
        var stbb : StringBuilder = new StringBuilder();
        Breaks.breakable {
            //截取字符串a
          for (i <- 0 to a1.length) {
            if (pdnum(a1.charAt(i))) {
              stba += a1.charAt(i);
            } else {
              Breaks.break();
            }
          }
        }
        Breaks.breakable {
            //截取字符串b
          for(i <- 0 to b1.length){
            if(pdnum(b1.charAt(i))){
              stbb += b1.charAt(i);
            }else{
              Breaks.break();
            }
          }
        }
        
          //容错代码
        if(stba.size == 0){
  		  stba +='0';
  		}
  	  if(stbb.size ==0){
      	  stbb += '0';
    		}
          
          //最终决定
        if(stba.toString().toInt < stbb.toString().toInt) {
            true;
        } else {
            false;
        }
      })
      print(list3);
    }
      
    //定义一个方法 判断字符是否为数字
    def pdnum(chare : Char): Boolean = {
      if(chare.equals('0') || chare.equals('1') || chare.equals('2') || chare.equals('3') ||
        chare.equals('4') || chare.equals('5') || chare.equals('6') || chare.equals('7') ||
        chare.equals('8') || chare.equals('9') ){
        return true;
      }else{
        return false;
      }
    }
  }
  ```




## 分组 - groupBy

- #### 分组指数据按照指定格式进行分组

  #### 格式 - K代表要以什么进行分组

  ```scala
  def gropBy[K](f:(A) => K):Map[K,List[A]]
  ```

- #### 说明

  | groupBy方法          | 解释                                                         |
  | -------------------- | ------------------------------------------------------------ |
  | [K]                  | 分组字段                                                     |
  | f(A) => K            | 传入一个函数<br>接收集合元素类型的参数<br>将K最为Key进行分组 |
  | 返回值Map[K,List[A]] | 返回Map，分组字段作为Key 分组结果作为Value                   |

  

- #### 使用

  (不知道为什么值会是一个Some包裹的List，所以需要使用get进行取值)

  ```scala
  //创建元组集
  var list1 = List("老王" -> "女","老卢" -> "男","老铭"-> "男","老胜" -> "女","老阿" -> "女");
  var list2 = list1.groupBy((f : (String,String)) =>{
      f._2; //按照字段二进行分组
  })
  
  //输出列表内容
  println(list2);
  
  //获取各人数
  print("男: " + list2.get("男").get.size + "  " + "女: " + list2.get("女").get.size);
  ```

  输出 : 

  ​    Map(男 -> List((老卢,男), (老铭,男)), 女 -> List((老王,女), (老胜,女), (老阿,女)))
  ​    男: 2  女: 3



## 聚合 - reduce

- 聚合：指的是将一个列表中的数据合并为一个 元素(变量)

- reduce:用来对集合元素进行合并

  fold:用来对集合元素进行折叠

- #### 格式

  传入参数必须是同类或者是对方父类，不能是下级类

  ```
  def reduce[A1 >:A](op:(A1,A1) => A1):A1
  ```

  

- #### 说明

  | reduce方法       | 说明                                                         |
  | ---------------- | ------------------------------------------------------------ |
  | [A1 >:A]         | A1必须是集合元素类型的父类，或者和集合类型相同.              |
  | op:(A1,A1) => A1 | 传入函数对象，进行聚合<br>第一个A1：当前聚合后的变量<br>第二个A1:   当前要进行聚合的元素 |
  | return A1        | 聚合后的元素                                                 |

  

```scala
def main(args: Array[String]): Unit = {
    var list1 = List("老王" -> "女","老卢" -> "男","老铭"-> "男","老胜" -> "女","老阿" -> "女");
    
    //进行分组
    var list2 = list1.groupBy((f : (String,String)) =>{
        f._2;
    })
    var list3:ListBuffer[(String,String)] = new ListBuffer[(String,String)]();
    for((k,v) <- list2) {
        //表合并
        list3 ++= v;
    }
    
    //将数据载入list4
    var list4:ListBuffer[String] = new ListBuffer[String]();
    list3.foreach((f:(String,String))=>{
        var sb = new StringBuilder();
        sb++=f._1 ++=f._2;
        list4 += sb.toString();
    })
    
    //聚合
    list4 += list4.reduce((f:String,e:String)=>{
        var sb =new StringBuilder();
        
        //删除多余元素 确保表内只有一个元素
        for(i <-f.length-3 to f.length -1){
            sb += f.charAt(i);
        }
        list4 -= sb.toString();
        f + e;
    })
    
    //list.drop(1) 删除表的前一个元素
    
    //另一种删除方法
    list4 = list4.filter((p:String) => {
        if(p.size<=3){
            false;
        }else{
            true;
        }
    })
    print(list4)
}
```

输出结果为 : ListBuffer(老卢男老铭男老王女老胜女老阿女)



## 折叠

scan

与聚合类型 多了个初始化值

```scala
def fold[A1 >: A](z : A1)(op:(A1,A1) => A1):A1

//简写
def fold(初始化值)(op:A1,A1 => A1)
```

fold = foldLet 表示从左往右计算

foldRight 表示从右往左计算

- #### 示例

  ```scala
  def main(args: Array[String]): Unit = {
      var listst : ListBuffer[String] = ListBuffer("老王","老朱","老侯","老民","劳心","劳烦" ,"劳累");
      listst += listst.fold("姓名集:")((e : String, e1 : String) => {
          e + " " +  e1;
      })
      listst = listst.drop(listst.size - 1);
      print(listst)
  }
  ```

  输出结果 : ListBuffer(姓名集: 老王 老朱 老侯 老民 劳心 劳烦 劳累)

## 函数式编程 - 列表练习

- #### 要求 :

  - ​	已有列表 分别为 学生姓名 语文 数学 英语

  - ​	获取语文成绩大于等于60分的
  - ​	获取其总分
  - ​	按总成绩降序排列
  - ​	输出

```scala
def main(args: Array[String]): Unit = {
    //分别为 学生姓名 语文 数学 英语
    var list : ListBuffer[Tuple4[String,Int,Int,Int]] = new ListBuffer[Tuple4[String,Int,Int,Int]]();
    list += Tuple4("张三",37,90,100);
    list += Tuple4("李四",90,73,81);
    list += Tuple4("王五",60,90,76);
    list += Tuple4("赵六",59,21,72);
    list += Tuple4("田七",100,100,100);
    //要求获取语文大于60(含)
    list = list.filter((p:(String,Int,Int,Int)) =>{
        p._3 >= 60;
    })
    var listadd:ListBuffer[(String,Int)] = new ListBuffer[(String,Int)]();
    list.foreach((f:(String,Int,Int,Int)) =>{
        listadd += Tuple2(f._1,(f._2+f._3+f._4));
    })
    //按照总成绩降序排列
    listadd = listadd.sortBy((e : (String,Int)) => {
        e._2
    })
    //降序
    listadd = listadd.reverse;
    print(listadd)
}
```

输出结果 ： ListBuffer((田七,300), (李四,244), (张三,227), (王五,226))



# 模式匹配 - match

## 简单模式匹配

- #### 格式 与常量值相同 执行表达式 全false 执行 _

  ```scala
  变量名 match{
  	case "常量1" => 表达式;
  	case "常量2" => 表达式;
  	case "常量3" => 表达式;
  	case "常量4" => 表达式;
  	case _ => 表达式; //默认匹配项
  }
  ```

  - #### 示例

    ```scala
    def main(args: Array[String]): Unit = {
        println("请输入:");
        var str = StdIn.readLine();
        str = str match{
            case "你好" => "我很好";
            case "你" => "好";
            case _ => "没选项";
        }
        println(str);
    }
    ```

    输入 : 你好

    输出 : 我很好

## 简单模式匹配 - 类型匹配

- #### 格式与简单模式一样

  ```scala
  变量名 match{
  	case "常量1" : Type => 表达式;
  	case "常量2" : Type => 表达式;
  	case "常量3" : Type => 表达式;
  	case "常量4" : Type => 表达式;
      case _ : Type => 表达式
  	case _ => 表达式; //默认匹配项
  }
  ```

  

## 模式匹配 - 守卫

- #### 在 case 语句中添加 if 语句

  ```scala
  变量名 match{
  	case "常量1" : Type if => 表达式;
  	case "常量2" : Type if => 表达式;
  	case "常量3" : Type if => 表达式;
  	case "常量4" : Type if => 表达式;
      case _ : Type if => 表达式
      case _ : Type => 表达式
  	case _ => 表达式; //默认匹配项
  }
  ```

  

## 模式匹配 - 匹配样例类

- #### 格式

  ```scala
  对象名 match{
  	case 样例类型(字段1,字段2,字段3....字段n) => 表达式;
  	....
  	case _ => 表达式;
  }
  ```

  样例类型后面跟的字段必须与样例类一致

  对象名的类型必须为 Any

- #### Dome

  ```scala
  def main(args: Array[String]): Unit = {
      var yl01 : Any = new Cust("李四",12);
      //模式匹配
      yl01 match {
          case Cust(name,age) if(age == 12)=> println("Cust类型 12岁");
          case Ord(id) => printf("Ord类型")
          case _ => println("不匹配");
      }
  }
  ```

  输出 : Cust类型 12岁



## 模式匹配 - 集合

### 模式匹配 - 数组

- #### 格式

  ```scala
  对象名 match {
  	case Array(0) => 表达式; //单个元素并为0
  	case Array(_*) => 表达式; //是个数组就行
  	case Array(0,_*) => 表达式; //开头为0 长度不限制
  	case _ => 表达式;
  }
  ```

- #### 使用

- 这里定义了一个方法 不然一个对象写一个match  可以直接填入传入对象 或 反射获取Name

  ```scala
  object Test_1 {
      def main(args: Array[String]): Unit = {
          var array1 = Array(1,2,3,4);
          ppei(array1);
      }
      def ppei(array: Array[Int]) = {
          array match {
              case Array(1,_*) => printf("数组");
              case Array(1,x,y) => println("长度为3 开头为1");
              case _ => printf("不匹配");
          }
      }
  }
  ```

  输出 数组

### 模式匹配 - 列表

使用方法跟数组一样 把 Array改成List



### 模式匹配 - 元组

使用方法跟数组一样 把Array删掉

- ### 格式

  ```scala
  对象名 match {
  	case (1,x,y) => 表达式;
  	case (1,1,1) => 表达式;
  	case (_*) => 表达式;
  }
  ```

  

## 模式匹配 - 变量声明中的模式匹配

快速从数组中获取数据 定义变量xyz 从array1中匹配赋值

```scala
def main(args: Array[String]): Unit = {
    var array1 = (0 to 10).toArray;
    var Array(_,x,y,z,_*) = array1;
    println(x,y,z)
}
```

输出结果 :  (1,2,3)



## 模式匹配 - For表达式

匹配 value 为12

```scala
for((k,12) <- Map);

//等同于
for((k,v) <- Map if(v == 12));
```

```scala
var map1 = Map[String,Int]();
map1 += "李四" -> 12;
map1 += "王五" -> 12;
map1 += "沥青" -> 13;
for((k,12) <-  map1){
    println(k);
}
```

输出 李四 \n 李四 \n



# Option类型

- ## 两个值

- None -> 空

- Some(x) -> 实际值

  

- ### 使用

  ```scala
  def chushu(a : Int , b : Int) = {
      if(b == 0){
          None;
      }
      else
      {
          Some(a / b);
      }
  }
  def main(args: Array[String]): Unit = {
      var e = chushu(1,0);
      e match {
          case None => println("除数为0");
          case Some(x) => println(s"结果为: ${x}" )
      }
  }
  ```

  结果为 ： 除数为0



# 偏函数

- ### 定义

  ```scala
  var/val 函数名 : PartialFunction[传入类型,传出类型] = {
  	case 常量1 => 返回值;
  }
  ```

  

```scala
object Test_5 {
    def main(args: Array[String]): Unit = {
        var sum : PartialFunction[Int,String] = {
            case 1 => "一" ;
            case 2 => "二";
            case 3 => "三";
            case _ => "无";
        }
        println(sum(1));
        println(sum(2));
        println(sum(4));
    }
}
```

输出 : 一 \n二 \n无 \n



数组/集合 结合偏函数 -> .map{} 直接表示是一个偏函数

```scala
def main(args: Array[String]): Unit = {
    //定义一个列表 1-10
    /*
    * 将1-3转为[1-3] String
    * 4-8同上
    * 其他转换为 (8-*]
    * */
    
    var arr1 = arr.map{
    case x if x>=1 && x<=3 => "[1-3]";
    case x if x>=4 && x<=8 => "[4-8]";
    case _ => "(8-*]";
	}
	
	//遍历
    arr1.foreach((f : String)=>{
        println(f);
    })
}
```

​	输出 : "[1-3]""[1-3]""[1-3]""[4-8]""[4-8]""[4-8]""[4-8]""[4-8]""(8-*]""(8-*]""

# 正则表达式

- ##### 匹配表达式

- ##### Scala中提供了Regex类来定义

- ##### 直接使用 String.r 转

| .                | 这里至少有一个字符                 |      |
| ---------------- | ---------------------------------- | ---- |
| +                | 前面的字符至少出现过一次或者无数次 |      |
| 任意字符         | 表示这里必须出现这个字符           |      |
| \                | 转义符                             |      |
| findAllMatchIn() | 校正，获取所有满足regex的内容      |      |
| (.+)             | 分组                               |      |
| \s               | 表示空字符 (空格,\t\r\n等)         |      |

- #### 示例

  ```scala
  def main(args: Array[String]): Unit = {
      var regex = """.+@.+\..+""".r;
      var email = "234111@de.com";
      
      var e = regex.findAllMatchIn(email);
      if(e.size != 0){
          print("可以");
      }
  }
  ```

  输出 -> 可以

- #### 筛选合法邮箱

  ```scala
  def main(args: Array[String]): Unit = {
      var listm : ListBuffer[String] = new ListBuffer[String]();
      listm += "38219283@qq.com";
      listm += "adwa2846@gmail.com";
      listm += "zhansan@163.com";
      listm += "123waf.com";
      
      //定义正则
      var regex = """.+@.+\..+""".r;
      
      //筛选
      listm = listm.filter((p:String) => {
          !(regex.findAllMatchIn(p).size == 0);
      })
      
      print(listm);
  }
  ```

  输出 : ListBuffer(38219283@qq.com, adwa2846@gmail.com, zhansan@163.com)

- ##### 	获取邮箱运营商

  ```scala
  listm = listm.map((s : String) =>{
      s.split("@")(1)
  })
  print(listm);
  ```

  ```scala
  var regex: Regex = """.+@(.+)\..+""".r;
  ```

  ```scala
  var list2 = listm.map{
      case x @ regex(r) => x -> r;
      case x => x -> "未匹配";
  }
  print(list2);
  ```

  输出 ListBuffer((38219283@qq.com,qq), (adwa2846@gmail.com,gmail), (zhansan@163.com,163), (123waf.com,未匹配))



# 异常处理

- ### 格式

  这样程序会继续执行

  ```scala
  try{
  //会出问题的
  }catch{
  	case ex:异常类型 => 代码
  }finally{
  	//无论如何都触发
  }
  ```

  直接抛出

  ```scala
  throw new Excepiton("描述")；
  ```

  

# 提取器 - Extractor - unapply()

- ##### 创建一个类

- ##### 为他创建一个伴生对象

- ##### 在伴生对象内实现 apply()和unapply()方法

- ##### apply()用于封装

- ##### unapply()用于解

  ```scala
  //创建一个类
  class Studen(var name : String,var age : Int) { }
  //创建它的伴生对象
  object Studen {
      
      //实现apply方法
      def apply(name: String, age: Int): Studen = new Studen(name, age);
  
      //实现unapply方法
      def unapply(s: Studen) = {
          if (s == null) {
              None;
          } else {
              Some(s.name, s.age);
          }
      }
  }
  ```

  ```scala
  object Test_ {
      //创建入口
      def main(args: Array[String]): Unit = {
          var student : Studen = new Studen("梨花",13);
          //创建模式匹配
          student match{
              case Studen(name,age) => print(name,age);
              case _ => print("错");
          }
      }
  }
  ```

  输出 : (梨花,13)



# IO流 - 数据读取与写入

- ### 导包 : scala.io.Source

## 读取文本字符

| formFile(url,textb) | 获取文件,以及编码方式    |
| ------------------- | ------------------------ |
| getLines();         | 获取按行读取的一个迭代器 |
| buffered            | 获取以字符为单位的迭代器 |
| mkString            | 获取全部文本内容         |
| close               | 关闭流                   |

```scala
def main(args: Array[String]): Unit = {
    //关联源文件
    var sou = Source.fromFile("src/IOStrmer/res.text","UTF-8");
    //获取迭代器
    var souinter = sou.getLines();
    var list = new ListBuffer[String]();
    //遍历并存入
    while(souinter.hasNext) {
        list += souinter.next();
    }
    //输出集合
    print(list)
    sou.close();
}
```

```scala
def main(args: Array[String]): Unit = {
    //关联源
    var sou = Source.fromFile("src/IOStrmer/numt");
    
    var st = sou.mkString;//获取文件内全部内容
    
    //s
    var sts:Array[String] = st.split("\\s+");
    
    //转为Int
    var stsint = sts.map((f:String) =>{
        f.toInt;
    })
    for(i <- 0 to stsint.length - 1){
        println(stsint(i));
    }
}
```



## 读取URL

| fromURL | 传入一个URL 关联 |
| ------- | ---------------- |

## 读取二进制文件

| java.io.file |      |
| ------------ | ---- |

```scala
def main(args: Array[String]): Unit = {
    //获取文件
    var file = new File("src/IOStrmer/numt2");
    //创建文件输入流
    var files = new FileInputStream(file);
    //创建数组存储内容
    var bye = new Array[Byte](file.length().toInt);
    //写入内容
    files.read(bye);
    //关闭文件输入流
    files.close();
    
    var file2 = new File("src/IOStrmer/numt3");
    //创建这个文件
    file2.createNewFile();
    var files2 = new FileOutputStream(file2);
    files2.write(bye);
    files2.close();
}
```



## 序列化 - Serializable



```scala
//若该类需要进行序列化 需要继承 Serializable
class Student(var name:String,var age:Int) extends Serializable{
}

def main(args: Array[String]): Unit = {
    //创建对象
    var stu = new Student("利好",23);
    //创建对象序列化输出流
    var oos = new ObjectOutputStream(new FileOutputStream("src/IOStrmer/numt6.txt"));
    //输出
    oos.writeObject(stu);
    oos.close();
    
    //反序列化
    var ois =new ObjectInputStream(new FileInputStream("src/IOStrmer/numt6.txt"));
    //输入并进行类型转换
    var stu2 : Student = ois.readObject().asInstanceOf[Student];
    ois.close();
    print(stu2.name + stu2.age);
}
```

```scala
def main(args: Array[String]): Unit = {
    //创建学生列表 动态
    var list = ListBuffer[Student]();
    //读取文件
    var sou = Source.fromFile("src/IOStrmer/Student.txt");
    var int = sou.getLines();
    while(int.hasNext){
        var st =int.next();
        var strings : Array[String] = st.split("""\s+""");
        list+= new Student(strings(0),strings(1).toInt,strings(2).toInt,strings(3).toIn
    }
                           
    //排序
    list.sortWith((s1 : Student ,s2:Student)=>{
        s1.add() > s2.add();
    })
	
   	//创建写入文件
    var file = new File("src/IOStrmer/stu.txt");
    file.createNewFile();
    
    //创建输出缓冲流
    var fos = new BufferedOutputStream(new FileOutputStream(file));
    //创建存储数据
    var arr : ArrayBuffer[Byte] = new ArrayBuffer[Byte]();
                           
    //存入数组
    list.foreach((f:Student) =>{
        var sb = new StringBuilder();
        sb ++= "姓名: " ;sb ++= f.name.toString;
        sb ++= "语文成绩: ";sb++=f.Chinese.toString;
        sb ++= "数学成绩: ";sb++=f.math.toString;
        sb ++="英语成绩: ";sb++=f.English.toString;
        sb ++= "\r\n";
        arr ++=sb.toString.getBytes();
    })
    //写入
    fos.write(arr.toArray);
    fos.flush();
    fos.close();
    sou.close();
}
case class Student(var name : String,var Chinese:Int,var math:Int,var English:Int){
    def add(): Int = {
        Chinese + math + English
    }
}
```



# 高级函数

### 作为值的函数

```scala
var 变量名 = (参数名 : 参数类型) => {方法体}
```

- #### 调用以及使用 传参需要函数的都可以放入

  ```
  list.map(变量名);
  ```

  

### 匿名函数

匿名函数指 没有命名的函数

上面使用map() 传入的参数就是一个匿名函数



### 柯里化

```scala
原 : def func(x:Int,y:Int) = x + y
柯里化 : def func(x:Int)(y:Int) = x + y
原 : func(1,1)
柯里化 : func(1)(1)
```

```scala
def func(x:Int)(y:Int) = x + y
//底层
def func1(x:Int) = {
	(y:Int) => x+y
}
```

- 柯里化拼接字符串

  ```scala
  def func(str1:String,str2:String)(fun : (String,String) => String) = fun(str1,str2);
  var str1 = "abc";
  var str2 = "xyz";
  str1 = func(str1,str2)((str1,str2) => str1+str2); ||
  str1 = func(str1,str2)((str1,str2) => {
      str1 + str2;
  })
  printf(str1);
  ```

  输出 abcxyz



### 闭包

- ##### 可以访问不在当前作用域范围数据的一个函数

  ```scala
  var y = 10;
  var sum = (x : Int)=>{
  	x + y;
  }
  printf(sum(20))
  ```

  最后输出为 30

  def定义方法，不使用def定义函数



### 控制抽象

- ##### 也是函数中的一种，可以更灵活的使用函数

- ##### 假设函数A的参数列表需要接受一个函数B，B没有输入值也没有返回值 那么它就是控制抽象函数

- ##### 如果一个函数的参数列表接收了一个无参无返回值的函数 那么它就是一个控制抽象函数

- ### 例子

```scala
def main(args: Array[String]): Unit = {
   	//定义一个函数
    var funct = (fun01 : () => Unit) =>{
        println("开始");
        //中途调用
        fun01();
        println("结束");
    }
    funct(() =>{
        println("1");
        println("2");
        println("3");
        println("4");
        println("8");
        println("12");
    })
}
```

输出结果 => "开始" \n "1" \n "2" \n ... "结束" \n



### 柯里化练习

```scala
def sum(num1:Double,num2:Double)(fun : (Double,Double) => Double) = fun(num1,num2)
var a1 = 1.2;
var a2 = 1.3;
var sum1 = sum(a1,a2)((a1,a2) => a1 + a2);
```



# 隐式转换 - implicit

- ##### 隐式转换 : 指的是用implicit关键字声明的带有单个参数的方法

- ##### 隐式参数 : 指得是用implicit关键字修饰的变量

- ##### 是使用的地方要用import关键字导包

- ##### 是用来将一种数据转换为另一种数据的

- #### 使用步骤

  1. 在object中定义单例对象 进行隐士转换 使用implitcit关键字

  2. 在需要用到的地方使用import关键字导

```scala
//定义一个类 用来实现原没有的方法
class toRed(file : File){
  def read = Source.fromFile(file).mkString;
}
//定义一个单例对象 用于静态调用
object refile {
  implicit def fietored(file: File) = new toRed(file);
}
def main(args: Array[String]): Unit = {
  //手动导入
  import refile.fietored
  var file = new File("src/main/scala/implicit_/内容.txt");
  println(file.read);
}
```

## 隐式转换的时机

- #### 调用对象中不存在的方法时，就会调用

## 隐式转换方法的自动调用

- ##### 在作用域范围内定义 即可自动调

```scala
class Fileread(file : File){
    def read = Source.fromFile(file).mkString;
}

def main(args: Array[String]): Unit = {
    implicit def filere(file : File) = { new Fileread(file)}
    var file : File = new File("src/main/scala/implicit_/内容.txt");
    println(file.read);
}
```

## 隐式参数

- #### 使用implicit关键字标记参数列表，调用方法时这个参数列表可以不给初始化值。编译器会去找缺省值，给这个方法

```scala
//定义一个show方法 实现功能
def show(name : String)(implicit spstr : (String,String)) = 
    spstr._1 + name + spstr._2;
}
//定义一个单例对象 决定隐式值
object spstr_defa{
    implicit var str : (String,String) = ("<<<",">>>");
}
def main(args: Array[String]): Unit = {
    //导入隐式值
    import spstr_defa.str;
    println(show("你好"));
}
```

- #### 放在作用域内 自动导入

  ```scala
  //自动导
  def show(name: String)(implicit spiltstr : (String,String)) = {
      spiltstr._1 + name + spiltstr._2;
  }
  def main(args: Array[String]): Unit = {
      implicit var spilt_str:(String,String) = ("<<<" , ">>>");
      println(show("你好"));
  }
  ```

  ## 

# 综合案例

```scala
class List_sum[A](list : ListBuffer[A]){
    
    //定义方法
    def list_zip() = {
        //使用类型进行模式匹配
        list(0).getClass.getTypeName match {
            //如果是int类型就走求和
            case int => {
                //创建Int集合存储传入为 泛型
                var e : ListBuffer[Int] = new ListBuffer[Int];
                //进行类型转换
                list.foreach((f : A) => {
                    e += f.asInstanceOf[Int];
                })
                //累加数
                var sum = 0;
                //遍历求和
                e.foreach((f: Int) => {sum += f})
                Some(sum);
            }
            case _ => None;
        }
    }
}
object ListbuToList_sum {
    //创建隐式
    implicit def listbutolistsum(list : ListBuffer[Int]) = {
        new List_sum[Int](list);
    }
}
def main(args: Array[String]): Unit = {
    //导包
    import ListbuToList_sum.listbutolistsum
    var list : ListBuffer[Int] = new ListBuffer[Int]();
    list ++= (0 to 20).toList;
    println(list.list_zip());
}
```



# 递归

## 格式

- ```
  def show() : Int ={
  	show();
  }
  ```

  - ##### 必须要有一个出口

  - ##### 不能递归构造函数

  - ##### 递归需要有规律

  - ##### 递归需要有数据类型的返回值

  - ##### 递归就是我传给你你传给我

## 运行逻辑

- ##### 先进行载入，然后反向输出(栈) 即 -> 传入后先不进行处理 而是遇到出口时候，按照先进后出



## 例子

### 求阶乘

5! = 5 * 4!

n! = n * (n-1)!

```scala
def show(sum : Int):Int ={
    if(sum == 1) {return 1}
    else{
        sum * show(sum - 1);
    }
}
```

![image-20241019143718913](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20241019143718913.png)

### 斐波那契数列

```scala
//斐波那契数列
//已知数列 1 1 2 3 5 8 13 21
//第三个月开始 数字就是前面两数之和
def show(num : Int): Int = {
    if(num == 1 || num == 2) {
        return 1;
    }else{
        return (num - 1) + show(num - 2);
    }
}
def main(args: Array[String]): Unit = {
    println(show(12));
}
```



### 文件夹遍历

自写 30min

```scala
def Fileruns(file : File): Unit = {
    //如果是文件就直接打印
    if(file.isFile){
        println(file.getAbsoluteFile)
    }else{
        //不是就遍历并递归
        var f = file.listFiles();
        f.foreach((f:File) =>{
            Fileruns(f);
        })
    }
}
def main(args: Array[String]): Unit = {
    Fileruns(new File("C:/安装包"));
}
```

```scala
def Fileruns(file: File): Unit = {
    if (!file.isDirectory || file.listFiles().length == 0) {
    }
    else {
        var f = file.listFiles();
        for (fil <- f) {
            if (fil.isFile) {
                println(fil.getAbsoluteFile)
            }
            else {
                Fileruns(fil);
            }
        }
    }
}
def main(args: Array[String]): Unit = {
    Fileruns(new File("C:/Program Files/JetBrains"));
}
```



# 泛型

## 使用 - 泛型方法

```scala
//泛型方法
def len[A](list : List[A]) : A ={list(list/2)}
```

- #### 取中间值(泛型方法)

  ```scala
  def main(args: Array[String]): Unit = {
      var list = List[Int](10);
      list = (0 to 10).toList;
      println(midder(list));
  }
  def midder[T](list : List[T]) ={
      list(list.length/2);
  }
  ```

## 使用 - 泛型类

- #### 在创建对象的时候明确具体类型

  ```scala
  def main(args: Array[String]): Unit = {
      var e = new run(5,"2");
      println(e.a.getClass.getTypeName);
      println(e.b.getClass.getTypeName);
  }
  class run[T,V](var a : T,var b : V){
  }
  ```

```scala
输出:
int
java.lang.String
```



## 使用 - 泛型特质

```scala
trait orn[T]{
    var ore : T;
    def show;
}

//继承的时候需要确定类型
class orn_new(a : String) extends orn[String]{
    override var ore: String = a;
    override def show : Unit = println(ore);
}
def main(args: Array[String]): Unit = {
    new orn_new("利好").show;
}
```

输出

```scala
"利好"
```



## 上下界

### 上界

- #### 使用 该类 必须继承V或者就是V

  ```scala
  [T <: V]
  ```

  ```scala
  //创建Person类 私有化空构造
  class Person private{
      var name : String = "";
      var age : Int = 0;
      def this(name : String,age : Int) = {
          this();
          this.name = name;
          this.age = age;
      }
  }
  
  //创建一个类 继承 Person
  class Studnet(var a : String,var b : Int) extends Person(a,b){
  }
  
  //定义泛型方法 指定类型必须继承Person或者为本身
  def array[T <: Person](e : Array[T]): Unit = {
      for(p <- e){
          println(p);
          println(p.age + " " + p.name)
      }
  }
  def main(args: Array[String]): Unit = {
      //创建Person动态集合
      var listPer : ArrayBuffer[Person] = new ArrayBuffer[Person];
      listPer += new Person("2",3);
      listPer += new Person("3",4);
      //转换可接收的类型
      var listBuPer = listPer.toArray;
      array(listBuPer);
      
      //创建Student动态集合
      var listStu : ArrayBuffer[Studnet] = new ArrayBuffer[Studnet];
      listStu += new Studnet("2",3);
      listStu += new Studnet("3",4);
      //转换为可接收的类型
      var listBuStu = listStu.toArray;
      array(listBuStu);
  }
  ```

- 输出

  ```scala
  classall.upClass$Person@11531931
  3 2
  classall.upClass$Person@45c8e616
  4 3
  classall.upClass$Studnet@4cdbe50f
  3 2
  classall.upClass$Studnet@66d33a
  4 3
  ```

### 下界

- #### 与上界相反 需要是指定类型的父类

  ##### 既有上界 又有下界 => [T >: 类型 <:类型] 上界在前 下界在后

- ##### 非变         T与V之间有父子关系 但是T与V没有任何关系

- ##### 协变 	+        T与V之间有父子关系 T与V也有父子关系

- ##### 逆变	 -         父类变子类 子类变父类       T与V有父子关系，之前是父子关系 之后是子父关系

# 案例 : 排序去重

要求:

​	有一个文件，里面有数字 一个数字占一行，要求对里面的数据进行排序后输出到新的文件

代码位置 : C:\LiMGren\codeor\scalatest\Test\src\main\scala\classall\Dome.scala

```scala
def main(args: Array[String]): Unit = {
    //定义数据源
    var source = Source.fromFile("src/main/scala/classall/1.txt");
    //读取所有数据
    var string: String = source.mkString;
    //分割
    var list1 : List[String] = string.split("""\s+""").toList;
    //转Int
    var list2 : List[Int] = list1.map((f : String) =>{f.toInt});
    //去重 排序
    list2 = list2.toSet.toList.sorted;
    //创建写入
    var bw = new BufferedWriter(new FileWriter("src/main/scala/classall/2.txt"));
    //写入
    list2.foreach((f:Int)=>{
        bw.write(f.toString);
        bw.newLine();
        bw.flush();
    })
    bw.close();
}
```



# 集合 - Traversable 特质

### 创建

Traversable本身是特质，所以创建会调用底层实现

- 默认的Traversable是定长的

- #### 空的

```scala
//通过empty方法创建
var t1 = Traversable.empty[T]
//通过小括号创建
var t2 = Traversable[T]();
//通过Nil创建
var t3 = Nil;
```

- #### 带参

```scala
var t1 = List(1,2,3).toTraversable;
var t2 = Traversable(1,2,3);
```



### 转置 - transpose

- #### 示例

- 1  4  7  2  5  8  3  6  9

- 1  2  3  4  5  6  7  8  9

  | 1    | 2    | 3    |
  | ---- | ---- | ---- |
  | 4    | 5    | 6    |
  | 7    | 8    | 9    |
  |      |      |      |
  | 1    | 4    | 7    |
  | 2    | 5    | 8    |
  | 3    | 6    | 9    |

  ```scala
  def main(args: Array[String]): Unit = {
      var t1 : Traversable[Traversable[Int]] = Traversable(Traversable(1,4,7),Traversable(2,5,8),Traversable(3,6,9));
      t1 = t1.transpose;
      println(t1);
  }
  ```

- 输出

  ```scala
  List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9))
  ```

  

### 拼接 - concat

- ##### 使用++=会产生大量的内存浪费 使用concat可以防止

```scala
def main(args: Array[String]): Unit = {
    var t1 = Traversable(1,2,3);
    var t2 = Traversable(465,123);
    var t3 = Traversable(98,12,4);
    t1 = Traversable.concat(t1,t2,t3);
    println(t1);
}
```

输出

```scala
List(1, 2, 3, 465, 123, 98, 12, 4)
```



### 偏函数 - collect

```scala
def main(args: Array[String]): Unit = {
    var t1 = (1 to 10).toTraversable;
    var pl : PartialFunction[Int,Int] = {
        case num if num % 2 == 0 => num;
    }
    t1 = t1.collect(pl);
    println(t1);
}
```

输出

```scala
Vector(2, 4, 6, 8, 10)
```



### scan 折叠

```scala
def main(args: Array[String]): Unit = {
    //求阶乘
    var t1 = (1 to 5).toTraversable;
    //			初始值 会先拿初始值去乘以 e
    //	然后拿e 去乘以 b
    // 第二次开始的时候 初始值+1
    var t2 = t1.scan(1)((e :Int,b : Int) =>{
        e * b;
    })
    println(t2);
}
```

源码

```scala
def scanLeft[B, That](z: B)(op: (B, A) => B)(implicit bf: CanBuildFrom[Repr, B, That]): That = {
  val b = bf(repr)
  b.sizeHint(this, 1)
  var acc = z
  b += acc
  for (x <- this) { acc = op(acc, x); b += acc }
  b.result
}
```

其中

```scala
var acc = z
b += acc
```



### 取指定元素

| head       | 取第一个，不存在 报错NoSuchElementException |      |
| ---------- | ------------------------------------------- | ---- |
| last       | 取最后一个，同上                            |      |
| headOption | 取第一个 返回值为Option                     |      |
| lastOption | 取最后一个 返回值Option                     |      |
| find       | 查找第一个满足要求的                        |      |
| slice      | 截取集合中的一部分元素                      |      |

#### slice - 截取段

```scala
def slice(from : Int , until : Int) : Traversable[A]
```

- from -> 从哪里开始

- until -> 从哪里结束

- 包含from 不含 until

- 可以结合find使用

  ```scala
  def main(args: Array[String]): Unit = {
      //不能这样写
      //var t1 = (0 to 20).toList.toTraversable;
      var t1 = Traversable(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
      var index = 0;
      var a = t1.toList.find((a : Int) =>{
          if(a % 5 == 0){
              true;
          }
          else {
              //计算元素所属索引
              index += 1;
              false;
          }
      })
      println(a);
      var t2 = t1.slice(index,t1.size);
      println(t2);
  }
  ```

- 输出

  ```scala
  Some(5)
  List(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
  ```

  

### 条件判断 

#### 所有条件都满足 - forall()

#### 任意一个元素条件满足 - exist() 

- ##### 只要一个元素满足就返回

  ```scala
  def main(args: Array[String]): Unit = {
      var t1 = Traversable(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
      println(t1.forall((a:Int) =>{
          a % 2 == 0;
      }));
      println(t1.exists((a:Int)=>{
          a % 2 == 0;
      }))
  }
  ```

- ##### 输出

- false\ntrue\n



### 聚合函数 - count

| count   | 统计集合中满足条件的元素个数 |
| ------- | ---------------------------- |
| sum     | 获取集合中所有元素的元素乘积 |
| product | 获取集合中所有的元素最小值   |
| max     | 最大值                       |
| min     | 最小值                       |



### 集合类型转换

- #### Traversable是顶级特质

- #### toXxx()



### 填充元素

| fill()    | 快速生成指定数量的元素             |
| --------- | ---------------------------------- |
| iterate() | 根据指定的条件，生成指定个数的元素 |
| range()   | 生成某个区间的指定间隔的所有数据   |

#### fill - 生成

```scala
def fill[A](n: Int)(elem: => A): CC[A]
```

- ##### 解释 

n => 个数

elem => 任意数据

```scala
def main(args: Array[String]): Unit = {
    
    var t1 = Traversable.fill(10)("Leng");
    println(t1);
    
    var t2 = Traversable.fill(10)(Random.nextInt(12) + 3);
    println(t2);
    
    var t3 = Traversable.fill[List[(String,String)]](5)(List(("itcast","itcast")));
    println(t3);
}
```

- 输出

  ```scala
  List(Leng, Leng, Leng, Leng, Leng, Leng, Leng, Leng, Leng, Leng)
  List(5, 3, 14, 10, 10, 14, 13, 10, 6, 11)
  List(List((itcast,itcast)), List((itcast,itcast)), List((itcast,itcast)), List((itcast,itcast)), List((itcast,itcast)))
  ```

  - #### 其中t3使用全写



#### iterate - 指定生成

```scala
//iterate[T](开始值,长度)((f : T)=>{
//      f => 会作为当前值 即未到尾时的最后一个值
// })
var t1 = Traversable.iterate[Int](1,10)((f : Int)=>{
    f * 10;
})
println(t1);
```

- ##### 输出

```scala
List(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000)
```



#### range() - 截断生成

```scala
//range[类型 : 必须为Math(数学)](start : 第一个值,end : 最后一个值) : CC[T]
//range[类型 : 必须为Math(数学)](start : 第一个值,end : 最后一个值 , 隐式) : CC[T]
//隐式默认为1 => 间隔多少截取
//def range[T: Integral](start: T, end: T): CC[T] = range(start, end, implicitly[Integral[T]].one)
var t1 = Traversable.range[Int](0,601,50);
println(t1);
```

- ##### 输出

```scala
List(0, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600)
```



### Dome

```scala
case class Student(var name : String,var age : Int)
def main(args: Array[String]): Unit = {
    //创建名称列表
    var list : ListBuffer[String] = new ListBuffer[String];
    list += "张三";
    list += "李四";
    list += "王五";
    list += "赵六";
    list += "田七";
    
    //创建学生Traversable
    var a = list.size;
    var t1 : Traversable[Student]= Traversable.iterate[Student](new Student("",1),list.size+1)(
        (f : Student)=>{
            a -=1;
            new Student(list(a),Random.nextInt(10) + 10);
        }
    )
    //删除头
    t1 = t1.tail;
    
    //排序 按照年龄
    var list1 : List[Student] = t1.toList;
    t1 = list1.sortWith((stu : Student,stu2 : Student) =>{
        stu.age > stu2.age;
    }).toTraversable;
    //输出
    println(t1);
}
```

- ##### 结果

```scala
List(Student(田七,19), Student(张三,16), Student(王五,15), Student(李四,12), Student(赵六,10))
```



## 集合 - Iterable特质

- #### Iterable特质 继承 Traversable特质

- #### 它拥有获取迭代器的抽象方法 def iterator : Iterator[A]



### 迭代的两种方式

- #### 通过iterator()方法实现

  ##### 属于主动迭代，可以通过hasNext()检查后面是否还有元素 可以主动调用Next()获取对象

- #### 通过foreach()

  ##### 属于被动迭代，值提供函数，不能控制遍历过程



### 分组遍历 - grouped

- #### 将Iterable对象中的元素分成固定大小的组，然后遍历

```scala
var iner = Iterable[Int](1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50);
//def grouped(size: Int): Iterator[Repr]

//分成五组 iter内包含了5个迭代器
var iter = iner.grouped(5)
while (iter.hasNext){
    println(iter.next());
}
```

输出

```scala
List(1, 2, 3, 4, 5)
List(6, 7, 8, 9, 10)
List(11, 12, 13, 14, 15)
List(16, 17, 18, 19, 20)
List(21, 22, 23, 24, 25)
List(26, 27, 28, 29, 30)
List(31, 32, 33, 34, 35)
List(36, 37, 38, 39, 40)
List(41, 42, 43, 44, 45)
List(46, 47, 48, 49, 50)
```



### 生成 带索引元组集 - zipWithIndex

```scala
var list : ListBuffer[String] = new ListBuffer[String];
list += "张三";
list += "李四";
list += "王五";
list += "赵六";
list += "田七";
var iterable = list.toIterable;

//直接调用方法即可 默认从开始
//def zipWithIndex[A1 >: A, That](implicit bf: CanBuildFrom[Repr, (A1, Int), That]): That
var iterable2 = iterable.zipWithIndex.map((f:(String,Int)) =>{
    (f._2,f._1);
});
println(iterable2)
```

- ##### 输出

```scala
List((0,张三), (1,李四), (2,王五), (3,赵六), (4,田七))
```



### 迭代顺序判断 - sameElements

```scala
list1.sameElements(list2);
```





## Seq

### 元素的获取

- ##### seq1(index);

- ##### seq1.apply(index);



### 索引的获取

- #### 获取不到返回-1

| indexOf        | 获取指定元素的索引(第一次出现)             |
| -------------- | ------------------------------------------ |
| lastIndexOf    | 获取指定元素在列表中最后一次出现的索引     |
| indexWhere     | 获取满足条件的元素在集合中第一次出现的索引 |
| lastIndexWhere | 获取满足条件的元素，在集合中最后一次的索引 |
| indexOfSlice   | 获取指定的子序列在集合中第一次出现的位置   |

| 1    | 传一个数据        |
| ---- | ----------------- |
| 2    | 传一个数据        |
| 3    | (规则,从索引开始) |
| 4    | (规则,从索引开始) |
| 5    | (序列,索引开始)   |

- ##### 对于indexOfSlice

  有表 seq1(1,2,3)

  ```scala
  seq1.indexOfSlice(Seq(1,2));//返回0
  ```

  

### 数据判断

| startsWith    | 判断集合是否以指定的子序列开头   |
| :------------ | -------------------------------- |
| endsWith      | 判断集合是否以指定的子序列开头   |
| contains      | 判断集合是否包含某个指定的数据   |
| containsSlice | 判断集合是否包含某个指定的子序列 |

### 元素修改

| updated | 修改指定索引的元素值 |
| ------- | -------------------- |
| patch   | 修改指定区间元素的值 |

| 1    | updated(索引,元素值)                             |
| ---- | ------------------------------------------------ |
| 2    | patch(索引,元素值,个数) 元素值小于个数将删除元素 |



## Stack

- ### Stack表示栈(数据结构) 先进后出

- #### 按照存入顺序的反向顺序读取

| 特点   | 先进后出 |
| ------ | -------- |
| 存元素 | 压栈     |
| 取元素 | 弹栈     |

| top                 | 获取栈顶元素不移除               |
| ------------------- | -------------------------------- |
| push                | 入栈                             |
| pop                 | 移除栈顶，并返回一个值           |
| clear               | 移除所有元素                     |
| dup[ArrayStack特有] | 复制栈顶元素                     |
| preserving          | 执行一个表达式，完成后栈不会改变 |





## 队列 - Queue

- #### 导包 mutable.Queue

- 先进先出，栈只有一个口，队列有两个口 头尾一个

| enqueue      | 入队，可以传入0-多个元素 |
| ------------ | ------------------------ |
| dequeue      | 出队，移除一个元素       |
| dequeueAll   | 移除所有满足条件的元素   |
| dequeueFirst | 移除第一个满足条件的元素 |



## Set

HashSet : 元素唯一，无序 字典树(存取无序)

LinkedHashSet : 元素唯一，有序(存取有序)

TreeSet : 元素唯一，排序(默认升序)



## Map

```scala
map.filterKeys(_ == "B"); //取键为B的键值对对象
```

| contains(值) | 判断是否存在这个键 |
| ------------ | ------------------ |
|              |                    |
|              |                    |
|              |                    |

```scala
//先获取数据
println("请输入数据:");
var str = StdIn.readLine();
//创建Map集合
var map = mutable.HashMap[Char,Int]();
for(s <- str){
    if (map.contains(s)){
        var e = map(s);
        map -= s;
        map += s -> (e+1);
    }else{
        map += s -> 1;
    }
}
for((k,v) <- map){
    println(k.toString + v)
}
```

- #### 输出

```scala
请输入数据:
aanwjjgnhfawg
w2
n2
h1
j2
g2
a3
f1
```



# Actor

- #### Actor并发编程模型可以用来开发比Java线程效率更高的并程序

- #### Actor是基于事件(消息)的并发编程模型

## 使用

- ##### 定义具体实现类或者对象 继承 Actor特质

- ##### 重写act方法

- ##### 调用Actor的方法start



## 发送消息

| !    | 发送异步消息,没有返回值      |
| ---- | ---------------------------- |
| !?   | 发送同步消息,等待返回值      |
| !!   | 发送异步消息,返回Future[Any] |

给actor1线程发送一串消息

```
actor1 ! "你好"
```

## 接收消息

使用receive方法 只能使用一次

需要传入一个偏函数

```
{
	case 变量名:类型 => 代码块
}
```

isSet判断接收信息



## 线程

- #### TimeUnit.SECONDS.sleep(3)单位秒 线程休眠

| loop  | 循环                           |
| ----- | ------------------------------ |
| react | 阻塞线程，直到超时或接收到消息 |


