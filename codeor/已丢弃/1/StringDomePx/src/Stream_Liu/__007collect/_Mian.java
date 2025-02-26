package Stream_Liu.__007collect;

import java.util.*;
import java.util.stream.Collectors;

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

        //collect : 回收
        manname = manname.stream().filter(s -> s.length()==3).collect(Collectors.toList());
        System.out.println(manname);

        //Set
        Set<String> set_  = wumenname.stream().filter(s -> s.length() == 2).collect(Collectors.toSet());
        System.out.println(set_);

        List<String> list_ = new ArrayList<String >();
        list_.add("周润发,12");
        list_.add("成龙,23");
        list_.add("刘德华,20");
        list_.add("吴京,40");
        list_.add("周星驰,45");
        list_.add("李连杰,60");

        //Map

        Map<String,String> map = list_.stream()
                .filter(s -> Integer
                        .parseInt(s.split(",")[1]) > 25)
                .collect(Collectors.toMap(
                        s -> s.split(",")[0],
                        s -> s.split(",")[1]));
        System.out.println(map);
    }
}
