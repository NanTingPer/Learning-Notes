package ArrlyListOfHashMap;

import javax.naming.PartialResultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class HsahMap
{
    public static void main(String[] args)
    {
        //在HashMap内嵌套ArrayList
        //先创建HashMap
        HashMap<String, ArrayList<String>> hash = new HashMap<String,ArrayList<String>>();

        //创建ArrayList对象
        ArrayList<String> dsgg = new ArrayList<String >();
        dsgg.add("胡磊");
        dsgg.add("李密");
        hash.put("电商高工班",dsgg);

        ArrayList<String> jsj = new ArrayList<String >();
        jsj.add("力宏");
        jsj.add("丽红");
        hash.put("计算机高班",jsj);

        //遍历
        //先遍历HsahMap
        //遍历之前先获取 HsahMap KeySet
        Set<String> setkey = hash.keySet();
        for (String  list : setkey)
        {
            //获取内嵌的List集合
            ArrayList<String> ar =  hash.get(list);
            //遍历ArrayList
            System.out.println("\n" + list);
            for (int i = 0;i<ar.size();i++)
            {
                String a = ar.get(i);
                System.out.println(a);
            }
        }
    }
}
