using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Services;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Dpa.Library.ViewModel
{
    public class MainViewModel : ViewModelBase
    {
        public ICommand PopStackCommand { get; }
        public ICommand PutStackCommand { get; }
        public ICommand ControlIsOpenCommand { get; }
        private bool _isOpen = false;
        private string _title = "今日推荐";
        private MenuItem _selectedItem;

        public bool IsOpen { get => _isOpen; set => SetProperty(ref _isOpen, value); }
        public string Title { get => _title; set => SetProperty(ref _title, value); }
        public MenuItem SelectedItem { get => _selectedItem; set => SetProperty(ref _selectedItem, value); }
        public ObservableCollection<ViewModelBase> ViewModeStack { get; private set; } = new();
        
        public MainViewModel()
        {
            PopStackCommand = new RelayCommand(PopStack);
            ControlIsOpenCommand = new RelayCommand(ControlIsOpen);
        }

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

        /// <summary>
        /// 控制开关
        /// </summary>
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

        public void SetViewAndClearStack(string view,ViewModelBase viewModelBase)
        {
            ViewModeStack.Clear();
            PutStack(viewModelBase);
            SelectedItem = MenuItem.Items.FirstOrDefault(f => f.View == view);
            Title = SelectedItem.Name;
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
