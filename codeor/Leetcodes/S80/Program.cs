Console.WriteLine("hELLO");
//给你一个有序数组 nums ，
//请你 原地 删除重复出现的元素，
//使得出现次数超过两次的元素只出现两次 ，
//返回删除后数组的新长度。

//不要使用额外的数组空间，
//你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。

//不能改变数组长度 删除重复项目
int[] value = [0, 0, 1, 1, 1, 1, 2, 3, 3];
var k = RemoveDuplicates(value);
Console.WriteLine(k);
Console.WriteLine(string.Join(',', value));
    static int RemoveDuplicates(int[] nums)
    {
        if(nums.Length <= 2) {
            return nums.Length;
        }
        int end = 2, ste = 2;
    
        //[0, 0, 1, 1, 1, 1, 2, 3, 3]
       //  0  1  2  3  4  5  6  7  8
       // 假设走到2号位，因为 4 和 2 相等 所以end会卡着不动，直到ste脱离相等的范围
    
        while (ste < nums.Length) {
            if (nums[end - 2] != nums[ste]) {
                nums[end] = nums[ste];
                end++;
            }
            ste++;
        }
    
        return end;
    }