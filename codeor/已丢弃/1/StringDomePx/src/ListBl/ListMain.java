package ListBl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ListMain
{
    public static void  main(String[] sasg)
    {
        List<XsClass> xslist = new ArrayList<XsClass>();
        Scanner sr = new Scanner(System.in);
        XsClass xs = new XsClass();
        String name = "";
        int age;

        while (true)
        {
            System.out.println("请输入学生姓名:");
            name = sr.next();
            System.out.println("请输入学生年龄:");
            age = sr.nextInt();
            xs.setAge(age);
            xs.setName(name);
            xslist.add(xs);
            BlList.BlListr(xslist);
            /*
            Iterator<XsClass> it = xslist.iterator();
            while (it.hasNext())
            {
                XsClass xs_n = new XsClass();
                xs_n = it.next();
                System.out.println("姓名: " + xs_n.getName() + "   年龄: " + xs_n.getAge());
            }
             */
        }
    }
}
