using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows;

namespace Test.Method
{
    public static class TxtFileReader
    {
        public static void FileReader(string url,MainWindow window)
        {
            try
            {
                #region 载入字典
                string lang = File.ReadAllText(url.Trim());
                lang = lang.Replace("\r\n", "");
                string[] lang2 = Regex.Split(lang, "[{}]");
                Dictionary<String, String> ModAndEnglish = [];
                //载入字典
                fulm.ListToMap(lang2, ModAndEnglish);
                #endregion

                #region 条目分割
                Dictionary<String, List<Tuple<String, String>>> ModAndEnglish_ = [];
                //使用元组 _1是原文 _2是中文
                foreach (KeyValuePair<String, String> KV in ModAndEnglish)
                {
                    List<String> TempList = [];
                    //使用.分割
                    String[] Tempsplit1 = Regex.Split(KV.Value, "[.]");
                    for (int i = 0; i < Tempsplit1.Length; i++)
                    {
                        //将 原文,中文 加入集合
                        TempList.Add(Tempsplit1[i]);
                    }
                    //创建元组数组
                    List<Tuple<String, String>> TempListTuple = [];
                    foreach (String s in TempList)
                    {
                        //使用,分割 偶数原文 奇数中文
                        String[] split1 = Regex.Split(s, "[,]");//s.split("[,]");
                                                                //遍历分割 用来分配给元组数组
                        for (int i = 0; i < split1.Length - 1; i += 2)
                        {
                            Tuple<String, String> tul2 = new Tuple<String, String>(split1[i], split1[i + 1]);
                            TempListTuple.Add(tul2);
                        }
                    }
                    ModAndEnglish_.Add(KV.Key, TempListTuple);
                }
                #endregion

                #region 条目分配
                foreach (var kv in ModAndEnglish_)
                {
                    //这里我是明确知道要三个值的
                    String[] split1 = Regex.Split(kv.Key, "[ ]");//String[] split1 = k.split();
                    String ModName = "";
                    String ModClass = "";
                    String ModMeth = "";
                    for (int i = 0; i < split1.Length; i++)
                    {
                        if (i == 0) ModName = split1[i];
                        if (i == 1) ModClass = split1[i];
                        if (i == 2) ModMeth = split1[i];
                    }
                    Tuple<String, String, String> tuple3 = new Tuple<String, String, String>(ModName, ModClass, ModMeth);
                    fulm.ModAndEnglish_Chinese.Add(tuple3, kv.Value);
                    FileWriter.FileWriterToFile();
                }
                #endregion
            }
            catch (Exception ex) 
            {
                window.按钮.Content = "URL错误";
            }
            
            
            
            
            
            

            

            
        }
    }
}
