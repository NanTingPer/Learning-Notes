using GensokyoWPNACC.DrawDustType;
using GensokyoWPNACC.MyUtils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Collections.Generic;
using Terraria;
using Terraria.DataStructures;
using Terraria.GameContent;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.XProjectile
{
    public class XRootProjTwo : ModProjectile
    {
        private int timer = 0;
        private Zt状态 zt = Zt状态.OneProj;
        private Player player;
        private int direction;
        private int TextureHeigth;
        private int ztTime = 50;
        public override void AI()
        {
            player.direction = direction;
            Projectile.Center = player.Center;
            player.heldProj = Projectile.owner;

            if (timer <= ztTime) zt = Zt状态.OneProj;
            else zt = Zt状态.TwoProj;

            switch (zt) {
                case Zt状态.OneProj:
                    OnePrj(Projectile);
                    break;

                case Zt状态.TwoProj:
                    TwoPrj(Projectile);
                    break;

                default:
                    break;
            }

            if (timer > ztTime * 2) Projectile.Kill();
            timer++;
            player.SetCompositeArmFront(true, Player.CompositeArmStretchAmount.ThreeQuarters, Projectile.rotation + 3.14f - (1f * direction));
            base.AI();
        }

        public override void OnSpawn(IEntitySource source)
        {
            player = Main.player[Projectile.owner];
            direction = player.direction;
            TextureHeigth = TextureAssets.Projectile[Type].Height();
            base.OnSpawn(source);
        }


        public enum Zt状态
        {
            OneProj = 0,
            TwoProj = 1
        }

        public void OnePrj(Projectile proj)
        {
            if(Math.Abs(Projectile.rotation) < 4)
                Projectile.rotation += 0.15f * direction;
        }

        public void TwoPrj(Projectile proj)
        {
            if(Math.Abs(Projectile.rotation) > 0.5)
                Projectile.rotation -= 0.15f * direction;
        }

        public override bool PreDraw(ref Color lightColor)
        {
            ProjectileID.Sets.TrailCacheLength[Type] = 100;
            //Projectile.DrawThis(direction);
            //Projectile.DrawLight(direction, ModContent.Request<Texture2D>("GensokyoWPNACC/TestContent/SoftSword/轻剑弹幕"), 20, -TextureAssets.Projectile[Type].Height() - 20);
            //x = 点p的y - 线段此时距y原点的距离(b) / m(斜率)
            //斜率 = tan(角)
            var 原点 = player.Center;
            List<Vertex> VERTEXLIST = [];
            var LineYOffst = -50;
            var LineLenght = 300;
            var LineTanRot = (float)Math.Tan(80f / 180f);
            var LineStarUp = 原点 + new Vector2(0, LineYOffst);
            var LineStopUp = LineStarUp + new Vector2(LineLenght * player.direction, 0).RotatedBy(LineTanRot * player.direction);
            var LineStarDown = 原点 + new Vector2(0, -LineYOffst);
            var LineStopDown = LineStarDown + new Vector2(LineLenght * player.direction, 0).RotatedBy(-LineTanRot * player.direction);
            var sb = WakeProjectile.SpriteBatch;
            sb.StartDraw(null, BlendState.Opaque);
            float[] rots = Projectile.oldRot;
            for (int i = 0; i < 20; i++) {
                if (Projectile.oldPos[i] == Vector2.Zero)
                    continue;
                //x = 点p的y - 线段此时距y原点的距离(b) / m(斜率)
                var rot = direction == -1 ? rots[i] + MathHelper.PiOver4 : rots[i] - MathHelper.PiOver4;
                Vector2 upPointVecTwo = LineStarUp + new Vector2(0, 60).RotatedBy(rot).ToScreen();
                Vector2 upPointVec = new Vector2((upPointVecTwo.Y - 50) / LineTanRot, upPointVecTwo.Y);
                //Vector2 upPointVec = Projectile.Center + new Vector2(0, 60).RotatedBy(rot);
                //upPointVec = MapArcToLine(player.Center, 60, rot, LineStarUp, LineStopUp).ToScreen();
                Vector2 dnPointVec = Projectile.Center + new Vector2(0, 0).RotatedBy(rot).ToScreen();
                Vertex upPoint = new Vertex(upPointVec, Color.Azure, new Vector3(i * 4f / rots.Length, 0, 1));
                Vertex dnPoint = new Vertex(dnPointVec, Color.Beige, new Vector3(i * 4f / rots.Length, 1, 1));
                VERTEXLIST.Add(upPoint);
                VERTEXLIST.Add(dnPoint);
            }
            if (VERTEXLIST.Count > 2) {
                EffectAssets.TestShader.Value.Techniques[0].Passes[0].Apply();
                sb.GraphicsDevice.Textures[0] = ModContent.Request<Texture2D>("GensokyoWPNACC/TestContent/SoftSword/轻剑弹幕").Value;
                sb.GraphicsDevice.DrawUserPrimitives(PrimitiveType.TriangleStrip, VERTEXLIST.ToArray(), 0, VERTEXLIST.Count - 2);
            }
            sb.StopDraw();
            return false;
        }

        public override bool? Colliding(Rectangle projHitbox, Rectangle targetHitbox)
        {
            var stop = player.Center + new Vector2(0, TextureHeigth + 20).RotatedBy(-Projectile.rotation + (Math.PI / 5 * direction));

            float temp = 0f;
            bool tempB = Collision.CheckAABBvLineCollision(targetHitbox.GetPosition(), targetHitbox.GetBox(), player.Center, stop, 1, ref temp);
            if(tempB) Main.NewText(tempB);
            return tempB;
        }

        public override void OnHitNPC(NPC target, NPC.HitInfo hit, int damageDone)
        {
            DrawDust.Power1(target.Center).Color = Color.White;
            base.OnHitNPC(target, hit, damageDone);
        }

        public override void SetDefaults()
        {
            Projectile.friendly = true;                
            Projectile.DamageType = DamageClass.Default; 
            Projectile.aiStyle = -1;
            Projectile.penetrate = -1;                 
            Projectile.ignoreWater = true;          
            Projectile.tileCollide = false;             
            Projectile.scale = 1f;                      
            Projectile.timeLeft = 99999;                   
            Projectile.extraUpdates = 2;
            base.SetDefaults();
        }

        public override void SetStaticDefaults()
        {
            ProjectileID.Sets.TrailingMode[Type] = 4;
            ProjectileID.Sets.TrailCacheLength[Type] = 10;
            base.SetStaticDefaults();
        }

        private Vector2 MapArcToLine(Vector2 arcCenter, float arcRadius, float arcAngle, Vector2 lineStart, Vector2 lineEnd)
        {
            // 计算弧线上的点
            Vector2 arcPoint = arcCenter + new Vector2((float)Math.Cos(arcAngle) * arcRadius, (float)Math.Sin(arcAngle) * arcRadius);

            // 将弧线点映射到线段上
            Vector2 lineDirection = lineEnd - lineStart; // 线段方向
            Vector2 arcToLine = arcPoint - lineStart;    // 从线段起点到弧线点的向量

            // 投影到线段方向
            float projection = Vector2.Dot(arcToLine, lineDirection) / lineDirection.LengthSquared();
            projection = MathHelper.Clamp(projection, 0, lineDirection.Length()); // 限制在线段范围内

            // 映射到线段上的点
            return lineStart + lineDirection * (projection / lineDirection.Length());
        }
    }
}
