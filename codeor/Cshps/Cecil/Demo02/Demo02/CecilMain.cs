using Cecil = Mono.Cecil;
using Mono.Cecil.Cil;
using System.Reflection;
using Mono.Cecil;
using Mono.Collections.Generic;
using Mono.Cecil.Rocks;
using System.Text.RegularExpressions;
using System.Data.SQLite;
namespace Demo02
{
    public class CecilMain
    {
        //数据库链接
        private static SQLiteConnection _sqlConn;
        public static SQLiteConnection SQLConn
        {
            get
            {
                if (_sqlConn is null)
                    SQLiteConnector();
                return _sqlConn;
            }
            private set => _sqlConn = value;
        }
        
        static void Main(string[] args)
        {
            SQLiteCommand SCommand = new SQLiteCommand("select * from stardict limit 10", SQLConn);
            SQLiteDataReader Data = SCommand.ExecuteReader();
            while (Data.Read())
            {
                Console.WriteLine($"{Data["id"]}\t{Data["word"]}\t{Data["translation"]}"); 
            }

            string dllPath = "C:\\Users\\23759\\Documents\\My Games\\Terraria\\tModLoader\\ModReader\\FargowiltasSouls\\FargowiltasSouls.dll";

            
            //使用Location可以获取目标dll路径
            ModuleDefinition target_module = ModuleDefinition.ReadModule(dllPath);

            //获取类型
            IEnumerable<TypeDefinition> target_types = target_module.GetTypes();

            string regex = @"[\/.\s:]";
            Regex reg = new Regex(regex);

            //遍历方法并提取字符串
            foreach (TypeDefinition type in target_types)
            {
                foreach (MethodDefinition method in type.GetMethods())
                {
                    if(method.Body != null && !method.Name.Equals("PreDraw"))
                    { 
                        foreach (Instruction il in method.Body.Instructions)
                        {
                            if (il.OpCode.Equals(OpCodes.Ldstr))
                            {
                                string str = (string)il.Operand;
                                if(reg.Count(str) <= 0)
                                    Console.WriteLine($"{method.FullName} string => {str}");
                            }
                        }
                    }
                }
            }

            Console.Read();
        }

        static void SQLiteConnector()
        {
            SQLConn = new SQLiteConnection("Data Source = D:\\CodeRun\\Learning-Notes\\codeor\\Cshps\\Cecil\\Demo02\\SerblDll\\bin\\Debug\\net8.0\\stardict.db");
            SQLConn.Open();
        }

        static void CloseSQLite()
        {
            SQLConn.Close();
        }
    }
}
