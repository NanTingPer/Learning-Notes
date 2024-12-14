using Mono.Cecil;
using Mono.Cecil.Cil;
using Mono.Cecil.Rocks;

namespace DLL
{
   public class Class1
    {
        public static void Main()
        {
            string dllPath = "D:\\CodeRun\\Learning-Notes\\codeor\\Cshps\\Cecil\\Run\\run\\bin\\Debug\\net8.0\\run.dll"
            var dllModule = ModuleDefinition.ReadModule(dllPath);

            var ass = dllModule.Types;

            var type = ass.FirstOrDefault(f => f.Name == "Program");
            var method = type.GetMethods().FirstOrDefault(f => f.Name == "Main");

            Dictionary<int, string> ilString = new Dictionary<int, string>();
            var ils = method.Body.Instructions;
            foreach (var item in ils)
            {
                if(item.OpCode == OpCodes.Ldstr)
                {
                    ilString.Add(item.Offset, (string)item.Operand);
                }
            }

            foreach(var item in ilString)
            {
                for(int i = item.Key;i < ils.Count;i++)
                {
                    if(ils[i].OpCode == OpCodes.Call || ils[i].OpCode == OpCodes.Callvirt)
                    {
                        if (ils[i].Operand.ToString().Contains("Mod"))
                        {
                            
                        }
                        break;
                    }
                }
            }
        }
    }
       
}
