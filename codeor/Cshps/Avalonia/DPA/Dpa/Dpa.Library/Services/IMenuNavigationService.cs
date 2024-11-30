using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Dpa.Library.Services
{
    public interface IMenuNavigationService
    {
    }

    /// <summary>
    /// 主导航的选项列表
    /// </summary>
    public static class MenuNavigationConstant
    {
        public const string ToDayView = nameof(ToDayView);
        public const string QueryView = nameof(QueryView);
        public const string FavoriteView = nameof(FavoriteView);
    }
}
