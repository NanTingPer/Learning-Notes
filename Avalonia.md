### Avalonia

# App.axaml / axaml

> - xmlns:名字="using:dpa" => 这个名字直接代表这个命名空间
> 
> - local:ServiceLocator => 表示dpa.ServiceLocator

- ??=
  
  - Rxxx ??= xxx
  
  - 如果 Rxxx 不为空 返回xxx 否则直接返回Rxxx

- 函数的调用
  
  - 有相当于函数的东西，这个东西回过来去调用Services(事务 功能的实现)
  
  - 在ViewModel 内创建一个私有的readonly，**事务接口类型的私有变量** 
  
  - **通过构造函数获取 事务接口 类型的实例并赋给私有变量**
    
    - 这样就非常明显的把依赖关系显示出来了
  
  - 如果你想要**别人去调用**你这个 **事务** 得先**写个东西**让别人能调用

# 一、MVVM设计模式

- Avalonia的MVVM很古典 没有特别的封装

- ViewModel的东西最终会被View显示出来

# 1.1

> Avalonia自带依赖注入容器
> 
> AvaloniaLocator.Current.GetService<类名>();获取

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

# 1.2 将数据输出到View

假设需要把一条消息带到View层

> #### ViewModel

- 准备一个私有的成员变量(代表要显示在VIew层的数据) 

- 包装一下,包装成 属性
  
  - get 直接返回 私有变量
  
  - set => SetPrpoperty(ref 私有, value)

- 定义一个方法(SayHello)，给属性赋值

- 给SayHello包装成一个 ICommand { get; }

- 在构造函数内，将ICommand与SayHello进行关联
  
  - SayHelloCommand = new RelayCommand(SayHello)

```csharp
namespace AvaloniaMvvm.ViewModels;
/// <summary>
/// @ => 显示Hello的步骤
/// </summary>
public partial class MainWindowViewModel : ViewModelBase
{
    private readonly ICreateInivte _icreateInivte;

    //包装成ICommand
    //@ 4
    public ICommand SayHelloCommand { get; }


    /// <summary>
    /// 构造函数直接指明依赖关系
    /// </summary>
    /// <param name="icreateInivte"></param>
    public MainWindowViewModel(ICreateInivte icreateInivte)
    {
        _icreateInivte = icreateInivte;
        //绑定SayHello
        //@ 5
        SayHelloCommand = new RelayCommand(SayHello);
    }
    //要显示在View的数据
    //@ 1
    private string _message;

    //包装一下要显示在View的数据
    //@ 2
    public string Message
    {
        get => _message;
        set => SetProperty(ref _message, value);
    }

    //赋给定的值
    //@ 3
    private void SayHello(){Message = "Hello";}
}
```

### View如何与ViewModel联系起来

> ##### View

- 在axaml中 Window标签内的 就是View

- 模板中的 <vm:MainWindowViewModel> 就相当于 ViewModel = new MainWindowViewModel但这种方法只能new无参构造

### 服务定位器模式

> ##### 所有的Servier 和 ViewModel都是单例
> 
> ##### 为什么要弄ServiceLocator,为了解决View如何找到ViewModel
> 
> ##### 在项目中，除非是算法类对象，不然都不应该自己去new 对象

- 在项目中创建一个新的类 规范命名(ServiceLocator)
  
  - 相当于万能前台，你找什么对象 都找他
  
  - 就是对依赖注入容器的封装

- 安装依赖注入容器nuget包
  
  - Microsoft.Extensions.DependencyInjection

- ServiceLocator在该类的构造函数内进行ViewModel注册
  
  - 创建一个服务集`var serviceCollection = new ServiceCollection`
  
  - 注册ViewModel `serviceCollection.AddSingleton<ViewModel>()`
  
  - 注册ViewModel依赖`serviceCollection.AddSingleton<ICreateInivte,CreateInivte>()`  其中 前面是依赖的接口，后面是该接口的实现类

> ##### ServiceLocator 只能注册 不提供类型实例

- `serviceCollection.BuildServiceProvider();` 形成一个ServiceProvider，从里面取出对象，所以需要一个**成员变量** 用于**接受返回值** readonly修饰

- 创建一个/多个 ViewModel类型的私有变量 并封装成属性 只有get

> ##### MainWindow的View如何找到 他的ViewModel
> 
> 通过上面的ServiceLocator类就能获取属于他的ViewModel
> 
> 那不还得new吗，所以将ServiceLocator注册成资源

- App.axaml `Application.Resources`标签 注册资源，整个App共享
  
  - `ResourceDictionary` 标签内
    
    - `local:ServiceLocator x:Key="ServiceLocator"` new了ServiceLocator，他的名字的ServiceLocator

- MainWindow的Window标签添加`DataContext`属性
  
  - `= "{Binding MainWindowViewModel, Source={StaticResource ServiceLocator}"`
    
    - Binding 数据绑定
    
    - Source 数据源  
    
    - StaticResource 静态资源  
    
    - ServiceLocator 名称
  
  - => 去ServiceLocator内找 MainWindowViewModel

> ### 显示内容

- 在Window内添加 StackPanel标签 (类似HTML的p标签)
  
  - 将<TextBlock>放进去 里面的Binding xxx 改成Binding Message(前面定义的)
  
  - 添加按钮空间 绑定Command 为 SayHello

> 在ViewModel准备要显示的数据，要执行的功能
> 
>         准备服务定位器 内部引入依赖注入容器 将ViewModel与ViewModel依赖的类型都注册到依赖注入容器，在服务定位球通过依赖注入容器对外公开一个属性，任何人只要通过服务定位器内的这个属性，就能找到这个类型的实例
> 
>         将服务定位器注册为全局资源
> 
>         在View内通过全局资源找到服务定位器，再通过服务定位器 找到viewmodel,就可以在view通过viewmodel找到里面的内容了

> AvaloniaUseCompiledBindingsByDefault>false</AvaloniaUseCompiledBindingsByDefault => 在项目文件中，将这个改成false

> #### ServiceLocator

```csharp
namespace AvaloniaMvvm;
/// <summary>
/// @ => 步骤
/// </summary>
public class ServiceLocator
{
    //创建依赖注入容器
    //@ 1
    private ServiceCollection _serviceCollection = new ServiceCollection();

    //@ 3
    private readonly IServiceProvider _serviceProvider;
    //用于从容器获取制定类型实例
    /// <summary>
    /// 获取MainWindowViewModel实例
    /// @ 5
    /// </summary>
    public MainWindowViewModel MainWindowViewModel => _serviceProvider.GetService<MainWindowViewModel>();

    public ServiceLocator()
    {
        //向容器注册依赖
        //@ 2
        _serviceCollection.AddScoped<MainWindowViewModel>();
        _serviceCollection.AddScoped<ICreateInivte, CreateInivte>();
        //获取类型实例
        //@ 4
        _serviceProvider = _serviceCollection.BuildServiceProvider();
    }
}
```

> #### App.axaml

```xml
 <!-- 用于注册全局App资源 -->
<Application.Resources>
    <ResourceDictionary>
        <!-- local指向依赖注入容器  x:key 表示调用时使用的名 -->
        <local:ServiceLocator x:Key="ServiceLocator"></local:ServiceLocator>
    </ResourceDictionary>
</Application.Resources>
```

> #### MainWindow.axaml
> 
> DataContext是Window标签的

```xml
DataContext="{Binding MainWindowViewModel, Source={StaticResource ServiceLocator}}">
<!-- 上面的语句，用于获取MainWindowViewModel对象 --> 
<StackPanel>
        <!-- 将文本绑定到Text中 -->
        <TextBlock Text="{Binding Message}" HorizontalAlignment="Center" VerticalAlignment="Center"/>
        <!-- 这里绑定Command 一但事件被调用，Message内容就会被更改 而Text刚好绑定了Message这个内容 -->
        <Button Content="Clink My" Command="{Binding SayHelloCommand}">
        </Button>
</StackPanel>
```

# 1.3 基本CRUD (入门)

> 为主键字段打上特性标记(数据库内容的那个类型那边)
> 
> [PrimaryKey,AutoIncrement]

- 在事务接口定义两个方法

- 在事务实现实现两个方法

- 在ViewModel调用两个方法
  
  - InitializeAsync 调用之前创建的 CreateInivte 类内的方法
  
  - InitializeCommand 用于关联上面那个方法
    
    - 异步关联需要使用AsyncRelayCommand

- InsertAsync 调用之前的 CreateInivte 内相应的方法
  
  - 内容现场new

- InsertCommand 用于关联

- 在View创建两个按钮 分别绑定

- 安装 sqlitebrowser`sudo apt install sqlitebrowser`(用于查看sqlite数据)

- 实现 改查删

> 

- 在View上显示列表 `ItemsControl ItemsSource="{Binding xxx}"` 使用ItemsControl控件

- ItemsControl内有个`<ItemsControl.ItemTemplate>`
  
  - `<DataTemplate>`内创建文本控件`TextBlock`

> #### Proces

```csharp
//PrimaryKey是作为键
//AutoIncrement是自增
[PrimaryKey,AutoIncrement]
public int Id { get; set; }
```

## 1.0 增

> #### 接口

```cs
/// <summary>
/// 插入数据
/// </summary>
/// <param name="Proces"> 要被插入的数据 </param>
/// <returns></returns>
Task InsterAsync(SQLDataType sqlDataType);
```

> #### 事务类

```cs
/// <summary>
/// 插入数据
/// </summary>
/// <param name="sqlDataType"> 插入的内容 </param>
public async Task InsterAsync(SQLDataType sqlDataType)
{
    await Connection.InsertAsync(sqlDataType);
}
```

> #### ViewModel

```cs
/// <summary>
/// 用于插入数据
/// </summary>
/// <returns></returns>
private async Task InsData()
{
    await _icreateInivte.InsterAsync(new SQLDataType { Name = "1"/*new Random().NextInt64().ToString()*/ });
}

/// <summary>
/// 用来插入数据 绑定 InsData
/// </summary>
public ICommand InsDataCommand { get; }

//构造函数
InsDataCommand = new AsyncRelayCommand(InsData);
```

> #### View

```cs
<Button Content="插入数据" Command="{Binding InsDataCommand}"></Button>
```

## 2.0 查

> #### 接口

```cs
/// <summary>
/// 查
/// </summary>
/// <returns>返回全部数据</returns>
Task<List<Models.SQLDataType>> ScanAsync();
```

> #### 事务

```cs
/// <summary>
/// 返回全部数据
/// </summary>
public Task<List<SQLDataType>> ScanAsync()
{
    //因为取数据需要知道具体类型 所以需要使用Table打开表
    return Connection.Table<SQLDataType>().ToListAsync();
}
```

> #### ViewModel

```cs
/// <summary>
/// 数据集
/// </summary>
public ObservableCollection<SQLDataType> SQLDataList { get; set; } = new();
/// <summary>
/// 用于获取全部数据的方法
/// </summary>
/// <returns></returns>
private async Task GetSQLData()
{
    SQLDataList.Clear();
    List<SQLDataType> e = await _icreateInivte.ScanAsync();
    foreach (SQLDataType sqlDataType in e)
    {
        SQLDataList.Add(sqlDataType);
    }
}
```

> View

```cs
<Button Content="查看数据" Command="{Binding GetSQLDataCommand}"></Button>
<ItemsControl ItemsSource="{Binding SQLDataList}">
  <ItemsControl.ItemTemplate>
    <DataTemplate>
      <TextBlock Text="{Binding Name}"></TextBlock>
    </DataTemplate>
  </ItemsControl.ItemTemplate>
</ItemsControl>
```

## 3.0 删

> #### 接口

```cs
/// <summary>
/// 删
/// </summary>
Task DeleteAsync(SQLDataType sqlDataType);
```

> #### 事务

```cs
/// <summary>
/// 删除数据
/// </summary>
/// <param name="sqlDataType"></param>
/// <returns></returns>
public Task DeleteAsync(SQLDataType sqlDataType)
{
    return Connection.DeleteAsync(sqlDataType);
}
```

> #### ViewModel

```cs
/// <summary>
/// 用于删除数据
/// </summary>
/// <returns></returns>
private async Task DeleteData()
{
    await _icreateInivte.DeleteAsync(new SQLDataType() {Id = 1});
}
```

> #### View

```cs
<Button Content="删除数据" Command="{Binding DeleteDataCommand}"></Button>
```

# 2.0 DPA 👇

> 创建新项目，解决方案与项目不共同目录 名 Dpa
> 
> 安装sqlit-net-pcl **(给Dpa.Library)**
> 
> 安装Semi.Avalonia(UI组件库) nuget包 **(给Dpa)**
> 
> - 在App.axaml
>   
>   ```csharp
>   <Application.Styles>
>       <FluentTheme />
>   </Application.Styles>
>   
>   <!-- 更改为 -->
>   
>   <Application.Styles>
>           <semi:SemiTheme Locale="zh-CN" />
>   </Application.Styles>
>   
>   <!-- 顶级Application标签内 -->
>   
>   xmlns:semi="https://irihi.tech/semi"
>   ```
> 
> - 在解决方案创建新项目,Dpa.Library
>   
>   - 右键项目属性，将可空类型关闭
>   
>   - 新建模型 项目文件夹 Models
>   
>   - 下载数据库文件 gitee zhangyin  **poetrydb.sqlite3**
>   
>   - 新建模型类 **(Poetry)**
> 
> 特性 : SQLite.Ignore => 排除字段
> 
>            SQLite.Column("字段名") => 这个属性对应数据库哪个字段
> 
>            SQLite.Table("表名") => 这个类是哪个表的映射
> 
> - 新建服务(项目文件夹)/事务 规范命名:Services
> 
> - 在 服务 项目文件夹内新建 服务接口
> 
> - 在 服务 项目文件夹内新建 服务接口实现类
> 
> - 将数据库拷贝到**类库项目**文件夹下
>   
>   - 将其的Build action 更改为EmbeddedResource
>   
>   - 属性 -> 构建操作 -> EmbeddedResource

> ## 模型类

```cs
namespace Dpa.Library.Models;

[SQLite.Table("Works")]
public class Poetry
{
    [SQLite.Column("id")]
    public int Id { get; set; }

    [SQLite.Column("name")]
    public string Name { get; set; } = string.Empty;

    [SQLite.Column("author_name")]
    public string Author { get; set; } = string.Empty;

    [SQLite.Column("dynasty")]
    public string Dynasty { get; set; } = string.Empty;

    [SQLite.Column("content")] 
    public string Content { get; set; } = string.Empty;

    private string _snippet;

    [SQLite.Ignore]
    public string Snippet
    {
        get => _snippet;
        set => _snippet ??= Content.Split("。")[0].Replace("\r\n", "");
    }
}
```

## 1.0 拷贝数据库到用户文件

> #### 事务实现类

1. 更改资源文件全限定名
   
   1. 在编辑项目文件 找到 `Include`` EmbeddedResource`标签
   
   2. 添加
   
   `<LogicalName>poetrydb.sqlite3</LogicalName>`

2. 获取目标文件流(要复制到的地方)

`new FileStream(文件路径,FileMode.OpenOrCreate)`

```cs
<ItemGroup>
  <None Remove="poetrydb.sqlite3" />
  <EmbeddedResource Include="poetrydb.sqlite3">
      <LogicalName>poetrydb.sqlite3</LogicalName>
  </EmbeddedResource>
</ItemGroup>
```

1. 如果 文件存在打开，不存在 创建

2. 记得关闭 `Stream.Close()` 也可以在定义流行的前面添加两个修饰 `await using` 会自动释放资源

3. 获取资源文件流`typeof(PoetrySty).Assembly.GetManifestResourceStream(DbName)` DbName是 文件在项目内的名称 一样要加 `await using` 

4. 流对流拷贝 `await 资源流.CopyToAsync(目标流)`

> ## 事务接口

```cs
using System.Linq.Expressions;
using Dpa.Library.Models;

namespace Dpa.Library.Services;

public interface IPoetrySty
{
    /// <summary>
    /// 判断数据库是否迁移到用户应用目录
    /// </summary>
    bool IsInitialized { get; }

    /// <summary>
    /// 用来初始化数据库
    /// </summary>
    System.Threading.Tasks.Task InitializeAsync();

    /// <summary>
    /// 获取数据
    /// </summary>
    /// <param name="poetryName"> 该条数据在数据库中的ID </param>
    /// <returns></returns>
    Task<Poetry> GetPoetryAsync(string id);

    /// <summary>
    /// 通过过滤匹配
    /// </summary>
    /// <param name="where"></param>
    /// <param name="skip">跳过多少行</param>
    /// <param name="take">返回多少行</param>
    /// <returns></returns>
    Task<List<Poetry>> GetPoetryAsync(Expression<Func<Poetry,bool>>  where,int skip,int take);
}
```

> ## 事务实现
> 
> 后面才发现没贴上来 部分方法已经实现

```cs
using System.Linq.Expressions;
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
    public readonly int NumberPoetry = 30;

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
        get => _connection ??= new SQLiteAsyncConnection(DbPath);
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

    /// <summary>
    /// 获取给定ID的数据
    /// </summary>
    /// <param name="id"> 要获取的id </param>
    /// <returns> 返回对应的数据 </returns>
    public Task<Poetry> GetPoetryAsync(string id)
    {
        //FirstOrDefaultAsync是异步获取数据
        //返回第一条匹配的数据 或 返回空
        return Connection.Table<Poetry>().FirstOrDefaultAsync(poer => poer.Id.Equals(id));
    }

    /// <summary>
    /// 获取给定条件的诗歌
    /// </summary>
    /// <param name="where"> Func委托 </param>
    /// <param name="skip"></param>
    /// <param name="take"></param>
    /// <returns></returns>
    public Task<List<Poetry>> GetPoetryAsync(Expression<Func<Poetry, bool>> where, int skip, int take)
    {
        //Func<Poetry,bool> where
        //Connection.Table<Poetry>().where(f => where(f))
         return Connection.Table<Poetry>().Where(where).Skip(skip).Take(take).ToListAsync();
    }

    /// <summary>
    /// 关闭数据库
    /// </summary>
    /// <returns> 空 </returns>
    public System.Threading.Tasks.Task CloseConnection()
    {
        return Connection.CloseAsync();
    }

}

public static class PoetryStyConfigName
{
    public static readonly int Version = 1;
    public static readonly string VersionKey = nameof(PoetryStyConfigName) + "." + nameof(Version);
}
```

## 2.0 单元测试

> 新建 Unit Test项目 项目名自定义 规范化: `项目名.UnitTest` 类型xUnit
> 
> 将单元测试项目依赖于被测试项目
> 
> 单元测试的目录形式与被测设项目一致
> 
> 创建PoetryStyTest 测试项目，测试类命名规范 : 类名 + Test
> 
> 测试方法命名规范 : Test + 方法名
> 
> 测试方法命名规范 : 方法名_测试条件 **(建议)**
> 
> - 方法名_用户正常输入
> 
> - 方法名_用户不正常输入
> 
> 单元测试方法需要使用 **[Fact]** 特性标记
> 
> 在单元测试中 调用前使用 `Assert.False(File.Exists(FilePath)` 用于测试文件是否存在，如果是False测试通过，反之
> 
> 调用后使用`Assert.True(File.Exists(FilePath)` 一样的 因为是为了测试迁移是否成功

> ### 单元测试

```csharp
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
```

### 2.1 单元测试的资源清理

1. 单元测试类继承 IDisposable接口

2. 定义Dispose方法 里面实现/调用方法 进行资源清理

```cs
public class PoetryStyTest : IDisposable
{
    private PoetrySty poetrySty;
    [Fact]
    public async Task InitializeAsync_Def()
    {
        poetrySty = new PoetrySty();
        //如果文件不存在测试通过
        Assert.False(File.Exists(poetrySty.DbPath));
        //调用
        await poetrySty.InitializeAsync();
        //如果文件存在 测试通过
        Assert.True(File.Exists(poetrySty.DbPath));
    }
    public void Dispose()
    {
        Delete.Del(poetrySty.DbPath);
    }
}

namespace Dpa.Test.DeleteDatabases;
public class Delete
{
    public static void Del(string FilePath) => File.Delete(FilePath);
}
```

3. 在测试函数运行之前也应该清理一次
   
   在构造函数内清理一次
   
   使用内置方法 进行清理
   
   - Dispose => 结束后
   
   - PoetryStyTest => 构造函数内

```cs
public PoetryStyTest()
{
    PublicMethod.Del();
}

public void Dispose()
{
    PublicMethod.Del();
}
/// <summary>
/// 删除全部文件
/// </summary>
public static void Del() => Directory.Delete(PathFile.getPath(),true);
```

## 3.0 键值存储

1. 定义接口
   
   1. Get Set方法 第一个参数三key,第二个是value
   
   2. 分别建立 int,string,DataTime类型的GetSet

2. 创建类实现读写
   
   1. GetSet方法，键是文件名，值是内容

3. 在事务类构造函数 依赖于 存储类 （类型写接口）, 
   
   1. 定义一个私有变量
   
   2. 构造函数内赋值

4. 在事务类内，定义一个版号判断的方法

> #### IConfig接口

```cs
namespace Dpa.Library.ConfigFile;
public interface IConfig
{
    void Set(string key,string value);
    string Get(string key, string value);

    void Set(string key,int value);
    int Get(string key, int value);

    void Set(string key,DateTime value);
    DateTime Get(string key, DateTime value);
}
```

> #### Config

```cs
namespace Dpa.Library.ConfigFile;
public class Config : IConfig
{
    /// <summary>
    /// 写入配置数据
    /// </summary>
    /// <param name="key"> 配置名 </param>
    /// <param name="value"> 写入的值 </param>
    private void SetData(string key, string value)
    {
        string filePath = PathFile.GetFileOrCreate(key);
        File.WriteAllText(filePath,value);
    }
    /// <summary>
    /// 读取配置数据
    /// </summary>
    /// <param name="key"> 键 </param>
    /// <returns></returns>
    private String Get(string key) => File.ReadAllText(PathFile.GetFileOrCreate(key));
    public void Set(string key, string value)
    {
        SetData(key,value);
    }
    public string Get(string key, string value)
    {
        if(Get(key) == null) return value;
        return Get(key);
    }
    public void Set(string key, int value)
    {
        SetData(key,value.ToString());
    }
    public int Get(string key, int value)
    {
        if(Get(key) == null) return value;
        return int.Parse(Get(key));
    }
    public void Set(string key, DateTime value)
    {
        SetData(key,value.ToString());
    }
    public DateTime Get(string key, DateTime value)
    {
        if(Get(key) == null) return value;
        return Convert.ToDateTime(Get(key));
    }
}
```

> #### PoetrySty

```cs
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

public static class PoetryStyConfigName
{
    public static readonly int Version = 1;
    public static readonly string VersionKey = nameof(PoetryStyConfigName) + "." + nameof(Version);
}
```

### 3.1单元测试

> 由于为构造函数设置了传入参数 所以单元测试报错了
> 
> 安装Moq NuGet包 （Mock技术）
> 
> Mock用于伪造一个接口实现
> 
> 只有采用面向接口设计，才能在单元测试的时候，使用Mock来进行测试

```cs
Mock<IConfig> IConfig = new Mock<IConfig>();
IConfig MockIConfig = IConfig.Object;
poetrySty = new PoetrySty(MockIConfig);
```

### 3.2 版本判断单元测试

| Setup  | 控制Mock对象的行为 |
| ------ | ----------- |
| Verify | 验证调用行为规范    |

```cs
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

    //测试是否为True(断言这里为True)
    Assert.True(poetrySty_IsInitialized.IsInitialized); 

    //是否有人使用给定参数，并且调用了一次
    IConfig.Verify(f => f.Get(PoetryStyConfigName.VersionKey,default(int)), Times.Once());

}
```

# 3.1 单首获取 单元测试

1. 获取PoetrySty 由于需要反复获取，所以创建一个公共方法

```cs
/// <summary>
/// 获取一个PoetrySty 每次都必须迁移数据库的
/// </summary>
/// <returns> 返回最终的PoetrySty </returns>
public static async Task<PoetrySty> GetPoetryStyAndInitia()
{
    Mock<IConfig> Iconfig = new Mock<IConfig>();

    //伪造返回值
    Iconfig.Setup(p => p.Get(PoetryStyConfigName.VersionKey, -1)).Returns(-1);
    IConfig config = Iconfig.Object;
    //构建对象
    PoetrySty poetrySty = new PoetrySty(config);
    await poetrySty.InitializeAsync();
    return poetrySty;
}
```

2. 创建单元指定方法的单元测试并测试

```cs
/// <summary>
/// GetPoetryAsync 测试单条内容的获取
/// </summary>
/// <returns></returns>
[Fact]
public async Task GetPoetryAsync_Default()
{
    PoetrySty poetrySty = await PublicMethod.GetPoetryStyAndInitia();
    Poetry poetryAsync = await poetrySty.GetPoetryAsync("10001"); 
    Assert.Contains("临江仙",poetryAsync.Name);
}
```

3. 由于前面失误操作，需要更改PoetrySty的一些代码

```cs
/// <summary>
/// 获取数据库连接
/// </summary>
private SQLiteAsyncConnection Connection
{
    get => _connection ??= new SQLiteAsyncConnection(DbPath);
}
```

# 3.3 诗歌全加载

```cs
public Task<List<Poetry>> GetPoetryAsync(Expression<Func<Poetry, bool>> where, int skip, int take)
{
    //Func<Poetry,bool> where
    //Connection.Table<Poetry>().where(f => where(f))
     return Connection.Table<Poetry>().Where(where).Skip(skip).Take(take).ToListAsync();
}
```

> 单元测试

```cs
/// <summary>
/// 获取一陀诗
/// </summary>
[Fact]
public async Task GetPoetryAsync_AllDefault()
{
    PoetrySty poetrySty = await PublicMethod.GetPoetryStyAndInitia();
    List<Poetry> Poetrys = await poetrySty.GetPoetryAsync(
        //方法传参数 要求Expression<Func<Poetry,bool>>
        //设置始终返回 true  Expression.Constant(true)
        Expression.Lambda<Func<Poetry,bool>>(Expression.Constant(true),
            Expression.Parameter(typeof(Poetry),"p")),0,int.MaxValue);

    //断言 数组长度 等于 给定长度
    Assert.Equal(poetrySty.NumberPoetry,Poetrys.Count());
    await poetrySty.CloseConnection();
}
```

# 3.4  ViewModel

> ViewModel只为View层准备数据，不与View层发生关系，应该独立
> 
> 因此ViewModel也方在Library
> 
> **前提 CommunityToolkit.Mvvm (nuget包)**
> 
> View **Microsoft.Extensions.DependencyInjection (nuget包)**
> 
> - **如果提示版本不对 更改即可**
> 
> 项目之间不能相互依赖，所以还需要创建一个ViewModelBase继承ObservableObject

1. 创建ViewModel类，继承ViewModelBase

2. ViewModel内使用ICommxxx包装 业务

3. 创建依赖注入 ServiceLocator

4. 在App.axaml内 注册资源

5. 删除MainWindow自带的Design.DataContext 并绑定

```sc
DataContext="{Binding xxxxViewModel,Source={StaticResource ServiceLocator} }"
```

6. 删除 x:DataType="vm:MainWindowViewModel"

7. 在Veiw显示诗 使用ItemsControl控件 绑定 诗歌集合`ItemsSource="{Binding xxx}"`
   
   <DataTemplate><TextBlock Text="{Binding Name}"> 
   
   只是现在显示用 后面还得删

8. 在View模块安装 Avalonia.Xaml.Behaviors nuget包

9. 引入两个名称空间

```xml
xmlns:i="using:Avalonia.Xaml.Interactivity"
xmlns:ia="using:Avalonia.Xaml.Interactions.Core"
```

10. 对事件的触发进行绑定
    
    事件名 = Initialized
    
    ```xml
    <i:Interaction.Behaviors>
        <ia:EventTriggerBehavior EventName="事件名">
            <ia:InvokeCommandAction Command="{Binding ICommand名}">
    ```



> #### 创建ViewModel类，继承ViewModelBase

```cs
public class ContentViewModel : ViewModelBase{}
```

> #### ViewModel内使用ICommxxx包装 业务

```cs
public ICommand GetPoetryAllICommand;

private readonly IPoetrySty _poetrySty;

public ContentViewModel(IPoetrySty poetrySty)
{
    _poetrySty = poetrySty;
    GetPoetryAllICommand = new AsyncRelayCommand(GetPoetryAsyncAll);
}
public ObservableCollection<Poetry> PoetryList { get; } = new();

/// <summary>
/// 获取全部数据
/// </summary>
private async System.Threading.Tasks.Task GetPoetryAsyncAll()
{
    //每次调用
    PoetryList.Clear();
    
    List<Poetry> Poetrys = await _poetrySty.GetPoetryAsync(
        //方法传参数 要求Expression<Func<Poetry,bool>>
        //设置始终返回 true  Expression.Constant(true)
        Expression.Lambda<Func<Poetry,bool>>(Expression.Constant(true),
            Expression.Parameter(typeof(Poetry),"p")),0,int.MaxValue);
    foreach (Poetry poetry in Poetrys)
    {
        PoetryList.Add(poetry);
    }
}
```

> #### 依赖注入ServiceLocator

```cs

```
