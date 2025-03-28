# ASP .NET8 入门

## 仓储 + 服务架构模式

仓储负责一个实例，其对实例的控制主要看对RM的实现，服务层主要写一些接口，控制器主要将数据进行转发。

需要有一个公共的模块层，所以需要新建一个类库项目 `Common`层 ，其主要写`公共类` `帮助类` 

需要有一个`Model`层，主要存放各种数据模型，例如数据库的`User`，其需要引用`Common`，为了不对外完整暴露用户信息，需要一个试图模型，就是展示出来的，可以是`UserVo`，如果`User`有两个字段 例如`id, Name`，那么`UserVo`就将要显示的字段展现出来，例如将`Name`展现出来，那么就定义一个与`Name`同类型的 属性 / 字段，`UserName`

需要一个服务层`IService`，其提供服务的同时，涉及到返回`Model`的数据，应当使用`ModelVo`，还需对应的服务实现层，在服务实现时，从仓储层取得的数据是一个实体数据，因此需要进行对应的转换，将其转换为视图模型。其依赖`Model`层，可以定义一个`User`接口，返回用户信息

需要一个核心层，仓储层`Repository` 建议面向接口开发`依赖注入等`，其需要引用`Model`

- 仓储更多的是控制实体对象，数据库连接等。将数据对外部进行抛出。不负责业务

原则 : 仓储层的实体模型不允许直接暴露到外层(顶层API)





## 泛型基类

> 适用于简单的服务，例如增删改查。如果这个服务比较复杂还是建议再新建一个`Service`，但两种模式并不冲突可以共存。

​	当有多个类共用一套模板架子时，能否将模板架子重构为一个可变的？传哪个对象进来，就对哪个对象进行处理。

例如将接口更改为泛型接口

```cs
public interface IBaseRepository<TEntity> where TEntity : class
{
	Task<List<TEntity>> Query();
}
```

​	服务层的实现设计两个泛型，一个是实体类一个是视图类，因此需要定义两个泛型

```cs
public interface IBaseService<TEntity, TVo> where TEntity : class, where TVo : class
{
    Task<List<TVo>> Query();
}
```

在使用服务时，我们不需要再为每个实体类与视图类定义一个`Query`方法，我们只需要确定好视图类与实体类的映射关系即可。可以少很多重复与繁琐的代码



## 泛型关系映射



## 原生依赖注入串联

无需关心内部结构，无需关心何时注册，无需关心是否出现内存泄漏，是否需要垃圾回收。这些都由依赖注入容器完成。

ASP .NET8默认模板已经提供了依赖注入的依赖，直接用就行。



## 小总结

仓储层只关心对象实例的控制。因此简单的泛型基类只需要一个`TEntity` 实体模型

`Service`需要对具体的业务进行处理，因此返回的是一个`Vo`，基础要求是每个接口都采用视图模型对外开放，同时依赖仓储，因此需要两个泛型



## 自定义项目框架模板

准备一个`nuget.exe`，在同级目录下创建`content`文件夹和`license`文件夹，将项目源代码(含解决方案)放入`content`文件夹，`license`存放协议

在`content`文件夹内再有一个`.template.config`文件夹，内部是此`nuget`的基本信息，`template.json`

```json
{
    "$schema":"http://json.schemastore.org/template",
    "author":"作者",
    "ckassufucaruibs":["Web/WebAPI"],//类型
    "name":"模板名称",
    "identity":"",//
    "shortName":"",
    "tag":{
        "language":"C#",
        "type":"project",
    },
    "sourceName":"",//资源名称用于批量替换名称空间
    "preferNameDirectory":true
}
```

然后命令行执行 ，配置文件后缀为`nuspec`

`nuget pack 配置文件名称`
`pause`
