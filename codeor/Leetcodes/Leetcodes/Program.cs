namespace S27
{
    /// <summary>
    /// <code>
    /// 给你一个数组 nums 和一个值 val，你需要 原地 移除所有数值等于 val 的元素。元素的顺序可能发生改变。然后返回 nums 中与 val 不同的元素的数量。
    /// 假设 nums 中不等于 val 的元素数量为 k，要通过此题，您需要执行以下操作：
    /// 更改 nums 数组，使 nums 的前 k 个元素包含不等于 val 的元素。nums 的其余元素和 nums 的大小并不重要。
    /// 返回 k。
    /// </code>
    /// </summary>
    internal class Program
    {
        static void Main(string[] args)
        {
            int[] values = [2, 2, 3];
            int ne= RemoveElement(values, 2);
            Console.WriteLine(string.Join(",", values) + "  长度: " + ne);
        }

        public static int RemoveElement(int[] nums, int val)
        {
            int count = 0;
            int tmpCount = 0;
            foreach (var item in nums) {
                if (item == val)
                    tmpCount++;
            }
            
            for (int i = 0; i < nums.Length; i++) {
                if (nums[i] == val) {
                    count++;
                    for (int j = i; j < nums.Length - 1; j++) {
                        int temp = nums[j + 1];
                        nums[j] = temp;
                    }
                    if(count <= tmpCount) {
                        i -= count;
                        if (i < 0) i = -1;
                    }
                }
            }
            
            for(int i = 1; i <= tmpCount; i++) {
                nums[^i] = default;
            }
            return nums.Length - tmpCount;
        }
    }
}
