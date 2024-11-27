using Avalonia.Controls;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;

namespace Dpa.Service;

public class RootNavigationService : IRootNavigationService
{
    public void NavigateTo(string view)
    {
        if (view.Equals(nameof(ToDayViewModel)))
        {
            ServiceLocator.Current.MainWindowModel.View = ServiceLocator.Current.ToDayViewModel;
        }
    }
}