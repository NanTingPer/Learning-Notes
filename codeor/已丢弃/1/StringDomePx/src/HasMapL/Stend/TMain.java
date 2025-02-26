package HasMapL.Stend;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class TMain
{
    public static void main(String[] args)
    {
        //实例化所需对象
        Scanner sc = new Scanner(System.in);
        Map<Integer,Sten> map = new HashMap<Integer,Sten>();

        //定义所需变量
        int age;
        String name;
        int cs,js = 0;

        System.out.println("请输入次数");
        cs = sc.nextInt();

        while (js < cs)
        {
            System.out.println("请输入姓名: ");
            name = sc.next();
            System.out.println("请输入年龄: ");
            age = sc.nextInt();

            map.put(js+1,new Sten(name,age));
            js++;
        }

        //遍历集合并输出
        Set<Map.Entry<Integer, Sten>> setmap = map.entrySet();

        for (Map.Entry<Integer,Sten> mapa : setmap)
        {
            int key;
            key = mapa.getKey();
            Sten sten;
            sten = map.get(key);//效果一样
//            sten = mapa.getValue();

            System.out.println("序号: " + key + " , " + "姓名: " + sten.getName() + " , " + "年龄: " + sten.getAge());
        }
    }
}
