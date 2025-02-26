package IO.TreeSetToTxt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        TreeSet<Student> stu = new TreeSet<Student>(new Comparator<Student>()
        {
            @Override
            public int compare(Student s1, Student s2)
            {
                if (!(s2.toString().equals(s1.toString())))
                {
                    if (s2.zf() > s1.zf())
                    {
                        return 1;
                    }
                    else if(s2.zf() == s1.zf())
                    {
                        if (s2.getChis() > s1.getChis() && s2.getEnglsih() > s1.getEnglsih())
                        {
                            return 1;
                        }
                        else
                        {
                            return -1;
                        }
                    }
                }

                return 0;
            }
        });
        BufferedWriter br = new BufferedWriter(new FileWriter("StringDomePx\\src\\IO\\TreeSetToTxt\\Student.txt"));
        Scanner sc = new Scanner(System.in);
        int yuwen;int yingyu;    int shuxue;        int age;        String name;
        for (int i = 0;i<3;i++)
        {
            System.out.println("请输入姓名:");
            name = sc.next();
            System.out.println("请输入年龄:");
            age = sc.nextInt();
            System.out.println("请输入语文成绩:");
            yuwen = sc.nextInt();
            System.out.println("请输入数学成绩:");
            shuxue = sc.nextInt();
            System.out.println("请输入英语成绩:");
            yingyu = sc.nextInt();
            stu.add(new Student(name,age,yuwen,shuxue,yingyu));
        }
        for (Student st : stu)
        {
            br.write(st.toString());
            br.newLine();
            br.flush();
        }
        br.close();
    }
}
