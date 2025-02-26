package Stream_Liu._005Map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//map : 传入参数是一个函数式编程接口Function 即 转换数据类型
//mapToInt : 返回的是一个Integer 可以进行Integer的相关操作
public class _Mian
{
    public static void main(String[] args)
    {
        List<String> list = new ArrayList<String>();
        list.add("22");
        list.add("33");
        list.add("44");
        list.add("55");
        list.add("66");
        list.add("77");
        Stream<Integer> str = list.stream().map(s -> Integer.parseInt(s));
        Stream<Integer> str2 = list.stream().map(s -> Integer.parseInt(s));
        str.forEach(System.out::println);
        str2.forEach(s -> System.out.println(s.getClass().getTypeName()));

        System.out.println("++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(list.stream().mapToInt(Integer::parseInt).sum());
    }
}
