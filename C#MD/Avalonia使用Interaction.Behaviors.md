# 使用Interaction.Behaviors

> 让事件能够像行为一样绑定到Command上

现在，Avalonia的`Interaction.Behaviors`已经迁移到`Xaml.Behaviors.Interactivity`中，而`Core`已经迁移到` Xaml.Behaviors.Interactions`，在安装这两个软件包后，就可以跟以前一样编写了

```xaml
<Image x:Name="IndexImage" Source="avares://NewbeApp/Assets/Index.png" Grid.Column="0" Width="100">
<Interaction.Behaviors>
  <EventTriggerBehavior EventName="Tapped">
    <InvokeCommandAction Command="{Binding ToIndex}"></InvokeCommandAction>
  </EventTriggerBehavior>
</Interaction.Behaviors>
</Image>
```



# 使用静态资源

全局静态资源需要在`App.axaml`中注册，如果类型需要注册为静态资源，那么这个类型不能是静态的，不能是抽象的，不能是有参构造的。
```cs
<Application.Resources>
  <local:ServerLocator x:Key="ServerLocator"/>
</Application.Resources>
```

这样在使用的时候，要绑定他的属性只需要

```xaml
xxx="{Binding 属性名称, Source={StaticResource 资源名称}}"
```

