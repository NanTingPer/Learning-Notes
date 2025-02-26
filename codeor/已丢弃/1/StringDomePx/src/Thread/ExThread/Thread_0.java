package Thread.ExThread;

public class Thread_0 extends Thread
{
    @Override
    public void run()
    {
        for (int i = 0; i < 100; i++)
        {
            System.out.println(i);
        }
    }
}
