# 颜色混合

```cs
//加法 使用BlendState.Additive 默认是 BlendState.AlphaBlend,
//还有一个BlendState.Opaque 会直接忽略 Alpha通道 （透明）
SpriteBatch sb = Main.spriteBatch;
sb.Begin(SpriteSortMode.Immediate, BlendState.Additive);
sb.End();
```

