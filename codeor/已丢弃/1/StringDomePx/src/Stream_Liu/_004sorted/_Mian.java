package Stream_Liu._004sorted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

//sorted : 排序流中数据
//一个无参 : 自然排序
//一个带参 : 提供比较器
public class _Mian
{
    public static void main(String[] args)
    {
        List<String> list = new ArrayList<String>();
        list.add("林徐");
        list.add("林个请");
        list.add("林徐汇");
        list.add("林徐");
        list.add("高徐汇");
        list.add("徐汇");


        Stream<String> str = list.stream().sorted(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                //o1上一个 o2下一个
                if(o1.length() > o2.length())
                {
                    return 1;
                }
                else if (o1.length() == o2.length())
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
        }) ;
        str.forEach(System.out::println);
    }
}
