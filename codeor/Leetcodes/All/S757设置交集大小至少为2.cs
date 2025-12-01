using System;
using System.Collections.Generic;
using System.Text;
namespace All;

/// <summary>
/// 算出一个集合，这个集合至少包含每个子集中的2个数
/// </summary>
internal class S757设置交集大小至少为2
{
    public int IntersectionSizeTwo(int[][] intervals)
    {

        // 最终的交集
        List<List<int>> list = new List<List<int>>();
        for (int i = 0; i < intervals.Length; i++) {
            list.Add(Enumerable.Range(intervals[i][0], intervals[i][1]).ToList());
        }
        List<int> result = new List<int>();
        for (var i = 0; i < list.Count - 1; i++) {
            var oneIntersect = list[i].Intersect(list[i + 1]);
            var oneIntersectSort = oneIntersect.ToList();
            oneIntersectSort.Sort();
            if (oneIntersect.Count() >= 2) {
                result.AddRange(oneIntersect.TakeLast(2));
            } else {
                list[i].Sort();
                result.AddRange(list[i].TakeLast(2));
            }
        }

        Console.WriteLine(string.Join(", ", result));
        
        return 0;
    }
}
