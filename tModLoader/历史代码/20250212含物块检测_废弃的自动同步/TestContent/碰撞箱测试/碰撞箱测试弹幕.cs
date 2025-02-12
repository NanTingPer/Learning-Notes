using Microsoft.Xna.Framework;
using System.Threading.Tasks;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;
using static Microsoft.Xna.Framework.Vector2;

namespace GensokyoWPNACC.TestContent.碰撞箱测试
{
    public class 碰撞箱测试弹幕 : ModProjectile
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
            Projectile.timeLeft = 666;                   //存活时间

            base.SetDefaults();
        }

        public override void AI()
        {
            Projectile.velocity = Projectile.DirectionTo(Lerp(Projectile.Center, Main.MouseWorld, 0.2f)) * 10f;
            Main.NewText("弹幕的位置:{X:" + (int)Projectile.position.X + " Y:" + (int)Projectile.position.Y + "}");
            base.AI();
        }

        public override bool? Colliding(Rectangle projHitbox, Rectangle targetHitbox)
        {
            Main.NewText($"弹幕碰撞箱:{projHitbox}");
            var projPos = new Vector2(projHitbox.X, projHitbox.Y) + new Vector2(0, 60).RotatedBy(3.14f / 4);
            var projHix = new Vector2(100, 100);
            
            var targetPos = new Vector2(targetHitbox.X, targetHitbox.Y);
            var targetHix = new Vector2(targetHitbox.Width, targetHitbox.Height);
            //Collision.CheckAABBvAABBCollision()
            //传入第一个的位置和尺寸 传入第二个的位置和尺寸
            

            #region 粒子
            Task.Run(() =>
            {
                //上X轴
                for (int i = (int)projPos.X; i < projPos.X + projHix.X; i++)
                {
                    var pos = new Vector2(i, projPos.Y);
                    var pos2 = new Vector2(i, projPos.Y + projHix.Y);
                    Dust.NewDustPerfect(pos, DustID.FireworksRGB, Zero).noGravity = true;
                    Dust.NewDustPerfect(pos2, DustID.FireworksRGB, Zero).noGravity = true;
                }

                //左Y轴
                for (int i = (int)projPos.Y; i < projPos.Y + projHitbox.Y; i++)
                {
                    var pos = new Vector2(projPos.X, i);
                    var pos2 = new Vector2(projPos.X + projHix.X, i);
                    Dust.NewDustPerfect(pos, DustID.FireworksRGB, Zero).noGravity = true;
                    Dust.NewDustPerfect(pos2, DustID.FireworksRGB, Zero).noGravity = true;
                }
            });

            //Dust.QuickBox
            #endregion 粒子

            return Collision.CheckAABBvAABBCollision(projPos, projHix, targetPos, targetHix);
            return base.Colliding(projHitbox, targetHitbox);
        }
    }
}
