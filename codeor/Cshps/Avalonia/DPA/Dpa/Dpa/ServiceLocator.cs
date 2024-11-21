using System;
using Avalonia;
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
    public  ServiceLocator()
    {
        _serviceCollection.AddScoped<ContentViewModel>();
        _serviceCollection.AddScoped<IPoetrySty, PoetrySty>();

        _serviceProvider = _serviceCollection.BuildServiceProvider();
    }
    
}