package Thread.PriorityThread;

/*
 线程优先级
 最高10 最低1 默认5
 通过setPriority设置 get获取
 */
public class Mmian
{
    public static void main(String[] args)
    {
        java.lang.Thread th1 = new Thread();
        java.lang.Thread th2 = new Thread();
        java.lang.Thread th3 = new Thread();
        th1.setName("一号");
        th2.setName("二号");
        th3.setName("三号");
        th1.setPriority(8);
        th2.setPriority(5);
        th3.setPriority(2);
        th1.start();
        th2.start();
        th3.start();

    }
}
