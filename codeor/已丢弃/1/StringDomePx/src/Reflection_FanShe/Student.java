package Reflection_FanShe;

import java.awt.*;

public class Student
{
    private int age;
    public String name;
    String xb;

    public Student()
    {
        age = 10;
        name = null;
        xb = null;
    }

    public Student(int age, String name, String xb)
    {
        this.age = age;
        this.name = name;
        this.xb = xb;
    }

    @Override
    public String toString()
    {
        return "性别: " + xb + " 姓名: " + name + " 年龄: " + age;
    }

    public void setXb(String xb)
    {
        this.xb = xb;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    private void setName(String name)
    {
        this.name = name;
    }

    public String getXb()
    {
        return xb;
    }

    public int getAge()
    {
        return age;
    }

    public String getName()
    {
        return name;
    }
}
