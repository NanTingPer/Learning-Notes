# PuppeteerSharp

### .NET的浏览器截图工具

[无头 Chrome .NET API](https://github.com/hardkoded/puppeteer-sharp)

> 安装NuGet PuppeteerSharp 



```CS
var bf = new BrowserFetcher();
//下载浏览器
await bf.DownloadAsync();

Console.WriteLine("下载完成");

//Headless => 是否以无头模式运行 (有无界面就是有无头)
//Puppeteer 用来启动谷歌浏览器实例
await using var browser = await Puppeteer.LaunchAsync(new LaunchOptions { Headless = true });

//使用谷歌浏览器打开一个新页面
await using var page = await browser.NewPageAsync();

//设置截取大小
await page.SetViewportAsync(new ViewPortOptions() { Height = 2000, Width = 3000 });

Console.WriteLine("开始访问");
//跳转到指定页面
await page.GoToAsync("https://www.bilibili.com/");
Console.WriteLine("访问完成");
//截图报错到哪里
await page.ScreenshotAsync("C:\\1-个博\\1.png");
Console.WriteLine("保存完成");
Environment.Exit(0);
```

