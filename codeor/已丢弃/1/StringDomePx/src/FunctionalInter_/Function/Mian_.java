package FunctionalInter_.Function;

import javax.print.DocFlavor;
import java.util.function.Function;

public class Mian_
{
    public static void main(String[] args)
    {

        System.out.println(func("124",s -> Integer.parseInt(s ) + 10));
    }
    private  static  int func(String str, Function<String,Integer> fun)
    {
        int a = fun.apply(str);
        return a;
    }

}
