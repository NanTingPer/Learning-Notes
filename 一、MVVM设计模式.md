### Avalonia

- ??=
  
  - Rxxx ??= xxx
  
  - 如果 Rxxx 不为空 返回xxx 否则直接返回Rxxx

# 一、MVVM设计模式

- Avalonia的MVVM很古典 没有特别的封装

- ViewModel的东西最终会被View显示出来

## 1.1 实战

1. 创建一个数据模型
   
   1. Poetry类
      
      - Id
      
      - Name (初始空)

2. 创建一个数据库访问接口
   
   1. 新文件夹 Services
   
   2. 添加接口
      
      1. IPoetryStorage
         
         - InsertAsync(Poetry xx) 返回 Task 用于插入数据
         
         - InitializeAsync() 用于初始化数据库

3. 创建接口实现类(单独文件夹) 实现成员

4. 安装SQLite依赖（nuget）
   
   - sqlite-net-pcl

5. 在实现类中创建 常量 (数据库名)

6. 创建新的 项目文件夹
   
   1. 创建一个类 用于获取数据库存放位置

7. 实现 初始化接口



> 接口ICreateInivte

```csharp
using System.Diagnostics;
using System.Threading.Tasks;

namespace AvaloniaMvvm.Servicer;

public interface ICreateInivte
{
    /// <summary>
    /// 插入数据
    /// </summary>
    /// <param name="process"> 要被插入的数据 </param>
    /// <returns></returns>
    Task InsterAsync(Process process);

    Task InitiaAsync();
}
```



> 模型Proces

```csharp
using System;

namespace AvaloniaMvvm.Models;

public class Proces
{
    public int Id { get; set; }
    public string Name { get; set; } = String.Empty;
}
```



> 获取文件位置PathReturn

```csharp
using System.IO;
namespace AvaloniaMvvm.Models;

public class PathReturn
{
    /// <summary>
    /// 获取本软件的文件存放位置
    /// </summary>
    /// <returns></returns>
    public static string getApplicConfPath()
    {
        //获取系统给定的应用文件存放位置
        string FilePath = Path.Combine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData), "AvaloniaMvvm");
        if (File.Exists(FilePath))
        {
            File.Create(FilePath).Close();
            return FilePath;
        }
        return FilePath;
    }
}
```



> 操作代码

```csharp
using System.Diagnostics;
using System.IO;
using System.Threading.Tasks;
using AvaloniaMvvm.Servicer;
using SQLite;
using Tmds.DBus.Protocol;

namespace AvaloniaMvvm.Models;

public class DbRun : ICreateInivte
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
    /// <param name="process"> 插入的内容 </param>
    public async Task InsterAsync(Process process)
    {
        await Connection.InsertAsync(process);
    }

    /// <summary>
    /// 初始化数据库
    /// </summary>
    public async Task InitiaAsync()
    {
        //异步创建数据表
        await Connection.CreateTableAsync<Process>();
    }
}
```


