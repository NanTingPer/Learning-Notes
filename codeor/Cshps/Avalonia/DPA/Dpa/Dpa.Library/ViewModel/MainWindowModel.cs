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
    private void OnInitialized()
    {
        _rootNavigationService.NavigateTo(ViewInfo.MainView);
    }

}