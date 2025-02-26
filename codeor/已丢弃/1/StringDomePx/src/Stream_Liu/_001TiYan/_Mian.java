package Stream_Liu._001TiYan;

import java.util.ArrayList;

public class _Mian
{
    //将一个过滤一个List集合
    public static void main(String[] args)
    {
        ArrayList<String> list = new ArrayList<String >();
        list.add("林个请");
        list.add("林徐汇");
        list.add("高徐汇");
        list.add("徐汇");
        list.add("林徐");

        //filter 返回匹配流  forEach遍历并执行操作
        //每次操作都返回一个Stream流
        list.stream().filter(s -> s.length() == 3).filter(s -> s.startsWith("林")).forEach(System.out::println);
    }
}
