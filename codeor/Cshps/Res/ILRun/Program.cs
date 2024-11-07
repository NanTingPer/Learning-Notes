using System.Reflection;

namespace ILRun
{
    internal class Program
    {
        public static void RunHello()
        {
            Console.WriteLine("Hello");
        }

        static void Main(string[] args)
        {
            Assembly asm = Assembly.LoadFile("C:\\Users\\Administrator\\Documents\\GitHub\\Learning-Notes\\codeor\\Cshps\\Res\\ILCursor\\obj\\Debug\\net8.0\\ILCursor.dll");
            //// ConsoleApp1.exe  ClassLibrary1.dll 存放他们两个的目录
            //// 要被IL注入的程序集的名称，类，方法
            string AsmName = "ILCursor.dll";
            string ClassName = "ILCursor.Program";
            string MethodName = "Main";

            //// 要插入的程序集的名称 类和方法
            Assembly asm2 = Assembly.LoadFile("C:\\Users\\Administrator\\Documents\\GitHub\\Learning-Notes\\codeor\\Cshps\\Res\\ILClass\\bin\\Debug\\net8.0\\ILClass.dll");
            string AsmName2 = "ILClass.dll";
            string ClassName2 = "ILClass.Class1";
            string MethodName2 = "Hello";


            //// 输出程序集的名称
            

            //// 获取目标程序集
            //// 找到类型
            //// 查找目标方法
            //// 加载目标程序集
            //// 查找目标类型
            //// 查找目标方法
            //// 获取目标方法 IL Processor即IL操作器
            //// 在该方法的开始处插入调用的指令

        }
    }
}
