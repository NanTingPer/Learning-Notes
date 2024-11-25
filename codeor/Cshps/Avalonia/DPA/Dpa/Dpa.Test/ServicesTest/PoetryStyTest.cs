using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Dpa.Library.ConfigFile;
using Dpa.Library.Models;
using Dpa.Library.Services;
using Dpa.Test.DeleteDatabases;
using Moq;
using Xunit;

namespace Dpa.Test.ServicesTest;

public class PoetryStyTest : IDisposable
{
    public PoetryStyTest()
    {
        PublicMethod.Del();
    }

    /// <summary>
    /// 获取一陀诗
    /// </summary>
    [Fact]
    public async Task GetPoetryAsync_AllDefault()
    {
        PoetryStyService poetrySty = await PublicMethod.GetPoetryStyAndInitia();
        List<Poetry> Poetrys = await poetrySty.GetPoetryAsync(
            //方法传参数 要求Expression<Func<Poetry,bool>>
            //设置始终返回 true  Expression.Constant(true)
            Expression.Lambda<Func<Poetry,bool>>(Expression.Constant(true),
                Expression.Parameter(typeof(Poetry),"p")),0,int.MaxValue);
        
        //断言 数组长度 等于 给定长度
        Assert.Equal(poetrySty.NumberPoetry,Poetrys.Count());
        await poetrySty.CloseConnection();
    }
    
    
    
    /// <summary>
    /// GetPoetryAsync 测试单条内容的获取
    /// </summary>
    /// <returns></returns>
    [Fact]
    public async Task GetPoetryAsync_Default()
    {
        PoetryStyService poetrySty = await PublicMethod.GetPoetryStyAndInitia();
        Poetry poetryAsync = await poetrySty.GetPoetryAsync("10001"); 
        Assert.Contains("临江仙",poetryAsync.Name);
        await poetrySty.CloseConnection();
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
        // Assert.False(File.Exists(PoetrySty.DbPath));
        //调用
        await poetrySty.InitializeAsync();
        //如果文件存在 测试通过
        Assert.True(File.Exists(PoetrySty.DbPath));
    }

    public void Dispose()
    {
        PublicMethod.Del();
    }
}