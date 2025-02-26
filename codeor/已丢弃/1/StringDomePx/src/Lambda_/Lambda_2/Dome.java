package Lambda_.Lambda_2;

public class Dome
{
    public static void main(String[] args)
    {
        add_((int x,int y) ->
        {
            return x+y;
        });

    }

    private static void add_(Inte_ e)
    {
        //会去寻找实现类 而写的Lambda表达式内 实现了该类
        int a = e.add(10,20);
        System.out.println(a);
    }
}
