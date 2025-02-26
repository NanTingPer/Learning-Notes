package HasMapL.Stend;

import java.util.Objects;

public class Sten
{
    private int age;
    private String name;

    private Sten(){}
    public Sten(String name,int age)
    {
        this.age = age;
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sten sten = (Sten) o;
        return age == sten.age && Objects.equals(name, sten.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(age, name);
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
        return ("姓名: " + this.name + " 年龄: " + this.age);
    }
}
