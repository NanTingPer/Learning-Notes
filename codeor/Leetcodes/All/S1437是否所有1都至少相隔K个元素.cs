namespace All;

public class S1437是否所有1都至少相隔K个元素
{
    public bool KLengthApart(int[] nums, int k)
    {
        int splitZero = 0;
        bool isInit = false;
        for (int i = 0; i < nums.Length; i++) {
            //遇到第一个零才开始计算，不然 001001 k=2 其实是要返回true的
            if(nums[i] != 1 && isInit == false) {
                continue;
            }

            //第一次的init会让nums[i] == 1 这样就直接返回false了
            if(isInit == true) {
                if (nums[i] == 0) {
                    splitZero += 1;
                } else {
                    if (splitZero < k) {
                        return false;
                    }
                    splitZero = 0;
                }
            }
            
            //跳过第一个1
            isInit = true;
        }
        return true;
    }
}
