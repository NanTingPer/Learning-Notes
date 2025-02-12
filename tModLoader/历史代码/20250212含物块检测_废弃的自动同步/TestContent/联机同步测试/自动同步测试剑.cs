using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.联机同步测试
{
    public class 自动同步测试剑 : ModItem
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
            Item.shoot = ModContent.ProjectileType<自动同步测试弹幕>();
            Item.scale = 2f;
            Item.shootSpeed = 3f;
            Item.useTurn = true;    //是否可转身
            Item.consumable = false;    //是否消耗品
            base.SetDefaults();
        }
    }
}
