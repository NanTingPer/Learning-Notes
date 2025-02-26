package FangFaYinYong.Dome_01;

public class Dome
{
    public static void main(String[] args)
    {
        printStirng(s -> System.out.println(s));

        printStirng((String s) ->{
            System.out.println(s);
        });

        //:: 方法引用
        printStirng(System.out::println);

    }

    private static void printStirng(Inter_ e)
    {
        e.show("爱生活");
    }
}
