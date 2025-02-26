package Stream_Liu._006Add;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class _Mian
{
    public static void main(String[] args)
    {
        List<String> manname = new ArrayList<String>();
        manname.add("林心如");
        manname.add("张曼玉");
        manname.add("林青霞");
        manname.add("柳岩");
        manname.add("林志玲");
        manname.add("王祖贤");

        List<String> wumenname = new ArrayList<String >();
        wumenname.add("周润发");
        wumenname.add("成龙");
        wumenname.add("刘德华");
        wumenname.add("吴京");
        wumenname.add("周星驰");
        wumenname.add("李连杰");

        //要求1 manname 组  3个字以上 只要4个
        //要求2 wumenname 组 3个字以上 只要3个
        //要求3 合并两个
        //要求4输出
        //filter 过滤
        Stream<String> Mstr = manname.stream().filter(s -> s.length() == 3).limit(4);
        Stream<String> Wstr = wumenname.stream().filter(s -> s.length() == 3).limit(3);
        Stream<String> MWstr = Stream.concat(Mstr,Wstr);
        MWstr.map(Student::new).forEach(System.out::println);
        System.out.println("===================================================");
        Stream.concat(manname.stream().filter(s -> s.length()==3).limit(4),
                wumenname.stream().filter(s -> s.length()==3).limit(3)).map(Student::new)
                .forEach(System.out::println);

    }
}
