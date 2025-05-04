# ReactiveUI.Interaction

> [原文](https://www.reactiveui.net/docs/handbook/interactions/)

​	有时候，`ViewModel`代码需要由用户决定是否执行。例如是否删除文件或删除遇到错误时。

​	这种交互是`ReactiveUI`提供的一种方案。这会暂停`ViweModel`的代码执行，直到用户提供解决方案

## API概述

​	`Interaction<TInput, TOutput>`类是交互结构的基础。它将互相交互的组件粘合在一起，协调交互，并分发给处理程序。

​	交互接受输入并生成输出。`View`使用输入来处理交互。`ViewModel`接受交互的输出。例如，`ViewModel`可能需要在删除文件之前要求用户确认。`View`可以传入文件路径作为输入，返回一个`bool`作为输出，表示是否删除该文件。

​	`Interaction<TInput, TOutput>`的`input`和`output`是泛型参数，因此对于输入和输出的类型，没有任何限制。

> 注意: 有时候，或许并不要输入内容，或者输入的类型不重要。这时可以使用`Unit`作为输入类型，也可以作为输出类型，同时表明，这次交互不进行决策，只是进行通知。

​	处理该交互的程序(handlers receive)会接收到一个`InteractionContext<TInput, TOutput>`。对交互进行输入，则使用上下文公开的属性`Input`。处理程序可以使用`InteractionContext`的`SetOutput`方法提供输出。

以下是交互组件的典型排列方式:

- **View Model** : 需要知道问题的答案，例如 : "是否删除这个文件"。
- **View** : 向用户提问，并在交互中过程中提供答案。

​	虽然这种情况最常见，但并不是强制性的。例如，`View`可以在没有任何用户干预的情况下自行回答问题。或者这两个组件可能都是视图模型。`ReactiveUI`不会以任何方式限制他们协作。

​	假设最常见的情况是，`ViewModel`创建并公开`Interaction<TInput, TOutput>`实例。它关联的视图通过调用`Interaction`的`RegisterHandler`方法来注册处理程序。为了启动`Interaction`，`ViewModel`会将`TInput`类型的实例传递给`Interaction`的`Handle`方法。当异步方法将结果返回时，`ViewModel`会接收到类型为`TOutpu`的结果
```cs
public Interaction<MusicStoreViewModel, AlbumViewModel?> ShowStore { get; } = new();
//MusicStoreViewModel是消息处理程序 返回的结果是AlbumViewModel
await ShowStore.Handle(new MusicStoreViewModel());
```

## 示例

> `this.WhenActivated`需要类继承`ReactiveUserControl`或者`ReactiveWindow`
> 或者实现`IActivatableView`

```cs
public class ViewModel : ReactiveObject
{
    private readonly Interaction<string, bool> confirm;//创建交互绑定
    
    public ViewModel()
    {
        this.confirm = new Interaction<string, bool>();
    }
    
    public Interaction<string, bool> Confirm => this.confirm;
    
    public async Task DeleteFileAsync() //交互发生时 触发的方法
    {
        var fileName = "a";
        
        // this will throw an exception if nothing handles the interaction
        //此调用从View返回结果
        var delete = await this.confirm.Handle(fileName);
        
        if (delete)
        {
            // delete the file
        }
    }
}

public class View : ReactiveWindow<ViewModel>
{
    public View()
    {
        //当ViewModel的内容被激活，会进行此内容的调用
        this.WhenActivated(f =>
        {
            //此调用用于注册ViewModel中交互的处理程序
            f.Invoke(this.ViewModel.Confirm.RegisterHandler(interaction  =>
            {
                this.DisplayAlert("Confirm Delete",
                    $"Are you sure you want to delete '{interaction.Input}'?",
                    "YES",
                    "NO");
            }));
        });
    }

    private bool DisplayAlert(string str1, string str2, string str3, string str4)
    {
        //DisplayAlert实际是弹一个选择框框的
        return true;
    }
}
```



​	还可以创建一个`Interaction<TInput, TOutput>`，用于程序中多个组件间共享。一个常见的例子是错误恢复。许多组件可能会引发错误，我们只需要一个常见的处理程序。下面是实现这个功能的示例：

```cs
public enum ErrorRecoveryOption
{
    Retry,
    Abort
}

/// <summary>
/// 存储公共的交互
/// </summary>
public static class Interactions
{
    public static readonly Interaction<Exception, ErrorRecoveryOption> Errors = new Interaction<Exception, ErrorRecoveryOption>();
}

public class SomeViewModel : ReactiveObject
{
    public async Task SomeMethodAsync()
    {
        while (true)
        {
            Exception failure = null;
            
            try
            {
                //调用方法
                DoSomethingThatMightFail();
            }
            catch (Exception ex)
            {
                //存储异常
                failure = ex;
            }
            
            if (failure == null)
            {
                break;
            }
            
            // 如果发生异常则将其发送给处理程序
            // this will throw if nothing handles the interaction
            var recovery = await Interactions.Errors.Handle(failure);
            
            if (recovery == ErrorRecoveryOption.Abort)
            {
                break;
            }
        }
    }
}

public class RootView : ReactiveWindow<SomeViewModel>
{
    public RootView()
    {
        //为公共的交互注册处理程序
        Interactions.Errors.RegisterHandler(interaction =>
        {

            //var action = await this.DisplayAlert(
            //    "Error",
            //    "Something bad has happened. What do you want to do?",
            //    "RETRY",
            //    "ABORT");
            //选择框的结果
            var @bool = true;

            interaction.SetOutput(@bool ? ErrorRecoveryOption.Retry : ErrorRecoveryOption.Abort);
        });
    }
}
```

> 注意：为了清晰起见，这里的示例代码混合了 `TPL`(并发) 和 `Rx`(响应式)编程。生产环境一般会固定使用一种。

> 警告: `Handle`返回的`Observative`是冷的。必须订阅才能调用处理程序。



## 处理程序优先级

​	`Interaction<TInput, TOutput>`实现了一个处理程序链。可以注册多个处理程序，后注册的优先级比先注册的优先级高(栈)。当`Handle`方法触发交互时，每个处理程序都会处理此交互(即设置输出)。处理程序没有实际交互的义务。如果一个处理程序选择不设置输出，那就会调用链中的下一个处理程序。

> **注意** `Interaction<TInput, TOutput>`类被设计为可扩展(非`sealed`修饰，可以被继承) 。子类可以改变`Handle`的行为，只尝试列表中的第一个处理程序。即不使用调用链。

​	因为有这种优先级链，因此可以定义一个默认的处理程序，使其暂时覆盖此处理程序。例如，根的处理程序可能提供了默认的错误恢复行为。但应用程序中的特定视图可能知道如何中不提示用户的情况下从特定错误中恢复。它可以在激活时注册处理程序，然后在停用时处理此注册。但是，这种方法需要一个共享的交互实例。



## 未处理的交互

​	如果一个交互没有处理程序或者没有任何处理程序返回结果，那么就默认这个交互是未处理的。在这种情况下，调用`Handle` 会抛出`UnhandledInteractionException<TInput, TOutput>`异常。这个异常具有`Interaction`和`Input`属性，提供了关于错误的更多细节。

## 测试

​	可以通过注册一个交互处理程序轻松测试`ViewModel`中的交互逻辑

```cs
[Fact]
public async Task interaction_test()
{
    var fixture = new ViewModel();
    fixture
        .Confirm
        .RegisterHandler(interaction => interaction.SetOutput(true));
        
    await fixture.DeleteFileAsync();
    
    Assert.True(/* file was deleted */);
}
```

​	如果测试遇到了共享交互，你可能会想中测试返回前拦截。

```cs
[Fact]
public async Task interaction_test()
{
    var fixture = new SomeViewModel();
    
    using (Interactions.Error.RegisterHandler(interaction => interaction.SetOutput(ErrorRecoveryOption.Abort)))
    {
        fixture.SomeMethodAsync();
        
        // assert abort here
    }
}
```

