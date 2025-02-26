package TreeSetDome;

public class Xs //implements Comparable
{
    private int age;
    private String name;

    public Xs(){}
    public Xs(int age,String name)
    {
        this.age  = age;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return ("姓名: " + this.name + " 年龄: " + this.age);
    }
}
