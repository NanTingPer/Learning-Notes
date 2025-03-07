using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Collections.Generic;
using Terraria;
using Terraria.DataStructures;
using Terraria.GameContent;
using Terraria.ID;
using Terraria.ModLoader;
using Vertex = Microsoft.Xna.Framework.Graphics.VertexPositionColorTexture;

namespace 刀光教程
{
    public class 弹幕 : ModProjectile
    {
        private Player Player;
        private float dir;
        private int timer = 0;
        public override void AI()
        {
            if (Projectile.rotation < 0 || Projectile.rotation > float.Pi * 2) {
                timer++;
                if (timer > 40)
                    Projectile.Kill();
            } else {
                Projectile.rotation += 0.1f * dir;
            }
            Projectile.Center = Player.Center;
            Player.heldProj = Projectile.owner;
            Player.SetCompositeArmFront(true, Player.CompositeArmStretchAmount.Full, Projectile.rotation);
            base.AI();
        }
        public override bool PreDraw(ref Color lightColor)
        {
            ProjectileID.Sets.TrailCacheLength[Type] = 20;
            SpriteBatch sb = Main.spriteBatch;
            List<Vertex> VERTEXLIST = new List<Vertex>();
            var r = TextureAssets.Item[ModContent.ItemType<武器>()];
            float[] oldrot = Projectile.oldRot;
            for (int i = 0; i < oldrot.Length; i++) {
                if (Projectile.oldPos[i] == Vector2.Zero) continue;
                Vector3 upPointVec = ((Player.Center + new Vector2(0, r.Height() + 20).RotatedBy(oldrot[i])) - Main.screenPosition).ToVector3();
                Vector3 doPointVec = (Player.Center - Main.screenPosition).ToVector3();

                Vertex upPoint = new Vertex(upPointVec, Color.Black, new Vector2(i * 1.0f / oldrot.Length, 0));
                Vertex doPoint = new Vertex(doPointVec, Color.Black, new Vector2(i * 1.0f / oldrot.Length, 1));
                VERTEXLIST.Add(upPoint);
                VERTEXLIST.Add(doPoint);
            }
            int num = VERTEXLIST.Count;
            VERTEXLIST.RemoveAt(0);
            if (num > 2) {
                //sb.GraphicsDevice.RasterizerState = new RasterizerState() { CullMode = CullMode.None };
                sb.GraphicsDevice.Textures[0] = TextureAssets.Projectile[Type].Value;
                sb.GraphicsDevice.DrawUserPrimitives(PrimitiveType.TriangleStrip, VERTEXLIST.ToArray(), 0, num - 2);
            }
            
            sb.Draw(r.Value, Player.Center - Main.screenPosition, null, Color.White, Projectile.rotation + float.Pi - float.Pi / 4, new Vector2(0, r.Height()), 1f, SpriteEffects.None, 1f);
            return false;
        }

        public override bool? Colliding(Rectangle projHitbox, Rectangle targetHitbox)
        {
            var r = TextureAssets.Item[ModContent.ItemType<武器>()];
            Vector2 targetPos = new Vector2(targetHitbox.X, targetHitbox.Y);
            Vector2 targetBox = new Vector2(targetHitbox.Width, targetHitbox.Height);
            Vector2 stopPoint = Player.Center + new Vector2(0, r.Height() + 20).RotatedBy(Projectile.rotation);
            float rr = 0;
            bool istrue = Collision.CheckAABBvLineCollision(targetPos, targetBox, Player.Center, stopPoint, 10f,ref rr);
            for(float i = 0; i < 1; i += 0.1f) {
                //Dust.QuickDust(Vector2.Lerp(Player.Center, stopPoint, i), Color.LightCyan);
            }

            return istrue;
        }

        public override void SetDefaults()
        {
            Projectile.friendly = true;
            Projectile.DamageType = DamageClass.Default;
            Projectile.width = 0;
            Projectile.height = 0;
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

        public override void OnSpawn(IEntitySource source)
        {
            Player = Main.player[Projectile.owner];
            Projectile.rotation = 3.14f;
            dir = Player.direction;
            base.OnSpawn(source);
        }

    }
}
