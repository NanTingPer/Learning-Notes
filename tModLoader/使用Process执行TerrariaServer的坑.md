---
title: "使用Process执行TerrariaServer的坑"
date: 2025-10-25T20:52:00+08:00
draft: false
tags: ["C#"]
---
# 使用Process执行TerrariaServer的坑

当服务器控制台需要我输入的时候，我使用`ServerProcess!.StandardInput.WriteLine(index);`怎么输都没用。一开始使用`ServerProcess.StandardOutput.ReadLine();`读取控制台读取不到`Choose World: `，因为在泰拉源码中，这一行以及后面的输入提示，都是使用的`Write`不是`WriteLine`所以需要一个字符一个字符读，或者等待输入。

后面我改用了`ServerProcess.StandardOutput.Read(@char, 0, 1);`自己去一个字符一个字符读取，`Choose World: `也输出了，但是我怎么输入都就是没用，`AI`也问烂了，deepseek ，通义，copilot 回答的结果都是一样的。让我用`OutputDataReceived`事件，然后调用`BeginOutputReadLine`，不是，我都说了泰拉输出使用的`Write`，这样不也照样阻塞吗，更何况根本不是阻塞的问题。

解决: 设置`ProcessStartInfo`的`StandardInputEncoding = Encoding.Unicode` 默认的也不行，默认`UTF8` 我一开始就是用的`UTF8`卡我两个小时。
```cs
private readonly static ProcessStartInfo startInfo = new ProcessStartInfo()
{
    UseShellExecute = false, //不使用命令行执行
    CreateNoWindow = true,
    RedirectStandardOutput = true,
    RedirectStandardInput = true,
    RedirectStandardError = true,
    StandardErrorEncoding = Encoding.UTF8,
    StandardInputEncoding = Encoding.Unicode, //需要Unicode 前面的问题 就是他！
    StandardOutputEncoding = Encoding.UTF8,
    FileName = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Terraria\\TerrariaServer.exe"
};
```

