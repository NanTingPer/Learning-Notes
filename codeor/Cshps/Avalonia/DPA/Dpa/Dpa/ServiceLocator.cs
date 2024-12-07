using System;
using Avalonia;
using Dpa.Library.ConfigFile;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;
using Dpa.Service;
using Microsoft.Extensions.DependencyInjection;

namespace Dpa;

public class ServiceLocator
{
    //依赖注入容器
    private ServiceCollection _serviceCollection = new ServiceCollection();
    private IServiceProvider _serviceProvider;
    
    //对外暴露ContentViewModel
    public ContentViewModel ContentViewModel => _serviceProvider.GetService<ContentViewModel>();

    //对外暴露ToDayViewModel
    public ToDayViewModel ToDayViewModel => _serviceProvider.GetService<ToDayViewModel>();
    
    //对外暴露MainViewModel
    public MainWindowModel MainWindowModel => _serviceProvider.GetService<MainWindowModel>();

    //TODO 测试
    public IRootNavigationService RootNavigationService => _serviceProvider.GetService<IRootNavigationService>();

    public MainViewModel MainViewModel => _serviceProvider.GetService<MainViewModel>();

    public QueryViewModel QueryViewModel => _serviceProvider.GetService<QueryViewModel>();

    public FavoriteViewModel FavoriteViewModel => _serviceProvider.GetService<FavoriteViewModel>();

    public InitializationViewModel InitializationViewModel => _serviceProvider.GetService<InitializationViewModel>();

    private static ServiceLocator _current;
    /// <summary> 
    /// <para> 用来从资源中试图获取ServiceLocator实例 </para> 
    /// <para> 包含全部ViewModel实例                  </para>
    /// </summary>
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
        _serviceCollection.AddScoped<IPoetryStyService, PoetryStyService>();
        _serviceCollection.AddScoped<IConfig, Config>();

        _serviceCollection.AddScoped<ToDayViewModel>();
        _serviceCollection.AddScoped<IToDayPoetryStyService, JinRiShiCiService>();
        _serviceCollection.AddScoped<IAlertService, AlertService>();

        _serviceCollection.AddScoped<MainWindowModel>();
        _serviceCollection.AddScoped<IRootNavigationService, RootNavigationService>();

        _serviceCollection.AddScoped<MainViewModel>();

        _serviceCollection.AddScoped<IMenuNavigationService, MenuNavigationService>();

        _serviceCollection.AddScoped<QueryViewModel>();

        _serviceCollection.AddScoped<FavoriteViewModel>();

        _serviceCollection.AddScoped<InitializationViewModel>();
        
        _serviceProvider = _serviceCollection.BuildServiceProvider();
    }
    
}