package FunctionalInter_._003;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Dome
{
    //如果返回值为函数式接口 那么可以直接使用Lambda表达式
    public static void main(String[] args)
    {
        ArrayList<String> ar = new ArrayList<String >();
        ar.add("a");
        ar.add("dddd");
        ar.add("bd");
        ar.add("ddd");
        System.out.println(ar);
        Collections.sort(ar,getComparator());
        System.out.println(ar);
    }

    private static Comparator<String> getComparator()
    {


            Comparator<String> co = new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    return o1.length() - o2.length();
                }
            };
            return co;

        //        return (String s1,String s2) ->{
//           return s1.length() - s2.length();
//        };

//        return (s1,s2) -> s1.length() - s2.length();
    }
}
