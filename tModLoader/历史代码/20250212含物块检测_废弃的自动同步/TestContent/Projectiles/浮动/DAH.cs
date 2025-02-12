using Microsoft.Xna.Framework;
using System;
using Terraria;
using Terraria.DataStructures;
using Terraria.ID;
using Terraria.ModLoader;
using static Microsoft.Xna.Framework.Vector2;
using static System.Math;
using static Microsoft.Xna.Framework.MathHelper;

namespace GensokyoWPNACC.TestContent.Projectiles.浮动
{
    public class DAH : ModProjectile
    {
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
            Projectile.timeLeft = 120;                   //存活时间

            base.SetDefaults();
        }

        public override void SetStaticDefaults()
        {
            ProjectileID.Sets.MinionSacrificable[Type] = false; //牺牲自己召唤新的
            ProjectileID.Sets.CultistIsResistantTo[Type] = true; //自导 同时不攻击邪教徒
            ProjectileID.Sets.MinionTargettingFeature[Type] = true; //右键指定
            base.SetStaticDefaults();
        }


        private int timers = 0;
        public override void AI()
        {
            var @this = Projectile;
            timers++;
            //var stop = @this.Center + new Vector2(0, (float)Math.Sin(timers) * 20);
            //Projectile.velocity.Y = Normalize(Lerp(stop, @this.Center, 0.5f)).Y * 3f;
            Projectile.velocity.Y = (float)Sin(timers);

            base.AI();
        }

        public override void OnSpawn(IEntitySource source)
        {
            Projectile.Center = Main.player[Projectile.owner].Center;
            base.OnSpawn(source);
        }

        //本体是否造成伤害
        public override bool MinionContactDamage()
        {
            return true;
        }
    }
}
