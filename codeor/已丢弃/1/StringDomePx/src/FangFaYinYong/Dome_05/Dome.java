package FangFaYinYong.Dome_05;

public class Dome
{
    public static void main(String[] args)
    {
        //lambda 表达式
        printNameAge((s,i) -> new Student(s,i));

        //引用构造器
        printNameAge(Student::new);
    }

    private static void printNameAge(Student_ e)
    {
        Student st = e.fh("李精",16);
        System.out.println(st.getName_Age());
    }
}
