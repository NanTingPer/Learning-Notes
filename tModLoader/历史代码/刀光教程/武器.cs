using Terraria.ID;
using Terraria.ModLoader;
using Terraria;

namespace 刀光教程
{
    public class 武器 : ModItem
    {
        public override void SetDefaults()
        {
            Item.damage = 10;
            Item.value = 20;
            Item.rare = ItemRarityID.Gray;
            Item.maxStack = 1;
            Item.useTime = 15;
            Item.useAnimation = 15;
            Item.DamageType = DamageClass.Melee;
            Item.useStyle = ItemUseStyleID.Swing;
            Item.knockBack = 0.2f;
            Item.useTurn = false;
            Item.consumable = false;
            Item.shoot = ModContent.ProjectileType<弹幕>();
            Item.noUseGraphic = true;
            base.SetDefaults();
        }

        public override bool CanUseItem(Player player)
        {
            return player.ownedProjectileCounts[ModContent.ProjectileType<弹幕>()] > 0 ? false : true;
        }

        public override bool? CanHitNPC(Player player, NPC target)
        {
            return false;
        }
    }
}
