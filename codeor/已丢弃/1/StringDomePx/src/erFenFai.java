public class erFenFai
{
    public static void main(String[] args)
    {
        int[] arry = new int[]{10,20,30,40,60,121,140,151,231,421,3313};
        System.out.println(erfencz(arry,3313));
    }

    public  static int erfencz(int[] aryy,int num)
    {
        //左
        int left = 0;
        //右
        int heig = aryy.length -1;
        while (left <= heig)
        {
            //中间位置
            int mind = (heig + left) / 2;
            //如果这个数字小于中间位置 右边位移
            if(num < aryy[mind])
            {
                heig = mind-1;
            }
            else if(num > aryy[mind])
            {
                left = mind+1;
            }
            else
            {
                return mind;
            }
        }
        return -1;

    }
}
