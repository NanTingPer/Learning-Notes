# 多ViewModel通信

简单的通信可以使用静态来实现，但是当`ViewModel`变得多起来，并多个`ViewModel`之间相互都要通知的话，在后面新建`ViewModle`也需要再去注册东西，非常的繁琐而且不方便



## 记录类 `recird`

​	其不可变，并且判断相等是由内部全部字段与属性的值，由于不可变，仅可使用`with`语句进行修改后复制新的对象，对于参照对象很有用，而且是线程安全的。

​	可以从记录类型继承，不能从类继承，相应的。类不能从记录类继承

```cs
Person person2 = person1 with { FirstName = "John" };
```



## Messenger

`Messenger`(事件聚合者)，同时还能分离多个`ViewModel`就是用来聚合和管理多个`ViewModel`的消息的，与中介者模式相比，`Messenger`只管收和发，不进行额外处理。



## 仿

可以创建一个事件处理类，其为单例，这样在任何地方都可以调用

```cs
class EventAggregator
{
    public static EventAggregator instance {get;} = new EventAggregator();
    //这个方法用来注册消息 object是目标，Action是目标接受到后调用的方法(回调)
   	//消息订阅者使用这个方法进行订阅
   	public void Register<TMessage>(object receiver, Action<TMessage>)
    {
        
	}
    
    //发送消息，所有订阅了TMessage类型的 都能收到对应的消息
    public void Send<TMessage>(TMessage)
    {
        
    }
}
```

创建一个消息类型，这里使用类默认构造器

```cs
record StringMessage(string Message)
```



