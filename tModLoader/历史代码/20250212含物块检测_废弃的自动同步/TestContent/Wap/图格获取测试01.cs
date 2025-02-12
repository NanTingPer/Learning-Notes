using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Drawing;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.Wap
{
    public class 图格获取测试01 : ModItem
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
            Item.consumable = false;    //是否消耗品
            Item.axe = 0; //斧
            Item.pick = 0;   //镐
            Item.hammer = 0; //锤
            base.SetDefaults();
        }

        public override bool CanUseItem(Player player)
        {
            int num = 0;
            var 鼠标图格位置 = player.Center.ToTileCoordinates();//Main.MouseWorld.ToTileCoordinates()/*.ToTileCoordinates()*/;
            var x = (int)鼠标图格位置.X;
            var y  = (int)鼠标图格位置.Y;
            for (int i = x - 20; i < x + 20; i += 1)
            {
                for(int j = y - 20; j < y + 20; j += 1)
                {
                    Vector2 vector = new Vector2(i << 4, j << 4);
                    if (WorldGen.TileType(i, j) != -1)
                    {
                        for (int a = 0; a < 2; a++)
                        {
                            Dust.NewDustPerfect(vector, DustID.FireflyHit, Vector2.Zero).noGravity = true;
                        }
                        num++;
                    }
                    
                }
            }

            Main.NewText($"{num}个方块");
            return base.CanUseItem(player);
        }
    }
}
