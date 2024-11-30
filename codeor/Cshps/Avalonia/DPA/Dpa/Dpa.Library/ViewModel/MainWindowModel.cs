using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Services;
using System.Windows.Input;

namespace Dpa.Library.ViewModel;

public class MainWindowModel : ViewModelBase
{
    private ViewModelBase _view;
    private IRootNavigationService _rootNavigationService;
    public ICommand OnInitializedCommand { get; }

    public MainWindowModel(IRootNavigationService rootNavigationService)
    {
        _rootNavigationService = rootNavigationService;
        OnInitializedCommand = new RelayCommand(OnInitialized);
    }
    public ViewModelBase View
    {
        get => _view;
        set => SetProperty(ref _view, value);
    }

    /// <summary>
    /// 这里是主窗口
    /// 启动都会先执行 MainWindow的小玩意
    /// 只需要在MainWindow的View(axaml)文件 中 使用事件绑定的方式 将Initia绑定到该方法的ICommand中\n
    /// 就可以将主界面显示为MainView
    /// </summary>
    private void OnInitialized()
    {
        _rootNavigationService.NavigateTo(ViewInfo.MainView);
    }

}