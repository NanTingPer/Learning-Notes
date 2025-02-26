package 力扣.面试真题;

public class _28_找出字符串中第一个匹配项的下标
{
    class Solution {
        public int strStr(String haystack, String needle)
        {
            //存储目标长度进行判断
            int mblenth = needle.length();
            //存储传入长度进行判断
            int jylenth = haystack.length();
            //使用可变长字符串进行判断
            StringBuilder sb = new StringBuilder();
            //遍历传入字符串
            for (int i = 0;i < haystack.length();i++)
            {
                sb.delete(0,sb.length());
                //如果剩下的字符串长度已经比目标长度短那就无需多言了
                if (jylenth - i < mblenth) return -1;
                //合并后面的相对长度字符串
                for (int j = i;j <= mblenth;j++)
                {
                    sb.append(haystack.charAt(j));
                    //如果相等返回i
                    if (sb.toString().equals(needle))
                    {
                        return  i;
                    }
                }
            }
            return -1;
        }
    }
}
