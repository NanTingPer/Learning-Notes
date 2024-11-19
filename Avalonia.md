### Avalonia

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