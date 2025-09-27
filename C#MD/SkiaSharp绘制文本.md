---
title: "SkiaSharp绘制文本"
date: 2025-09-27T12:34:00+08:00
draft: false
tags: ["C#"]
---

# SkiaSharp绘制文本

## 创建`SKBitmap`

第一步是创建`SKBitmap`，然后创建能够操作此位图的画布，对于`SKBitmap`的创建，可以使用其静态方法 `Decode` 的重载，或者直接`new`

```cs
using var bitmap = new SKBitmap(new SKImageInfo(42, 20, SKColorType.Rgba8888, SKAlphaType.Premul));
using var canvas = new SKCanvas(bitmap);
```



## 创建`SKFont`

第二步就是获取字体对象了，首先需要先获取字体文件的`SKTypeface`然后创建`SKFont`对象，只支持`TTF`文件

1. 获取字体文件的路径

```cs
var fontpath = Path.Combine(Environment.CurrentDirectory, "font", "LUCON.TTF");
```

2. 创建字体 / 分步创建

```cs
var typeref = SKTypeface.FromFile(fontpath);
var skfont = new SKFont(typeref);
```



## 绘制文本

拥有了画布和字体后，我们就可以对位图进行操作了，直接使用`Canvas`的`DrawText`方法就可以绘制文本

```cs
// 5,14是字体矩形左上角对于图片0,0点的偏移
canvas.DrawText("文本", 5, 14, SKTextAlign.Left, skfont, new SKPaint());
```



## 转为字节数组

操作完成后，我们就需要对图片进行保存或者发送到API请求方了，但是我们不能直接使用`bitmap.Bytes`因为这是`SKBitmap`的字节数组，其未被格式化。因此我们需要如下操作

```cs
using var image = SKImage.FromBitmap(bitmap); //转为image对象
using var data = imgbit.Encode(SKEncodedImageFormat.Png, 1000); //格式化
var bytes = data.ToArray(); //此bitmap为png时的数组
var base64 = Convert.ToBase64String(values); //此图片的base64编码

File.WriteAllBytes(filepath, bytes); //直接保存到文件
```

