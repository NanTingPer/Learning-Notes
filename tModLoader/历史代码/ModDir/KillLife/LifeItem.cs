using Microsoft.Xna.Framework;
using Terraria;
using Terraria.DataStructures;
using Terraria.ID;
using Terraria.ModLoader;

using static KillLife.KillLife;

namespace KillLife;

public class LifeItem : ModItem
{
    //public override string Texture => "Terraria/Images/Projectile_" + ProjectileID.EmpressBlade;
    public override void SetDefaults()
    {
        Item.damage = 0;
        Item.autoReuse = false;
        Item.useTime = 20;
        Item.useAnimation = 20;

        Item.useStyle = ItemUseStyleID.HoldUp;
        Item.DamageType = DamageClass.Generic;

        Item.noUseGraphic = true;
        Item.noMelee = true;

        Item.shoot = projType;

        Item.channel = true;
        base.SetDefaults();
    }

    public override bool AltFunctionUse(Player player)
    {
        return base.AltFunctionUse(player);
    }

    private readonly static int projType = ModContent.ProjectileType<LifeProjectile>();

    public override bool CanUseItem(Player player)
    {
        if(player.ownedProjectileCounts[projType] > 0) {
            return false;
        }
        return base.CanUseItem(player);
    }

    public override bool Shoot(Player player, EntitySource_ItemUse_WithAmmo source, Vector2 position, Vector2 velocity, int type, int damage, float knockback)
    {
        if (player.ownedProjectileCounts[projType] == 0) {
            Projectile.NewProjectile(player.GetSource_ItemUse(this.Item), Main.MouseWorld, default, ModContent.ProjectileType<LifeProjectile>(), default, default, player.whoAmI, lifeProjectileNetField);
            SendNetField();
            //Main.NewText("NetField: " + lifeProjectileNetField);
        }
        
        return false;
    }
}