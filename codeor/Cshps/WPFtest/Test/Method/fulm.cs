using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Test.Method
{
    public class fulm
    {
        public static Dictionary<Tuple<String,String,String>, List<Tuple<String, String>>> ModAndEnglish_Chinese = [];
        public static void ListToMap(String[] list,Dictionary<String,String> dic)
        {
            for (int i = 0; i < list.Length - 1; i += 2)
            {
                dic.Add(list[i], list[i + 1]);
            }
        }
    }
}
