using System;
using System.Collections.Generic;
using System.Text;

namespace All;

public class S3234统计1显著字符串的数量
{
    public int NumberOfSubstrings(string s)
    {
        int stringLength = s.Length;

        //pre的index代表在s[index - 1]之前出现的0的次数
        int[] pre = new int[stringLength + 1];
        pre[0] = -1;

        #region 计算零出现的次数
        //s.Count(f => f == '0');
        for (int i = 0; i < stringLength; i++) {
            if (i == 0 || (i > 0 && s[i - 1] == '0')) {
                pre[i + 1] = i;
            } else {
                pre[i + 1] = pre[i];
            }
        }
        #endregion
        Console.WriteLine(string.Join(',', pre));

        int significantCount = 0;
        //遍历字符串
        for (int i = 1; i <= stringLength; i++) {
            //count0: 1
            int count0 = s[i - 1] == '0' ? 1 : 0;
            //j: 1
            int j = i;
            while (j > 0 && count0 * count0 <= stringLength) {
                // 1 - 0 - 1
                // count1: 0
                int count1 = (i - pre[j]) - count0;

                //判断是否是显著的字符串
                if (count0 * count0 <= count1) {
                    //1, 0
                    significantCount += Math.Min(j - pre[j], count1 - count0 * count0 + 1);
                }
                j = pre[j];
                count0++;
            }
        }
        return significantCount;

        //int countvalue = 0;
        //for (int i = 0; i < s.Length; i++) {
        //    //取从 i -> j 范围
        //    for (int j = i; j <= s.Length; j++) {
        //        string targetStr = s[i..j];
        //        int _0 = 0;
        //        int _1 = 0;
        //        bool yes = true;
        //        for (int x = 0; x < targetStr.Length; x++) {
        //            if (targetStr[x] == '1') {
        //                _1 += 1;
        //            } else {
        //                _0 += 1;
        //            }
        //            if ((_0 * _0) > targetStr.Length) { //跳跃
        //                yes = false;
        //                i += _0 / 2;
        //                break;
        //            }
        //        }
        //        int _02 = _0 * _0;
        //        if ((_1 > _02 || _1 == _02) && yes && (_1 != 0 || _0 != 0)) {
        //            Console.WriteLine($"1数量: {_1}\t0数量{_02}");
        //            countvalue += 1;
        //        }

        //        //if (Count1Max(targetStr, out int _0Count, out int _1)) {
        //        //    countvalue += 1;
        //        //    Console.WriteLine(targetStr + "\t0:" + _0Count + "\t1:" + _1);
        //        //}
        //    }
        //}
        //return countvalue;
    }

    public string Rever(string str)
    {
        string newstr = "";
        for (int i = str.Length - 1; i > 0; i--) {
            newstr += str[i];
        }
        return newstr;
    }

    public bool Count1Max(string str, out int _0Count, out int _1)
    {
        /*int */_1 = CountStr(str, out int _0);
        _0Count = _0 * _0;
        if (_1 > (_0 * _0) || _1 == (_0 * _0) && //2. 一直没看到是0的数量的平方
            (_1 != 0 || _1 != 0)) { //一直没看到是大于或等于
            Console.Write($"{_1} > {_0 * _0}\t");
            return true;
        }
        return false;
    }

    //返回1的数量 输出0的数量
    public int CountStr(string str, out int _0)
    {
        _0 = 0;
        int _1 = 0;
        for (int i = 0; i < str.Length; i++) {
            if (str[i] == '0') {
                _0 += 1;
            } else {
                _1 += 1;
            }
        }
        return _1;
    }

}
