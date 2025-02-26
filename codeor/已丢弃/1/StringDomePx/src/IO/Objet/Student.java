package IO.Objet;

import java.io.Serializable;

public class Student implements Serializable
{
    private static final long serialVersionUID = 1;
    private transient int Age; //不参与序列化
    private String Name;
    private Student(){};

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

    public Student(int age, String name)
    {
        Age = age;
        Name = name;
    }
}
