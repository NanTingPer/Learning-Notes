package ArrayListPx;

public class Sx
{
    private int age;
    private String name;

    private Sx(){}
    public Sx(int age, String name)
    {
        this.age = age;
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "年龄: " + age + " , " + "姓名: " + name;
    }
}
