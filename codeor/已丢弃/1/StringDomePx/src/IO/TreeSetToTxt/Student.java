package IO.TreeSetToTxt;

public class Student
{
    private Student(){}

    private String Name;
    private int Age;
    private int Chis;
    public Student(int age, int chis, int num, int englsih,String name)
    {
        Name = name;
        Age = age;
        Chis = chis;
        this.num = num;
        Englsih = englsih;
    }

    public int zf()
    {
        return Englsih + Chis + num;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public int getAge()
    {
        return Age;
    }

    public void setAge(int age)
    {
        Age = age;
    }

    public int getChis()
    {
        return Chis;
    }

    public void setChis(int chis)
    {
        Chis = chis;
    }

    public int getNum()
    {
        return num;
    }

    public void setNum(int num)
    {
        this.num = num;
    }

    public int getEnglsih()
    {
        return Englsih;
    }

    public void setEnglsih(int englsih)
    {
        Englsih = englsih;
    }

    public Student(String name, int age, int chis, int num, int englsih)
    {
        Name = name;
        Age = age;
        Chis = chis;
        this.num = num;
        Englsih = englsih;
    }

    private int num;
    private int Englsih;

    @Override
    public String toString()
    {
        return "姓名: " + Name +  "\t年龄: " + Age + "\t语文: " + Chis + "\t英语: " + Englsih + "\t数学: " + num + "\t总分:" + zf();
    }
}
