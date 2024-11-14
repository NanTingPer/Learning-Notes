using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Test.Method
{
    public class FileWriter
    {
        public static void FileWriterToFile()
        {
            foreach(var kv in fulm.ModAndEnglish_Chinese)
            {
                ModFileNum FileNum = new ModFileNum(0);
                String FilePath = "C:/ModLang/" + kv.Key.Item1.Trim() + "_";
                //Exists 用于判断指定路径是否存在
                if (!Directory.Exists(FilePath))
                { 
                    //不存在直接创建
                    DirectoryInfo 存放文件夹 = Directory.CreateDirectory(FilePath);
                }
                //目录 = C:/ModLang/模组名称数字 然后在里面创建.cs
                String ModFile = FilePath.Trim() + "/"  + kv.Key.Item1.Trim() + FileNum.getNumConut();

                //创建并覆盖要写入的文件
                using (FileStream bw = File.Create(ModFile + ".cs"))
                {
                    bw.Write(StringToByte("using FargoCP.Systems;"));
                    bw.Write(StringToByte("using System.Collections.Generic;StringToByte("));
                    bw.Write(StringToByte("using Terraria.ModLoader;"));
                    //标记 更改 后续
                    bw.Write(StringToByte("namespace FargoCP.ModLeng." + ModFile));
                    bw.Write(StringToByte("{"));
                    bw.Write(StringToByte("\tinternal class " + kv.Key.Item1 + FileNum.getNumConut() + " : ModSystem"));
                    bw.Write(StringToByte("\t{"));
                    bw.Write(StringToByte("\t\tprivate class " + kv.Key.Item1 + "{ }"));
                    bw.Write(StringToByte("\t\t[ExtendsFromMod(\"" + kv.Key.Item1 + "\"), JITWhenModsEnabled(\"" + kv.Key.Item1 + "\")]"));
                    bw.Write(StringToByte("\t\tprivate class ForL : ForceLocalizeSystem<" + kv.Key.Item1 + ", ForL>{}"));
                    bw.Write(StringToByte("\t\tpublic override void PostSetupContent()"));
                    bw.Write(StringToByte("\t\t{"));
                    bw.Write(StringToByte("\t\t\tMod mod;"));
                    bw.Write(StringToByte("\t\t\tif(ModLoader.TryGetMod(" + kv.Key.Item1 + ",out mod))"));
                    bw.Write(StringToByte("\t\t\t{"));
                    bw.Write(StringToByte("\t\t\t\tForL.LocalizeByTypeFullName(\"" + kv.Key.Item2 + "\", \"" + kv.Key.Item3 + "\", new ()"));
                    bw.Write(StringToByte("\t\t\t\t{"));
                    bw.Flush();

                    List<Tuple<String, String>> iter = kv.Value;

                    foreach (Tuple<String, String> tpule in iter)
                    {
                        bw.Write(StringToByte("\t\t\t\t\t{" + tpule.Item1.Trim() + "," + tpule.Item2.Trim() + "},"));
                        bw.Flush();
                    }

                    bw.Write(StringToByte("\t\t\t\t});"));
                    bw.Write(StringToByte("\t\t\t\t}"));
                    bw.Write(StringToByte("\t\t\t}"));
                    bw.Write(StringToByte("\t\t\tbase.PostSetupContent();"));
                    bw.Write(StringToByte("\t\t}"));
                    bw.Write(StringToByte("\t}"));
                    bw.Write(StringToByte("}"));
                    bw.Flush();
                }
                //每次遍历都改变数字 阻断文件冲突
                FileNum.setNumConut(FileNum.getNumConut() + 1);
            }
            fulm.ModAndEnglish_Chinese.Clear();
        }

        public static byte[] StringToByte(String str)
        {
            String str2 = str + "\r\n";
            return Encoding.UTF8.GetBytes(str2);/*Convert.FromBase64String(str2);*/
        }

        public class ModFileNum
        {
            private int numConut;
            public ModFileNum(int a) { numConut = a; }
            public int getNumConut()
            {
                return numConut;
            }
            public void setNumConut(int numConut)
            {
                this.numConut = numConut;
            }
        }

    }
}
