package Thread.Dome_All;

/*
* notify(); 唤醒
* wait(); 等待
*
* 唤醒等待使用该方法的线程
* 等待 让使用该方法的线程等待被唤醒
* */
public class Dome_main
{
    public static void main(String[] args)
    {
        Box b = new Box();
        And a = new And(b);
        Boss bo = new Boss(b);

        Thread th1 = new Thread(a);
        Thread th2 = new Thread(bo);

        th1.start();
        th2.start();
    }
}
