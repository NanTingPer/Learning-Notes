namespace 西夏浦本质论.数组;

public static class 区间
{
    public static void MethodOne()
    {
        int[] nums = [
            0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16    
        ];

        //使用join可以在每个成员间，使用给定字符分割
        //区间包含开头 不包含结尾
        Console.WriteLine(string.Join(", ", nums[0.. 7]));

        //0, 1, 2, 3, 4, 5, 6

        //区间前面加上^ 可以倒着取
        Console.WriteLine(string.Join(", ", nums[^7.. ^0]));
        //10, 11, 12, 13, 14, 15, 16

        //全取
        Console.WriteLine(string.Join(", ", nums[..]));
    }
}
