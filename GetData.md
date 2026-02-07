```cs
/// <summary>
/// 获取数据的泛用接口
/// </summary>
/// <typeparam name="TModel"> 返回的数据模型类型 </typeparam>
/// <typeparam name="TController"> 获取数据的控制器类型 </typeparam>
/// <returns></returns>
public static async Task<TModel> GetData<TModel, TController>()
    where TModel : class, new()
    where TController : ControllerBase, IToolData<TModel>
{
    TModel? retModelValue = null;
    TController? controller = null;
    using var scopeService = Instance!.Services.CreateScope();
    try {
        controller = scopeService.ServiceProvider.GetService<TController>();
    } catch(Exception e) {
        Log.Logger.Error(e.Message); Log.Logger.Error(e.StackTrace ?? "无相关堆栈调用");
        return new TModel();
    }
    if(controller is null) return new TModel();

    retModelValue = await controller.GetData();
    if(retModelValue is null) return new TModel();
    return retModelValue;
}

/// <summary>
/// 工具数据获取接口
/// </summary>
/// <typeparam name="TModel"> 返回的数据模型类型 </typeparam>
public interface IToolData<TModel>
    where TModel : class, new()
{
    /// <summary>
    /// 获取数据
    /// </summary>
    /// <returns></returns>
    Task<TModel> GetData();
}
```
