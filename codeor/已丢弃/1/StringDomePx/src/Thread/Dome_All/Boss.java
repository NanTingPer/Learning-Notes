package Thread.Dome_All;

/**
 * 生产者
 * */
public class Boss implements Runnable
{
    private Box box;
    public Boss(Box box){this.box = box;}
    @Override
    public void run()
    {
        for (int i =1;i<=5;i++)
        {
            box.SetMikr(i);
        }
    }
}
