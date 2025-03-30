using Mono.Cecil.Cil;
using MonoMod.Cil;
using MonoMod.RuntimeDetour;
using System;
using System.Reflection;
using ThoriumMod;
using static InfiniteL.InfiniteL;

namespace InfiniteL.Items;

public class InspirationHook
{
    static InspirationHook()
    {
        #region func
        Func<Instruction, bool> bardResourceMaxFunc1 = il => il.MatchLdarg0();

        //这个func判断指令为 ldci4并值为40
        Func<Instruction, bool> bardResourceMaxFunc2 = il => {
            il.MatchLdcI4(out int value);
            return value == 40;
        };

        //这个func判断指令为成员调用，给定名称为 bardResourceMax
        Func<Instruction, bool> bardResourceMaxFunc3 = il => {
            return il.MatchLdfld/*<int>*/(typeof(ThoriumPlayer), "bardResourceMax");
        };

        Func<Instruction, bool> bardResourceMax2Func1 = il => il.MatchLdarg0();
        Func<Instruction, bool> bardResourceMax2Func2 = il => {
            il.MatchLdcI4(out int value);
            return value == 60;
        };
        Func<Instruction, bool> bardResourceMax2Func3 = il => {
            return il.MatchLdfld/*<int>*/(typeof(ThoriumPlayer), "bardResourceMax2");
        };
        #endregion

        MethodInfo PostUpdateEquips = typeof(ThoriumPlayer).GetMethod("PostUpdateEquips", BindingFlags.Public | BindingFlags.Instance);
        BardResourceMaxHook =
        new ILHook(PostUpdateEquips, ilc => {
            ILCursor Cursor = new ILCursor(ilc);
            HookAlft(Cursor, bardResourceMaxFunc1 , bardResourceMaxFunc3 , bardResourceMaxFunc2 , 40, Max);
            HookAlft(Cursor, bardResourceMaxFunc1 , bardResourceMaxFunc2 , bardResourceMaxFunc3 , 40, Max);
            HookAlft(Cursor, bardResourceMax2Func1, bardResourceMax2Func3, bardResourceMax2Func2, 60, Max + 20);
            HookAlft(Cursor, bardResourceMax2Func1, bardResourceMax2Func2, bardResourceMax2Func3, 60, Max + 20);
        });
        BardResourceMaxHook.Apply();
    }

    private static void HookAlft(ILCursor Cursor, Func<Instruction, bool> func1, Func<Instruction, bool> func2, Func<Instruction, bool> func3, int sourceValue, int tarGetValue)
    {
        if (Cursor.TryGotoNext(MoveType.Before, func1, func2, func3)) {
            for (int i = 0; i < 4; i++) {
                if (Cursor.Next is null) continue;
                Cursor.Index++;
                var il = Cursor.Previous;
                if (il.Operand is object value) {
                    if (int.TryParse(value.ToString(), out int newvalue)) {
                        if (newvalue == sourceValue) il.Operand = tarGetValue;
                    }
                }
            }
        }
    }

}
