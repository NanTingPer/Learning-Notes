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
        _serviceCollection.AddScoped<IPoetryStyService, PoetryStyService>();
        _serviceCollection.AddScoped<IConfig, Config>();

        _serviceCollection.AddScoped<ToDayViewModel>();
        _serviceCollection.AddScoped<IToDayPoetryStyService, JinRiShiCiService>();
        _serviceCollection.AddScoped<IAlertService, AlertService>();

        _serviceProvider = _serviceCollection.BuildServiceProvider();
    }
    
}