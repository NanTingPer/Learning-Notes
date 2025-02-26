package Lambda_.Lambda_1;

public class Dome
{
    public static void main(String[] args)
    {
        //匿名内部类
        Dome_(new Inte_()
        {
            @Override
            public void Inte_fly(String s)
            {
                //来源于方法中传递的 e.Inte_fly("你好");
                System.out.println(s);
                System.out.println("哈哈哈");
            }
        });

        System.out.println("Lambda表达式↓-----------------------------------------");

        //Lambda表达式
        Dome_((String s) ->
        {
            System.out.println(s);
            System.out.println("哈哈哈");
        });
    }
    private static void Dome_(Inte_ e)
    {
        e.Inte_fly("你好");
    }
}
