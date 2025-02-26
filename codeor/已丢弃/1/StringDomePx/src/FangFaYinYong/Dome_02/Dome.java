package FangFaYinYong.Dome_02;

public class Dome
{
    public static void main(String[] args)
    {
        //实现原理
        /**
         * printInt(e -> Integer.parseInt(e));
         * 本身 printInt 需要的参数是一个接口 而接口本身没有功能
         * 实际上 e-> Integer.parseInt(e)
         * 就是一个实现了该接口的该方法
         */



        //lambda表达式
        printInt(e -> Integer.parseInt(e));  //默认返回
        printInt((String  e) ->
        {
            return Integer.parseInt(e);
        });

        //方法引用
        printInt(Integer::parseInt);//默认返回
    }

    private static void printInt(Inter_ e)
    {
        int a = e.printInt("666");
        System.out.println(a);
    }
}
