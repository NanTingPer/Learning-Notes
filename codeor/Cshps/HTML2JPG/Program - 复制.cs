//using PuppeteerSharp;
//using System.Text.RegularExpressions;
//using Tesseract;

//namespace HTML2JPG;
//public class Program
//{
//    static async Task Main(string[] args)
//    {
//        //var r = await Puppeteer.CreateBrowserFetcher(new BrowserFetcherOptions() { Browser = SupportedBrowser.Chrome })
//        //    .DownloadAsync();
//        #region 正确
//        var browser = await Puppeteer.LaunchAsync(new LaunchOptions()
//        {
//            Browser = SupportedBrowser.Chrome,
//            Headless = false,
//            Args = new string[] { "--incognito" }
//        });

//        var 无痕上下文 = await browser.CreateBrowserContextAsync();
//        var 无痕页面 = await 无痕上下文.NewPageAsync();
//        await 无痕页面.SetViewportAsync(new ViewPortOptions() { Width = 1080 });
//        var page = await 无痕页面.GoToAsync("https://calamity.huijiwiki.com/wiki/%E6%B5%B7%E8%80%80%E4%B9%8B%E5%88%83");
//        //var page = await 无痕页面.GoToAsync("https://calamity.huijiwiki.com/wiki/%E4%BA%B5%E6%B8%8E%E5%AE%88%E5%8D%AB");
//        await 无痕页面.ScreenshotAsync("C:\\1-个博\\灾厄\\海耀之刃.png", new ScreenshotOptions() { FullPage = true });
//        await browser.CloseAsync();
//        #endregion
//    }

//    #region PuppeteerSharp Nuget PuppeteerSharp
//    /*
//    #region URL
//    //var httpC = new HttpClient();
//    ////Uri uri = new Uri("https://calamity.huijiwiki.com/api.php?action=parse&page=荒漠灾虫&format=json");
//    //Uri uri = new Uri("https://calamity.huijiwiki.com/api/rest_v1/page/html/%E8%8D%92%E6%BC%A0%E7%81%BE%E8%99%AB");
//    //HttpResponseMessage r = await httpC.GetAsync(uri);
//    //Console.WriteLine(await r.Content.ReadAsStringAsync());
//    #endregion

//    var r = await Puppeteer.CreateBrowserFetcher(new BrowserFetcherOptions() { Browser = SupportedBrowser.Chrome })
//        .DownloadAsync();
//    var browser = await Puppeteer.LaunchAsync(new LaunchOptions() 
//    { 
//        //Browser = SupportedBrowser.Chrome,
//        ExecutablePath = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
//        Headless = false,
//        Args = new string[] { "--incognito" }
//    });

//    #region 初探 不继承无痕
//    //var page = await browser.NewPageAsync();
//    //await page.SetUserAgentAsync("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
//    //var hmzc = await page.GoToAsync("https://calamity.huijiwiki.com/api/rest_v1/page/html/%E8%8D%92%E6%BC%A0%E7%81%BE%E8%99%AB");
//    ////var hmzc = await page.GoToAsync("https://www.baidu.com");
//    //await page.ScreenshotAsync("C:\\1-个博\\2.png");
//    #endregion

//    var 无痕上下文 = await browser.CreateBrowserContextAsync();
//    var 无痕页面 = await 无痕上下文.NewPageAsync();
//    await 无痕页面.SetViewportAsync(new ViewPortOptions() { Width = 1080 });
//    var page = await 无痕页面.GoToAsync("https://calamity.huijiwiki.com/api/rest_v1/page/html/%E8%8D%92%E6%BC%A0%E7%81%BE%E8%99%AB");
//    //var page = await 无痕页面.GoToAsync("https://calamity.huijiwiki.com/wiki/%E4%BA%B5%E6%B8%8E%E5%AE%88%E5%8D%AB");
//    await 无痕页面.ScreenshotAsync("C:\\1-个博\\灾厄\\亵渎守卫.png", new ScreenshotOptions() { FullPage = true });
//    await browser.CloseAsync();
//    */
//    #endregion

//    #region Microsoft.Playwright 秒变人机
//    //using var browe = await Playwright.CreateAsync();
//    //var r = await browe.Chromium.LaunchAsync();
//    //var page = await r.NewPageAsync();
//    //var gotoPage = await page.GotoAsync("https://calamity.huijiwiki.com/wiki/%E4%BA%B5%E6%B8%8E%E5%AE%88%E5%8D%AB");
//    //var image = await page.ScreenshotAsync(new PageScreenshotOptions() { Path = "C:\\1-个博\\灾厄\\亵渎守卫.png", FullPage = true });
//    #endregion

//    #region 图片文本识别
//    //var tEngine = new TesseractEngine("tessdata", "chi_sim", EngineMode.Default);
//    //Pix pix = Pix.LoadFromFile("C:\\1-个博\\灾厄\\亵渎守卫.png");
//    //Page page = tEngine.Process(pix);
//    //string str = page.GetText();
//    //Console.WriteLine(Regex.Replace(str, @"\s", ""));
//    #endregion


//}

