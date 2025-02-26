package StringNum;

import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Mmain
{
    public static void main(String[] arge)
    {
        Scanner sc = new Scanner(System.in);
        //字符为键 整型为值
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        System.out.println("请输入字符串: ");
        String str = sc.next();

        for (int i = 0; i < str.length(); i++)
        {
            //判断是否存在
            int cc;//可不用定义
            char c = str.charAt(i);
            //返回无 则添加
            if (map.containsKey(c) == false)
            {
                map.put(str.charAt(i),1);
            }
            else
            {

                cc = map.get(c);//可不用定义
                map.put(c,cc+1);//cc+1可直接写为 map.get(c) + 1
            }
        }
        //遍历并串联
        Set<Map.Entry<Character,Integer>> SetMap = map.entrySet();
        String st = "";//内存浪费
        for (Map.Entry<Character,Integer> se : SetMap)
        {
            char cha = se.getKey();
            int in = se.getValue();
            st = st + cha + "(" + in +")";
        }
        System.out.println(st);
    }

}
