package Stream_Liu._006Add;

public class Student
{
    public Student(String name)
    {
        this.name = name;
    }
    private String name;

    public void  setName(String  name)
    {
        this.name = name;
    }

    public String getName()
    {
        if(this.name == null)
        {
            setName("空");
            return name;
        }
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
