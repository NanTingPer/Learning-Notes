using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace fy
{
    /// <summary>
    /// 2023 12 20
    /// </summary>
    public class 复用
    {
        public static void Mian(String[] args)
        {
            int a = 10;
            int b = 20;
            int c = 30;
            int d = 31;
            int e = 32;
            复用 ac = new 复用();
            int dd = ac.mj(a, b);
            int cc = ac._2xmj(a, b, c);
            int bb = ac.__2xmj(a, b, c, d);
            System.Console.WriteLine(dd);
            System.Console.WriteLine(cc);
            System.Console.WriteLine(bb);
        }

        public int mj(int x,int y)
        {
            int zh;
            zh = x * y;
            return zh;
        }

        public int _2xmj(int x,int y,int z)
        {
            int zh;
            zh = mj(x, y) * z;
            return zh;
        }

        public int __2xmj(int x, int y, int z , int d)
        {
            int zh;
            zh = _2xmj(x, y,z) * d;
            return zh;
        }
    }
}
