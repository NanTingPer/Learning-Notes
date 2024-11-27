using Dpa.Library.Services;

namespace Dpa.Library.ViewModel;

public class MainWindowModel : ViewModelBase
{
    private ViewModelBase _view;
    public ViewModelBase View
    {
        get => _view;
        set => SetProperty(ref _view, value);
    }
}