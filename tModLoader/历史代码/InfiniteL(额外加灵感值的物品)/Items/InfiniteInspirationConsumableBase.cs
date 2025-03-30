using System;
using Terraria;
using Terraria.Audio;
using ThoriumMod;
using ThoriumMod.Items.BardItems;
using ThoriumMod.Utilities;

namespace InfiniteL.Items;

public abstract class InfiniteInspirationConsumableBase : InspirationConsumableBase
{
    public int count = 3;

    /// <summary>
    /// 瑟银原版最大值
    /// </summary>
    public int BardResourceMax = 40;

    /// <summary>
    /// 本物品使用完后 最大的灵感值
    /// <para>本物品需要多少灵感值 + 本物品最大能提供多少灵感值</para>
    /// </summary>
    new public int InspirationTotal => InspirationBase + InspirationIncreaseTotal;

    /// <summary>
    /// 是否显示需要多少灵感才能使用 
    /// <para>默认true</para>
    /// </summary>
    public override bool ShowInspirationRequirementTooltip => base.ShowInspirationRequirementTooltip;

    /// <summary>
    /// 使用本物品需要的灵感值 
    /// <para>默认40(瑟银原版上限40)</para>
    /// </summary>
    public override int InspirationBase => BardResourceMax;

    /// <summary>
    /// 本物品最大能加多少灵感值 
    /// <para>默认10</para>
    /// </summary>
    public override int InspirationIncreaseTotal => base.InspirationIncreaseTotal;

    /// <summary>
		/// 使用本物品单次能增加多少灵感值 
    /// <para>默认1</para>
		/// </summary>
    public override int InspirationIncreasePerUse => base.InspirationIncreasePerUse;

    /// <summary>
    /// 使用本物品时，玩家头顶字的颜色 
    /// <para> 默认Color(165, 255, 185) </para> 
    /// </summary>
    public override Color CombatTextColor => base.CombatTextColor;

    /// <summary>
    /// 使用此物品的效果，直接拷贝至父类
    /// </summary>
    public override bool? BardUseItem(Player player)
    {
        ThoriumPlayer thoriumPlayer = player.GetThoriumPlayer();//获取ModPlayer
        if (CanBeUsed(this, player)) //判断能不能用，判定内容看方法实现
        {
            //跳字 + 发生
            CombatText.NewText(player.Hitbox, CombatTextColor, InspirationIncreasePerUse, false, false);
            if (PlayedSound != null) {
                SoundStyle value = PlayedSound.Value;
                SoundEngine.PlaySound(value, new Vector2?(player.Center), null);
            }
            //增加最大灵感
            thoriumPlayer.bardResourceMax2 += InspirationIncreasePerUse;
            thoriumPlayer.bardResourceMax += InspirationIncreasePerUse;
            return new bool?(true);
        }
        return null;
    }

    /// <summary>
    /// 能否使用此物品，此方法上级为上级拷贝，请勿使用
    /// <para> 玩家最大灵感，大于等于本模组所需的灵感 </para>
    /// <para> 玩家最大灵感，小于本物品使用完成后的灵感 </para>
    /// <para> 灵感增加值要大于0 </para>
    /// </summary>
    /// <returns></returns>
    [Obsolete]
    public static bool CanBeUsed(InspirationConsumableBase modItem, Player player)
    {
        ThoriumPlayer thoriumPlayer = player.GetModPlayer<ThoriumPlayer>();
        bool one = thoriumPlayer.bardResourceMax >= modItem.InspirationBase;
        bool two = thoriumPlayer.bardResourceMax < modItem.InspirationTotal;
        bool _3 = modItem.InspirationIncreasePerUse > 0;
        return one && two && _3;
    }
}
