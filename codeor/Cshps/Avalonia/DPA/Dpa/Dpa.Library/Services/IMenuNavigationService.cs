using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Dpa.Library.Services
{
    public interface IMenuNavigationService
    {
        void NavigateTo(string view);
    }

    /// <summary>
    /// <para>  主导航的选项列表  </para> 
    /// <para>  侧边栏导航         </para>
    /// </summary>
    public static class MenuNavigationConstant
    {
        public const string ToDayView = nameof(ToDayView);
        public const string QueryView = nameof(QueryView);
        public const string FavoriteView = nameof(FavoriteView);
    }
}
