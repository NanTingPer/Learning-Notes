# 属性

| 名称         | 描述     |
| ------------ | -------- |
| Scala        | 缩放     |
| Rot          | 选择     |
| Color        | 颜色     |
| Velocity     | 速度     |
| position     | 位置     |
| DrawPosition | 屏幕位置 |
| LifeTime     | 时间     |
| Active       | 是否活着 |



# StartDraw

```cs
protected virtual void StartDraw(Effect effect = null, BlendState blendState = null)
{
    blendState ??= BlendState.Additive;
    SpriteBatch.Begin(SpriteSortMode.Immediate, blendState, SamplerState.LinearClamp, DepthStencilState.None, RasterizerState.CullNone, effect, Main.GameViewMatrix.TransformationMatrix);
}
```



# ExecuteDraw

```cs
public void ExecuteDraw()
{
    if (!(LifeTime > 0 && Active)){ return; }
    LifeTime--;
    Action?.Invoke(this);
    position += Velocity;

    if (LifeTime <= 0) Active = false;

    StartDraw();
    Draw(SpriteBatch, GraphicsDevice);
    SpriteBatch.End();
}
```







```cs
protected override void StartDraw(Effect effect = null, BlendState blendState = null)
{
    blendState ??= BlendState.AlphaBlend;
    SpriteBatch.Begin(SpriteSortMode.Immediate, blendState, SamplerState.LinearClamp, DepthStencilState.None, RasterizerState.CullNone, effect, Main.GameViewMatrix.TransformationMatrix);
}
public override void Draw(SpriteBatch spriteBatch, GraphicsDevice graphicsDevice)
{
    spriteBatch.Draw(DrawTexture.Value, new Rectangle((int)DrawPosition.X, (int)DrawPosition.Y, (int)(10 * Scala),(int)(200 * Scala)) , null/*null*/, Color, Rot, Origin, /*Scala*/ SpriteEffects.None, 1f);
    spriteBatch.End();
    SpriteBatch.Begin(SpriteSortMode.Immediate, BlendState.Additive, SamplerState.LinearClamp, DepthStencilState.None, RasterizerState.CullNone, null, Main.GameViewMatrix.TransformationMatrix);
    spriteBatch.Draw(DrawTexture.Value, new Rectangle((int)DrawPosition.X, (int)DrawPosition.Y, (int)(10 * Scala), (int)(200 * Scala)), null/*null*/, Color, Rot, Origin, /*Scala*/ SpriteEffects.None, 1f);
}
```

```cs
public static DrawDust Power1(Vector2 Pos)
{
    DrawDust dust = new TestDrawDust
    {
        Rot = Main.rand.NextFloat(-3.14f, 3.14f),
        Scala = Main.rand.NextFloat(0.5f, 0.7f),
        Position = Pos + new Vector2(Main.rand.NextFloat(-10f, 10f), Main.rand.NextFloat(-10f, 10f)),
        Color = Color.Coral
    };
    dust.Action = dust => {
        dust.Scala -= 0.02f;
        if (dust.Scala < 0)
            dust.Active = false;
    };
    NewDust(dust);

    return dust;
}
```

