package ItForHastSet;

import javax.swing.text.html.parser.TagElement;
import java.util.Objects;

public class Xs
{
    private int age;
     private String name;
    public Xs(){}
    public Xs(int age,String name)
    {
        this.age = age;
        this.name = name;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return ("姓名: " + this.name + " 年龄: " + this.age);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Xs xs = (Xs) o;
        return age == xs.age && Objects.equals(name, xs.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(age, name);
    }
}
