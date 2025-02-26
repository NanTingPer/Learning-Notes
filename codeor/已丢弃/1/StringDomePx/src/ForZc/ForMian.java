package ForZc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ForMian
{

    public static void main(String[] args)
    {
        Scanner sr = new Scanner(System.in);
        List<Xs> XsList = new ArrayList<Xs>();
        String Name;
        int Age;
        while (true)
        {
            System.out.println("请输入姓名");
            Name = sr.next();
            System.out.println("请输入年龄");
            Age = sr.nextInt();
            XsList.add(new Xs(Age,Name));
            for (Xs en : XsList)
            {
                System.out.println("年龄: " + en.getAge() + " 姓名: " + en.getName());
            }
        }
    }
}
