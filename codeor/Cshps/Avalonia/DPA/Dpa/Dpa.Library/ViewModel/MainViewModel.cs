using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Dpa.Library.ViewModel
{
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

    public class MenuItem 
    {
        private MenuItem() { }

        public string Name { get; private init; }
        public string View { get; private init; }
        private static MenuItem TodayView => new() 
        { 
            Name = "今日推荐", 
            View = MenuNavigationConstant.ToDayView 
        };
        private static MenuItem QueryView => new() 
        { 
            Name = "诗词搜索", 
            View = MenuNavigationConstant.QueryView 
        };
        private static MenuItem FavoriteView => new() 
        { 
            Name = "诗词收藏", 
            View = MenuNavigationConstant.FavoriteView
        };
        public static IEnumerable<MenuItem> Items { get; } = 
        [
            TodayView,
            QueryView, 
            FavoriteView
        ];
    }
}
