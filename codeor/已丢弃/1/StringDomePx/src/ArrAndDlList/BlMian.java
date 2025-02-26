package ArrAndDlList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BlMian
{
    public static void main(String[] args)
    {
        //创建Arlist集合
        List<String> array = new ArrayList<String>();
        array.add("你好");
        array.add("我很好");
        //遍历 For
        for (int i = 0;i < array.size();i++)
        {
            System.out.println(array.get(i));
        }
        System.out.println("________________");
        //____________________It(迭代器)
        Iterator<String> it = array.iterator();
        while (it.hasNext())
        {
            System.out.println(it.next());
        }
        System.out.println("________________");

        //linkdList
        List<String> linked = new LinkedList<String>();
        linked.add("你好");
        linked.add("我很好");
        for (int i = 0;i<linked.size();i++)
        {
            System.out.println(linked.get(i));
        }
        System.out.println("________________");
        Iterator<String> it2 = linked.iterator();
        while (it2.hasNext())
        {
            System.out.println(it2.next());
        }
        System.out.println("________________");
    }
}
