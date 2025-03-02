# 模组加载UI界面

1. 主要界面 包含启动全部等`Terraria.ModLoader.UI.UIMods` patches路径 `tModLoader\patches\tModLoader\Terraria\ModLoader\UI`

2. 对于单个模组界面`Terraria.ModLoader.UI.UIModItem` 其被存储在主要界面的`items`字段，patches路径与主界面一致

   ```cs 
   private readonly List<UIModItem> items = new List<UIModItem>();
   ```



## UIModItem

1. 其承载的模组被存储在`_mod`字段，`LocalMod`是一个`internal`修饰类

   ```cs
   private readonly LocalMod _mod;
   ```

   

