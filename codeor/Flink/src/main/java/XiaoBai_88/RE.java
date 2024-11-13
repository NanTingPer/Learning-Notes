package XiaoBai_88;

public class RE
{
    public static void main(String[] args)
    {
        int a = 10;
        for(int i =0;i<a;i++)
        {
            for(int e=0;e<a-i;e++){
                System.out.print(" ");
            }
            for(int j=0;j<i * 2;j++)
            {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
