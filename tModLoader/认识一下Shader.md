---
title: "tModLoader 认识一下Shader"
date: 2025-11-03T16:22:00+08:00
draft: false
tags: ["tModLoader"]
---

> 如果要使用着色器 建议Begin时设置`BlendState` 为 `BlendState.NonPremultiplied`

| 语义        |        |                    |                                |
| ----------- | ------ | ------------------ | ------------------------------ |
| SV_POSITION | float4 | 作为像素作色器输入 | 顶点在其次座标系 xyzw 中的位置 |
| COLOR0      | float4 | 作为像素着色器输入 | 顶点的RGBA颜色                 |
| TEXCOORD0   | float2 | 作为像素着色器输入 | 纹理uv坐标                     |



# Effect的引入

```cs
private const string shader = "path";
public static Asset<Effect> Bloom { get; private set; }
static EffectAssets()
{
    Bloom = GetEffect(nameof(Bloom));
}
public static Asset<Effect> GetEffect(string name)
{
    return Request<Effect>($"{shader}{name}");
}

//其实就一行
Request<Effect>("Path");
```



# Effect的应用时机

```cs
//在Begin的时候说明要使用的Effect
spriteBatch.Begin(effect: exampleEffect);

//在Begin后的Draw中将会实际应用
spriteBatch.Draw(exampleTexture, texturePosition, Color.White);
spriteBatch.DrawString(exampleFont, "Hello World", textPosition, Color.White);

// For most SpriteSortMode values, actual drawing with the effect happens here
//对于 SpriteSortMode 值的效果，会在End() 时生效
spriteBatch.End();
```

# Effect的参数更改

## 立即模式

```cs
// 指定Immediate 这回造成严重的性能问题
spriteBatch.Begin(effect: exampleEffect, sortMode: SpriteSortMode.Immediate);

// 更改Effect参数值，这会立即生效并应用！
exampleEffect.Parameters["ExampleParameter"].SetValue(1.0f);
spriteBatch.Draw(texture1, position, color);

// 再次更改 也会立即生效
exampleEffect.Parameters["ExampleParameter"].SetValue(0.5f);
spriteBatch.Draw(texture2, position, color);

// 由于处于立即模式，此end的作用也只有结束
spriteBatch.End();
```



## 批处理模式

```cs
// 默认使用批处理，因此只有当调用End时才会实际应用效果
spriteBatch.Begin(effect: exampleEffect);

// 更改参数并绘制内容
exampleEffect.Parameters["ExampleParameter"].SetValue(1.0f);
spriteBatch.Draw(texture1, position, color);

// 更改参数并绘制其他内容
exampleEffect.Parameters["ExampleParameter"].SetValue(0.5f);
spriteBatch.Draw(texture2, position, color);

// 对于Effect的参数值，只有在调用End时才会应用
// 也代表 0.5f 会应用于两次绘制
// 而不是每次调用都使用不同的值
spriteBatch.End();
```



# 灰度fx

```GLSL
sampler uImage0 : register(s0); // 原图像
float Saturation; //灰度的强度，0全色 1全灰

struct PSINPUT{
    float4 pos : SV_POSITION;   //顶点在其次座标系 xyzw 中的位置
    float4 color : COLOR0;      //颜色
    float2 uv : TEXCOORD0;      //纹理uv坐标
};

float4 Gray(PSINPUT input) : COLOR
{
    //对材质采样
    float4 color = tex2D(uImage0, input.uv);

    //计算灰度值
    float grayscale = dot(color.rgb, float3(0.3, 0.59, 0.11));

    //创建灰色
    // float3 graycolor = float3(grayscale, grayscale, grayscale);

    //利用灰度值在给定的强度间进行平滑计算
    float3 finalcolor = lerp(grayscale, color.rgb, Saturation);

    //不干扰透明度 只干扰颜色
    return float4(finalcolor, color.a);
}

technique GrayDraw
{
    pass p0
    {
        PixelShader = compile ps_2_0 Gray();
    }
}
```



# 计算亮度fx

```glsl
sampler uImage0 : register(s0); // 原图像
float Intensity = 0.6; //亮度强度

struct PSINPUT
{
    float4 pos : SV_POSITION;   //顶点在其次座标系 xyzw 中的位置
    float4 color : COLOR0;      //颜色
    float2 uv : TEXCOORD0;      //纹理uv坐标
};

// 计算亮度的方法
float CountLight(float4 color)
{
    return 0.2125 * color.r + 0.7154 * color.g + 0.0721 * color.b;
}
// 像素着色器：接收结构体
float4 PixelLight(PSINPUT input) : COLOR0
{
    float4 color = tex2D(uImage0, input.uv);
    float val = clamp(CountLight(color) - Intensity, 0.0, 1.0);
    return color * val;
}

technique Technique1
{
    pass Pass1
    {
        PixelShader = compile ps_2_0 PixelLight();
    }
}