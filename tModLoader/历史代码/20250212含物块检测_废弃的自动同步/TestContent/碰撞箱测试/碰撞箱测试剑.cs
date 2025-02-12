using Microsoft.Xna.Framework;
using Terraria;
using Terraria.DataStructures;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.碰撞箱测试
{
    public class 碰撞箱测试剑 : ModItem
    {
        public override void SetDefaults()
        {
            Item.damage = 20; //攻击力
            Item.value = 20;    //价格
            Item.rare = ItemRarityID.Gray; //稀有度
            Item.maxStack = 1;  //最大堆叠
            Item.useTime = 15;  //使用时间
            Item.useAnimation = 15; //动画时间
            Item.DamageType = DamageClass.Melee;  //伤害类型
            Item.useStyle = ItemUseStyleID.Swing;   //使用样式
            Item.knockBack = 0.2f;  //击退
            Item.useTurn = true;    //是否可转身
            Item.shoot = ModContent.ProjectileType<碰撞箱测试弹幕>();
            Item.shootSpeed = 10f;
            base.SetDefaults();
        }

        public override bool Shoot(Player player, EntitySource_ItemUse_WithAmmo source, Vector2 position, Vector2 velocity, int type, int damage, float knockback)
        {
            if (player.ownedProjectileCounts[ModContent.ProjectileType<碰撞箱测试弹幕>()] > 1)
            {
                return false;
            }
            return base.Shoot(player, source, position, velocity, type, damage, knockback);
        }
    }
}
