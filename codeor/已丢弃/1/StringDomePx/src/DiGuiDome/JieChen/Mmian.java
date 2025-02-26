package DiGuiDome.JieChen;

public class Mmian
{
    public static void main(String[] args)
    {
        System.out.println(dg(0));
    }
    public static int dg(int a)
    {
        if (a == 1)
        {
            return 1;
        }
        else if (a == 0)
        {
            return 0;
        }
        else
        {
            return dg(a - 1)  * a;
        }

    }
}
