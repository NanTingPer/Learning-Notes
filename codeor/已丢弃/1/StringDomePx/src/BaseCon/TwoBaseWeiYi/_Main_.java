package BaseCon.TwoBaseWeiYi;

public class _Main_
{
    public static void main(String[] args)
    {
        //任何数据对0进行 异或 运算都等于他本身(二进制 " ^ ")
        //任何数据对本身进行 异或 运算都等于0(二进制 " ^ ")
        int a,b;
        a = 10;
        b = 20;
        System.out.println("转换前a: " + a + " b: " + b);
        //交换
        a = a ^ b;  // a = a ^ b
        b = a ^ b;  // b = a ^ b ^ b    相当于 b = a ^ 0 而任何数据对0进行 异或 都等于他本身
        a = a ^ b;  // a =  a ^ b ^ a    经过上一轮的异或 b 已经变成了a 所以这里直接代入a 相当于 b ^ 0
        System.out.println("转换后a: " + a + " b: " + b);

        //位移计算 2 * 8   << 表示左位移  3表示位移3个位
        //相当于 2 * 2的三次方
        System.out.println(2 << 3);

        //判读数据奇偶
        //使用一个数与1进行 与 运算 如果第一位(二进制中唯一的奇数位)是1 那么就是奇数否者偶数
        //与运算 : 11为1,10为0,00为0
//        int a = 9;
//        if((a & 1) == 0)
//        {
//            System.out.println("是偶数");
//        }
//        else {
//            System.out.println("是奇数");
//        }
    }
}
