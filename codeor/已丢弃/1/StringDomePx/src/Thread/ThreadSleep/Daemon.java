package Thread.ThreadSleep;
/*
守护线程
 */
public class Daemon
{
    public static void main(String[] args)
    {
        Thread.currentThread().setName("Thread");
        DaemonDome dt1 = new DaemonDome();
        DaemonDome dt2 = new DaemonDome();
        DaemonDome dt3 = new DaemonDome();

        dt1.setDaemon(true);
        dt2.setDaemon(true);
        dt3.setDaemon(true);

        dt1.start();
        dt2.start();
        dt3.start();

        for (int i = 0; i < 20; i++)
        {
            System.out.println(Thread.currentThread().getName() + " : " +i);
        }
    }
}
