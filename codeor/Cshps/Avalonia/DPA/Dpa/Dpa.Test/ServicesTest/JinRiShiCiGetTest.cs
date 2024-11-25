using Dpa.Library.ConfigFile;
using Dpa.Library.Models;
using Dpa.Library.Services;
using Dpa.Test.DeleteDatabases;
using Moq;
using Xunit;

namespace Dpa.Test.ServicesTest;

public class JinRiShiCiGet_Test
{
    /// <summary>
    /// 获取 JinRiShiCiGet类所需的全部接口Mock
    /// </summary>
    /// <returns></returns>
    public async static Task<Tuple<JinRiShiCiGet, Mock<IAlertService>>> GetJinRi()
    {
        Mock<IConfig> iconfigMock = new Mock<IConfig>();
        IConfig config = iconfigMock.Object;
        
        Mock<IAlertService> ialertserviceMock = new Mock<IAlertService>();
        IAlertService alertService = ialertserviceMock.Object;
        
        IPoetrySty petrysty = await PublicMethod.GetPoetryStyAndInitia();//ipetrysty.Object;
        
        JinRiShiCiGet jinri = new JinRiShiCiGet(config, alertService, petrysty);
        
        return new Tuple<JinRiShiCiGet, Mock<IAlertService>>(jinri, ialertserviceMock);
    }

    public JinRiShiCiGet_Test()
    {
        PublicMethod.Del();
    }

    [Fact(Skip = "需要请求")]
    public async Task GetTokenAsync_Default()
    {
        Tuple<JinRiShiCiGet, Mock<IAlertService>> tup2 = await GetJinRi();
        JinRiShiCiGet jinri = tup2.Item1;
        string tokenAsync = await jinri.GetTokenAsync("https://v2.jinrishici.com/token");
        //返回结果不为空就是正确
        Assert.True(!string.IsNullOrEmpty(tokenAsync));
    }

    [Fact(Skip = "需要请求")]
    public async Task GetTokenAsync_ErrorURL()
    {
        Tuple<JinRiShiCiGet, Mock<IAlertService>> tup2 = await GetJinRi();
        JinRiShiCiGet jinri = tup2.Item1;
        Mock<IAlertService> ialertserviceMock = tup2.Item2;
        string tokenAsync = await jinri.GetTokenAsync("https://v2.6666.com/token");
        //使用错误的URL 如果报错信息被执行过一次就是通过
        ialertserviceMock.Verify(p => p.AlertAsync("今日诗词服务器", ""), Times.Once);
    }

    [Fact]
    public async Task GetToDayPoetry_Random()
    {
        Tuple<JinRiShiCiGet, Mock<IAlertService>> tup2 = await GetJinRi();
        JinRiShiCiGet jinri = tup2.Item1;
        ToDayPoetry toDayPoetry = await jinri.RandomGetPortryAsync();
        //因为是随机获取 所以数据源是本地 就是正确
        Assert.Equal(jinri.Source_DBSQL, toDayPoetry.Source);
    }


    public void Dispose()
    {
        PublicMethod.Del();
    }

}