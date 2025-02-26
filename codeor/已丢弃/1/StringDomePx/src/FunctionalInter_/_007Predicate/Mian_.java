package FunctionalInter_._007Predicate;

import java.util.function.Predicate;

public class Mian_
{
    public static void main(String[] args)
    {
        boolean bl = tjpd("hhhhhhhhhh",s->s.length()>8);
        System.out.println(bl);
    }
    public  static boolean tjpd(String st, Predicate<String> pre)
    {
        //非
        return pre.negate().test(st);
    }
}
