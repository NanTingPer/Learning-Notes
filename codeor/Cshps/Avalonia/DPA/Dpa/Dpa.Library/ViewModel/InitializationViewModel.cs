using Dpa.Library.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Dpa.Library.ViewModel
{
    public class InitializationViewModel : ViewModelBase
    {
        private readonly IMenuNavigationService _menuNavigationService;
        private readonly IRootNavigationService _rootNavigationService;
        private readonly IPoetryStyService _poetryStyService;
        public ICommand InitiaCommand { get; }

        public InitializationViewModel(IRootNavigationService rootNavigationService,IMenuNavigationService menuNavigationService,IPoetryStyService poetryStyService)
        {
            _menuNavigationService = menuNavigationService;
            _rootNavigationService = rootNavigationService;
            _poetryStyService = poetryStyService;
        }

        /// <summary>
        /// 初始化
        /// </summary>
        private async void Initia()
        {
            if (_poetryStyService.IsInitialized)
            {
                ViewToMainView();
                return;
            }
            await _poetryStyService.InitializeAsync();
            await System.Threading.Tasks.Task.Delay(1000);
            ViewToMainView();
        }

        /// <summary>
        /// 引导View显示
        /// </summary>
        private void ViewToMainView()
        {
            _rootNavigationService.NavigateTo(ViewInfo.MainView);
            _menuNavigationService.NavigateTo(MenuNavigationConstant.ToDayView);
        }

    }
}
