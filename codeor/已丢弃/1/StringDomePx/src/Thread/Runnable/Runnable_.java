package Thread.Runnable;
/*
类实现 Runnable 接口 重写 run方法

创捷该类实例 作为 Thread参数传递

*/
public class Runnable_
{
    public static void main(String[] args)
    {
        RunnableDome rd = new RunnableDome();
        Thread th1 = new Thread(rd,"Th1");
        Thread th2 = new Thread(rd,"Th2");

        th1.start();
        th2.start();
    }
}
