using MonoMod.RuntimeDetour;
using System.Reflection;
using Terraria;
using Terraria.ModLoader;
using Terraria.ModLoader.IO;

namespace InfiniteL;

// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
/// <summary>
/// 瑟银模组灵感值:
///     bardResourceMax 不能改这个，这个决定能不能使用瑟银自己的
///     bardResourceMax2 应该改这个？ 但是他在加载值的时候是与bardResourceMax同步的
/// 
/// Boss是否被击杀似乎需要自己记录
///     1. 定义全局NPC类，在NPC被击败那边判断是否是给定NPC
///     2. 定义ModSystem类， 用于保持世界中，此NPC是否被击败
///     3. 定义ModPlayer类， 用于记录增加的灵感值数目
/// </summary>
public class InfiniteL : Mod
{
    //sortBefore = ThoriumMod

    /// <summary>
    /// 最大灵感，瑟原 40
    /// </summary>
    public static int Max { get => 40 + 40; }
    /// <summary>
    /// bardResourceMax, bardResourceMax2 = bardResourceMax + 20
    /// <para> ThoriumPlayer ModPlayer => PostUpdateEquips() </para>
    /// </summary>
    public static ILHook BardResourceMaxHook;
    
    public override void Unload()
    {
        BardResourceMaxHook = null;//卸载钩子
        base.Unload();
    }
}

public class TestGItem : GlobalItem
{
    public override bool? UseItem(Item item, Player player)
    {
        //NPC.downed
        //NPC.killCount
        //Terraria.Condition
        //ThoriumPlayer tModPlayer = player.GetModPlayer<ThoriumPlayer>();
        //Main.NewText("bardResourceMax2: " + tModPlayer.bardResourceMax2);
        //Main.NewText("bardResourceMax : " + tModPlayer.bardResourceMax);
        return base.UseItem(item, player);
    }
}

public class ExaResourcePlayer : ModPlayer
{
    public override void OnEnterWorld()
    {
        base.OnEnterWorld();
    }
    public override void SaveData(TagCompound tag)
    {
        base.SaveData(tag);
    }

    public override void LoadData(TagCompound tag)
    {
        base.LoadData(tag);
    }
}
