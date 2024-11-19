using Dpa.Library.ConfigFile;
using Dpa.Library.Models;
using Dpa.Library.Task;
using SQLite;

namespace Dpa.Library.Services;

public class PoetrySty : IPoetrySty
{
    /// <summary>
    /// 判断版本号
    /// </summary>
    public bool IsInitialized => _config.Get(PoetryStyConfigName.VersionKey, default(int)) == PoetryStyConfigName.Version;

    private IConfig _config;

    public PoetrySty(IConfig config)
    {
        _config = config;
    }
    
    /// <summary>
    /// 有多少首诗
    /// </summary>
    public const int NumberPoetry = 30;

    public const string DbName = "poetrydb.sqlite3";
    
    /// <summary>
    /// 数据库路径
    /// </summary>
    public static readonly  string DbPath = PathFile.GetFilePath(DbName);
    
    private SQLiteAsyncConnection _connection;

    /// <summary>
    /// 获取数据库连接
    /// </summary>
    private SQLiteAsyncConnection Connection
    {
        get => _connection;
        set => _connection ??= new SQLiteAsyncConnection(DbPath);
    }
    
    /// <summary>
    /// 迁移数据库文件
    /// </summary>
    public async System.Threading.Tasks.Task InitializeAsync()
    {
        if (!IsInitialized)
        {
            //目标文件流，模式为 存在打开 不存在 创建
            await using FileStream FromStream = new FileStream(DbPath, FileMode.OpenOrCreate);

            //资源文件流
            await using Stream DbStream = typeof(PoetrySty).Assembly.GetManifestResourceStream(DbName);

            //复制流
            await DbStream.CopyToAsync(FromStream);

            //版本迁移
            _config.Set(PoetryStyConfigName.VersionKey, PoetryStyConfigName.Version);
        }
    }

    public Task<Poetry> GetPoetryAsync(string id)
    {
        throw new NotImplementedException();
    }

    public Task<Poetry> GetPoetryAsync(Func<Poetry, bool> where, int skip, int take)
    {
        throw new NotImplementedException();
    }
}

public static class PoetryStyConfigName
{
    public static readonly int Version = 1;
    public static readonly string VersionKey = nameof(PoetryStyConfigName) + "." + nameof(Version);
}