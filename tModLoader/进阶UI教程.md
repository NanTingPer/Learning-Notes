# UI入门教程

## 1. 真正让UI失效

现在，让我们了解更多关于UI的内容。在基础UI中，我们知道了`UserInterface`用来承载UI，我还能控制是否绘制 / 将我们都层插入UI层来达到控制UI是否显示。但，它不显示就真的没效果了吗？不是的。不显示只是绘制层面的，但是你点击那个位置还是会触发事件。

如何让UI真正都失效？只需要设置`UserInterface`都状态为`unll`即可

```cs
userIf.SetState(null);
```

如要生效，再把状态设置回去就可以了
```cs
userIf.SetState(bossUi);
```



## 2. UI的动态布局大小

我们不希望我们的UI使用绝对值的位置，当你设置较大的绝对位置时，再缩小窗口大小，你会看到效果的。你都UI可能跑出屏幕之外。
使用`Left.Set / Top.Set`方法虽然可以设置相对值，但是其不会实时变化，只会在初始化都时候固定一次。

你可能会说，我要是在`Update`方法进行实时更新呢？当然没问题，但是实施起来可不会如你所愿。你会发现，你的子组件似乎不听指挥。这里就需要使用`Recalculate`方法了，这个方法用来从新调整布局。也就是每次更改`Top Left`等布局相关的属性/字段时，就需要手动的更新一次布局。
每个`布局`都有一个`Elements`字段，他是此布局承载的全部控件，我们需要让其承载的控件一起更新。否则你会看到`主画布`布局更新了，但是其组件没有更新

```cs
public override void Recalculate()
{
    base.Recalculate();
    foreach (var uiel in Elements) { //遍历承载控件
        uiel.Recalculate();
    }
}
public override void Update(GameTime gameTime)
{
    Top.Set(0, 0.8f);
    Left.Set(0, 0.6f);
    Recalculate();
    base.Update(gameTime);
}
```



