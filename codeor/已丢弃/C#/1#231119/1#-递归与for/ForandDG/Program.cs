// See https://aka.ms/new-console-template for more information
using Fr;
using System.ComponentModel.Design;
using System.Reflection.Metadata;
using System.Runtime.InteropServices;
// 2023 11 28
//El ac = new El();
//ac.Ifw(5);
//ac.Forw(5);
//ac.Net1_100(100);
//ac.Dg_1(100);
dynamic a = 0;//灵活性数据类型（伪）
Console.WriteLine("选择商品\n 1,苹果 2,菠萝 3,西瓜");
string b = Console.ReadLine();
int c = int.Parse(b);//字符串转
int d = Convert.ToInt32(b);//字符串转
if (d == 1)
    {
    Console.WriteLine("56一斤\n 输入购买数量");
    string sl = Console.ReadLine();
    int slz = Convert.ToInt32(sl);
    Console.WriteLine($"共{slz *56 }元");
}
else if (d == 2)
    {
    Console.WriteLine("80一斤\n 输入购买数量");
    string sl = Console.ReadLine();
    int slz = Convert.ToInt32(sl);
    Console.WriteLine($"共{slz * 80}元");

}
else if (d==3)
    {
    Console.WriteLine("100一斤\n 输入购买数量");
    string sl = Console.ReadLine();
    int slz = (int)Convert.ToInt32(sl);
    Console.WriteLine($"共{slz * 100}元");
}

Console.WriteLine("输入值(循环次数)：");
string dy = Console.ReadLine();
int dy1 = (int)Convert.ToInt32(dy);
int i;
i = 0;

//while循环 不符合条件退出
while (i < dy1)
{
    i++;
    Console.WriteLine($"while当前值:{i}");
}

//for循环计算钱钱
double nanei = 10000;
//int zg = (int)Convert.ToDouble(nanei);
for (int o = 1; nanei < 15000; o++)
{
    nanei += nanei * 0.1;
    Console.WriteLine($"几年:{o}年");
}

Console.WriteLine("选择操作:\n 1,数组 2,打印选项1的数组 3,往数组添加数字并不打乱");
int xz =  Convert.ToInt32(Console.ReadLine());
//定义数组并实例化
int[] sz = new int[10];
int[] px = new int[10];
int[] pxfz = new int[10];
bool pd = false;
int pdx = 0;
px[0] = 1;
px[1] = 1;
px[2] = 2;
px[3] = 3;
px[4] = 5;
px[5] = 6;
px[6] = 10;
px[7] = 15;
px[8] = 19;
/*
int pxjs;
int pxzz;
pxzz = 9;
pxjs = 0;
*/
if (xz == 1 )
{
    for (int o = 0; o < sz.Length; o++)
    {
        sz[o] = o;
        Console.WriteLine($"数组int[{o}]为{sz[o]}");
    }
}
else if (xz==2)
{
    for (int o = 0; o < sz.Length; o++)
    {
        sz[o] = o;
        Console.WriteLine($"数组int[{o}]为{sz[o]}");
    }

}
else if (xz == 3 )
{
    Console.WriteLine("请输入一个数字");
    int q = Convert.ToInt32(Console.ReadLine());
    for (int o = 0; o < px.Length-1; o++)
    {
        if (q > px[o])
        {
            if (q <= px[o+1])
            {
                /*
                pxfz[o + 1] = px[o];
                for (int o2 = o; o2 < px.Length-1; o2++)
                {
                    px[o2] = q;
                    if (o2 <> o)
                    {

                    }
                    /*
                    px[px.Length-1-pxjs] = px[pxzz];
                    pxjs++;pxzz--;
                    Console.WriteLine(px[o]);
                    Console.WriteLine($"pxjs为:{pxjs}");
                    Console.WriteLine(o);
                    
                */
                pd = true;
                pdx = o;
            }
        }
    }
    if (pd = true)
    {
        pxfz[pdx + 1] = px[pdx];
        for (int o = 0; o < px.Length-1; o++)
        {
            px[pdx] = q;
            if (o < pdx + 1)
            {
                if (o != pdx + 1)
                {
                    pxfz[o] = px[o];
                }
            }
            if (o>pdx + 1)
            {
                if (o != pdx + 1)
                {
                    pxfz[o] = px[o];
                }
            }
            /*
            if (o != pdx+1)
            {
                pxfz[o] = px[o];
            }
            */
            Console.WriteLine($"o为{o}");
            Console.WriteLine($"值为{pxfz[o]}");
            Console.WriteLine($"第{o + 1}项为{pxfz[o]}");
        }
    }
    else
    {
        px[px.Length-1] = q;
        for (int o = 0; o < px.Length-1; o++)
        {
            Console.WriteLine($"第{o}项为{px[o]}");
            //终止循环 break;
            //不往下重新continue;
        }
    }
}
