package Thread.Dome_All;

/**
 * 用户
 * */
public class And implements Runnable
{
    private Box box;
    public And(Box box){this.box = box;};
    @Override
    public void run()
    {
        while (true)
        {
            box.GetMikr();
        }
    }
}
