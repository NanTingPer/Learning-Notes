using PuppeteerSharp;

namespace WebImagePupp
{
    internal class Program
    {
        static void Main(string[] args)
        {
            Task.Run(async () =>
            {
                var bf = new BrowserFetcher();
                //下载浏览器
                await bf.DownloadAsync();
                Environment.Exit(0);
            });

            while (true)
            {
            };
        }
    }
}
