---
title: "Tesseract 文本识别"
date: 2025-03-14T17:47:00+08:00
draft: false
tags: ["C#"]
---
# Tesseract文本识别

## 安装

在项目中安装`nuget`包 `Tesseract`

## 使用

在代码文件中进行引用`using Tesseract;`

去Github中下载语言包`https://github.com/tesseract-ocr/tessdata` 然后放到项目的`bin\Debug\.netx`下

1. 加载图片

   ```cs
   Pix pix = Pix.LoadFromFile("C:\\1-r\\r\\r.png");
   ```

2. 创建识别引擎，这里的`tessdata`要改成 `bin\Debug\.netx`下相应的文件夹名称，`chi_sim`根据语言进行更改

   ```cs
   var tEngine = new TesseractEngine("tessdata", "chi_sim", EngineMode.Default);
   ```

3. 识别

   ```cs
   Page page = tEngine.Process(pix); //处理图像
   string str = page.GetText(); //获取字符串
   Console.WriteLine(str);	//输出
   ```

4. 使用正则替换文本

   ```cs
   Console.WriteLine(Regex.Replace(str, @"\s", ""));
   ```

   