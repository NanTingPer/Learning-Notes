package FanShe._001;

public class Student
{
    private String Name;
    private int Age;
    private String XingBie;
    public Student(){};
    public Student(String s,int i,String x)
    {
        this.Name = s;
        this.Age = i;
        this.XingBie = x;
    }

    private Student(String s,int i)
    {
        XingBie = null;
        Age = i;
        Name = s;
    }

    @Override
    public String toString()
    {
        return "姓名 : " + Name + " 年龄 : " + Age + " 性别 : " + XingBie;
    }
}
