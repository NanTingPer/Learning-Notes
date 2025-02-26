package FunctionalInter_._002;

public class Dome
{
    //函数式编程接口可以直接使用Lmabda表达式作为参数传递
    public static void main(String[] args)
    {
        Runble(() -> System.out.println(Thread.currentThread().getName() + "线程启动了"));
    }

    private static void Runble(Runnable e)
    {
        new Thread(e).start();
    }
}
