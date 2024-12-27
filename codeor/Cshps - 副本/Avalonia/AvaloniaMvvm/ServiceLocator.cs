using System;
using AvaloniaMvvm.Models;
using AvaloniaMvvm.Servicer;
using AvaloniaMvvm.ViewModels;
using Microsoft.Extensions.DependencyInjection;

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