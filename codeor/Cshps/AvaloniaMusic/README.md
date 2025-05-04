> 本项目跟随AvaloniaDocs中的示例程序，指在复习往欺知识

# 一、添加并排版控件

> 本章节的前提是使用默认MVVM模板创建了一个Avalonia项目

## 1. 添加控件

​	要添加控件，其实默认的`MainWindow.axaml`已经提供了一个默认的`TextBlock`，我们只需要照葫芦画瓢，添加`Button`
```xaml
<Button Content="Hello Button"></Button>
```

​	为了更好的控制按钮的位置，`示例是在左上角`，可以设置`Button.Margin`，但是为了更好的管理一个区域的控件，更好的做法是创建一个`Panel`，并在内部添加控件`Append`
```xaml
<Panel Margin="40">
        <Button
		   Content="Hello Button"
            HorizontalAlignment="Right" 
            VerticalAlignment="Top">
        </Button>
                <!--水平Right 垂直Top-->
</Panel>
```

## 2.设置图标

> AvaloniaUI提供了众多[Icons](https://avaloniaui.github.io/icons.html)

​	为了资源能够使用，我们需要创建一个类目来存储资源，并进行引用。

- 在心仪的位置创建`Avalonia Styles`类，并给一个心仪的名称
- 编辑 `axaml` 文件，在`<!-- Add Styles Here -->`下创建`<Style>`对象，并设置其属性
- 其中 `x:Key`将会是将来引用需要使用的资源名称

```xaml
<!-- Add Styles Here -->
<Style>
	<Style.Resources>
	<StreamGeometry x:Key="store_microsoft_regular">M11.5 9.5V13H8V9.5H11.5Z M11.5 17.5V14H8V17.5H11.5Z M16 9.5V13H12.5V9.5H16Z M16 17.5V14H12.5V17.5H16Z M8 6V3.75C8 2.7835 8.7835 2 9.75 2H14.25C15.2165 2 16 2.7835 16 3.75V6H21.25C21.6642 6 22 6.33579 22 6.75V18.25C22 19.7688 20.7688 21 19.25 21H4.75C3.23122 21 2 19.7688 2 18.25V6.75C2 6.33579 2.33579 6 2.75 6H8ZM9.5 3.75V6H14.5V3.75C14.5 3.61193 14.3881 3.5 14.25 3.5H9.75C9.61193 3.5 9.5 3.61193 9.5 3.75ZM3.5 18.25C3.5 18.9404 4.05964 19.5 4.75 19.5H19.25C19.9404 19.5 20.5 18.9404 20.5 18.25V7.5H3.5V18.25Z</StreamGeometry>
	</Style.Resources>
</Style>
```

- 添加完成后，为`App.axaml`文件添加内容，完成后需要重新构建

```xaml
<Application.Styles>
    <FluentTheme />
    <StyleInclude Source="avares://Avalonia.MusicStore/Icons.axaml" />
</Application.Styles>
```

- 为按钮使用资源，`StaticResource`表示为静态资源，由于我们已经将其导入`App.axaml`，所以可以直接使用`x:Key`引用

```xaml
<Panel Margin="40">
        <Button
            HorizontalAlignment="Right"
            VerticalAlignment="Top">
            <PathIcon Data="{StaticResource store_microsoft_regular}" />
        </Button>
            <!--水平Right 垂直Top-->
</Panel>
```

![image-20250503164355055](./markdownImage/一1.png)



# 二、为按钮添加行为

## 1. 添加`Command`

​	转到`MainWindowViewModel`，为其添加`ICommand`类型的属性，并在构造函数中赋值。由于使用`ReactiveUI`其赋值方式与`微软社区MVVM工具包`不同，需要使用`ReactiveCommand.Create` ，可以传入一个无参`Action`，然后我们在`MainWindow.axaml`中为按钮添加这个`Command`

```cs
public ICommand OpenStoreWindowCommand{get; set;}
public MainWindowViewModel()
{
    OpenStoreWindowCommand = ReactiveCommand.Create(OpenStoreWindow);
}
private void OpenStoreWindow()
{
    
}
```

```xaml
<Button
    Command="{Binding OpenStoreWindowCommand}"
    HorizontalAlignment="Right" 
    VerticalAlignment="Top">
    <PathIcon Data="{StaticResource store_microsoft_regular}" /> 
</Button>
```

​	之所以能引用到我们创建的`ICommand`属性，是因为其上面为这个`axaml`绑定了`MainWindowViewModel`对象

```xaml
<Design.DataContext>
    <!-- This only sets the DataContext for the previewer in an IDE,
         to set the actual DataContext for runtime, set the DataContext property in code (look at App.axaml.cs) -->
    <vm:MainWindowViewModel/>
</Design.DataContext>
```

​	现在按钮按下后，什么都不会发生，因为我们的方法没有内容，但是如果使用断点进行调试是可以命中的

![image-20250504124750413](C:/Users/23759/AppData/Roaming/Typora/typora-user-images/image-20250504124750413.png)



## 2. 创建对话窗口

​	在`MVVM`中，每个视图都对应一个模型，先在`Views`中创建一个`Avalonia Window`，并在`ViewModels`中创建一个`Class`。

​	命名规范遵循`Avalonia Window`以`Window`结尾，`ViewModel`以`ViewModel`结尾，这是`ViewLocator`决定的。

​	在这个案例中，我们需要创建两个窗口，一个是`MusicStore`一个是`Album`，`MusicStore`中有许多`Album`，一个`Album`就是一个唱片

> 如果使用Visual Studio，可能需要修改名称空间。否则名称空间是在根的，或者使用此[插件](https://gitee.com/fanbal/avalonia-maid2022)
>
> 如果有多项目需求，可以将默认的权限修饰符`internal`更改为`public`

![image-20250504125949031](C:/Users/23759/AppData/Roaming/Typora/typora-user-images/image-20250504125949031.png)



## 3. 创建交互属性

​	我们回到`MainWindowViewModel.cs`中，创建一个`ReactiveUI`下的`Interaction`类型属性，其泛型表示哪两个模型互操作[此类型文档](https://www.reactiveui.net/docs/handbook/interactions/)

> 强烈建议在继续之前，查看文档！

```cs
//商店中可能没有唱片
public Interaction<MusicStoreViewModel, AlbumViewModel?> ShowStoreInteraction { get; } = new();
private async Task OpenStoreWindow()
{
    //传一个MusicStoreViewModel给消息处理程序
	//其调用SetOutput后，这里就得到了一个Album
    var album = await ShowStoreInteraction.Handle(new MusicStoreViewModel());
}
```

​	现在还有任何实际作用，我们的`ShowStoreInteraction`未被任何处理程序绑定，运行只会报错`ReactiveUI.UnhandledInteractionException`

​	让我们把视角切换到`MainWindow.axaml.cs`，并让`MainWindow`继承`ReactiveWindow<MainWindowViewModel>`，这样会让`View`和`ViewModel`强绑定，这也是`ReactiveUI`期望的，随后在`MainWindow`中为`ShowStoreInteraction`(交互)注册处理程序

```cs
public partial class MainWindow : ReactiveWindow<MainWindowViewModel>
```

```cs
public MainWindow()
{
    this.WhenActivated(action =>
    {
        action.Invoke(this.ViewModel!.ShowStoreInteraction.RegisterHandler(HandleMethod));
    });
    InitializeComponent();
}
public async Task HandleMethod(IInteractionContext<MusicStoreViewModel, AlbumViewModel> context)
{
    //创建音乐商店窗口，并设置其内容为输入值，输入值为AlbumViewModel
    var musicStore = new MusicStoreWindow();
    musicStore.DataContext = context.Input;
    
    //显示这个商店窗口，父窗口就是MainWindow
    var r = await musicStore.ShowDialog<AlbumViewModel>(this);
    //设置输出
    context.SetOutput(r);
}
```

为了让弹出的窗口符合样式，我们可以设置`MusicStoreWindow`属性

```xaml
TransparencyLevelHint="AcrylicBlur"
Background="Transparent"
ExtendClientAreaToDecorationsHint="True"
WindowStartupLocation="CenterOwner" <!--基于父窗居中-->
Height="500"
Width="500"
```

![image-20250504151646720](C:/Users/23759/AppData/Roaming/Typora/typora-user-images/image-20250504151646720.png)

让我们梳理执行逻辑

`MainWindow`按下 => 执行`MainWindowViewModel.OpenStoreWindowCommand` 此`Command`绑定`OpenStoreWindow` => 触发处理程序 等待返回值， 处理程序由`MainWindow`注册为`HandleMethod`=> 执行`HandleMethod` => `SetOutput`