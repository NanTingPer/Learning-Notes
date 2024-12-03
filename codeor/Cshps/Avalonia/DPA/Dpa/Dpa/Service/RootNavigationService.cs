using Avalonia.Controls;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;

namespace Dpa.Service;

/// <summary>
/// 用于MainView的Content导航
/// </summary>
public class RootNavigationService : IRootNavigationService
{
    /// <summary>
    /// 用于MainView的Content导航
    /// </summary>
    /// <param name="view"></param>
    public void NavigateTo(string view)
    {
        ServiceLocator SL = ServiceLocator.Current;
        if (view.Equals(ViewInfo.MainView))
        {
            SL.MainWindowModel.View = SL.MainViewModel;
            //SL.MainViewModel.SetViewAndClearStack(MenuNavigationConstant.ToDayView,ServiceLocator.Current.ToDayViewModel);
        }
        else
        {
            SL.MainWindowModel.View = SL.InitializationViewModel;
        }
    }
}