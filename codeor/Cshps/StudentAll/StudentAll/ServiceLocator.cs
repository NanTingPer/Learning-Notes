using Avalonia;
using Microsoft.Extensions.DependencyInjection;
using StudentAll.SQLite;
using StudentAll.ViewModels;
using System;

namespace StudentAll
{
    public class ServiceLocator
    {
        private ServiceCollection serviceCollection = new ServiceCollection();
        private IServiceProvider serviceProvider;

        private static ServiceLocator _curre;
        public static ServiceLocator ServiceLoactor
        {
            get
            {
                if (_curre != null)
                    return _curre;
                if (Application.Current.TryGetResource(nameof(ServiceLocator), null, out object? ser))
                {
                    _curre = (ServiceLocator)ser;
                }
                return _curre;
            }
        }

        public ServiceLocator()
        {
            serviceCollection.AddScoped<MainViewModel>();
            serviceCollection.AddScoped<MainWindowViewModel>();
            serviceCollection.AddScoped<SQLiteService>();

            serviceProvider = serviceCollection.BuildServiceProvider();
        }
        public MainViewModel MainViewModel => serviceProvider.GetService<MainViewModel>();
        public MainWindowViewModel MainWindowViewModel => serviceProvider.GetService<MainWindowViewModel>();
        public SQLiteService SQLiteService => serviceProvider.GetService<SQLiteService>();

    }
}
