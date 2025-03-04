using System;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;

namespace Attri.Content
{
    public class TestProje : ModProjectile
    {

        [Test]
        public int 目标X = 0;

        [Test]
        public int 目标Y = 0;
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
            Projectile.timeLeft = 60;                   //存活时间

            base.SetDefaults();
        }

        public override void AI()
        {
            if(Projectile.owner == Main.myPlayer)
            {
                目标X = (int)Main.MouseWorld.X;
                目标Y = (int)Main.MouseWorld.X;
            }
            if(目标X != 0 && 目标Y != 0)
            {
                Projectile.Center = new Microsoft.Xna.Framework.Vector2(目标X, 目标Y);
            }
            Console.WriteLine(目标X + ", " + 目标Y);
            Main.NewText(目标X + ", " + 目标Y);
            base.AI();
        }

    }
}
