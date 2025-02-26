package Thread.ThreadSleep;
/*
等待线程停止
 */
public class Join
{
    public static void main(String[] args)
    {
        JoinDome jd1 = new JoinDome();
        JoinDome jd2 = new JoinDome();
        JoinDome jd3 = new JoinDome();

        jd1.start();
        try
        {
            jd1.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        jd2.start();
        jd3.start();
    }
}
