using GensokyoWPNACC.MyUtils;
using GensokyoWPNACC.PacketMode.Attributes;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Collections.Generic;
using System.IO;
using Terraria;
using Terraria.GameContent;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.联机同步测试
{
    public class 自动同步测试弹幕 : ModProjectile
    {
        [ProjectileNetField]
        public Vector2 vector2 = Vector2.Zero;

        [ProjectileNetProperty]
        public Vector2 vector { get => vector2; set => vector2 = value; }

        public override void SetDefaults()
        {
            Projectile.width = 8;
            Projectile.height = 8;
            Projectile.friendly = true;                 //友好
            Projectile.DamageType = DamageClass.Summon; //伤害类型
            Projectile.aiStyle = -1;
            Projectile.penetrate = -1;                  //穿透
            Projectile.ignoreWater = true;              //不受水
            Projectile.tileCollide = true;              //穿墙
            Projectile.scale = 1f;                      //缩放
            Projectile.timeLeft = 80;                   //存活时间

            base.SetDefaults();
        }
        public override void SetStaticDefaults()
        {

            base.SetStaticDefaults();
        }

        public override void AI()
        {
            if(Projectile.owner == Main.myPlayer)
            {
                vector2 = Main.MouseWorld;
                Projectile.netUpdate = true;
            }
            
            Main.NewText("字段: " + vector2);
            Main.NewText("属性: " + vector);
            Console.WriteLine("字段: " + vector2);
            Console.WriteLine("属性: " + vector);
               //;ContentSamples
            base.AI();
        }

    }
}
