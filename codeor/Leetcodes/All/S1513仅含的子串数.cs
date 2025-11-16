using System;
using System.Collections.Generic;
using System.Text;

namespace All;

public class S1513仅含的子串数
{
    public int NumSub4(string s)
    {
        int mo = 1_000_000_007;
        long count123 = 0;
        long currentIndex = 0;
        for (int i = 0; i < s.Length; i++) {
            if (s[i] == '1') {
                currentIndex += 1;
            } else {
                long tempValue = currentIndex * (currentIndex + 1) / 2;
                count123 = (tempValue + count123) % mo;
                currentIndex = 0;
            }
        }
        long tempValue2 = currentIndex * (currentIndex + 1) / 2;
        count123 = (tempValue2 + count123) % mo;
        return (int)count123;
    }

    public int NumSub3(string s)
    {
        int[] lengths = new int[s.Length + 1];
        int not0Count = 0;
        for(int i = 0; i < s.Length; i++) {
            if(s[i] == '1') {
                lengths[not0Count] = i;
                not0Count += 1;
            }
        }

        //如果 index + 1 = (index - 1)的值 + 1那么就是连续的
        //如果是连续那么计数+=1
        //如果不连续那么使用 (计数 * (计数+1)) / 2

        int returnCount = 0;
        int stopIndex = 0;
        int startIndex = -1;
        for (int i = 0; i < lengths.Length - 1; i++) {
            if(lengths[i] == lengths[i + 1] - 1 && lengths[i+1] != 0) {
                if(startIndex == -1) {
                    startIndex = i;
                }
                stopIndex = i + 1;
            } else {
                int count = stopIndex - startIndex;
                returnCount += (count * (count+1)) / 2 + count;
                startIndex = 0;
                stopIndex = 0;
            }
        }
        int endCount = (stopIndex - startIndex) * 2;
        returnCount += (endCount * (endCount + 1)) / 2 + endCount;

        Console.WriteLine(returnCount);
        Console.WriteLine(string.Join(',', lengths));
        return 1;


    }


    /// <summary>
    /// no
    /// </summary>
    /// <param name="s"></param>
    /// <returns></returns>
    public int NumSub2(string s)
    {
        //索引的值，代表离我最近的1有多远
        int counts = 0;
        for(int i = 0; i < s.Length; i++) {
            if(s[i] == '1') {
                counts += 1;
            }
        }
        return (counts * (counts + 1)) / 2;
        Console.WriteLine(string.Join(',', counts));
        return 1;
    }


    /// <summary>
    /// 暴力超时法
    /// </summary>
    /// <param name="s"></param>
    /// <returns></returns>
    public int NumSub(string s)
    {
        int retCount = 0;
        for (int i = 0; i <= s.Length; i++) {
            for (int j = i; j <= s.Length; j++) {
                bool isNot0 = true;
                string sonString = s[i..j];
                if (string.IsNullOrEmpty(sonString)) {
                    continue;
                }

                for (int v = 0; v < sonString.Length; v++) {
                    if (sonString[v] == '0') {
                        isNot0 = false;
                        break;
                    }
                }
                if (isNot0 == true) {
                    retCount += 1;
                } else {
                    break;
                }
            }
        }
        int mo = 10;
        for (int i = 0; i < 10; i++) {
            mo = mo * 10;
        }
        mo += 7;
        return retCount % mo;
    }
}

//给你一个二进制字符串 s（仅由 '0' 和 '1' 组成的字符串）。

//返回所有字符都为 1 的子字符串的数目。

//由于答案可能很大，请你将它对 10^9 + 7 取模后返回。