using Dpa.Library.Services;
using Dpa.Library.ViewModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Dpa.Service
{
    /// <summary>
    /// 侧边栏导航
    /// </summary>
    public class MenuNavigationService : IMenuNavigationService
    {
        /// <summary>
        /// 用于 侧边栏的导航
        /// </summary>
        /// <param name="view"> 目标视图 </param>
        /// <exception cref="Exception"> null </exception>
        public void NavigateTo(string view)
        {
            ViewModelBase View = view switch
            {
                MenuNavigationConstant.ToDayView => ServiceLocator.Current.ToDayViewModel,
                _ => throw new Exception("找不到视图")
            };
            ServiceLocator.Current.MainViewModel.SetViewAndClearStack(view,View);
        }
    }
}
