package FangFaYinYong.Dome_04;

public class Dome
{
    public static void main(String[] args)
    {
        //lambda表达式
        GeString((s,x,y) -> s.substring(x,y));
        //方法引用
        GeString(String::substring); //将第一个参数作为发起者 后面的参数传递给方法
    }
    private static void GeString(Inter_ e)
    {
        String s = "HelloWorld";
        s = e.st(s,2,5);
        System.out.println(s);
    }
}
