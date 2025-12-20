# UseMiningTools

将`SpecialToolUsageSettings`设置为`public`，在`ItemCheck_UseMiningTools`中添加额外`ItemLoader`调用

```cs
private void ItemCheck_UseMiningTools(Item sItem)
{
	SpecialToolUsageSettings specialToolUsageSettings = default(SpecialToolUsageSettings);
	if (sItem.type == 4711) {
		SpecialToolUsageSettings specialToolUsageSettings2 = default(SpecialToolUsageSettings);
		specialToolUsageSettings2.IsAValidTool = true; 
		specialToolUsageSettings2.UsageAction = UseShovel;
		specialToolUsageSettings = specialToolUsageSettings2;
	}
    ItemLoader.UseMiningTools(sItem, this, ref specialToolUsageSettings);
}
```

```cs
// Global Item
/// <summary>
/// 如果 <see cref="Player.SpecialToolUsageSettings.UsageAction"/> 不为空，那么将跳过实际的工具调用
/// <br/> 而是执行此委托中的逻辑
/// </summary>
/// <param name="item"> 这个物品 </param>
/// <param name="usageSettings"> 这个物品的特殊工具配置 </param>
public virtual void UseMiningTools(Item item, Player player, ref Player.SpecialToolUsageSettings usageSettings)
{

}
```

---

## ModItem

```cs
public virtual void UseMiningTools(Item item, Player player, ref Player.SpecialToolUsageSettings usageSettings)
{
}
```

---

## ItemLoader

直接将`usageSettings`给用户是合理的吗？这或许将只接受最后一个`GloablItem`的内容。

1. 遍历时，对`IsAValidTool`取 `&=`
2. 遍历时，对`UsageCondition`使用`List<CanUseToolCondition>`添加，`UsageAction`同理
3. 在遍历完成后，重新对`usageSettings`赋值

```cs
item.ModItem?.UseMiningTools(item, player, ref usageSettings);
bool defaultAValidTool = usageSettings.IsAValidTool;
Player.SpecialToolUsageSettings.CanUseToolCondition canUseToolCondition = usageSettings.UsageCondition;
Player.SpecialToolUsageSettings.UseToolAction useToolAction = usageSettings.UsageAction;;
foreach (var g in HookUseMiningTools.Enumerate(item)) {
	g.UseMiningTools(item, player, ref usageSettings);
	defaultAValidTool &= usageSettings.IsAValidTool;
	canUseToolCondition += usageSettings.UsageCondition;
	useToolAction += usageSettings.UsageAction;
}
usageSettings.IsAValidTool = defaultAValidTool;
usageSettings.UsageAction = useToolAction;
usageSettings.UsageCondition = canUseToolCondition;
```



# ModPlayer.PickTile

大概是这样

```cs
ItemCheck_UseMiningTools_ActuallyUseMiningTool(){
    else if (sItem.pick > 0) {
		PickTile(x, y, sItem.pick);
	}
}
```

```cs
ItemCheck_UseMiningTools_ActuallyUseMiningTool(){
    else if (sItem.pick > 0) {
        int pick = sItem.pick;
        PlyaerLoader.PickTile(this, sItem, x, y, ref pick);
    	PickTile(x, y, pick);
    }
}
```



## ModPlayer

```cs
/// <summary>
/// 可以用来修改这次工具的攻击力
/// </summary>
/// <param name="item"> use item </param>
/// <param name="x"> tile x position </param>
/// <param name="y"> tile y position </param>
/// <param name="pick"> PickTile pick </param>
public virtual void PickTile(Item item, int x, int y, ref int pick)
{

}
```



## PlayerLoader

```cs
private delegate void DelegatePickTile(Item item, int x, int y, ref int pick);
private static HookList HookPickTile = AddHook<DelegatePickTile>(p => p.PickTile);
public static void PickTile(Player player, Item item, int x, int y, ref int pick)
{
	foreach (var modPlayer in HookPickTile.Enumerate(player)) {
		modPlayer.PickTile(item, x, y, ref pick);
	}
}
```



hi，我开启了一个分支，其中包含了此需求的一些更改。
我计划将`Item`更改为`IEntitySource`，使用`Player.GetSource_ItemUse`。
但我有一个问题，在`Mount`中我应该如何正确获取Item Object，使用`new Item`然后`SetDefault`是正确的吗？

如果可以的话，这或许可以成为我的第一次贡献。





```cs
Hi, I've opened a branch that includes some changes for this feature.
I'm planning to change Item to `IEntitySource` using `Player.GetSource_ItemUse`.
However, I have a question: In `Mount`, what would be the correct way to retrieve the Item object? Would it be correct to use `new Item` and then `SetDefault`?

Currently, I have implemented the following features:
- can use `ModItem` to define custom tools similar to the Gravedigger's Shovel.
- can intercept the usage of special tools in `GlobalItem`.
- can modify pick-related tools in `ModPlayer`.

If possible, this could hopefully become my first contribution. Hope this is okay.
```





# ItemLoader.UseMiningTools

## 1

```cs
private delegate void DelegateUseMiningTools(Item item, Player player, ref Player.SpecialToolUsageSettings usageSettings);
internal static HookList HookUseMiningTools = AddHook<DelegateUseMiningTools>(g => g.UseMiningTools);
public static void UseMiningTools(Item item, Player player, ref Player.SpecialToolUsageSettings usageSettings)
{
    item.ModItem?.UseMiningTools(item, player, ref usageSettings);
    bool defaultAValidTool = usageSettings.IsAValidTool;
    Player.SpecialToolUsageSettings.CanUseToolCondition canUseToolCondition = usageSettings.UsageCondition;
    Player.SpecialToolUsageSettings.UseToolAction useToolAction = usageSettings.UsageAction;

    bool anyGlobal = false;  //If there are no hooks, the global returns false
    bool globalUsageCondition = true;
    foreach (var g in HookUseMiningTools.Enumerate(item)) {
        anyGlobal = true;
        g.UseMiningTools(item, player, ref usageSettings);
        defaultAValidTool &= usageSettings.IsAValidTool;
        if(usageSettings.UsageCondition != null) {
            globalUsageCondition &= usageSettings.UsageCondition(player, item, Player.tileTargetX, Player.tileTargetY);
            //canUseToolCondition += usageSettings.UsageCondition;
        }
        if(usageSettings.UsageAction != null) {
            useToolAction += usageSettings.UsageAction;
            usageSettings.UsageAction = null;
        }
    }

    usageSettings.IsAValidTool = defaultAValidTool;
    usageSettings.UsageAction = useToolAction;

    usageSettings.UsageCondition = (player, item, x, y) => {
        bool orig = canUseToolCondition?.Invoke(player, item, x, y) ?? true;
        if (!anyGlobal) { // not global
            return orig;
        }
        else {
            return globalUsageCondition && orig;
        }
    };
```



# 多钩子拆分

`IsAValidTool` => `ref IsAValidTool` or `IsAValidTool && return Value`

`UsageAction` => `MiningUsage`

`UsageCondition` => `MiningUsageCondition`