package Lambda_;

import java.lang.Thread;

public class Lambda_0
{
    public static void main(String[] args)
    {
        //()  代表形参
        ///-> 执行的代码
        new Thread(() ->
        {
            System.out.println("内容");
        }).start();


        Runnable r = () -> System.out.println("你好");
        new Thread(r).start();

        new Thread(() -> System.out.println("你好")).start();

        interface_ in = new interface_Dome();
        interface_001(in);

        interface_001(new interface_()
        {
            @Override
            public void dog()
            {
                System.out.println("你好！欢迎使用");
            }
        });

        interface_001(()->
        {
            System.out.println("你好！欢迎使用");
        });
    }

    private static void interface_001(interface_ e)
    {
        e.dog();
    }
}
