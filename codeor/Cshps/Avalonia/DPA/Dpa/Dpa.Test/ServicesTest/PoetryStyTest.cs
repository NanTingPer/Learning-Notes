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
    
    private IPoetrySty poetrySty_IsInitialized;
    [Fact]
    public void IsInitialized_Default()
    {
        Mock<IConfig> IConfig = new Mock<IConfig>();
        
        
        //如果有人使用 PoetryStyConfigName.VersionKey,default(int) 去调用这个函数
        //返回PoetryStyConfigName.Version
        IConfig
            .Setup(f => f.Get(PoetryStyConfigName.VersionKey,default(int)))
            .Returns(PoetryStyConfigName.Version);
        IConfig Config = IConfig.Object;
        
        poetrySty_IsInitialized = new PoetrySty(Config);
        
        //测试是否为True
        Assert.True(poetrySty_IsInitialized.IsInitialized); 
        
        //是否有人使用给定参数，并且调用了一次
        IConfig.Verify(f => f.Get(PoetryStyConfigName.VersionKey,default(int)), Times.Once());
    }
    
    
    private IPoetrySty poetrySty;
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