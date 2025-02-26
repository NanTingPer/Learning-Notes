package Stream_Liu._003concat_distinct;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Stream;

//concat : 静态方法 合并两个流
//distinct : 去流元素重复 使用 Objet.eq(Objet)
public class _Mian
{
    public static void main(String[] args)
    {
        List<String>  list = new ArrayList<String >();
        list.add("林个请");
        list.add("林徐汇");
        list.add("高徐汇");
        list.add("徐汇");
        list.add("林徐");
        list.add("林徐");

        //将前两个元素组成一个流
        Stream<String> str1 = list.stream().limit(2);
        //将后两个元素组成一个流
        Stream<String > str2 = list.stream().skip(list.size() - 2);
        //合并str1 与 str2
        Stream<String> str3 = Stream.concat(str1,str2);
        str3.forEach(System.out::println);
        //去重
        System.out.println("++++++++++++++++++++++++++++++++++");
        Stream<String> str4 = list.stream().distinct();
        str4.forEach(System.out::println);
    }
}
