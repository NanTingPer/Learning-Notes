package FunctionalInter_._001;

public class Dome
{
    static Inter_Dome id = () -> System.out.println("调用了函数式接口的 show方法");
    public static void main(String[] args)
    {
        id.show();
    }
    //使用引用类型


}
