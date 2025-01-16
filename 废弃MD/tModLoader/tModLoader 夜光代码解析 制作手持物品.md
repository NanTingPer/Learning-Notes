tModLoader 夜光代码解析 制作手持物品



### 手持全部代码 稍后我们逐行解析

---

```cs
Vector2 vector4 = player.itemLocation + new Vector2((float)(8 * player.direction), -10f * player.gravDir);


Vector3 vector5 = new Vector3(1f, 0.7f, 0.8f) * 1.3f;
Vector2 vector6 = player.RotatedRelativePoint(vector4, false, true);
Lighting.AddLight(vector6, vector5);
if (Main.rand.Next(40) == 0)
{
    Vector2 vector7 = Main.rand.NextVector2Circular(4f, 4f);
    Dust dust10 = Dust.NewDustPerfect(vector6 + vector7, 43, new Vector2?(Vector2.Zero), 254, new Color(255, 255, 0, 255), 0.3f);
    if (vector7 != Vector2.Zero)
    {
        dust10.velocity = vector6.DirectionTo(dust10.position) * 0.2f;
    }
    dust10.fadeIn = 0.3f;
    dust10.noLightEmittence = true;
    dust10.customData = this;
}
```



`Vector2 vector4 = player.itemLocation + new Vector2((float)(8 * player.direction), -10f * player.gravDir);`

​	改行代码用来是物品要绘制的位置 有几个函数

| itemLocation | 物品停止使用后刷新一次 |
| ------------ | ---------------------- |
| direction    | 玩家的左右朝向         |
| gravDir      | 玩家的重力方向         |



---

```cs
Vector3 vector5 = new Vector3(1f, 0.7f, 0.8f) * 1.3f;
Vector2 vector6 = player.RotatedRelativePoint(vector4, false, true);
Lighting.AddLight(vector6, vector5);
```

​	对于`vector5`没什么好说的 是下面用来计算光照的 代表 rgb

| RotatedRelativePoint | 获取相对于玩家的旋转角度计算的新的位置 |
| -------------------- | -------------------------------------- |
| **参数**             |                                        |
| pos                  | 要被计算的位置                         |
| reverseRotation      | 是否反向旋转                           |
| addGfxOffY           | 平滑Y轴                                |

​	对于`RotatedRelativePoint`我们一般传入 `(位置, false, true)`



其他代码是粒子效果了

---



如果直接抄的话，会发现物品位置只更新一次，因为`player.itemLocation`只更新一次, 我们需要将他更改为`player.position` 也就是玩家位置，这个是实时更新的

接下来就是慢慢微调了

