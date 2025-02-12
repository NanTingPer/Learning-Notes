using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.Dusts.DrawDusts
{
    public class 绘制粒子测试武器 : ModItem
    {
        public override void SetDefaults()
        {
            Item.damage = 20;               //攻击力
            Item.value = 20;                //价格
            Item.rare = ItemRarityID.Gray;  //稀有度
            Item.maxStack = 1;              //最大堆叠
            Item.useTime = 15;              //使用时间
            Item.useAnimation = 15;         //动画时间
            Item.DamageType = DamageClass.Melee;    //伤害类型
            Item.useStyle = ItemUseStyleID.Swing;   //使用样式
            Item.knockBack = 0.2f;          //击退
            Item.useTurn = true;            //是否可转身
            Item.consumable = false;        //是否消耗品
            Item.axe = 0;                   //斧
            Item.pick = 0;                  //镐
            Item.hammer = 0;                //锤
            base.SetDefaults();
        }
        public override void SetStaticDefaults()
        {
            TextureDust = ModContent.Request<Texture2D>("GensokyoWPNACC/TestContent/Dusts/螺旋上升/螺旋上升粒子");
            base.SetStaticDefaults();
        }

        //private Asset<Texture2D> _texture;
        private Asset<Texture2D> TextureDust { get; set; }
        //{
        //    get
        //    {
        //        if (_texture == null)
        //            _texture = ModContent.Request<Texture2D>("GensokyoWPNACC/TestContent/Dusts/螺旋上升/螺旋上升粒子");
        //        return _texture;
        //    }
        //    set { _texture = value; }
        //}

        public override bool CanUseItem(Player player)
        {
            new DrawDustTest(player.Center, TextureDust);
            return base.CanUseItem(player);
        }
    }
}
