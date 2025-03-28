using System.IO;
using System.Threading.Tasks;
using Dpa.Library.ConfigFile;
using Dpa.Library.Services;
using Dpa.Library.Task;
using Moq;

namespace Dpa.Test.DeleteDatabases;
public class PublicMethod
{
    /// <summary>
    /// 删除全部文件
    /// </summary>
    public static void Del()
    {
        if (Directory.Exists(PathFile.getPath()))
        {
            Directory.Delete(PathFile.getPath(), true);
        }
    }

    
    /// <summary>
    /// 获取一个PoetrySty 每次都必须迁移数据库的
    /// </summary>
    /// <returns> 返回最终的PoetrySty </returns>
    public static async Task<PoetryStyService> GetPoetryStyAndInitia()
    {
        Mock<IConfig> Iconfig = new Mock<IConfig>();
        
        //伪造返回值
        Iconfig.Setup(p => p.Get(PoetryStyConfigName.VersionKey, -1)).Returns(-1);
        IConfig config = Iconfig.Object;

        //构建对象
        PoetryStyService poetryStyService = new PoetryStyService(config);
        await poetryStyService.InitializeAsync();
        return poetryStyService;
    }
}