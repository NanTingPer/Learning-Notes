package ArrlyListOfHashMap;

import java.util.*;

//在ArrarList内嵌套 HashMap
public class ArryList
{
    public static void main(String[] args)
    {
        //创建ArrayList集合
        ArrayList<HashMap<String,String>> List = new ArrayList<HashMap<String,String>>();

        //创建HashMap并将其添加入 ArrayList
        HashMap<String,String> ha1 = new HashMap<String,String>();
        ha1.put("01","李宏亮");
        ha1.put("02","李红梅");
        List.add(ha1);

        HashMap<String,String> ha2 = new HashMap<String,String>();
        ha2.put("01","李宏亮01");
        ha2.put("02","李红梅01");
        List.add(ha2);

        HashMap<String,String> ha3 = new HashMap<String,String>();
        ha3.put("01","李宏亮02");
        ha3.put("02","李红梅02");
        List.add(ha3);

        //遍历
        //先遍历ArrayList
        for (int i = 0;i < List.size(); i++ )
        {
            //内部遍历HashMap
            //获取HashMap
            HashMap<String, String> hamp = List.get(i);
            //获取键值
            Set<String> se = hamp.keySet();
            //遍历
            for(String  aa : se)
            {
                System.out.println(hamp.get(aa));
            }
        }

    }
}
