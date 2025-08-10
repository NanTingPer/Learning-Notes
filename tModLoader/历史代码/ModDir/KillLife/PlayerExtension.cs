using Microsoft.Xna.Framework;
using Terraria;
using Terraria.DataStructures;

namespace KillLife;

public static class PlayerExtension
{
    /// <summary>
    /// 平滑中心
    /// </summary>
    public static Vector2 SmoothCenter(this Player player)
    {
        return player.MountedCenter.Floor() + new Vector2(0, player.gfxOffY);
    }

    public static double Hurt(this Player player, PlayerDeathReason damageSource, int Damage, int hitDirection, bool pvp = false, bool 不出粒子 = false, int cooldownCounter = -1, bool 可闪避 = true, float armorPenetration = 0, float scalingArmorPenetration = 0, float knockback = 4.5f)
    {
        return player.Hurt(damageSource, Damage, hitDirection, pvp, 不出粒子, cooldownCounter, 可闪避, armorPenetration, scalingArmorPenetration, knockback);
    }
}