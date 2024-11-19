using Dpa.Library.Services;
using Xunit;

namespace Dpa.Test.ServicesTest;

public class PoetryStyTest
{
    [Fact]
    public async Task InitializeAsync_Def()
    {
        PoetrySty poetrySty = new PoetrySty();
        //如果文件不存在测试通过
        Assert.False(File.Exists(poetrySty.DbPath));
        //调用
        await poetrySty.InitializeAsync();
        //如果文件存在 测试通过
        Assert.True(File.Exists(poetrySty.DbPath));
        
        File.Delete(poetrySty.DbPath);
    }
}