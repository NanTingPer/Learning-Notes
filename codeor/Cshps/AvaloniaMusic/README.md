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