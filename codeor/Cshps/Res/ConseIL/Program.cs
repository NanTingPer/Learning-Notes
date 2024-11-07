
//using Mono.Cecil;
//using System.Reflection;


//namespace ConseIL
//{
//    internal class Program
//    {
//        static void Main(string[] args)
//        {
//            // 获取需要插入的方法
//            MethodInfo worldMethod = typeof(Program).GetMethod("Main");

//            // 获取当前模块的 ModuleDefinition，这是 Mono.Cecil 用来操作程序集的主要对象。
//            var moduleDefinition = ModuleDefinition.ReadModule(typeof(Program).Assembly.Location);


//            var cws = typeof(Console).GetMethod("WriteLine", new Type[] { typeof(string) })!;

//            new IL

//            il.Emit(OpCodes.Ldstr, "Hello World in IL!");
//            il.Emit(OpCodes.Call, cws);
//            il.Emit(OpCodes.Ret);
//        }
//    }
//}

using System;
using ConseIL;
using System.Reflection;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace ConseIL
{
    internal class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Hello");
            Console.ReadLine();
        }
    }
}
