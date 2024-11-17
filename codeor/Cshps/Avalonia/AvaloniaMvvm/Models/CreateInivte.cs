using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using AvaloniaMvvm.Servicer;
using SQLite;
using AvaloniaMvvm.Models;

namespace AvaloniaMvvm.Models;

public class CreateInivte : ICreateInivte
{
    /// <summary>
    /// 步骤
    ///     1,创建一个SQLiteAsyncConnection类型的私有成员
    ///     2,创建一个SQLiteAsyncConnection类型的 公共 属性
    ///            给上面定义的私有成员赋值
    ///     3,实现初始化数据库的方法
    ///             使用 那个公共属性的异步方法CreateTableAsync<>()
    ///     4,实现插入数据的方法
    ///             使用 Connection(公共属性) 的 InsertAsync()
    /// </summary>
    
    private const string tableName = "TableName";
    private static readonly string TableFilePath = Path.Combine(PathReturn.getApplicConfPath(), tableName);
    
    //SQLite连接器
    private SQLiteAsyncConnection _connection;
    
    /// <summary>
    /// 获取数据库连接
    /// </summary>
    /// <returns></returns>
    private SQLiteAsyncConnection Connection => _connection ??= new SQLiteAsyncConnection(TableFilePath);
    
    /// <summary>
    /// 插入数据
    /// </summary>
    /// <param name="sqlDataType"> 插入的内容 </param>
    public async Task InsterAsync(SQLDataType sqlDataType)
    {
        await Connection.InsertAsync(sqlDataType);
    }

    /// <summary>
    /// 初始化数据库
    /// </summary>
    public async Task InitiaAsync()
    {
        //异步创建数据表
        await Connection.CreateTableAsync<SQLDataType>();
    }

    
    /// <summary>
    /// 返回全部数据
    /// </summary>
    public Task<List<SQLDataType>> ScanAsync()
    {
        //因为取数据需要知道具体类型 所以需要使用Table打开表
        return Connection.Table<SQLDataType>().ToListAsync();
    }

    /// <summary>
    /// 删除数据
    /// </summary>
    /// <param name="sqlDataType"></param>
    /// <returns></returns>
    public Task DeleteAsync(SQLDataType sqlDataType)
    {
        return Connection.DeleteAsync<SQLDataType>(sqlDataType);
    }
}