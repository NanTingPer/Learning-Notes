 using System;
using System.Collections.Generic;
using System.Text;

namespace All;

internal class S28找出字符串中第一个匹配项的下标
{
    public int StrStr(string haystack, string needle)
    {
        int jump = needle.Length;
        for (int i = 0; i < haystack.Length; i += jump) {
            if (i + jump > haystack.Length) {
                continue;
            }
            if (needle == haystack[i..(i + jump)]) {
                return i;
            }
        }
        return -1;
    }
}
