using PuppeteerSharp;
using System.Runtime.CompilerServices;
using System.Threading.Tasks;

namespace HTML2JPG;

public static class WebViewScreenshot
{
#pragma warning disable CS8618 // 在退出构造函数时，不可为 null 的字段必须包含非 null 值。请考虑添加 "required" 修饰符或声明为可为 null。
    static WebViewScreenshot() { }
#pragma warning restore CS8618 // 在退出构造函数时，不可为 null 的字段必须包含非 null 值。请考虑添加 "required" 修饰符或声明为可为 null。

    private static IBrowserContext browserContext;
    private static IBrowser browser;

    [ModuleInitializer]
    internal static async void InitBrowser()
    {
        browser = await Puppeteer.LaunchAsync(new LaunchOptions()
        {
            Headless = true,
            Browser = SupportedBrowser.Chrome,
            Timeout = 60000
        });
        browserContext = await browser.CreateBrowserContextAsync();
    }

    private static ScreenshotOptions screenshotOptions = new ScreenshotOptions()
    {
        FullPage = true,
        FromSurface = false,
    };

    public static async Task<byte[]> Screenshot(string url)
    {
        IPage newPage = await browserContext.NewPageAsync();
        await newPage.SetRequestInterceptionAsync(true);
        newPage.Request += AdBlock;
        await newPage.EvaluateExpressionAsync( //删除列表
            """
               document.querySelectorAll('[class*="terraria"][class*="mw-collapsible"][class*="mw-made-collapsible"]')
            .forEach(el => el.remove());
            """);

        byte[] values = await newPage.ScreenshotDataAsync(screenshotOptions);
        await newPage.CloseAsync();
        return values;
    }

    /// <summary>
    /// 广告拦截
    /// </summary>
    private static async void AdBlock(object? sender, RequestEventArgs e)
    {
        if (e.Request.Url.Contains("app.wiki.gg") || e.Request.Url.Contains("doubleclick")) {
            await e.Request.AbortAsync();
        } else {
            await e.Request.ContinueAsync();
        }
    }
}
