package Thread.Runnable;

public class RunnableDome implements java.lang.Runnable
{
    @Override
    public void run()
    {
        for (int i = 0;i<100;i++)
        {
            System.out.println(Thread.currentThread().getName() + " : " + i);
        }
    }
}
