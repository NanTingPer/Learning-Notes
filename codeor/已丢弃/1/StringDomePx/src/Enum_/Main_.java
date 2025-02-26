package Enum_;

public class Main_
{
    public static void main(String[] args)
    {
        Red_Bule_Green red = Red_Bule_Green.Red;
        System.out.println(red);
        System.out.println(red.getName());
        red.show();

        Red_Bule_Green green = Red_Bule_Green.Green;
        System.out.println(green);
        System.out.println(green.getName());
        red.show();

        Red_Bule_Green blue = Red_Bule_Green.Blue;
        System.out.println(blue);
        System.out.println(blue.getName());
        red.show();
    }
}
