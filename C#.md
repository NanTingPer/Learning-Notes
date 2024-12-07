# C#

# 1.0 事件

> ### 事件模型的五个组成部分

- ##### 事件的拥有者 event source 对象
  
  - ##### 事件的拥有者同时也是事件的响应者

- ##### 事件成员 event 成员

- ##### 事件的响应者 event subscriber 对象
  
  - ##### 当一个对象被通知了 他们能接收到这个事件，他们就是事件的响应者
  
  - ##### 订阅了事件的类或者对象

- ##### 事件处理器 event handler 成员 -> 本质上是一个回调方法
  
  - ##### 事件的响应者触发的事件

- ##### 事件订阅 -> 把事件处理器与事件关联在一起，本质上是一种以委托类型为基础的"约定"
  
  - ##### 约定了事件能把什么样的事件发送给事件处理器,约束了事件处理器能处理什么样的消息
  
  - ##### 使用事件处理器 订阅事件

> ### 注

- ##### 事件处理器是方法成员

- ##### 挂接事件处理器的时候，可以使用委托实例，也可以直接使用方法名，这是个"语法糖"

- ##### 事件处理器对事件的订阅不是随意的，匹配与否由声明事件时所使用的委托类型来检测

- ##### 事件可以同步调用也可以异步调用

> ### 属性

- #### 表示类的状态

> ##### sender => 事件的拥有者 谁触发这个事件 谁就是事件的拥有者

# 2.0 依赖关系

> #### Car就依赖上Engine了 紧耦合了

```C#
class Engine{
    public int RPM{get;private set;}
    public void Work(int gas){
        this.RPM = 1000 * gas;
    }
}
class Car{
    private Engine _engine;
    public Car(Engine engine){
        _engine = engine;
    }
    public void Work(int rpt){
        _engine.Work(rpt);
    }
}
```

> 紧耦合的弊端
> 
> - 程序不好调试
> - 链条式隐式错误

> ### 接口实现松耦合
> 
> #### 接口让功能的提供方可以替换,避免了紧耦合的功能提供方的高风险高成本
> 
> #### 在紧耦合的情况下 服务提供方挂了,强依赖的其他功能也会挂掉
> 
> ##### 这样用户换手机就不需要去修改用户类 而是直接在更改new的类型就行了

```C#
internal class Program
{
    static void Main(string[] args)
    {
        PhoneUser user = new PhoneUser(new XiaoMi());
        user.PhoneAll();
    }
}

public interface IPhone
{
    void Sendr();
    void Reg();
    void Modone();
}

public class PhoneUser
{
    private IPhone Phone;
    public PhoneUser(IPhone phone)
    {
        this.Phone = phone;
    }

    public void PhoneAll()
    {
        this.Phone.Sendr();
        this.Phone.Reg();
        this.Phone.Modone();
    }
}

public class iPhon : IPhone
{
    public void Modone()
    {
        Console.WriteLine("苹果手机打电话");
    }

    public void Reg()
    {
        Console.WriteLine("苹果手机收消息");
    }

    public void Sendr()
    {
        Console.WriteLine("苹果手机发消息");
    }
}

public class XiaoMi : IPhone
{
    public void Modone()
    {
        Console.WriteLine("小米手机打电话");
    }

    public void Reg()
    {
        Console.WriteLine("小米手机收消息");
    }

    public void Sendr()
    {
        Console.WriteLine("小米手机发消息");
    }
}

public class Gix : IPhone
{
    public void Modone()
    {
        Console.WriteLine("谷歌手机打电话");
    }

    public void Reg()
    {
        Console.WriteLine("谷歌手机收消息");
    }

    public void Sendr()
    {
        Console.WriteLine("谷歌手机发消息");
    }
}
```

# 2.1 依赖反转

> ##### 依赖
> 
> - ##### 服务的使用者和服务的提供者有依赖关系，依赖越直接，依赖就越紧密(耦合关系)
> 
> ##### 反转

- 接口方法是纯虚方法
- 抽象方法 是纯虚方法带点非虚方法
- 没有具体实现的方法就是虚方法
- 抽象方法到virtual方法需要overrider
- virtual 到 override方法 需要加 override

# 2.2 单一职责原则

> #### 一个类应该只做一件事，或者一组相关的事
> 
> #### 接口隔离原则是同一个道理 把本质功能不同的接口隔离开
> 
> ##### 调用者绝不多要，方法所需要传入的接口，其功能不应该比你使用到的功能多

- ##### 过犹不及，在使用接口隔离原则和单一职责原则的时候，如果玩的过火的话，就会产生很多只有一个方法的接口和类，接口和类的颗粒度很小 需要把握接口和类的大小

# 2.3 显示接口实现

> ##### 接口.方法()
> 
> ##### 只有将实现了接口的实例才能来使用这个方法

```c#
interface IKiller{
    void Kill();
}
interface IGenter{
    void Love();
}
class WarmKiller : IGenter : Ikiiler{
    public IGenter(){
        Console.WriteLine("I Love You ....");
    }
    void Ikiller.kill(){
        Console.WriteLine("I Kill You ....");
    }
}
main(){
    IKiller Wk = new WarmKiller(); //这样才能调用 wk.kill 但是同时无法调用 Love
    var wk2 = (IGenter)wk;//这样就能调用Love了但是不能调用kill
}
```

# 2.4 反射

> #### 镜像 / 映像

- ##### 给一个对象，在不用New的情况下 也不知道他类型的情况下 能造出一模一样的对象
  
  - ##### 还能访问这个对象的各个成员

> ## 反射与依赖注入

- #### 依赖注入
  
  - ##### .Net依赖注入框架 => NeGet包 Microsoft.Extensions.DependencyInjection

- 创建变量 sc

```C#
var sc = new ServiceCollection();//容器
//装东西
//第一个是接口 第二个是谁实现了这个接口
sc.AddScoped(typeof(ITank),typeof(HeavyTank));
var sp = sc.BuildServiceProvider();

// --------------------------
//找容器要这个对象
Itank tank = sp.GetService<Itank>();
tank.Fire();
tank.Run();
```

- # 实例

```c#
namespace AppLeng
{
    internal class Program
    {
        static void Main(string[] args)
        {
            //利用反射加载类
            //查看程序所运行在的文件夹
            //Console.WriteLine(Environment.CurrentDirectory);

            //创建文件夹
            Directory.CreateDirectory(Environment.CurrentDirectory + "/Animals");

            //获取一个组合路径 Combine用于将路径组合起来 (string)
            string folder = Path.Combine(Environment.CurrentDirectory, "Animals");
            Console.WriteLine(folder);
            string[] files;
            //返回指定文件内的所有文件名
            files = Directory.GetFiles(folder);

            List<Type> animalTypes = [];

            //遍历文件内的所有文件名
            foreach(string filePath in files)
            {
                //加载指定位置的程序集
                Assembly assembly = AssemblyLoadContext.Default.LoadFromAssemblyPath(filePath);
                //获取该程序集内的所有类型(类)
                Type[] types = assembly.GetTypes();

                //遍历该程序集内的所有类型
                //如果有指定类型 那么就加载到类型集合
                foreach(Type type in types)
                {
                    if(type.GetMethod("Voice") != null)
                    {
                        animalTypes.Add(type);
                    }
                }
            }

            while (true)
            {
                for(int i = 0;i< animalTypes.Count;i++)
                {
                    Console.WriteLine($"{i + 1} , {animalTypes[i].Name}");
                }
                Console.WriteLine("======================================");

                Console.WriteLine("选择动物！!");

                //输入
                int index = How();
                if (index == -1) continue;

                if(index > animalTypes.Count || index < 1){Console.WriteLine("不要乱按哦！");continue;}

                Console.WriteLine("要叫几次呢?");

                //输入要叫多少次
                int num = How();
                if (num == 1) continue;

                //获取指定动物的类型
                Type animalType = animalTypes[index - 1];

                //获取方法对象用于调用
                MethodInfo AMethod = animalType.GetMethod("Voice");

                object AnimalType = Activator.CreateInstance(animalType);
                //设置参数列表
                object[] obj2 = [num];

                //调用
                AMethod.Invoke(AnimalType, obj2);

            }

        }

        public static int How()
        {
            string nums = Console.ReadLine();
            int num = 0;
            if (nums == null) { Console.WriteLine("小调皮"); return -1; }

            try { num = Convert.ToInt32(nums); }catch { Console.WriteLine("小调皮"); return -1; }

            if (num == 0) { Console.WriteLine("小调皮"); return -1; }

            return num;
        }
    }
}
```

## 2.4.1 反射创建泛型对象

- GetType获取泛型类，需要``泛型数量`

```cs
Type[] types = new Type[]
{
    typeof(int),
    typeof(int)
}
Type makeType = type.MakeGenericType(types);
object Generic = Activator.CreateInstance(makeType);
```



# 5.0 接口实现隔离

> ## 依赖关系

1. ##### IAlertService的实现类依赖于View与IAlertService无关

2. ##### JinRiShiCiGet 只知道自己依赖了一个 IAlertService , 并不清楚其实现类到底如何

3. ##### 这就MVVM + IService ，IService的实现是排除在架构之外的，他们多混乱 与这层关系无关

4. ##### Service依赖的接口与View层无关，那么就认为其不依赖View层

5. ##### JinRiShiCiGet依赖于IAlertService接口，而AlertService实现了IAlertService接口，但是JinRiShiCiGet是不知道AlertService的具体实现的，就好比你调用传入的IAlertService实现对象的时候，你只能看到IAlertService定义的玩意儿一样。

   ##### 这就是接口实现隔离





# 6.0  表达式树与Func

- 表达式树`Expression`可以使用`.Compile`转换为Func

- 表达式树`Expression`有一个静态方法`.Add`可以将多个表达式树拼接在一起

| Parameter                    | 该表达式要处理的参数         | `typeof(Student),"stu"`        |
| ---------------------------- | ---------------------------- | ------------------------------ |
| **Property**                 | **该表达式要处理的参数属性** | **`pExp,nameof(Student.Age)`** |
| **Constant**                 | **双目运算符右边的值**       | **`10`可以是变量**             |
| GreaterThan                  | 双目运算符中间的表达 >       | **`levelExp,rightExp`**        |
| Lambda`<Func<Student,bool>>` | 变换为表达式                 | `bodyExp,pExp`                 |



# 7.0 IL

| 指令 | 效果                   |                                                    |
| ---- | ---------------------- | -------------------------------------------------- |
| Pop  | 丢弃上一个方法的返回值 | il.InsertBefore(x,ilProcessor.Create(OpCodes.Pop)) |



| 方法    | 效果                  | Instruction |
| ------- | --------------------- | ----------- |
| Operand | 值 例如 "Hello World" |             |
| Next    | 该IL指令的全部内容    |             |
| OpCode  | 该IL的指令            |             |



1. 加载程序
2. 加载指定方法 获取IL 处理器

- ### Mono.Cecil库

youtube .NET IL Weaving Demo with Mono.Cecil

- ##### 将第二个dll的代码注入到第一个dll

- `Source`内有一个调用了cw输出内容的方法

1. 定义两个变量 分别存储两个不同DLL的路径

   ```csharp
   string appDll = "xxxx";
   string fnDll = "xxx";
   ```

   

2. 使用`ModuleDefinition.ReadModule(dll路径)`加载dll

   ```csharp
   var appModule =  ModuleDefinition.ReadModule(appdll);
   
   var fnModule =  ModuleDefinition.ReadModule(appdll);
   ```

   

3. 随后便可以像 `Assembly` 一样,使用他得到类型

   ```csharp
   var targetType = appModule.Types.FirstOrDefault(where表达式);
   ```

   

4. 使用 加载后的对象的`Types.FirstOrDefault`可以得到一个满足条件的`Type`对象 但是是第一个满足条件 (根据自己的需求进行过滤)

   - 第一层是Type
   - 第二层 `Type.Methods`.Any 方法
   - 第三层`CustomAttributes`.Any 查找属性
   - 第四层 `AttributeType`.Name == "xxx" 使用属性类型枚举 寻看是否等于指定值

5. 使用得到的Type `Type.Methods.First()` 取出第一个方法

   ```csharp
   var targetMethod = targetType.Methods.First();
   ```

   

6. 将同样的逻辑 应用于第二个DLL **(sourceDLL)**

7. 使用`AnyMethod.Body.GetILProcessor()`可以**获取IL处理器(该方法的)** 这是IL代码与该方法的主体进行交互的东西

8. 要往哪个**方法**插入代码 就**获取**谁的**IL处理器**

   ```csharp
   var processor = 第一个dll方法.Body.GetILProcessor();
   processor.Clear(); //清除该IL处理器内的全部IL指令
   ```

   

9. 使用 `AnyMethod.Body.Instructions` 可以得到**该方法的全部指令**

   ```csharp
   sourceMethod.Body.Instructions;
   ```

10. 遍历SourceMethod内的IL指令 将其`Append`到要修改的方法内

    ```csharp
    foreach(var i in sourceMethod.Body.Instructions)
    {
        processor.Append(i);
    }
    ```

11. 使用要被修改的dll的`Write`方法 , 并传入要输出到的路径

    ```csharp
    appMoudel.Write("../.output/App.dll");
    ```

12. 这时候直接编译会报错 `System.Console` 在另一个模块中声明

13. 如果使用DeBug模式打个断点进行观察 , 会发现 , **Source程序集引用中有3个目标** , 而**被注入的目标的程序集只有2个** , 缺少的那个就是 `System.Console` (**using**)

14. 检查 IL是否是简单的对象 , 如果是一个带有全名的实际方法引用 就添加对应的引用

    ```txt
    https://learn.microsoft.com/zh-cn/dotnet/api/microsoft.visualbasic.activities.visualbasicsettings.importreferences?view=netframework-4.8.1
    ```

    

    ```csharp
    //如果他是方法引用
    if(i.Operand is MethodReference {FullName : "System.Void System.Console::WriteLine(System.String)"} mf)
    {
        //添加对象引用
        //每个对象都表示该程序集的一个程序集引用和导入的命名空间
        //导入后需要重新分配给 IL指令
        i.Operand = appModule.ImportReference(mf);
    }
    ```

15. 使用`ImportReference()` 所有于mf相关的东西都会被导入到指定模块 , 这样可以避免发生来自不同模块的方法引用错误

16. 再次运行,编译App.dll 可以看到成功运行



```csharp
using Mono.Cecil;
using Mono.Cecil.Cil;
using Mono.Cecil.Rocks;
using List = Mono.Collections.Generic;

namespace CecilDemo
{
    internal class Program
    {
        static void Main(string[] args)
        {
            string _sourceDll = "D:\\CodeRun\\Learning-Notes\\codeor\\Cshps\\Cecil\\Cedll\\bin\\Debug\\net8.0\\Cedll.dll";
            string _sinkDll = "D:\\CodeRun\\Learning-Notes\\codeor\\Cshps\\Cecil\\Sinks\\bin\\Debug\\net8.0\\Sinks.dll";

            ModuleDefinition source = ModuleDefinition.ReadModule(_sourceDll);
            
            //需要开启可写
            ModuleDefinition sink = ModuleDefinition.ReadModule(_sinkDll,new ReaderParameters() { ReadWrite = true});

            IEnumerable<TypeDefinition> oTypes = source.GetTypes();
            IEnumerable<TypeDefinition> sTypes = sink.GetTypes();
            //Cedll.Program
            //Print

            //Sinks.Program
            //Main


            //取出源方法
            MethodDefinition? sourceMethod = oTypes.FirstOrDefault(f => "Cedll.Program".Equals(f.FullName))
                .GetMethods().FirstOrDefault(f => "Print".Equals(f.Name));

            if(sourceMethod is null) return;

            //取出目标方法
            MethodDefinition? sinkMethod = sTypes.FirstOrDefault(f => "Sinks.Program".Equals(f.FullName))
                .GetMethods().FirstOrDefault(f => "Main".Equals(f.Name));

            if(sinkMethod is null) return;

            //源方法的IL处理器
            ILProcessor sourcesMtIL = sourceMethod.Body.GetILProcessor();

            //目标方法的IL处理器
            ILProcessor sinkMtIL = sinkMethod.Body.GetILProcessor();

            //获取源方法的全部IL指令
            List::Collection<Instruction> sourceILs = sourceMethod.Body.Instructions;

            //清空目标方法的全部IL指令
            sinkMtIL.Clear();

            //遍历源方法的IL指令 并插入目标方法
            foreach (Instruction il in sourceILs)
            {
                //这样判断指定死了 但是源方法中有两个方法引用 一个是WriteKey 一个 输出
                if(il.Operand is MethodReference MethodF)
                {
                    il.Operand = sink.ImportReference(MethodF);
                }
                sinkMtIL.Append(il);
            }

            //保存回到原本的dll
            sink.Write();
        }
    }
}

```



# 8.0 Regex正则表达式

| 方法              | 作用             | 例子                                |
| ----------------- | ---------------- | ----------------------------------- |
| new Regex(表达式) | 创建正则匹配对象 | Regex reg = new Regex(@"[\/.\s:]"); |
| Count(string str) | 计算正则匹配次数 | reg.Count(str) <= 0                 |



# 9.0 C# SQL Lite

1. 安装 nuget 包 => `System.Data.SQLite.Core`



| 类型             | 方法名称                    | 作用            |
| ---------------- | --------------------------- | --------------- |
| SQLiteConnection | CreateFile(String filePath) | 加载/创建数据库 |
|                  |                             |                 |
|                  |                             |                 |



# 2.5 杨中科 .NET

# 1.0 .NET Standard

1. .NET Framework -> Windows程序
2. .NET Core -> 跨平台程序
3. .NET Standard -> 标准(标准库开发)

.NET Standard只提供标准不提供实现

- 统称 .NET

- 建立公共类库建议直接使用 .NET Standard 1.6版本
  
  - .NET Core全版本可用
  - .NET Framework 4.6.1后可用

- or .NET Standard 1.1
  
  - .NET Core版本可用
  - .NET Framework 4.5后可用

- .NET5 后 默认.NET指的是.NET Core

# 1.1 .NET Core 的项目文件

- 其项目文件只显示被排除的文件 而 Framework显示被包含的文件

- 目标运行时选择可移植的，任何操作系统都可以运行，但需要安装.NET运行环境

- 目标运行时选择独立时，所选架构的，无需.NET运行时环境

# 1.2 NetGe包管理

- Install-Package 包名 Version 指定版本 / 不指定就是最新稳定版

- Uninstall-Package 包名 / 卸载

- Update-Package 包名 / 更新

# 1.3 异步编程

- 异步不能提高单个任务的运行速度

- 关键字
  
  - async
  
  - await

> async、await不等于多线程
> 
> 是简化版的

        使用async关键字修饰的方法就是异步方法，规范的方法名称应该以Async结尾

        异步方法的返回值一般都是**Task<T> T是真正的返回值类型**

        即使**没有返回值**也建议使用Task作为返回值

- 只要方法内部使用了**await**，方法必须修饰**async**

- **调用异步方法**时，一般在方法前面**加上await关键字**，这样拿到的**返回值**就是泛型**指定的T类型**

- 即便目标异步方法返回Task 就应当要加上await关键字 不加 另有解决方法
  
  - 线程安全问题 await会让主线程等待当前方法完成

- 如果方法被async修饰了，就应当遵循返回Task

```cs
class Program  
{  
 static async Task Main(string[] args)  
 {  
 //使用异步写入  
 await File.WriteAllTextAsync("/home/r/桌面/1.txt", "Hello World!");  
 //使用异步读取  
 string str = await File.ReadAllTextAsync("/home/r/桌面/1.txt");  
 Console.WriteLine(str);  
 }  
}
```

- 如果方法**不支持async**，内部调用带有返回值的异步方法时，可以不使用await **直接使用.Result，如果不带返回值可以调用.Wait**
  - 尽量不要这要使用，会造成线程阻塞

## Lambda内的异步方法

- 加上 async 修饰 因为Lambda本质是匿名方法

```cs
MethonName(async () => {
    await File.WriteAllTextAsync(fliePath,"aaaaa");
});
```

## 异步方法实现

```csharp
public async Task<int> HttpDown(string url,string filePath)
{
    //创建会生成一个流，导致一直占用文件 可以进行手动释放
    //.Close()
    await using (File.Create(filePath)) ;

    //有实现 IDisposable接口
    //需要使用using进行资源的释放
    using (HttpClient client = new HttpClient())
    {
        //获取
        string str = await client.GetStringAsync(url);
        //写入
        await File.WriteAllTextAsync(filePath,str);
        return str.Length;
    }
}



async_Mthon method = new async_Mthon();
int len = await method.HttpDown("https://www.baidu.com",
                "/home/r/桌面/1.txt");
Console.WriteLine(len);
```

## async、await原理揭秘

    async方法会被编译器编译成一个类，然后里面使用状态机模型(switch语句),根据await切成多个case,然后这个类会被反复调用,每次调用num的值都会改变(case条件使用该值)

- await仅仅阻塞当前线程 &

- await调用的等待期间 .NET会把当前的线程返回给线程池，等待异步方法调用完成后，再从线程池取出一个新线程供后续代码继续执行

## 异步方法不等于多线程

        调用.NET的内置库的异步方法 线程ID会变是因为方法内部使用了类型Task.run类似的方法，开辟了新的线程。

        实际上不手动给异步方法丢到其他线程，其线程是不变的

## 异步方法不使用async修饰

        异步方法内直接将Task<T>返回回去，让调用者取出值

- async方法会生成一个类，影响运行效率，没有普通调用效率高

- 可能会占用非常多的线程
  
  因此在必要情况下不使用async修饰，直接将Task返回

- ### 什么情况下能不使用async修饰

         **如果一个异步方法只是对其他异步方法的调用，并没有其他太复杂的逻辑**，例如 等待A的返回结果再调用B，然后把A的返回结果拿到内部处理再返回。**那么就可以不使用async修饰**

## 异步编程不要使用Sleep()

        如果想在异步方法中暂停一段时间，应该使用await Task.Delay()。使用Thread.Sleep()会造成线程阻塞

## CancellationToken

> 有时需要提前终止任务，比如：请求超时、用户取消请求
> 
> CancellationTokenSource xx = new CancellationTokenSource();

```csharp
CancellationTokenSource cts = new CancellationTokenSource();
cts.Cancel();//发出终止信号
```

```csharp
public static async Task Run(string url,int num,CancellationToken token)
{
    using (HttpClient client = new HttpClient())
    {
        //循环下载指定次数
        for (int i = 0; i < num; i++)
        {
            string s = await client.GetStringAsync(url);
            //如果发出停止就抛异常
            if (token.IsCancellationRequested)
            {
                throw new OperationCanceledException();
            }
        }
    }
}
//创Token对象
CancellationTokenSource cts = new CancellationTokenSource();
//单位是毫秒，到这个时间了 就会触发停止
cts.CancelAfter(5000);
CancellationToken token = cts.Token;
await CancellationToken_.Run("https://www.baidu.com/", 100, token);
//手动终止
// cts.Cancel();

//创Token对象
CancellationTokenSource cts = new CancellationTokenSource();
//单位是毫秒，到这个时间了 就会触发停止
cts.CancelAfter(5000);
CancellationToken token = cts.Token;
await CancellationToken_.Run("https://www.baidu.com/", 100, token);
//手动终止
// cts.Cancel();
```

```csharp
//创Token对象
CancellationTokenSource cts = new CancellationTokenSource();
//单位是毫秒，到这个时间了 就会触发停止
cts.CancelAfter(5000);
CancellationToken token = cts.Token;
await CancellationToken_.Run("https://www.baidu.com/", 100, token);
//手动终止
// cts.Cancel();
```

## 1.1 线程池

> - 一组预先创建的线程，可以被重复使用来执行多个任务
> 
> - 避免频繁地创建和销毁线程，从而减少了线程创建和销毁的开销，提高了系统的性能和效率
> 
> - 异步编程默认使用线程池

- 原子操作

        在执行过程中不会被中断的操作。不可分割，**要么完全执行，要么完全不执行，没有中间状态**

        在多线程环境下，原子操作能够保证数据的一致性和可靠性，避免出现竞态条件和数据竞争的问题

- 线程的创建

> 创建Thread实例，并传入ThreadStart委托 还可以配置线程，如是否为后台线程
> 
> 调用Thread.Start方法，还可以传参

- 线程的终止

> 调用Thread.Join方法 等待线程的结束
> 
> - 会阻塞主线程
> 
> 调用Thread.Interrupt方法，中断线程的执行
> 
> - 会在相应的线程中抛出ThreadInterruptedException 捕获即可
> 
> - 如果线程中包含一个while(true)循环，那么需要保证包含等待方法，Thread.Sleep等（如IO操作）
> 
> 不能用Abort?
> 
> - 使用Abort方法来强制终止线程可能导致一些严重的问题 包括资源泄漏和不可预测的行为
> 
> - 较新版本的.NET中如果使用这个方法会报错 PlatformNotSupportedException
> 
> - 推荐使用Thread.Interrupt或CancellationToken

- 线程的挂起与恢复

> Thread.Suspend以及Thread.Resume
> 
> 较新版本的.NET中，这两个方法已经被标记过时 而且调用会报错
> 
> 推荐使用锁 信号量等方式实现这一逻辑

### 线程安全与同步机制

> #### Thread-Safety

- 锁与信号量

> - lock & Monitor
> 
> - Mutex
> 
> - Semaphore
> 
> > 线程间同步 使用一种方式告知其他线程我目前的工作，别打扰我，等我干完 资源让出来
> 
> - WaitHandle
>   
>   - ManualResetEvent
>     
>     > 如果多个线程都在用WaitOne等待信号量，那么每次Set(),这些WaitOne会被全部释放
>     > 
>     > 调用WaitOne后，保持开放 需要手动调用Reset()方法
>   
>   - AutoResetEvent
>     
>     > 如果多个线程都在用WaitOne等待信号量，
>     > 
>     > 那么每次Set(),只会释放一个WaitOne
>     > 
>     > 调用WaitOne后，会自动调用Reset()方法
> 
> - ReaderWriterLock
>   
>   > 允许多个Reader去读，只允许一个Writer去写，只允许一种，写的时候不能读，读的时候不能写

- 轻量型

> - SemaphoreSlim
> 
> - ManualResetEventSlim
> 
> - ReaderWriterLockSlim

- 不要自己造轮子

> - 线程安全的单例 : Lazy
> 
> - 线程安全的集合类型 : ConcurrentBag、ConcurrentStack、ConcurrentQueue、ConcurrentDictionary
> 
> - 阻塞集合 : BlockingCollection
>   
>   - 不适用异步编程
> 
> - 通道 : Channel
> 
> - 原子操作 : Interlocked
> 
> - 周期任务 : PeriodicTimer

锁会阻塞线程更建议使用信号量

### 信号量

        轻量，底层，看起来好像是在阻塞一个线程，但那个线程确实就该被阻塞，其无非就是在等待一个命令 比如队列又有了新的消息，其也不知道它要不要开始干活，突然信号来了告诉它要开始干活了，它就会去干一下。

        信号量阻塞相当于让线程挂起，可以让出自己的位置让CPU更好的处理其他线程，等到信号来了，又醒过来开始干活。

        比轮询效率更高，更准确
