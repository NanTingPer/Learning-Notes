using Dpa.Library.Services;
using Dpa.Service;

namespace Dpa.Library.ViewModel;

public class MainViewModel : ViewModelBase
{
    private ViewModelBase _view;

    private IRootNavigationService _RootNavigationService;
    public ViewModelBase View
    {
        get => _view;
        set => SetProperty(ref _view, value);
    }

    public MainViewModel(IRootNavigationService IR)
    {
        _RootNavigationService = IR;
        View = ServiceLocator.Current.ToDayViewModel;
    }
    
}