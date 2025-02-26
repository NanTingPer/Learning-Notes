package SterSet;

public class Xs implements Comparable<Xs>
{
    private int age;
    private String name;

    private Xs(){}
    public Xs(int age,String name)
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
    public int compareTo(Xs xs)
    {
        //名字不同或者年龄不同
        if (this.name.equals(xs.name) == false || this.age != xs.age)
        {
            if (this.age > xs.age)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return 0;
        }
    }

    @Override
    public String toString()
    {
        return ("年龄: " + this.age + " 姓名: " + this.name);
    }
}
