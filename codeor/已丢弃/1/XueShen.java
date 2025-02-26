package you.lx;

public class XueShen
{
    private  String Name;
    private  int age;
    public  XueShen(){}
    public  XueShen(String Name,int age)
    {
        this.age = age;
        this.Name = Name;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getAge()
    {
        return age;
    }

    public void setName(String xs)
    {
        this.Name = Name;
    }

    public String getName()
    {
        return Name;
    }
}
