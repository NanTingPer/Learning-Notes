using Avalonia.Data.Converters;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Dpa.Converters
{
    public class CountToBool : IValueConverter
    {
        /// <summary>
        /// 将数字转换为bool
        /// </summary>
        /// <param name="value"> 用来比较的值 </param>
        /// <param name="targetType"> 无 </param>
        /// <param name="parameter"> 被比较的值 </param>
        /// <returns>如果value比Max大 返回true</returns>
        public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            if ((value is int values) &&
                (parameter is string str) &&
                int.TryParse(str,out int max))
            {
                return values > max;
            }

            //value is int count && parameter is string str &&
            //int.TryParse(str, out int eeee) ? count > eeee : null;

            return null;
        }

        public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
