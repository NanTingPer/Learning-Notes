using Dpa.Library.Services;

namespace Dpa.Library.ViewModel;

public class MainViewModel : ViewModelBase
{
    private ViewModelBase _view;
    public ViewModelBase View
    {
        get => _view;
        set => SetProperty(ref _view, value);
    }
}