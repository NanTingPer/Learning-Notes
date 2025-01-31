---
title: "tModLoader 基础UI"
date: 2025-01-28T18:50:00+08:00
draft: false
tags: ["tModLoader"]
---

# 基础准备

​	首先创建一个类，继承`ModSystem`，无特殊需求`UI`类也可以丢这个类里面，成为内部类。由于`UI`只需要在客户端就行了，所以使用特性标记，仅在客户端加载`[Autoload(Side = ModSide.Client)]`

| 方法                                                    | 说明                             |
| ------------------------------------------------------- | -------------------------------- |
| Load()                                                  | 用来载入UI                       |
| UpdateUI()                                              | 用来更新UI                       |
| ModifyInterfaceLayers((List<GameInterfaceLayer> layers) | 用来确定UI所在层级，并注册UI层级 |

```CS
[Autoload(Side = ModSide.Client)]
class UISystem : ModSystem
{
	public override void Load()
	{
        base.Load();
    }
    
    public override void UpdateUI(GameTime gameTime)
	{
        base.UpdateUI(gameTime);
    }
    
    public override void ModifyInterfaceLayers(List<GameInterfaceLayer> layers)
	{
        base.ModifyInterfaceLayers(layers);
    }   
}

```



# 创建UI

​	我们创建一个`ModSystem`的内部类，并继承`UIState` / `UIElement`，只需要重写`OnInitialize()`方法即可。在这个方法里面我们进行界面的创建 布局等。

```cs
public class BzaUI : UIState
{
    public override void OnInitialize()
	{
        base.OnInitialize();
    }
}
```

​	原版有很多的控件供给我们使用，在`Terraria.GameContent.UI.Elements`名称空间下。我们使用`UIImageButton`创建一个自定义图像的按钮，并加入到画布中。

```cs
public class BzaUI : UIState
{
    private UIImageButton MyButton;
    public override void OnInitialize()
	{
        //导入图片资源
        Asset<Texture2D> tex2d = Request<Texture2D>("ModName/xxxxx/imageName");
        //使用图片资源创建控件
        MyButton = new UIImageButton(tex2d);
        
        //将控件添加到父组件 如果想要自定义画布可以使用UIPanel类
        Append(MyButton);
        base.OnInitialize();
    }
}
```



# 使用UI

​	在UI类创建完成后，就需要在`ModSystem`进行加载了，先创建一个我们自己的UI实例，然后创建`UserInterface`用来承载我们的`UI`。

```cs
[Autoload(Side = ModSide.Client)]
class UISystem : ModSystem
{
    public BzaUI bzaUI;//自己的UI
    public UserInterface userInterface;//用户接口
    
	public override void Load()
	{
        bzaUI = new BzaUI();
        bzaUI.Activate();//激活

        userInterface = new UserInterface();
        userInterface.SetState(bzaUI);//将接口状态设置为我们的UI

        base.Load();
    }
    
    public override void UpdateUI(GameTime gameTime)
	{
        //每帧都更新UI
        userInterface?.Update(gameTime);
        base.UpdateUI(gameTime);
    }
}

```

​	对于`ModifyInterfaceLayers`单独拉出来说，`layers`包含原版以及模组的全部UI层级。每个层级都有自己的名字。并且按照先后顺序进行绘制。具体可以使用`ModderTool`这个模组的一个`用户接口层`选项进行查看，我们就不定义他在谁之上谁之下了。直接往0处插入我们自己的图层。

​	使用`List(集合)`中的`Insert()`方法可以往指定索引处插入数据。这边我指定0，然后创建一个`LegacyGameInterfaceLayer`就是图层。

```cs
    public override void ModifyInterfaceLayers(List<GameInterfaceLayer> layers)
	{
        layers.Insert
		(
    		0, 
    		new LegacyGameInterfaceLayer(
             	"GensokyoWPNACC:Bza",	//我们层的名字
             	() =>{ bzaUI.Draw(Main.spriteBatch); return true;}, //一个func委托，里面是UI绘制的逻辑,返回UI是否可见
             	InterfaceScaleType.UI)	//我们图层的类型
        );
        base.ModifyInterfaceLayers(layers);
    }   
```

​	现在进入游戏应该就能在不起眼的小角落看到你的按钮了 当然他现在什么也干不了



# 添加点击事件

​	设置我们UI的`Top`和`Left`属性可以更改UI的位置

```cs
public override void OnInitialize()
{
	Top.Pixels = Main.screenHeight - 100;
	Left.Pixels = Main.screenWidth / 5;
}
```

​	现在我们为按钮添加点击事件(伪代码)

```cs
public override void OnInitialize()
{
        Asset<Texture2D> tex2d = Request<Texture2D>("ModName/xxxxx/imageName");
        MyButton = new UIImageButton(tex2d);
        //添加事件
    	XAddIB.OnLeftClick += new MouseEvent(this.XAdd);

        Append(MyButton);
}

private void XAdd(UIMouseEvent evt, UIElement listeningElement)
{
    //方法逻辑
}
```

​	这样我们单机按钮就会触发你的方法逻辑了 使用`Visual Studio`查看`MyButton`其中小闪电的就代表事件



# 绘制鼠标悬停文本

​	直接上代码了，这个确实是没什么好讲的

```cs
public override void ModifyInterfaceLayers(List<GameInterfaceLayer> layers)
{
    layers.Insert
    (
        0, 
        new LegacyGameInterfaceLayer
            (
                 "GensokyoWPNACC:Bza",
                 () =>{
                        var sb = Main.spriteBatch;
                        var font = FontAssets.MouseText.Value;
                        var ms = Main.MouseScreen + new Vector2(0, -20);
                        var color = Color.White;
                        var rot = 0f;
                        var origin = Vector2.Zero;
                        var scale = new Vector2(1.2f, 1.2f);
                     	var drawText = "6666";

                        bzaUI.Draw(Main.spriteBatch);

                        //字体绘制
                     	//isOnButton使用OnMouseOver事件控制
                        if (BzaUI.isOnButton)
                        {
                            ChatManager.DrawColorCodedStringWithShadow(sb, font, drawText, ms, color, rot, origin, scale);
                        }
                        return true;
                 },
                 InterfaceScaleType.UI
            )
    );
    
    base.ModifyInterfaceLayers(layers);
}

```

