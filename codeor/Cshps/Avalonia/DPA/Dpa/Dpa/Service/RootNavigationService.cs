using Avalonia.Controls;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;

namespace Dpa.Service;

public class RootNavigationService : IRootNavigationService
{
    public ViewModelBase ViewModel = ServiceLocator.Current.ToDayViewModel;
    
    public void NavigateTo(string view)
    {
        if (view.Equals(nameof(ToDayViewModel)))
        {
            ViewModel = ServiceLocator.Current.ToDayViewModel;
        }
    }
    
    public RootNavigationService()
    {
        NavigateTo(nameof(ToDayViewModel));
    }
}