package Stream_Liu._002limit_skip;

import java.util.ArrayList;
import java.util.List;

public class _Mian
{
    public static void main(String[] args)
    {
        List<String> list = new ArrayList<String>();
        list.add("林个请");
        list.add("林徐汇");
        list.add("高徐汇");
        list.add("徐汇");
        list.add("林徐");

        //取前三个输出
        list.stream().limit(3).forEach(System.out::println);
        System.out.println("++++++++++++++++++++++++++++++++++");
        //跳过前两个输出后面
        list.stream().skip(2).forEach(System.out::println);
        System.out.println("++++++++++++++++++++++++++++++++++");
        //输出第三个    跳过前两个 输出跳过后的第一个
        list.stream().skip(2).limit(1).forEach(System.out::println);
    }
}
