package FunctionalInter_._004SUPPLIER;

import java.sql.SQLOutput;
import java.util.function.Supplier;

//Supplier<T>
//给定什么 使用get方法就返回什么
public class Dome
{
    public static void main(String[] args)
    {
        System.out.println(getSt().get());
        String s = getString(() -> "你好");
        System.out.println(getString(() -> "你好"));
        int a = getInt(() -> 1);
        int b = getInt(() -> 2);
        System.out.println(a +b);
    }
    //定义一个方法返回一个字符串
    private static Supplier<String> getSt()
    {
        return () -> "字符串";
    }

    private static Integer getInt(Supplier<Integer> e)
    {
        return e.get();
    }
    private static String getString(Supplier<String > e)
    {
        return e.get();
    }
}
