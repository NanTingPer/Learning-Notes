package ListBl;

public class XsClass
{
    public XsClass()
    {
    }

    public XsClass(int age, String name)
    {
        this.age = age;
        this.name = name;
    }
    private int age;
    private String name;

    public int getAge()
    {
        return age;
    }

    public String getName()
    {
        return name;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
