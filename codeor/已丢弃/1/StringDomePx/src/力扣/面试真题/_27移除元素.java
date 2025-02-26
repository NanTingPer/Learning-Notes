package 力扣.面试真题;

class Solution {
    public int removeElement(int[] nums, int val) {
        int cd = 0;
        int[] nums2 = new int[nums.length];
        for(int i = 0 ;i< nums.length;i++)
        {
            if(nums[i] != val)
            {
                cd ++;
                nums2[i-cd] = nums[i];
            }
        }
        nums = nums2;
        return cd;
    }
}