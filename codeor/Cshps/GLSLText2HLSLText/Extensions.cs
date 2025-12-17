namespace GLSLText2HLSLText;

public static class Extensions
{
    extension(string str)
    {
        //public static bool operator >> (string str1, Func<ReadOnlySpan<char>, bool> func)
        //    => func(str1);

        /// <summary>
        /// 返回给定字符首次出现的位置
        /// </summary>
        /// <param name="target"></param>
        /// <returns>-1说明没找到</returns>
        public int FindIndex(string target) => str.FindIndex(f => f == target, target.Length);
        public int FindIndex(Func<string, bool> condition, int count)
        {
            int length = str.Length;
            for (int i = 0; i < length; i++) {
                if(length - i < count) {
                    return -1;
                }

                int tarIndex = i + count; //目标索引
                if(condition(str[i..tarIndex])){
                    return i;
                }
            }
            return -1;
        }
    }
}
