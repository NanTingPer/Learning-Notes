package 力扣.面试真题;

public class _58_最后一个单词的长度
{
    class Solution {
        public int lengthOfLastWord(String s) {
            for(int i = s.trim().length()-1; i >= 0 ;i--)
            {
                if(s.trim().charAt(i) == ' ')
                {
                    return s.trim().length() - i - 1;
                }
            }
            return s.trim().length();
        }
    }
}
