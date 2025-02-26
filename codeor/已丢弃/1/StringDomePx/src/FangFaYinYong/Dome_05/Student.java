package FangFaYinYong.Dome_05;

public class Student
{
    private int Age;
    private String Name;

    private Student(){};

   public Student(String name,int age)
    {
        Age = age;
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

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getName_Age()
    {
        return Name + "," + Age;
    }

}
