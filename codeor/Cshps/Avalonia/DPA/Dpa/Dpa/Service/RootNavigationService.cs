using Avalonia.Controls;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;

namespace Dpa.Service;

public class RootNavigationService : IRootNavigationService
{
    public void NavigateTo(string view)
    {
        ServiceLocator SL = ServiceLocator.Current;
        if (view.Equals(ViewInfo.MainView))
        {
            SL.MainWindowModel.View = SL.MainViewModel;
            SL.MainViewModel.PutStack(SL.ToDayViewModel);
            SL.MainViewModel.PutStack(SL.ContentViewModel);
        }
    }
}