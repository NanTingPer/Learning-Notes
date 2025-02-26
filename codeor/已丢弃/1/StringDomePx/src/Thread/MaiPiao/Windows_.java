package Thread.MaiPiao;

/*
* synchronized 同步代码块(锁)
* synchronized(new Obj){}
*synchronized 方法锁默认为 this
* synchronized 静态方法锁默认为 方法名.class
*
*
* Lock锁建议使用 try/finally
* 即便运行出问题也会释放锁
* xxx xxx = new xxx
*
* try
* {
*       xxx.lock();
* }
* catch(xxx x)
* {
* x.xxx;
* }
* finally
* {
* xxx.unlock();
* }
* */
public class Windows_
{
    public static void main(String[] args)
    {
        Runnable_ ru = new Runnable_();
        Thread Windows1 = new Thread(ru,"窗口1: ");
        Thread Windows2 = new Thread(ru,"窗口2");
        Thread Windows3 = new Thread(ru,"窗口3");

        Windows1.start();
        Windows2.start();
        Windows3.start();
    }
}
