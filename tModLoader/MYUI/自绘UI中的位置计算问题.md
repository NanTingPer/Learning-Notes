---
title: "tModLoader 自绘UI中的位置计算问题"
date: 2025-10-17T16:00:00+08:00
draft: false
tags: ["tModLoader"]
---
# 自绘UI中的位置计算问题

我们在类型中属性对于的值一般都是原始数值，而`SpriteBatch.Draw()`在`SpriteBatch.Begin()`中指定了变换矩阵。因此如果需要正确处理点击时的位置，应当将使用宽高创建的向量，使用`SpriteBatch.Begin()`中的矩阵进行变换。

如下，在绘制时采用了`Main.UIScaleMatrix`

```cs
public sealed override void DrawSelf(SpriteBatch spriteBatch)
{
    spriteBatch.End();
    spriteBatch.Begin(SpriteSortMode.Deferred, BlendState.Additive, SamplerState.PointClamp, DepthStencilState.Default, RasterizerState.CullNone, null, Main.UIScaleMatrix);
    foreach (var item in entitys) {
        ReLogic.Graphics.DynamicSpriteFontExtensionMethods.DrawString(
            spriteBatch,
            FontAssets.MouseText.Value,
            item?.ToString() ?? "",
            GetItemPosition(item!),
            Color.White,
            0f,
            Vector2.Zero,
            1f,
            SpriteEffects.None,
            1f
            );
    }
    spriteBatch.End();
    spriteBatch.Begin(SpriteSortMode.Deferred, BlendState.Opaque, SamplerState.PointClamp, DepthStencilState.Default, RasterizerState.CullNone, null, Main.UIScaleMatrix);
}

/// <summary>
/// 获取此实体需要绘制的位置
/// <code>
/// (index + 1) * ItemHeight + ItemVerticalSpacing
/// </code>
/// </summary>
private Vector2 GetItemPosition(Entity entity) => GetItemPosition(entitys.FindIndex(f => f.Equals(entity)));

/// <summary>
/// 获取此索引对应实体绘制的位置
/// <code>
/// (index + 1) * ItemHeight + ItemVerticalSpacing
/// </code>
/// </summary>
/// <returns></returns>
private Vector2 GetItemPosition(int index)
{
    //1. 先计算此元素在列表中的索引 然后根据索引计算其左上角位置
    _ = Main.UIScaleMatrix;

    //数量 * 元素高度 + 元素间距
    var itemOffset = new Vector2(0, index * ItemHeight + ItemVerticalSpacing); //TODO 现在X是没变的
    var origOffset = new Vector2(LeftPadding, TopPadding);

    return origOffset + itemOffset ;//origOffset + itemOffset;
}
```

但我在进行进行点击碰撞检测的时候，认为是鼠标坐标计算矩阵的问题，是我在进行点击碰撞检测的代码中，直接使用了`GetItemPosition`的值

> 原始代码

```cs
var itemPosition = GetItemPosition(item);
rectangle.X = (int)itemPosition.X;
rectangle.Y = (int)itemPosition.Y;
```

> 修正后的完整代码

```cs
/// <summary>
/// 鼠标是否点击元素
/// </summary>
private Entity? IsMouseClickItem(Vector2 mousePosition)
{
    //1. 计算变换后的UI大小
    //2. 遍历列表元素 并判断鼠标位置是否进行碰撞
    //鼠标位置使用 Main.GameViewMatrix.ZoomMatrix 矩阵
    //列表元素统一使用 Main.UIScaleMatrix

    //TODO 绘制使用了矩阵 这里计算也要 不然会偏移
    var newv2 = Vector2.Transform(new Vector2(ItemWidth, ItemHeight), Main.UIScaleMatrix);

    var rectangle = new Rectangle(0, 0, (int)newv2.X, (int)newv2.Y);
    foreach (var item in entitys) {
        //绘制位置未应用矩阵，因为在Begin中已经指定
        //如果要将未应用矩阵的元素与应用了矩阵的元素比较，会造成偏移
        //消除偏移 需要手动应用矩阵
        var itemPosition = GetItemPosition(item);
        itemPosition = Vector2.Transform(itemPosition, Main.UIScaleMatrix);
        rectangle.X = (int)itemPosition.X;
        rectangle.Y = (int)itemPosition.Y;
#if DEBUG
        TeachMod.Mod!.Logger.Debug($"{rectangle} {MouseRectangle}");
#End if
        if (CheckAABBvAABBCollision(rectangle, MouseRectangle)) {
            ItemClickEvent?.Invoke(new ItemClickEventArgs(item, this, mousePosition));
        }
    }
    return null;
}
```