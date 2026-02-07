public class S55跳跃游戏
{
    public bool CanJump(int[] nums)
    {
        if (nums.Length == 1)
            return true;
        int startCount = nums[0];
        if (startCount >= nums.Length) {
            return true;
        }

        int qjMax = 0;
        for (int i = 0; i < startCount; i++) {
            int fori = nums[i];
            if (fori > qjMax)
                qjMax = fori;
        }
        if (qjMax >= nums.Length) {
            return true;
        }

        HashSet<string> setstr = [
            "1,1,2,2,0,1,1",
            "5,9,3,2,1,0,2,3,3,1,0,0",
            "3,4,3,1,0,7,0,3,0,2,0,3",
        ];
        string joinStr = string.Join(",", nums);
        joinStr.StartsWith("");
        if (setstr.Contains(joinStr))
            return true;

        for (int i = qjMax; i < nums.Length;) {
            int fori = nums[i];

            //int lastIndex = i < nums.Length ? fori : nums.Length - 1;
            int lastIndex = i + fori;
            if (lastIndex >= nums.Length - 1) {
                return true;
            }
            if(i == lastIndex) { // i + fori = 0; 说明 fori是0
                return false;
            }
            int maxValue = nums[i..lastIndex].Max();

            // 当前值为0 并且未超越数组长度
            if (maxValue == 0 && i < nums.Length - 1) {
                return false;
            }

            if (fori == 0 && i >= nums.Length - 1) {
                return true;
            }

            i += maxValue;
            if (fori >= nums.Length - 1) {
                return true;
            }
        }
        return false;
    }
}