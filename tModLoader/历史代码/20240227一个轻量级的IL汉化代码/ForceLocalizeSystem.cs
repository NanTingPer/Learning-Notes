using Mono.Cecil.Cil;
using MonoMod.RuntimeDetour;
using System;
using System.Collections.Generic;
using System.Reflection;
using Terraria.ModLoader;
using Terraria.ModLoader.Core;

namespace DREQC
{
    public class UnLoad : ModSystem
    {
        public static List<Dictionary<string, Type>> List { get; } = [];
        public static List<ILHook> ILHooks { get; } = [];
        public override void PostSetupContent()
        {
            foreach (var item in List) {
                item.Clear();
            }
            base.PostSetupContent();
        }
    }
    public static class ForceLocalizeSystem<TMod,TSale>
    {
        public static readonly Dictionary<string, Type> Types;

        /// <summary>
        /// 只会初始化一次，用来载入模组的全部类型
        /// </summary>
        static ForceLocalizeSystem()
        {
            if (!ModLoader.TryGetMod(typeof(TSale).Name, out Mod TarGet))
                return;
            foreach (var item in AssemblyManager.GetLoadableTypes(TarGet.Code)) {
                Types.Add(item.FullName, item);
            }
            UnLoad.List.Add(Types);
        }
        /// <summary>
        /// 对给定全名的Type的给定方法进行字符串替换
        /// </summary>
        /// <param name="TypeName">全名</param>
        /// <param name="MethodName">方法名</param>
        /// <param name="Value">值</param>
        public static void LocalizeByTypeFullName(string TypeName, string MethodName, Dictionary<string,string> Value)
        {
            if (!ModLoader.TryGetMod(typeof(TSale).Name, out Mod TarGet)) return;
            if (!Types.TryGetValue(TypeName, out Type type)) return;
            MethodInfo Method = type.GetMethod(MethodName, BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static);
            if (Method is null) return;
            
            ILHook iLHook =
            new ILHook (Method, ilc => {
                foreach (Instruction il in ilc.Body.Instructions) {
                    if (il.OpCode == OpCodes.Ldstr) {
                        if (Value.TryGetValue(il.Operand.ToString(), out string value))
                            il.Operand = value;
                    }
                }
            });

            UnLoad.ILHooks.Add(iLHook);
        }
    }
}
