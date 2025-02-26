package Thread.Dome_All;

public class Box
{
    private int unm = 0;
    private boolean cat = false;
    public synchronized void GetMikr()
    {
        if (!cat)
        {
            try
            {
                wait();
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        System.out.println("取出了第" + unm + "瓶牛奶");
        cat = false;
        notify();
    }

    public synchronized void SetMikr(int unm)
    {
        if (cat)
        {
            try
            {
                wait();
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        this.unm = unm;
        System.out.println("存入了第" + unm + "瓶牛奶");
        cat = true;
        notify();
    }

    public int getUnm()
    {
        return unm;
    }
}
