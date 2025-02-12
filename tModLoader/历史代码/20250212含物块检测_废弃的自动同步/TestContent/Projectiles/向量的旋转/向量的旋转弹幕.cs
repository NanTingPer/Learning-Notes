using GensokyoWPNACC.MyUtils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.Projectiles.向量的旋转
{
    public class 向量的旋转弹幕 : ModProjectile
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
            Projectile.timeLeft = 600;                   //存活时间

            base.SetDefaults();
        }
        public override void AI()
        {
            Projectile.Center = Main.player[Projectile.owner].Center;
            base.AI();
        }
        private float rotTime = 0f;
        private int Y大小 = 0;
        private int YTwo = 50;
        public override bool PreDraw(ref Color lightColor)
        {
            Projectile.timeLeft = 9;
            rotTime += 0.01f;
            var sb = Main.spriteBatch;
            sb.End();
            sb.Begin(SpriteSortMode.Immediate, BlendState.Opaque, SamplerState.LinearWrap, DepthStencilState.None, RasterizerState.CullNone, null, Main.GameViewMatrix.TransformationMatrix);

            //Y大小 = (int)Math.Min(rotTime / 2.0f * 30, 200);
            Y大小 = (int)Math.Max(YTwo, 50);
            if (rotTime < MathHelper.PiOver2 * 2)
                YTwo++;
            else
                YTwo--;

            var mms = Main.MouseScreen;
            var mms2 = new Vector2(mms.X, mms.Y) + new Vector2(0, Y大小).RotatedBy(rotTime);

            Vertex[] VERTEX = new Vertex[]
            {
                new Vertex(mms,     Color.White, Vector3.Zero),
                new Vertex(mms2,    Color.White, Vector3.Zero),
            };

            var gd = Main.graphics.GraphicsDevice;
            gd.DrawUserPrimitives(PrimitiveType.LineList, VERTEX, 0, 1);
            sb.End();
            sb.Begin();

            if(rotTime > MathHelper.TwoPi)
            {
                rotTime = 0f;
            }

            Main.NewText(rotTime);
            return false;
        }

        //本体是否造成伤害
        public override bool MinionContactDamage()
        {
            return true;
        }
    }
}
