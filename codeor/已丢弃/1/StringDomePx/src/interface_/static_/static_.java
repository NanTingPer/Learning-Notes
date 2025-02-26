package interface_.static_;

public interface static_
{
    void show1();
    default void show2()
    {
        System.out.println("默认方法");
    }

    static void show3()
    {
        System.out.println("静态方法");
    }

}
