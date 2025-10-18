#nullable disable
using Mono.Cecil.Cil;
using MonoMod.Cil;
using MonoMod.RuntimeDetour;
using System.Runtime.CompilerServices;

namespace HookCosole;

internal class Program
{
    //public delegate void Manipulator(ILContext il);
    public static bool ILFunc(Instruction ins)
    {
        if(ins != null && ins.MatchLdstr(out string str)) {
            if (str == "Hello World") {
                return true;
            }
        }
        return false;
    }

    public static void ILHookMethod(ILContext il)
    {
        var ils = il.Instrs;
        var cursor = new ILCursor(il);
        while (cursor.Next != null && cursor.TryGotoNext(ILFunc)) {
            if (cursor.TryGotoNext(ILFunc)) {
                cursor.Remove();
                cursor.EmitLdstr("Hello");
                //func<string, string>

                cursor.EmitDelegate<Func<string, string>>((string _) => {
                    return "Hello World!";
                });
                //cw(string)

                break;
            }
            cursor.GotoNext();
        }
    }

    static void Main(string[] args)
    {
        var methodinfo = typeof(Student).GetMethod("PrintAge");
        var hook = new ILHook(methodinfo, ILHookMethod);
        hook.Apply();
        var student = new Student(10);
        student.PrintAge(10);
    }
}

public class Student(int age)
{
    public int Age { get; set; } = age;
    public int PrintAge(int a)
    {
        Console.WriteLine("Hello World");
        Console.WriteLine(a);
        Console.WriteLine(Age);
        return Age;
    }
}
