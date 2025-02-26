package FangFaYinYong.Dome_03;

public class Dome
{
    public static void main(String[] args)
    {
        printStr(s-> System.out.println(s.toUpperCase()));

        //对象的方法引用
        Dome_ d = new Dome_();
        printStr(d::printSt);
    }

    private static void printStr(Inter_ e)
    {
        e.show("HelloWorld");
    }
}
