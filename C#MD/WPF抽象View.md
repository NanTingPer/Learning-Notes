# WPF抽象View

在WPF中，创建的`UserControl`，在编写axaml时，实际不必须绑定`DataContext`，绑定的目的只是为了提供`Ins`语法检查 / 提示，但是在绑定`DataContext`时，无法使用抽象类型，可以试试创建一个无任何功能的实现进行绑定，以提供代码补全。

```xaml
<UserControl.DataContext>
    <vm:PlaltformViewModel />
</UserControl.DataContext>
```

然后在`Text`等空间中，对内容进行属性绑定就行。

```xaml
<TextBlock FontSize="20" Text="{Binding PlatformName}"/>
```

在主视图引用时，直接使用`controls`，然后覆盖其`DataContext`，效果是一致的。

```xaml
<controls:PlatformView Grid.Row="0" Grid.Column="0" DataContext="{Binding ImplViewModel}"/>
```

