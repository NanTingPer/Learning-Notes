package Thread.PriorityThread;

public class Thread extends java.lang.Thread
{
    public Thread(){}
    public Thread(String name)
    {
        super(name);
    }
    @Override
    public void run()
    {
        for (int i =0;i<100;i++)
        {
            System.out.println(getName() + ": " + i);
        }
    }
}
