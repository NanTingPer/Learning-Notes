在我的方案中，我需要为 `ModItem` 添加如下内容: 

- `IsAValidTool` 方法

  此方法的签名大概是这样。他主要用来替代`SpecialToolUsageSettings.IsAValidTool`
  在`#Player Line 38086`调用。有了这个方法，可以制作例如掘墓人铲的工具。

```cs
public virtual bool IsAValidTool(Player player);
```



- `CanUseToolCondition`方法

  此方法的签名大概是这样。他主要用来替代`SpecialToolUsageSettings.UsageCondition`
  用来决定是否能够执行`ToolUsageAction`

在`#Player Line 38097` `if (flag && specialToolUsageSettings.UsageCondition != null)` 之后调用

```cs
public virtual bool CanUseToolCondition(Player user, Item item, int targetX, int targetY);
```



- `ToolUsageAction`方法

  此方法的签名大概是这样。

  他主要用来代替`SpecialToolUsageSettings.UsageAction`。
  在调用`Player.ItemCheck_UseMiningTools_ActuallyUseMiningTool` 之前 / 之后调用。
  用来进行工具使用时的额外操作

```cs
public virtual void ToolUsageAction(Player user, Item item, int targetX, int targetY);
```

---

为 `ModPlayer` 添加 `ModifyPickPower` ，此方法签名大概是这样。

```cs
public virtual void ModifyPickPower(Player player, Item item, int x, int y, ref int pickPower);
```

`Item`的来源可能有点奇怪。因为我会为此定义一个`Item`的成员字段。如果直接`PickTile`方法的参数那样将是破坏性的。字段为 `pickItem` 其会在`ItemCheck_UseMiningTools_ActuallyUseMiningTool`的对`PickTile`调用之上进行赋值。

```cs
pickItem = sItem;
PickTile(x, y, sItem.pick);
```

对于`ModifyPickPower`的调用，可能会置于第一个`return`之后

```cs
if (tile.type == 504)
	return;
// 如果由 Mod Dev 调用的 PickTile, pickItem应该如何处理?
PlayerLoader.ModifyPickPower(this, pickItem, x, y, ref pickPower);
pickItem = null;
```

