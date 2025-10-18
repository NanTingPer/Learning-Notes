public class Test{
    public static void main(String[] args){
        int x = 3, y = 4, z = 5;
        String s = "xyz";
        System.out.println(s + x + y + z);

        if( x == 0 )
            System.out.println("冠军");
        else if( x >-3 )
            System.out.println("亚军");
        else
            System.out.println("季军");
    }
}