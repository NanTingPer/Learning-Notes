using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Fr
{
    public class El
    {
        public void Ifw(int a)
        {
            if (a == 0)
            {
                Console.WriteLine(a);
            }
            else 
            {
                Ifw(a - 1);
                Console.WriteLine(a);
            }
        }

        public void Forw(int a)
        {
            for (int i = a; i >= 0; i--)
            {
                Console.WriteLine(i);
            }
        }

        public int Net1_100(int a)
        {
            int c = 0;
            for (int i = a; i >= 0; i--)
            {
                c = c+i;
            }
            return c;
        }

        public void Bet_100(int a)
        {
            int c = 0;
            for (int i = a; i >= 0; i--)
            {
                c = c + i;
                Console.WriteLine(c);
            }

        }

        public void Dg_1(int a)
        {
            int c;
            c = 0;
            if (a == 0)
            {
                Console.WriteLine(1);
                //c = c + a;
            }
            else
            {
                //c = Dg_1(a+1);
                //int c = c + a;
                Dg_1(a - 1);
                Console.WriteLine(c);
            }
            //return c;
        }



    }
}
