namespace S26;

/// <summary>
/// <code>
/// 给你一个 非严格递增排列 的数组 nums ，请你 原地 删除重复出现的元素，使每个元素 只出现一次 ，返回删除后数组的新长度。元素的 相对顺序 应该保持 一致 。然后返回 nums 中唯一元素的个数。
///考虑 nums 的唯一元素的数量为 k ，你需要做以下事情确保你的题解可以被通过：
///更改数组 nums ，使 nums 的前 k 个元素包含唯一元素，并按照它们最初在 nums 中出现的顺序排列。nums 的其余元素与 nums 的大小不重要。
///返回 k 。
/// </code>
/// </summary>
internal class Program
{
    static void Main(string[] args)
    {
        int[] nums = [1, 1, 2];
        RemoveDuplicates(nums);
        Console.WriteLine(string.Join(",", nums));
    }

    public static int RemoveDuplicates(int[] nums)
    {
        int[] ints = new int[nums.Length];
        for (int i = 0; i < ints.Length; i++) {
            ints[i] = int.MaxValue;
        }

        for (int i = 0; i < nums.Length; i++) {
            bool 是否存在 = false;
            var inValue = nums[i];
            foreach (var item in ints) {
                if(item == inValue) {
                    是否存在 = true;
                    break;
                }
            }
            if (!是否存在) {
                ints[i] = inValue;
            }
        }

        for (int i = 0; i < ints.Length; i++) {
            for (int j = 0; j < ints.Length; j++) {
                if (ints[i] < ints[j]) {
                    var tmp = ints[j];
                    ints[j] = ints[i];
                    ints[i] = tmp;
                }
            }
        }

        for (int i = 0; i < ints.Length; i++) {
            nums[i] = ints[i];
        }

        var count = 0;
        foreach (var item in nums) {
            if (item == int.MaxValue)
                count++;
        }

        return nums.Length - count;
    }
}
