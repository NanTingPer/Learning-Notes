package TreeSetCJPx;

import java.util.Scanner;
import java.util.TreeSet;

public class Main_1
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        TreeSet<Xs> Tree_Xs= new TreeSet<Xs>();
        int yy;//英语
        int sx;//数学
        int yw;//语文
        int wl;//物理
        int age;
        String name;
        int js = 0;
        int js1 = 0;
        while (js < 5)
        {
            System.out.println("分别输入姓名与年龄:");
            name = sc.next();
            age = sc.nextInt();

            System.out.println("分别输入 语文 数学 英语 物理 成绩(回车):");
            yy = sc.nextInt();
            sx = sc.nextInt();
            yw = sc.nextInt();
            wl = sc.nextInt();
            if (Tree_Xs.add(new Xs(yw,sx,yy,wl,age,name)) == false)
            {
                js1++;
            }
            js ++;
        }

        for (Xs xs : Tree_Xs)
        {
            System.out.println(xs.toString());
        }
        System.out.print("失败次数: " + js1);

    }
}
