using Mono.Cecil;
using Mono.Cecil.Cil;
using Mono.Cecil.Rocks;
using List = Mono.Collections.Generic;

namespace CecilDemo
{
    internal class Program
    {
        static void Main(string[] args)
        {
            string _sourceDll = "D:\\CodeRun\\Learning-Notes\\codeor\\Cshps\\Cecil\\Cedll\\bin\\Debug\\net8.0\\Cedll.dll";
            string _sinkDll = "D:\\CodeRun\\Learning-Notes\\codeor\\Cshps\\Cecil\\Sinks\\bin\\Debug\\net8.0\\Sinks.dll";

            ModuleDefinition source = ModuleDefinition.ReadModule(_sourceDll);
            
            //需要开启可写
            ModuleDefinition sink = ModuleDefinition.ReadModule(_sinkDll,new ReaderParameters() { ReadWrite = true});

            IEnumerable<TypeDefinition> oTypes = source.GetTypes();
            IEnumerable<TypeDefinition> sTypes = sink.GetTypes();
            //Cedll.Program
            //Print

            //Sinks.Program
            //Main


            //取出源方法
            MethodDefinition? sourceMethod = oTypes.FirstOrDefault(f => "Cedll.Program".Equals(f.FullName))
                .GetMethods().FirstOrDefault(f => "Print".Equals(f.Name));

            if(sourceMethod is null) return;

            //取出目标方法
            MethodDefinition? sinkMethod = sTypes.FirstOrDefault(f => "Sinks.Program".Equals(f.FullName))
                .GetMethods().FirstOrDefault(f => "Main".Equals(f.Name));

            if(sinkMethod is null) return;

            //源方法的IL处理器
            ILProcessor sourcesMtIL = sourceMethod.Body.GetILProcessor();

            //目标方法的IL处理器
            ILProcessor sinkMtIL = sinkMethod.Body.GetILProcessor();

            //获取源方法的全部IL指令
            List::Collection<Instruction> sourceILs = sourceMethod.Body.Instructions;

            //清空目标方法的全部IL指令
            sinkMtIL.Clear();

            //遍历源方法的IL指令 并插入目标方法
            foreach (Instruction il in sourceILs)
            {
                //这样判断指定死了 但是源方法中有两个方法引用 一个是WriteKey 一个 输出
                if(il.Operand is MethodReference MethodF)
                {
                    il.Operand = sink.ImportReference(MethodF);
                }
                sinkMtIL.Append(il);
            }

            //保存回到原本的dll
            sink.Write();
        }
    }
}
