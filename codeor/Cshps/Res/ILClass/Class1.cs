using System.Reflection;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace ILClass
{
    class ILModifier
    {
        public static void Main(string[] args)
        {
            // 获取当前程序集
            var assemblyPath = Assembly.LoadFile("C:\\Users\\Administrator\\Documents\\GitHub\\Learning-Notes\\codeor\\Cshps\\Res\\ConseIL\\obj\\Debug\\net8.0\\ConseIL.dll").GetType().ToString();
            var assembly = AssemblyDefinition.ReadAssembly(assemblyPath);

            // 获取 Program 类
            var programType = assembly.MainModule.Types.First(t => t.Name == "Program");

            // 获取 Main 方法
            var mainMethod = programType.Methods.First(m => m.Name == "Main");

            // 获取 ILProcessor
            var processor = mainMethod.Body.GetILProcessor();

            // 查找 Console.WriteLine("Hello") 指令
            foreach (var instruction in mainMethod.Body.Instructions)
            {
                if (instruction.OpCode == OpCodes.Ldstr && (string)instruction.Operand == "Hello")
                {
                    // 修改字符串常量
                    instruction.Operand = "Hello, IL";
                    break;
                }
            }

            // 保存修改后的程序集
            assembly.Write(assemblyPath + ".modified.exe");

            // 加载修改后的程序集
            var modifiedAssembly = Assembly.LoadFile(assemblyPath + ".modified.exe");

            // 调用修改后的 Main 方法
            var modifiedProgramType = modifiedAssembly.GetType("ConseIL.Program");
            modifiedProgramType.GetMethod("Main").Invoke(null, new object[] { new string[] { } });
        }
    }

}