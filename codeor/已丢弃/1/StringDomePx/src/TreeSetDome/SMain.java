package TreeSetDome;

import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

public class SMain
{
    public static void main(String[] args)
    {
        int age;
        String name;
        TreeSet<Xs> xs = new TreeSet<Xs>(new Comparator<Xs>()
        {
            @Override
            public int compare(Xs thisxs, Xs xs)
            {
                //如果年龄跟名字都不一样就执行
                if (thisxs.getAge() != xs.getAge() || thisxs.getName().compareTo(xs.getName()) != 0)
                {
                    //如果前者年龄更小
                    if (thisxs.getAge() < xs.getAge())
                    {
                        return 1;
                    }
                    //如果前者姓名更小 1更大 0相等
                    else if (thisxs.getName().compareTo(xs.getName()) == -1)
                    {
                        return 1;
                    }
                    else
                    {
                        return -1;
                    }
                }
                else
                {
                    return 0;
                }
            }
        });

        Scanner sc = new Scanner(System.in);
        int js = 0;
        while (js < 4)
        {
            System.out.println("请输入年龄");
            age = sc.nextInt();
            System.out.println("请输入性别");
            name = sc.next();
            xs.add(new Xs(age,name));
            js++;
        }

        for(Xs xs1: xs)
        {
            System.out.println(xs1.toString());
        }
    }
}
