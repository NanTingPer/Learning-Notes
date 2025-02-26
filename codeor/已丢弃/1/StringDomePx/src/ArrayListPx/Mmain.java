package ArrayListPx;

/*
* 对List集合进行排序
* */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Mmain
{
    public static void main(String[] args)
    {
        ArrayList<Sx> list = new ArrayList<Sx>();
        list.add(new Sx(12,"a李民浩"));
        list.add(new Sx(14,"b李民浩"));
        list.add(new Sx(13,"c李民浩"));
        list.add(new Sx(15,"e李民浩"));
        list.add(new Sx(15,"d李民浩"));

        //排序
        Collections.sort(list, new Comparator<Sx>()
        {
            @Override
            public int compare(Sx o1, Sx o2)
            {
                if (o1.getName() == o2.getName() && o1.getAge() == o1.getAge())
                {
                    return 0;
                }

                if (o1.getAge() == o2.getAge())
                {
                    if (o1.getName().charAt(0) > o2.getName().charAt(0))
                    {
                        return 1;//放前面
                    }
                    else
                    {
                        return -1;//放后面
                    }
                }

                if (o1.getAge() > o2.getAge())
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
        });

        for (int i =0 ;i<list.size();i++)
        {
            System.out.println(list.get(i).toString());
        }
    }
}
