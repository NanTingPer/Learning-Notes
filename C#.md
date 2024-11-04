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
```



