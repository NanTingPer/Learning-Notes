package SterSet;

import java.util.Scanner;
import java.util.TreeSet;

public class SFpx
{
    public static void main(String[] args)
    {
        TreeSet<Xs> tre = new TreeSet<Xs>();
        String name;
        int age;
        Scanner sc = new Scanner(System.in);

        while (true)
        {
            System.out.println("请输入姓名: ");
            name = sc.next();
            System.out.println("请输入年龄: ");
            age = sc.nextInt();
            tre.add(new Xs(age, name));
            System.out.println("是否继续? 1,是 2,否");
            if (sc.nextInt() == 1)
            {
            } else
            {
                break;
            }
        }

        for (Xs xs : tre)
        {
            System.out.println(xs.toString());
        }
    }
}
