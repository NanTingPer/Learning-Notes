### Avalonia

# ViewModel

| PropertyChanged | 每次属性改变 都会调用这个事件 |
| --------------- | --------------- |

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

- #### 前端会订阅后端的事件，因此后端数据的改变前端能够实时变化 前后端分离开的逻辑也避免了后端线程导致UI线程卡死

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

> 这个IsInitialized是后面的东西 前面忘记粘贴代码了

```cs
/// <summary>
/// 初始化 迁移数据库文件
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
```

```cs
 /// <summary>
 /// 判断版本号
 /// </summary>
 public bool IsInitialized => _config.Get(PoetryStyConfigName.VersionKey, default(int)) == PoetryStyConfigName.Version;
```

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
using System.Runtime.Serialization;
using Dpa.Library.Task;

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
        if (Get(key).Equals(""))
        {
            SetData(key, value.ToString());
            return value;
        }
        return int.Parse(Get(key));
    }

    public void Set(string key, DateTime value)
    {
        SetData(key,value.ToString());
    }

    public DateTime Get(string key, DateTime value)
    {
        if (Get(key).Equals(""))
        {
            SetData(key, value.ToString());
            return value;
        }
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

- ### ViewModel 标记 Github提交

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

8. 在View模块安装 **Avalonia.Xaml.Behaviors nuget包**

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

11. 由于App.axaml.cs内已经绑定了 DataContext = new MainWindowViewModel(), 所以该行要删除

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
using System;
using Avalonia;
using Dpa.Library.ConfigFile;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;
using Microsoft.Extensions.DependencyInjection;

namespace Dpa;

public class ServiceLocator
{
    //依赖注入容器
    private ServiceCollection _serviceCollection = new ServiceCollection();
    private IServiceProvider _serviceProvider;

    //对外暴露ContentViewModel
    public ContentViewModel ContentViewModel => _serviceProvider.GetService<ContentViewModel>();


    /// <summary>
    /// 不知道 抄的
    /// </summary>
    private static ServiceLocator _current;
    public static ServiceLocator Current
    {
        get
        {
            if (_current is not null) return _current;
            if (Application.Current.TryGetResource(nameof(ServiceLocator),
                    null,
                    out var value) &&
                value is ServiceLocator serviceLocator) return _current = serviceLocator;
            throw new Exception("?????理论上不应该发生这种情况");
        }
    }

    //注入依赖
    public ServiceLocator()
    {
        _serviceCollection.AddScoped<ContentViewModel>();
        _serviceCollection.AddScoped<IPoetrySty, PoetrySty>();
        //后面加的 忘记PoetrySty需要Config作为参数了
        _serviceCollection.AddScoped<IConfig, Config>();

        _serviceProvider = _serviceCollection.BuildServiceProvider();
    }

}
```

> #### 在App.axaml内 注册资源

```xml
<!-- 注册资源 -->
<Application.Resources>
    <ResourceDictionary>
        <local:ServiceLocator x:Key="ServiceLocator"></local:ServiceLocator>
    </ResourceDictionary>
</Application.Resources>
```

> #### ViewModel

```xml
DataContext="{Binding ContentViewModel ,Source={StaticResource ServiceLocator}}"

<!-- 事件绑定 -->
<i:Interaction.Behaviors>
    <ia:EventTriggerBehavior EventName="Initialized">
        <ia:InvokeCommandAction Command="{Binding GetPoetryAllICommand}"></ia:InvokeCommandAction>
    </ia:EventTriggerBehavior>
</i:Interaction.Behaviors>
<!-- 数据显示的绑定 -->
<ItemsControl ItemsSource="{Binding PoetryList}">
    <ItemsControl.ItemTemplate>
        <DataTemplate>
            <TextBlock Text="{Binding Name}"></TextBlock>
        </DataTemplate>
    </ItemsControl.ItemTemplate>
</ItemsControl>
```

# 3.5 无限滚动

1. 为项目添加包 nuget **AvaloniaInfiniteScrolling.Collection** 两个项目都要

2. 在`App.axaml`的`Application.Styles`中添加`<StyleInclude Source="avares://AvaloniaInfiniteScrolling.Control/AvaloniaInfiniteScrollControlStyle.axaml" />`。

3. 修改 ViewModel
   
   - 将其恢复到只有构造函数 IPoetrySty

4. 创建`AvaloniaInfiniteScrollCollection<T> Name {get;}` 不给初始值

5. 在构造函数内进行初始化 new() 此时构造函数内只有两条语句

| OnCanLoadMore | 判断能不能被继续加载 | 需要为 对象提供一个函数用来执行判断 |
| ------------- | ---------- | ------------------ |
| OnLoadMore    | 能被继续加载加载数据 | 需要为 对象提供数据，数据会被加载  |

- Onxxxx代表 可以被 `.`之前的对象调用
7. 跳过条数为 当前集所有的数量 并指定返回的数量

8. 种告诉它数据(为了测试)

9. 更改Veiw层
   
   - 删除 Interaction.Behaviors 因为已经不需要通过事件去加载了
   
   - 引入名称空间 `xmlns:ais="using:AvaloniaInfiniteScrolling"`
   
   - 替换ItemsControl 控件
     
     - 使用新增命名空间下的`AvaloniaInfiniteScrollControl` 指定数据来源 `ItemsSource="{Binding xxx}"`
     
     - xxx:AvaloniaInfiniteScrollControl.ItemTemplate
       
       DataTemplate
       
       TextBlock Text="{Binding xxx}"
       
       
       
       
       <DataTemplate>
       
       <TextBlock Text="{Binding xxx">

## 

> #### 简单实现

> #### View Model

```cs
public AvaloniaInfiniteScrollCollection<Poetry> AvaloniaInfiniteScrolling { get; }
public ContentViewModel(IPoetrySty poetrySty)
{
    _poetrySty = poetrySty;
    // GetPoetryAllICommand = new AsyncRelayCommand(GetPoetryAsyncAll);
    AvaloniaInfiniteScrolling = new AvaloniaInfiniteScrollCollection<Poetry>()
    {
        //条件永远为True
        OnCanLoadMore = () => true,
        //载入数据
        OnLoadMore = () =>
        {
            //需求IEnumerable
            Task<List<Poetry>> tlp = _poetrySty.GetPoetryAsync(f => true, 0, 10);
            return tlp.ContinueWith(t => t.Result.AsEnumerable());
        }
    };
}
```

> ### App.axaml

```xml
<Application.Styles>
    <avalonia:SemiTheme Locale="zh-CN" />
    <StyleInclude Source="avares://AvaloniaInfiniteScrolling.Control/AvaloniaInfiniteScrollControlStyle.axaml" />
</Application.Styles>
```

> ### View

```cs
xmlns:ais="using:AvaloniaInfiniteScrolling"

<!-- DataContext 绑定  -->
<!-- 事件绑定 -->
<!-- <i:Interaction.Behaviors> -->
<!--     <ia:EventTriggerBehavior EventName="Initialized"> -->
<!--         <ia:InvokeCommandAction Command="{Binding GetPoetryAllICommand}"></ia:InvokeCommandAction> -->
<!--     </ia:EventTriggerBehavior> -->
<!-- </i:Interaction.Behaviors> -->
 
<ais:AvaloniaInfiniteScrollControl ItemsSource="{Binding AvaloniaInfiniteScrolling}">
    <ItemsControl.ItemTemplate>
        <DataTemplate>
            <TextBlock Text="{Binding Name}"></TextBlock>
        </DataTemplate>
    </ItemsControl.ItemTemplate>
</ais:AvaloniaInfiniteScrollControl>

<!--     ~1~ 数据显示的绑定 @1@ -->
<!-- <ItemsControl ItemsSource="{Binding PoetryList}"> -->
<!--     <ItemsControl.ItemTemplate> -->
<!--         <DataTemplate> -->
<!--             <TextBlock Text="{Binding Name}"></TextBlock> -->
<!--         </DataTemplate> -->
<!--     </ItemsControl.ItemTemplate> -->
<!-- </ItemsControl> -->
```



## 3.5.1 单元测试

1. 测试项目新建 ViewModels 项目文件夹

2. 创建ViewModelTest测试类

3. 在测试目录下创建文件 xunit.runner.json

> 使用单线程跑测试

```json
{
    "parallelizeAssembly": false,
    "parallelizeTestCollections": false
}
```

> 项目属性 Copy to output directory : Copy if newer

4. VeiwModel内有一个事件PropertyChanged => 用于判断属性改变

5. 测试 变化次数 状态的正确性 数据数的正确性

> ### 简单测试

```cs
public class ContentViewModel_Test
{
    [Fact]
    public async void AvaloniaInfiniteScrolling_Default()
    {
       PoetrySty poSty = await PublicMethod.GetPoetryStyAndInitia();
       ContentViewModel Cv = new ContentViewModel(poSty);
       AvaloniaInfiniteScrollCollection<Poetry> count = Cv.AvaloniaInfiniteScrolling;
       
       //翻阅源码得知，调用这个方法会对数据进行Load
       //OnLoadMore只是对加载方法的定义
       await Cv.AvaloniaInfiniteScrolling.LoadMoreAsync();
       Assert.Equal(10,getCount(count));
       Cv.PropertyChanged += (sender, args) =>
       {
           Assert.True("ScrollingState".Equals(args.PropertyName));
       };
       int getCount<T>(IEnumerable<T> ie)
       {
           return ie.Count();
       }
    }
}
```



# 3.6 访问Json Web服务

1. 创建服务接口 规范命名 : `ITodayPoetryService` 每日诗词服务,业务角度思考

```csharp
using Dpa.Library.Models;
namespace Dpa.Library.Services;
public interface IToDayPoetrySty
{
    Task<ToDayPoetry> GetToDayPoetry();
}
```



1. 创建接口实现类 用于取出数据 `JinrishiciService`实现角度思考的名字

```csharp
namespace Dpa.Library.Services;
public class JinRiShiCiGet : IToDayPoetrySty
{
}
```



1. 创建方法 用于返回 ToKen  目标网站标注 Get方法请求,创建HttpClient对象，使用GetAsync方法请求链接

```csharp
private string _ToKen;
/// <summary>
/// 获取今日诗词的Token 会访问网站
/// </summary>
/// <param name="url"> 从url获取Token </param>
public async Task<String> GetTokenAsync(string url)
{
    using HttpClient httpClient = new HttpClient() ;
    try
    {
        //使用Get请求url
        HttpResponseMessage Message = await httpClient.GetAsync(url);
        //404等抛出异常
        Message.EnsureSuccessStatusCode();
        
        //获取数据返回的原始Json
        string ToKenJson = await Message.Content.ReadAsStringAsync();
        
        //将Json对象反序列化为ToKenJson对象
        TokenJson ToKen = JsonSerializer.Deserialize<TokenJson>(ToKenJson);
        
        //将ToKen保存到本地
        _config.Set(JinRiShiCi_Config.ToKenConfgKey,ToKen.data);
        
        //将Token保存到内存
        this.ToKen = ToKen.data;
        
        //如果ToKen是空的 报错
        if (string.IsNullOrEmpty(this.ToKen)) throw new Exception(ErrorMessage.HttpRequestFileError);
        return ToKen.data;
    }
    catch (Exception e)
    {
        await _alertService.AlertAsync("今日诗词服务器", e.Message);
        return null;
    }
}

public class TokenJson
{
    [JsonPropertyName("data")]
    public string data{get; set; }
}
```



1. 使用获取的对象的 `Content.ReadAsStringAsync()` 可以返回原始Json

```csharp
string ToKenJson = await Message.Content.ReadAsStringAsync();
```



1. 高级粘贴可以直接将JSON文件粘贴为一个一个新类

   1. JsonSerializer.Deserialize<>() 对Json进行反序列化 变为指定类型的实例 第二个参数传递一个 JsonSerializerOptions对象 设置 `PropertyNameCaseInsensitive = true`大小写不敏感

   - 这里已经使用了[JsonPropertyName("Name")] 标记 不使用 `PropertyNameCaseInsensitive = true`了

- ##### 错误处理

5. try catch
   1. `_alertservice.alertasync("",e.Message)`
6. 创建新接口 用于弹出错误信息
7. IAlertService AlertAsync方法 要求传入一个  `(string title, string message)` 消息标题和消息内容 ， 错误信息单独配备一个类

```csharp
public interface IAlertService
{
    /// <summary>
    /// 报错
    /// </summary>
    /// <param name="title"> 标题 </param>
    /// <param name="mseeage"> 消息 </param>
    /// <returns></returns>
    System.Threading.Tasks.Task AlertAsync(string title, string mseeage);
}
```

5. JinrishiciService构造方法要求 IAlertService

```csharp
public JinRiShiCiGet(IConfig config,IAlertService alertService)
{
    _alertService = alertService;
    _config = config;
    ToKen = GetTokenAsync();
    //初始化
    if (string.IsNullOrEmpty(ToKen))
    {
        ToKen = GetTokenAsync(JinRiShiCi_Config.GetToKenUrl).Result;
    }
}
```



- 单元测试
- 使用键值存储Token 
- 单元测试 断言Token
- 每次测试都需要链接他人的服务器，这不是非常好的
  - 可以为测试特性更换为 `[Fact(Skip = "标注")]`

```csharp
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
```



9. 调用诗歌获取接口 复制JSON数据，粘贴为类，进行裁减 只保留需要的部分

```csharp
using System.Text.Json.Serialization;

namespace Dpa.Library.Models;

public class FinalJsonData
{
    /// <summary>
    /// 下面的Data
    /// </summary>
    [JsonPropertyName("data")]
    public Data Data { get; set; }
}

public class Data
{
    /// <summary>
    /// 第一句
    /// </summary>
    [JsonPropertyName("content")]
    public string Content { get; set; }
    
    public Origin origin { get; set; }
}

public class Origin
{
    /// <summary>
    /// 标题
    /// </summary>
    [JsonPropertyName("title")]
    public string Title { get; set; }
    
    /// <summary>
    /// 朝代
    /// </summary>
    [JsonPropertyName("dynasty")]
    public string Dynasty { get; set; }
    
    /// <summary>
    /// 作者
    /// </summary>
    [JsonPropertyName("author")]
    public string Author { get; set; }
    
    /// <summary>
    /// 正文
    /// </summary>
    [JsonPropertyName("content")]
    public string[] Content { get; set; }
}
```



9. 创建ToDayPoetry 模型类 ，用来承载今日诗词的数据

```csharp
using System.Text.Json.Serialization;

namespace Dpa.Library.Models;

public class ToDayPoetry
{
    /// <summary>
    /// 名字
    /// </summary>
    public string Name { get; set; } = string.Empty;
    
    /// <summary>
    /// 作者
    /// </summary>
    public string Author { get; set; } = string.Empty;
    
    /// <summary>
    /// 来源
    /// </summary>
    public string Source { get; set; } = string.Empty;
    
    /// <summary>
    /// 朝代
    /// </summary>
    public string Dynasty { get; set; } = string.Empty;

    /// <summary>
    /// 正文
    /// </summary>
    public string Content { get; set; } = string.Empty;
    
    /// <summary>
    /// 第一句
    /// </summary>
    public string Snippet { get; set; } = string.Empty;
}
```



- 往Http请求头添加内容

```csharp
httpClient.DefaultRequestHeaders.Add("头名称",头内容);
```

- Http异常抛出

> 返回404 405 403等 当作异常抛出

```csharp
httpClient.GetAsync().EnsureSuccessStatusCode();
```



11. 随机获取一条 定义方法`RandomGetPortryAsync ` 

```cs
/// <summary>
/// 随机从数据库获取一首 
/// </summary>
/// <returns></returns>
public async Task<ToDayPoetry> RandomGetPortryAsync()
{
    Random rom = new Random();
    int next = rom.Next(30);
    var list = await _poetrySty.GetPoetryAsync(f => true, next, 1);
    Poetry po = list[0];
    // Poetry po = await _poetrySty.GetPoetryAsync("10001");
    return new ToDayPoetry()
    {
        Author = po.Author,
        Content = po.Content,
        Dynasty = po.Dynasty,
        Name = po.Name,
        Snippet = po.Content.Split("。")[0],
        Source = Source_DBSQL
    };
}
```



11. 在精妙的接口设计下，`AlterService` 类操作View层，设计层面并不会造成有向有环图，`IAlterService`置于ViewModel

接口隔离

![image-20241124103322619](/home/r/桌面/image-20241124103322619.png)

## 3.6.1 错误弹窗

> #### ToDayViewModel

```csharp
namespace Dpa.Library.ViewModel;
public class ToDayVIewModel : ViewModelBase
{
    private IToDayPoetrySty _jinRiShiCiGet;
    private ToDayPoetry _toDayPoetry;
    public ICommand InitiailzationCommand;
    public ToDayPoetry ToDayPoetry
    {
        get => _toDayPoetry;
        set => SetProperty(ref _toDayPoetry, value);
    }
    
    public ToDayVIewModel(IToDayPoetrySty jinRiShiCiGet)
    {
        _jinRiShiCiGet = jinRiShiCiGet;
        InitiailzationCommand = new AsyncRelayCommand(Initiailzation);
    }
    
    
    /// <summary>
    /// 用于表示加载是否完成
    /// </summary>
    private bool isLoad = false;
    
    /// <summary>
    /// 用于初始化诗歌
    /// </summary>
    /// <returns> 诗歌 </returns>
    private async System.Threading.Tasks.Task Initiailzation()
    {
        _toDayPoetry = await _jinRiShiCiGet.GetToDayPoetryAsync();
        isLoad = true;
    }
}
```



1. 安装nuget包`Irihi.Ursa` UI组件包 `Irihi.Ursa.Themes.Semi`
2. App.axaml

```xml
Application>
	xmlns:u-semi="https://irihi.tech/ursa/themes/semi"
```

3. `AlterService`类实现方法，使用MessageBox.ShowAsync();弹出消息

```csharp
public class AlertService : IAlertService
{
    public async Task AlertAsync(string title, string mseeage)
    {
        await MessageBox.ShowAsync(mseeage, title);
    }
}
```



3. 对ToDayService进行依赖注入 并暴露 TodayViewModel
4. 使用事件绑定 在界面初始化的时候绑定`Command`对数据库进行初始化

> 依赖注入

```csharp
_serviceCollection.AddScoped<ToDayViewModel>();
_serviceCollection.AddScoped<IToDayPoetryStyService, JinRiShiCiService>();
_serviceCollection.AddScoped<IAlertService, AlertService>();

//对外暴露ToDayViewModel
public ToDayViewModel ToDayViewModel => _serviceProvider.GetService<ToDayViewModel>();
```



> ## 依赖关系

1. ##### IAlertService的实现类依赖于View与IAlertService无关

2. ##### JinRiShiCiGet 只知道自己依赖了一个 IAlertService , 并不清楚其实现类到底如何

3. ##### 这就MVVM + IService ，IService的实现是排除在架构之外的，他们多混乱 与这层关系无关

4. ##### Service依赖的接口与View层无关，那么就认为其不依赖View层

5. ##### JinRiShiCiGet依赖于IAlertService接口，而AlertService实现了IAlertService接口，但是JinRiShiCiGet是不知道AlertService的具体实现的，就好比你调用传入的IAlertService实现对象的时候，你只能看到IAlertService定义的玩意儿一样。

   ##### 这就是接口实现隔离

## 3.6.2 内容显示

3. 使用Grid进行页面布局 先使用一行一列

3. ##### Auto是内容多少就多少/剩下多少就多少

```xml
<Grid>
    <Grid.RowDefinitions>
        <RowDefinition Height="*"/>
    </Grid.RowDefinitions>
    
    <Grid.ColumnDefinitions>
        <ColumnDefinition Width="*"/>
    </Grid.ColumnDefinitions>
</Grid>
```

7. 使用`StackPanel`对诗句进行显示， 其显示方式是栈样式，从上往下 `VerticalAlignment="Bottom"` 对其样式 底端对齐。

   - `Background="#66000000"` 66表示透明度 标记于 <StackPanel xxxx="66xxxxxx">

   - `StackPanel`内再嵌入一个`StackPanel`并设定 `Background`时，其透明度基于上层颜色

8. 使用`StackPanel`内嵌 `StackPanel`不设置`Background` 设置`Margin` (边距) 单写 '8' 代表上下左右都为 8 ，在该内嵌的`StackPanel`内定义一个`Label` `Content`绑定 `今日诗词.第一句`
9. 再上面显示 标题的 `StackPanel`(StP2) 内再嵌入多个 `StackPanel` 并加入Label控件 分别显示 作者 等



# 3.6 错误总结

1. ##### 构造函数内使用异步方法同步使用 造成线程卡死,界面无法显示

2. ##### UI `StackPanel` 设置颜色会覆盖字体,导致多次调试文字不显示

3. ##### 本该为属性赋值 结果为字段赋值了,导致内容更新 事件没有触发



# 3.7 导航

1. 创建根导航接口`IRootNavigationService` 定义一个方法 `NavigateTo(string view)` 用于切换页面

2. 在`IRootNavigationService`接口同文件内 定义一个静态类，内部定义两个`const`常量，值就是要导航到的view的名

   ```csharp
   namespace Dpa.Library.Services;
   
   public interface IRootNavigationService
   {
       void NavigateTo(string view);
   }
   
   public static class ViewInfo
   {
       public const string InitializationView = nameof(InitializationView);
       public const string MainView = nameof(MainView);
       
   }
   ```

3. `InitializationView`就是开头加载转圈的页面

4. `MainView`就是加载完成后 每日的页面

5. #### 想要完成导航,View的属性必须发生变化. Service需要修改ViewModel

   > ### 依赖关系

   - 页面导航`RootNavigationService`需要找到`ServiceLocator`(依赖注入) 才能找到ViewModel,而 `ServiceLocator` 属于View层
   - View ->  ViewModel -> IRootNavigationService
   - RootNavigationService -> ServiceLocator -> ViewModel
   - IRootNavigationService 的**实现类 一定在View层**

```csharp
using Dpa.Library.Services;

namespace Dpa.Service;

public class RootNavigationService : IRootNavigationService
{
    public void NavigateTo(string view)
    {
        throw new System.NotImplementedException();
    }
}
```

6. MainView需要自己的ViewModel , 才能操作他 完成导航的效果



## 3.7.1 实现根导航

1. 创建`MainWindowViewModel`类 

2. `ViewModel`会被 `ViewModelLocator`变为一个 `Control` (控件) 从而渲染到`Content`(控件显示的内容)

3. 诗词从ViewModel来 所以ViewModel要提供诗词 , 控件要绑定到ViewModel上 , 那ViewModel要从ViewModel来

4. MainWindowViewModel内需要提供ViewModel 他才能导航

   ```csharp
   private ViewModelBase _viewModel;
   public ViewModelBase ViewModel
   {
       get => _viewModel;
       set => SetProperty(ref _viewModel, value);
   }
   ```

   ```csharp
   using Dpa.Library.Services;
   using Dpa.Service;
   
   namespace Dpa.Library.ViewModel;
   
   public class MainViewModel : ViewModelBase
   {
       private ViewModelBase _view;
   
       private IRootNavigationService _RootNavigationService;
       public ViewModelBase View
       {
           get => _view;
           set => SetProperty(ref _view, value);
       }
   
       public MainViewModel(IRootNavigationService IR)
       {
           _RootNavigationService = IR;
           View = ServiceLocator.Current.ToDayViewModel;
       }
       
   }
   ```

   

5. 将`MainWindowViewModel`添加到依赖注入 并暴露

   ```csharp
   _serviceCollection.AddScoped<MainViewModel>();
   _serviceCollection.AddScoped<IRootNavigationService, RootNavigationService>();
   
   //对外暴露MainViewModel
   public MainViewModel MainViewModel => _serviceProvider.GetService<MainViewModel>();
   ```

   

6. `MainWindow`的DataContext绑定到 `MainWindowViewModel`

   ```csharp
   DataContext="{Binding MainViewModel,Source={StaticResource ServiceLocator}}"
   Icon="/Assets/avalonia-logo.ico"
   Title="Dpa"
   Content="{Binding View}"
   >
   ```

   

7. 将`MainWindowModel`内的 `ViewModel`绑定到 一个`Control`上

8. `ContentControl` 绑定 `MainWindowModel`的 `ViewModel` , 但是这个控件有点多余 , 能不能直接显示 ? 能 !

   直接修改MainWindow的`Content` 绑定到 ViewModel

   ##### MainWindow的Content绑定玩意儿了,你里面就不能放控件了 会报错了

9. ##### RootNavigationService只需要为ServiceLocator的MainWindowViewModel.ViewModel 赋值就可以了

> ### 获取到ServiceLocator 该方法为静态方法 置于 ServiceLocator

```csharp
//试图获取当前应用程序中的资源
//该资源是通过App.axaml注册的
Application.Current.TryGetResource(nameof(ServiceLocator,null,out value));

public static ServiceLocator Current()
{
    if(Application.Current.TryGetResource(nameof(ServiceLocator,null,out value)) && value is ServiceLocator)
    {
        _current = value;
        return _current;
    }
}
```

10. ##### 取出赋值就可以进行导航 (还早)

```csharp
ServiceLocator.Current.MainWindowViewModel.Content = ??
```

```csharp
public void NavigateTo(string view)
{
    if (view.Equals(nameof(ToDayViewModel)))
    {
        ServiceLocator.Current.MainWindowModel.View = ServiceLocator.Current.ToDayViewModel;
    }
}

```



> ##### 在Rider 想要创建View文件需要安装 AvaloniaRider插件



11. 创建**Avalonia User Control**文件 名为 `ResultView` 进行测试(名字随意)
12. 将原来ToDayViewModel的那个界面直接搬过来 47:00 +- 2 

```xml
DataContext="{Binding TodayViewModel, Source={Statixxxx}}"
```

13. 可以将原来的 ViewModelBase 和 ViewModels删掉了





## 3.7.2 真 · 实现根导航

1. 创建`MainView` User Control , 用来承载整个导航

2. 使用`SplitView` 进行页面布局

   - SplitView.Pane 折叠的部分
   - Split.Content 主体内容部分

3. 安装控件显示图片NuGet包，`Projektanker.Icons.Avalonia.FontAwesome`

   ```xml
   Add xml namespace
   Add xmlns:i="https://github.com/projektanker/icons.avalonia" to your view.
   ```

   

4. 在`Program.cs`的`BuildAvaloniaApp()`方法 增加一个调用

   - `IconProvider.Current.Register<FontAwesomeIconProvider>();`

5. 创建Button , 定义 `<icon:Icon Foreground="Corld"> Value="fa-bars"/>` 



6. 设计界面

7. 创建`MainViewModel.cs `内部创建第二个类 `MenuItem` 用来承载ListBox的数据

   ```csharp
   using System;
   using System.Collections.Generic;
   using System.Linq;
   using System.Text;
   using System.Threading.Tasks;
   
   namespace Dpa.Library.ViewModel
   {
       public class MainViewModel
       {
       }
   
       public class MenuItem 
       {
           private MenuItem() { }
   
           public string Name { get; private init; }
           public string View { get; private init; }
           private static MenuItem TodayView => new() { Name = "今日推荐", View = "ToDayView" };
           private static MenuItem QueryView => new() { Name = "诗词搜索", View = "Qiery" };
           private static MenuItem FavoriteView => new() { Name = "诗词收藏", View = "Favorite" };
           public static IEnumerable<MenuItem> Items { get; } = 
           [
               TodayView,
               QueryView, 
               FavoriteView
           ];
       }
   }
   ```

8. 在`MainView`引入名称空间`xmlns:lvm="using:Dpa.Library.ViewModel"`

9. 将`ListBox`的物品来源绑定`Items`

   `ItemsSource="{Binding Source={x:Static lvm:MenuItem.Items}}"`

10. 在 `Dpa.Library.Services`名称空间下 创建 `IMenuNavigationService`接口 同文件下 创建静态类,

    `MenNavigationConstant` 

    ```csharp
    namespace Dpa.Library.Services
    {
        public interface IMenuNavigationService
        {
        }
    
        public static class MenuNavigationConstant
        {
            public const string ToDayView = nameof(ToDayView);
            public const string QueryView = nameof(QueryView);
            public const string FavoriteView = nameof(FavoriteView);
        }
    }
    ```

11. 更改`MenuItem`中View的赋值

12. MainViewModel继承ViewModelBase

    ```csharp
    public class MainViewModel : ViewModelBase
    {
        /// <summary>
        /// 主页面
        /// </summary>
        private ViewModelBase _view;
        public ViewModelBase View
        {
            get => _view;
            set => SetProperty(ref _view, value);
        }
    }
    ```

13. 将MainViewModel进行依赖注入 并暴露

    ```csharp
    public MainViewModel MainViewModel => _serviceProvider.GetService<MainViewModel>();
    _serviceCollection.AddScoped<MainViewModel>();
    ```

14. 设置MainView的绑定

    ```xml
    DataContext="{Binding MainViewModel, Source={StaticResource ServiceLocator}}"
    
    <!-- 在Content -->
    <ContentControl
    Grid.Row="1"
    Grid.Column="0"
    Grid.ColumnSpan="3"
    Background="Azure"
    Content="{Binding View}" />
    
    ```

15. 将MainView套入MainWindow

    ```csharp
    public class RootNavigationService : IRootNavigationService
    {
        public void NavigateTo(string view)
        {
            if (view.Equals(ViewInfo.MainView))
            {
                ServiceLocator.Current.MainWindowModel.View = ServiceLocator.Current.MainViewModel;
            }
        }
    }
    ```

16. `MainWindowModel`设置初始化

    ```csharp
    using CommunityToolkit.Mvvm.Input;
    using Dpa.Library.Services;
    using System.Windows.Input;
    
    namespace Dpa.Library.ViewModel;
    
    public class MainWindowModel : ViewModelBase
    {
        private ViewModelBase _view;
        private IRootNavigationService _rootNavigationService;
        public ICommand OnInitializedCommand { get; }
    
        public MainWindowModel(IRootNavigationService rootNavigationService)
        {
            _rootNavigationService = rootNavigationService;
            OnInitializedCommand = new RelayCommand(OnInitialized);
        }
        public ViewModelBase View
        {
            get => _view;
            set => SetProperty(ref _view, value);
        }
        private void OnInitialized()
        {
            _rootNavigationService.NavigateTo(ViewInfo.MainView);
        }
    
    }
    ```

17. MainWindow.axaml 事件绑定

    ```xml
    <Interaction.Behaviors>
        <EventTriggerBehavior EventName="Initialized">
            <InvokeCommandAction Command="{Binding OnInitializedCommand}" />
        </EventTriggerBehavior>
    </Interaction.Behaviors>
    ```



## 3.7.3  思维梳理

1. 应用程序启动 **从App.axaml.cs运行** , 其运行时new MainWindow()
2. MainWindow绑定了 MainWindowViewModel 并且事件绑定 OnInitializedCommand
3. OnInitializedCommand 绑定了 OnInitialized方法 该方法内调用了 `_rootNavigationService.NavigateTo`方法
4. _rootNavigationService 的实现 会更改 MainWindowViewModel内的属性 达到更改页面的目的



## 3.7.4 栈导航

1. 在MainViewModel内创建一个`ObservableCollection`集合(定义为属性) 类型为 `ViewModelBase` 并模拟栈

2. 创建 `PushContent` 方法 用于添加 `ViewModeBase`

   - 栈添加只能从顶部添加 使用 `Insert`从索引0插入数据

3. 创建 `GoBack` 方法 用于从伪栈取出数据

   ```csharp
   public class MainViewModel : ViewModelBase
   {
       public ObservableCollection<ViewModelBase> ViewModeStack { get; private set; } = new ();
   
   
       /// <summary>
       /// 主页面
       /// </summary>
       private ViewModelBase _view;
       public ViewModelBase View
       {
           get => _view;
           private set => SetProperty(ref _view, value);
       }
   
       /// <summary>
       /// 进
       /// </summary>
       /// <param name="viewModelBase"> 要进入的视图 </param>
       public void PutStack(ViewModelBase viewModelBase)
       {
           ViewModeStack.Insert(0, viewModelBase);
           View = ViewModeStack[0];
       }
   
       /// <summary>
       /// 出
       /// </summary>
       public void PopStack()
       {
           if (ViewModeStack.Count <= 1)
               return;
           ViewModeStack.RemoveAt(0);
           View =  ViewModeStack[0];
       }
   }
   ```

   

4. 创建转换器 在 View中

   ```csharp
   public class CountToBool : IValueConverter
   {
       /// <summary>
       /// 将数字转换为bool
       /// </summary>
       /// <param name="value"> 用来比较的值 </param>
       /// <param name="targetType"> 无 </param>
       /// <param name="parameter"> 被比较的值 </param>
       /// <returns>如果value比Max大 返回true</returns>
       public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
       {
           int? values;
           if ((values = (value as int?)) != null &&
               (parameter is string str) &&
               int.TryParse(str,out int max))
           {
               return values > max;
           }
   
           //value is int count && parameter is string str &&
           //int.TryParse(str, out int eeee) ? count > eeee : null;
   
           return null;
       }
   
       public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
       {
           throw new NotImplementedException();
       }
   }
   ```

   

5. 在MainView引入转换器的名称空间

   ```xml
   xmlns:lc="using:Dpa.Converters"
   ```

6. 声明

   ```xaml
   <UserControl.Resources>
       <lc:xxx x:key="XXXX">
   ```



```xaml
xmlns:lvm="using:Dpa.Library.ViewModel"
<UserControl.Resources>
    <cv:CountToBool x:Key="Convert" />
</UserControl.Resources>
```

7. 按钮IsVisible

   - IsVisible绑定到 视图列表长度
   - 对视图列表长度使用转换器进行转换 ConverterParameter 是给定比较值
   - 其与方法定义时的参数传入顺序一致
   - ViewModelStack.Count是value
   - Converter 是 Type
   - 1 是 Max

   ```xaml
   <Button
       x:Name="BackSpace"
       Grid.Row="0"
       Grid.Column="1"
       HorizontalAlignment="Stretch"
       VerticalAlignment="Stretch"
       Content="X"
       CornerRadius="0"
       IsVisible="{Binding ViewModeStack.Count, Converter={StaticResource Convert}, ConverterParameter=1}" />
   ```

8. 为出栈按钮绑定Command

   `Command="{Binding PopStackCommand}"`

   ```xaml
   <Button
       x:Name="BackSpace"
       Grid.Row="0"
       Grid.Column="1"
       HorizontalAlignment="Stretch"
       VerticalAlignment="Stretch"
       Command="{Binding PopStackCommand}"
       Content="X"
       CornerRadius="0"
       IsVisible="{Binding ViewModeStack.Count, Converter={StaticResource CountToBool}, ConverterParameter=1}" />
   ```

9. 在MainViewModel创建一个PopStackCommand

   ```csharp
   public ICommand PopStackCommand { get; }
   
   public MainViewModel()
   {
       PopStackCommand = new RelayCommand(PopStack);
   }

10. 方便测试手动往栈内添加

    ```csharp
    ServiceLocator SL = ServiceLocator.Current;
    if (view.Equals(ViewInfo.MainView))
    {
        SL.MainWindowModel.View = SL.MainViewModel;
        SL.MainViewModel.PutStack(SL.ToDayViewModel);
        SL.MainViewModel.PutStack(SL.ContentViewModel);
    }
    ```

11. 展开实现 ， 在ViewModel内定义一个属性 用来表示开启和关闭`bool`

12. 定义一个方法 使`Command`绑定他 用来决定开关

    ```csharp
    public ICommand ControlIsOpenCommand { get; }
    private bool _isOpen = false;
    public bool IsOpen { get => _isOpen; set => SetProperty(ref _isOpen, value); }
    
    //构造函数内将ICommand绑定
    public MainViewModel()
    {
        PopStackCommand = new RelayCommand(PopStack);
        ControlIsOpenCommand = new RelayCommand(ControlIsOpen);
    }
    
    //控制开关
    private void ControlIsOpen()
    {
        if (IsOpen == true)
        {
            IsOpen = false;
        }
        else
        {
            IsOpen = true;
        }
    }
    ```



## 3.7.5 当前逻辑关系

1. 主窗口 `MainWindow` 显示 自己`Model`下的`View`属性
2. 这个`View`属性被初始化显示未 `MainView` -> `MainViewModel`
3. `MainViewModel`下有属于自己的`View`属性 用来显示主界面
4. 想要更改主界面显示的界面 只需要更改 `MainViewModel`下的`View`属性即可





# 3.8  侧边栏导航

1. MainView创建多个属性 

   - `SelectedItem` 用来控制 ListBox 的选中项
   - `Title` 用来控制ListBox 上面的 那个Lable的文本

   ```csharp
   private string _title = "今日推荐";
   private MenuItem _selectedItem;
   
   public string Title { get => _title; set => SetProperty(ref _title, value); }
   public MenuItem SelectedItem { get => _selectedItem; set => SetProperty(ref _selectedItem, value); }
   ```

   

2. MainView创建创建一个方法 用来设置 `SelectedItem`和 `Title` 清空 Stack和 压入 Stack

   ```csharp
   public void SetViewAndClearStack(string view,ViewModelBase viewModelBase)
   {
       ViewModeStack.Clear();
       PutStack(viewModelBase);
       SelectedItem = MenuItem.Items.FirstOrDefault(f => f.View == view);
   	Title = SelectedItem.Name;
   }
   ```

   

3. 为`IMenuNavigationService`添加一个方法 用来实现导航功能  `NavigateTo`

   ```csharp
   public interface IMenuNavigationService
   {
       void NavigateTo(string view);
   }
   ```

   

4. 创建 `IMenuNavigationService` 实现类 在View

   ```csharp
   namespace Dpa.Service
   {
       /// <summary>
       /// 侧边栏导航
       /// </summary>
       public class MenuNavigationService : IMenuNavigationService
       {
           /// <summary>
           /// 用于 侧边栏的导航
           /// </summary>
           /// <param name="view"> 目标视图 </param>
           /// <exception cref="Exception"> null </exception>
           public void NavigateTo(string view)
           {
               ViewModelBase View = view switch
               {
                   MenuNavigationConstant.ToDayView => ServiceLocator.Current.ToDayViewModel,
                   _ => throw new Exception("找不到视图")
               };
               ServiceLocator.Current.MainViewModel.SetViewAndClearStack(view,View);
           }
       }
   }
   ```

   

5. 依赖注入 `IMenuNavigationService` 和他的实现类

6. 创建ListBox 点击事件，使用ICommand绑定

   > 1. 判断选中项是否为空
   >
   > 2. MainViewModel引入侧边导航类
   >
   >    ```csharp
   >    public ICommand ListBoxViewCommand { get; }
   >                      
   >    public MainViewModel(IMenuNavigationService menuNavigationService)
   >    {
   >        _menuNavigationService = menuNavigationService;
   >        PopStackCommand = new RelayCommand(PopStack);
   >        ControlIsOpenCommand = new RelayCommand(ControlIsOpen);
   >        ListBoxViewCommand = new RelayCommand(ListBoxToView);
   >    }
   >                      
   >    /// <summary>
   >    /// 绑定ListBox的选项点击事件
   >    /// </summary>
   >    private void ListBoxToView()
   >    {
   >        if(SelectedItem is null)
   >        {
   >            return;
   >        }
   >        MenuItem item = MenuItem.Items.FirstOrDefault(f => f.Name.Equals(SelectedItem));
   >        _menuNavigationService.NavigateTo(item.View);
   >    }
   >    ```
   >
   >    
   >
   > 3. 使用事件绑定 绑定Tapped事件 在ListBox内 > 后
   >
   >    ```xaml
   >    <ListBox
   >        Grid.Row="1"
   >        ItemsSource="{Binding Source={x:Static lvm:MenuItem.Items}}"
   >        SelectedItem="{Binding SelectedItem, Mode=TwoWay}">
   >        <Interaction.Behaviors>
   >            <EventTriggerBehavior EventName="Tapped">
   >                <InvokeCommandAction Command="{Binding ListBoxViewCommand}" />
   >            </EventTriggerBehavior>
   >        </Interaction.Behaviors>
   >        <ListBox.ItemTemplate>
   >            <DataTemplate>
   >                <Label
   >                    Margin="50,5,0,5"
   >                    Content="{Binding Name}"
   >                    FontSize="20" />
   >            </DataTemplate>
   >        </ListBox.ItemTemplate>
   >    </ListBox>
   >    ```
   >
   >    

   ```csharp
   public void OnMenuTappen()
   {
   	if(选择项 is null)
       {
   		return;
       }
   }
   ```

7. 创建两个ViewModel `FavoriteViewModel` `QueryViewModel` 继承 `ViewModelBase` 注册到依赖注入容器 (空Model) 并暴露

   ```csharp
   _serviceCollection.AddScoped<QueryViewModel>();
   _serviceCollection.AddScoped<FavoriteViewModel>();
   
   public QueryViewModel QueryViewModel => _serviceProvider.GetService<QueryViewModel>();
   public FavoriteViewModel FavoriteViewModel => _serviceProvider.GetService<FavoriteViewModel>();
   ```

   

   1. 创建初始化界面的ViewModel `InitializationViewModel`

   ```csharp
   using Dpa.Library.Services;
   using System;
   using System.Collections.Generic;
   using System.Linq;
   using System.Text;
   using System.Threading.Tasks;
   using System.Windows.Input;
   
   namespace Dpa.Library.ViewModel
   {
       public class InitializationViewModel : ViewModelBase
       {
           private readonly IMenuNavigationService _menuNavigationService;
           private readonly IRootNavigationService _rootNavigationService;
           private readonly IPoetryStyService _poetryStyService;
           public ICommand InitiaCommand { get; }
   
           public InitializationViewModel(IRootNavigationService rootNavigationService,IMenuNavigationService menuNavigationService,IPoetryStyService poetryStyService)
           {
               _menuNavigationService = menuNavigationService;
               _rootNavigationService = rootNavigationService;
               _poetryStyService = poetryStyService;
           }
   
           /// <summary>
           /// 初始化
           /// </summary>
           private async void Initia()
           {
               if (_poetryStyService.IsInitialized)
               {
                   ViewToMainView();
                   return;
               }
               await _poetryStyService.InitializeAsync();
               await System.Threading.Tasks.Task.Delay(1000);
               ViewToMainView();
           }
   
           /// <summary>
           /// 引导View显示
           /// </summary>
           private void ViewToMainView()
           {
               _rootNavigationService.NavigateTo(ViewInfo.MainView);
               _menuNavigationService.NavigateTo(MenuNavigationConstant.ToDayView);
           }
   
       }
   }
   ```

   

   1. 初始化完成后跳转到`MainView`

   2. 跳转后 将 `MainView` 的首页也生成

      ```xaml
      <UserControl
          x:Class="Dpa.InitializationView"
          xmlns="https://github.com/avaloniaui"
          xmlns:x="http://schemas.microsoft.com/winfx/2006/xaml"
          xmlns:d="http://schemas.microsoft.com/expression/blend/2008"
          xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
          d:DesignHeight="450"
          d:DesignWidth="800"
          DataContext="{Binding InitiaCommand, Source={StaticResource ServiceLocator}}"
          mc:Ignorable="d">
          <!--  事件绑定  -->
          <Interaction.Behaviors>
              <EventTriggerBehavior EventName="Initialized">
                  <InvokeCommandAction Command="{Binding InitiailzationCommand}" />
              </EventTriggerBehavior>
          </Interaction.Behaviors>
          <ProgressBar
              Width="100"
              Height="100"
              IsIndeterminate="True"
              ShowProgressText=""
              Theme="{DynamicResource ProgressRing}" />
      </UserControl>
      ```

      

8. 创建 ICommand 绑定 初始化方法

   1. 迁移数据库
   2. 等待1s `Task.Delay`
   3. 跳转MainView
   4. 显示主页

9. `InitializationView` 创建事件绑定 绑定初始化事件

10. 创建加载圈

    ```xaml
    <ProgressBar Width="xxx"
                 Height="xxx"
                 IsIndeterminate="True"
                 Theme="{DynamicResource ProgressRing}"
                 ShowProgressText>
    ```

11. 改造构造函数 引入页面跳转接口

12. `MainWindowViewModel` 初始化内判断数据库是否初始化，如果初始化了 那么导航到主页, 如果没有初始化那么 跳转到初始化页

    - 新建的UserControl 名称空间是错的 需要自己添加正确的名称空间
    - 更改.cs文件的名称空间 还要更改axaml的名称空间

    ```csharp
    <UserControl
        x:Class="Dpa.Views.QueryView"/>
    namespace Dpa.Views;
    public partial class QueryView : UserControl
    
    ```

    

13. 根导航现在只负责根导航了 `RootNavigationService`

- ### 下列可正常导航 界面正常

> #### RootNavigationService

```csharp
using Avalonia.Controls;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;

namespace Dpa.Service;

/// <summary>
/// 用于MainView的Content导航
/// </summary>
public class RootNavigationService : IRootNavigationService
{
    /// <summary>
    /// 用于MainView的Content导航
    /// </summary>
    /// <param name="view"></param>
    public void NavigateTo(string view)
    {
        ServiceLocator SL = ServiceLocator.Current;
        if (view.Equals(ViewInfo.MainView))
        {
            SL.MainWindowModel.View = SL.MainViewModel;
            //SL.MainViewModel.SetViewAndClearStack(MenuNavigationConstant.ToDayView,ServiceLocator.Current.ToDayViewModel);
        }
        else
        {
            SL.MainWindowModel.View = SL.InitializationViewModel;
        }
    }
}
```



> #### MainWindowModel

```csharp
using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Services;
using System.Windows.Input;

namespace Dpa.Library.ViewModel;

public class MainWindowModel : ViewModelBase
{
    private ViewModelBase _view;
    private IRootNavigationService _rootNavigationService;
    private IPoetryStyService _poetryStyService;
    public ICommand OnInitializedCommand { get; }

    public MainWindowModel(IRootNavigationService rootNavigationService,IPoetryStyService p)
    {
        _rootNavigationService = rootNavigationService;
        _poetryStyService = p;
        OnInitializedCommand = new RelayCommand(OnInitialized);
    }
    public ViewModelBase View
    {
        get => _view;
        set => SetProperty(ref _view, value);
    }

    /// <summary>
    /// 这里是主窗口
    /// 启动都会先执行 MainWindow的小玩意
    /// 只需要在MainWindow的View(axaml)文件 中 使用事件绑定的方式 将Initia绑定到该方法的ICommand中\n
    /// 就可以将主界面显示为MainView
    /// </summary>
    private void OnInitialized()
    {
        if(_poetryStyService.IsInitialized) 
        {
            _rootNavigationService.NavigateTo(ViewInfo.MainView);
            return;
        }
        _rootNavigationService.NavigateTo(ViewInfo.InitializationView);
    }

}
```



> ### InitializationViewModel

```csharp
using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Services;
using System.Threading.Tasks;

namespace Dpa.Library.ViewModel
{
    public class InitializationViewModel : ViewModelBase
    {
        private readonly IMenuNavigationService _menuNavigationService;
        private readonly IRootNavigationService _rootNavigationService;
        private readonly IPoetryStyService _poetryStyService;
        public ICommand InitiaCommand { get; }

        public InitializationViewModel(IRootNavigationService rootNavigationService,IMenuNavigationService menuNavigationService,IPoetryStyService poetryStyService)
        {
            _menuNavigationService = menuNavigationService;
            _rootNavigationService = rootNavigationService;
            _poetryStyService = poetryStyService;
            InitiaCommand = new RelayCommand(Initia);
        }

        /// <summary>
        /// 初始化
        /// </summary>
        private async void Initia()
        {
            if (_poetryStyService.IsInitialized)
            {
                ViewToMainView();
                return;
            }
            await _poetryStyService.InitializeAsync();
            await System.Threading.Tasks.Task.Delay(1000);
            ViewToMainView();
        }

        /// <summary>
        /// 引导View显示
        /// </summary>
        private void ViewToMainView()
        {
            _rootNavigationService.NavigateTo(ViewInfo.MainView);
            _menuNavigationService.NavigateTo(MenuNavigationConstant.ToDayView);
        }

    }
}
```



#  3.9 主页图片显示

1. 创建模型类 `ToDayImage`

   ```csharp
   public string FullStartDate {get;set;} = string.Empty;
   public DateTime ExpiresAt{get;set;}
   public string Copyright{get;set;} = string.Empty;
   public string CopyrigthLink{get;set;} = string.Empty;
   public byte[] ImageBytes{get;set;}
   ```

2. 创建每日图片服务接口 `IToDayImageStorage`

   ```csharp
   Task<TodayImage> GetToDayImageAsync(bool isIncludingImageStream);
   Task SaveToDayImageAsync(ToDayImage todayImage)
   ```

3. 创建每日图片实现类`ToDayImageStorage`

   ```csharp
   public class ToDayImageStorage
   {
       //这个是 存储配置文件的那个类 IConfig
   	private readonly IPreferenceStorage _preferenceStorage;
       
       public ToDayImageStorage(IPreferenceStorage preferenceStorage)
       {
           _preferenceStorage = preferenceStorage;
       }
       
       //配置文件的Key
       public static readonly string FullStartDateKey = nameof(ToDayImageStorage)+"."+nameof(ToDayImage.FullStartDate);
       public static readonly string ExpiresAtKey = nameof(ToDayImageStorage)+"."+nameof(ToDayImage.ExpiresAt);
       public static readonly string CopyrightKey = nameof(ToDayImageStorage)+"."+nameof(ToDayImage.Copyright);
       public static readonly string CopyrightLinkKey = nameof(ToDayImageStorage)+"."+nameof(ToDayImage.CopyrightLink);
       
       //默认值
       public const string FullStartDateDefault = "202412052130";
       public static readonly DateTime ExpiresAtDefault = new(2024,12,5);
       public const string CopyrightDefault = "Salt field province vitnam work (© Quangpraha/Pixabay)";
       
       
       
       
       
       
   }
   ```

   



# 99 直角按钮

```xml
设置直角Button，可以设置属性CornerRadius="0"，
也可以设置CornerRadius="0, 10, 20, 30"
来为左上、右上、右下、左下设置不同半径值的圆角（如0，10，20，30）。

水平拉伸 
HorizontalAlignment="Stretch"

垂直拉伸
VerrucakAkufbnebr="Stretch"
```

# 98 静态成员如何做绑定

```csharp
//假设在 Dpa.Library.ViewModel 下 有一个类 MenuItem 内有一个静态的 IEnumerable
//在View内 使用xmlns:引用名称="using:Dpa.Library.ViewModel"
xmlns:vls="using:Dpa.Library.ViewModel"

//使用 直接使用 Binding不指示具体属性就是直接指定整个静态成员
ItemsSource="{Binding Source={x:Static lvm:MenuItem.Items}}"
```



# 97 转换器

> #### 示例 : 使数字类型转换为bool

```csharp
/// <summary>
/// 将数字转换为bool
/// </summary>
/// <param name="value"> 用来比较的值 </param>
/// <param name="type"> 无 </param>
/// <param name="Max"> 被比较的值 </param>
/// <returns>如果value比Max大 返回true</returns>
public object Convert(object value,Type type,object Max)
{
    int? values;
    int? max;
    if( (values = (value as int?)) != null && 
        (max = (Max as int?)) != null)
    {
        return values > max;
    }

    return null;
    
    //如果可以 建议 也请 这样写
    //value is int count && Max is string str &&
    //int.TryParse(str, out int eeee) ? count > eeee : null;

}
```



# 96 Grid行列定义

- 使用 RowDefinitions="auto,auto" 可以直接定义两行 ColumnDefinitions同理



# 95 ListBox加载程序集合

- 先定义一个ListBox 然后使用 `ItemsSource`属性绑定静态数据源
- 然后使用`<ListBox.ItemTemolate>`批量加载数据 如果数据源是高级(对象)数据 可以使用 `Binding 属性名称` 进行单独显示指定属性
- ListBox选项是可选的
- SelectedItem 是对应被选中项

```xaml
<ListBox
    Grid.Row="1"
    ItemsSource="{Binding Source={x:Static lvm:MenuItem.Items}}"
    SelectedItem="{Binding ,Mode=TwoWay}">
    <ListBox.ItemTemplate>
        <DataTemplate>
            <Label
                Margin="50,5,0,5"
                Content="{Binding Name}"
                FontSize="20" />
        </DataTemplate>
    </ListBox.ItemTemplate>
</ListBox>
```



# 94 事件绑定

6. 在View模块安装 **Avalonia.Xaml.Behaviors nuget包**

7. 引入两个名称空间

```xaml
xmlns:i="using:Avalonia.Xaml.Interactivity"
xmlns:ia="using:Avalonia.Xaml.Interactions.Core"
```

10. 对事件的触发进行绑定

```xaml
<i:Interaction.Behaviors>
    <ia:EventTriggerBehavior EventName="Initialized">
        <ia:InvokeCommandAction Command="{Binding GetPoetryAllICommand}"></ia:InvokeCommandAction>
    </ia:EventTriggerBehavior>
</i:Interaction.Behaviors>
```





# 93 添加资源

> 右键解决方法 => 资源 => 添加
>
> 编辑项目

```xml
<ItemGroup>
<None Remove="stardict.db" />
<EmbeddedResource Include="stardict.db">
  <LogicalName>stardict.db</LogicalName>
</EmbeddedResource>
<Folder Include="ModelClass\" />
</ItemGroup>
```

```csharp
Assembly assembly = typeof(SQLiteService).Assembly;
//string resourceName = assembly.GetManifestResourceNames().FirstOrDefault(f => f.Contains("stardict"));

using Stream database = assembly.GetManifestResourceStream(_databaseName);
using Stream fromStram = new FileStream(GetAppFilePath.GetPathAndCreate(_databaseName), FileMode.Open);
await database.CopyToAsync(fromStram);
```



# 92 几个控件属性

## 1 通用

| HorizontalAlignment | 水平对齐方式 | 如果加上Content就是内容对齐方式 |
| ------------------- | ------------ | ------------------------------- |
| VerticalAlignment   | 垂直对齐方式 | 如果加上Content就是内容对齐方式 |
| CornerRadius        | 圆角润滑度   |                                 |





# 93 带参数Command

> #### 在axaml的事件绑定上`CommandParameter`后面跟的就是传递的参数

> 在DataGrid有个 SelectedItems 属性 [Avalonia UI Framework - API - DataGrid.SelectedItems Property](https://reference.avaloniaui.net/api/Avalonia.Controls/DataGrid/434E79A7)

```xaml
<DataGrid
    x:Name="grid"
    Grid.Row="4"
    HorizontalAlignment="Center"
    AutoGenerateColumns="True"
    Background="#88eeecc6"
    CanUserResizeColumns="True"
    GridLinesVisibility="All"
    IsReadOnly="True"
    ItemsSource="{Binding TranslatorList}">
    <Interaction.Behaviors>
        <EventTriggerBehavior EventName="SelectionChanged">
            <InvokeCommandAction Command="{Binding GetPitchs}" CommandParameter="{Binding #grid.SelectedItems}" />
        </EventTriggerBehavior>
    </Interaction.Behaviors>
    <!--
    <DataGrid.Columns>
        <DataGridTextColumn Binding="{Binding Word}" Header="单词" />
        <DataGridTextColumn Binding="{Binding Translation}" Header="翻译" />
    </DataGrid.Columns>
    -->
</DataGrid>

```



> #### 数据绑定当前页面其他内容
>
> 需要先为控件起个名字 `x:Name`

```xaml
<DataGrid
    x:Name="grid"/>
```

> 绑定

```xaml
<InvokeCommandAction Command="{Binding GetPitchs}" CommandParameter="{Binding #grid.SelectedItems}" />
```

> 绑定到页面的其他ViewModel的Command

```xaml
<InvokeCommandAction Command="{Binding "属性.Command",ElementName="x:Name定义的名字"}" />
<!-- 例如 x:Name = "TheResultView"  DataContext 绑定一个ViewModel  里面有一个showComm-->
<!-- 如果使用Riedr 反射已经不给提示了 -->
<InvokeCommandAction Command="{Binding DataContext.ViewModel,ElemntName=TheResultView}">
```



## 声明带参Command

> T是接收到的数据类型

```csharp
public IRelayCommand<T> CommandName { get; }
```

