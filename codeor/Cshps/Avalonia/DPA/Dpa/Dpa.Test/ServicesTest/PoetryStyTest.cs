using Dpa.Library.ConfigFile;
using Dpa.Library.Services;
using Dpa.Test.DeleteDatabases;
using Moq;
using Xunit;

namespace Dpa.Test.ServicesTest;

public class PoetryStyTest : IDisposable
{
    public PoetryStyTest()
    {
        Delete.Del();
    }
    
    private PoetrySty poetrySty;
    [Fact]
    public async Task InitializeAsync_Def()
    {
        Mock<IConfig> IConfig = new Mock<IConfig>();
        IConfig MockIConfig = IConfig.Object;

        poetrySty = new PoetrySty(MockIConfig);
        //如果文件不存在测试通过
        Assert.False(File.Exists(PoetrySty.DbPath));
        //调用
        await poetrySty.InitializeAsync();
        //如果文件存在 测试通过
        Assert.True(File.Exists(PoetrySty.DbPath));
    }

    public void Dispose()
    {
        Delete.Del();
    }
}