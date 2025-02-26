package interface_.priv;

public interface inter_
{
    default void show1()
    {
        System.out.println("show1开始");
        inter_.show5();
        show5();
        System.out.println("show1结束");
    }

    default void show2()
    {
        System.out.println("show2开始");
//        inter_.show5();
        show5();
        System.out.println("show2结束");
    }
    static void show3()
    {
        System.out.println("show3开始");
//        inter_.show5();
        show5();
        System.out.println("show3结束");
    }
    static void show4()
    {
        System.out.println("show4开始");
//        inter_.show5();//
        show5();
        System.out.println("show4结束");
    }

    private static void show5()
    {
        System.out.println("1");
        System.out.println("1");
        System.out.println("1");
        System.out.println("1");
    }
}
