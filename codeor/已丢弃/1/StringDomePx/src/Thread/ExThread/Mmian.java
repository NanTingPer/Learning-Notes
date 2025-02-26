package Thread.ExThread;

/*
* 通过继承 Thread来实现多线程
* 1，创建一个类继承Thread
* 2，重写Thread的run方法 内容为该线程所需运行
* 3，在主线程中创建该类的对象
* 4，使用该对象的start方法启动该线程
* */
public class Mmian
{
    public static void main(String[] sage)
    {
        Thread te = new Thread_0();
        Thread te0 = new Thread_0();
        te.start();
        te0.start();
    }

}
