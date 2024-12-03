using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Services;
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
            InitiaCommand = new AsyncRelayCommand(Initia);
        }

        /// <summary>
        /// 初始化
        /// </summary>
        private async System.Threading.Tasks.Task Initia()
        {
            if (_poetryStyService.IsInitialized)
            {
                ViewToMainView();
                return;
            }
            await _poetryStyService.InitializeAsync();
            await System.Threading.Tasks.Task.Delay(5000);
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
