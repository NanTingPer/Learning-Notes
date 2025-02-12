using Terraria.ID;
using Terraria.ModLoader;
using static Terraria.ModLoader.ModContent;

namespace GensokyoWPNACC.TestContent.Projectiles.浮动
{
    public class DAHItem : ModItem
    {
        public override string Texture => GetModProjectile(ProjectileType<DAH>()).Texture;
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
            Item.consumable = false;    //是否消耗品
            Item.shoot = ModContent.ProjectileType<DAH>();
            Item.shootSpeed = 0f;
            Item.axe = 0; //斧
            Item.pick = 0;   //镐
            Item.hammer = 0; //锤
            base.SetDefaults();
        }
    }
}
