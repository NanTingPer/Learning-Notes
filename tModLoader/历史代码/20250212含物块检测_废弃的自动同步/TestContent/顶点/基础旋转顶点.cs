using GensokyoWPNACC.MyUtils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System.Collections.Generic;
using Terraria;
using Terraria.GameContent;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.顶点
{
    public class 基础旋转顶点 : ModProjectile
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
            Projectile.timeLeft = 15;                   //存活时间

            base.SetDefaults();
        }

        public static Asset<Texture2D> MyTexture;
        public override void SetStaticDefaults()
        {
            MyTexture = ModContent.Request<Texture2D>("GensokyoWPNACC/TestContent/顶点/基础旋转顶点");
            ProjectileID.Sets.TrailingMode[Type] = 4;
            ProjectileID.Sets.TrailCacheLength[Type] = 20;
            base.SetStaticDefaults();
        }

        private Player player => Main.player[Projectile.owner];
        public override void AI()
        {
            Projectile.Center = player.Center;
            Projectile.rotation += 0.2f;
            base.AI();
        }

        public override bool PreDraw(ref Color lightColor)
        {
            var sb = Main.spriteBatch;
            var 顶点开始位置 = player.Center;
            List<Vertex> VERTEXLIST = new List<Vertex>();
            var op = Projectile.oldPos;
            var or = Projectile.oldRot;
            for (int i = 0; i < or.Length; i++)
            {
                if (op[i] != Vector2.Zero)
                {
                    VERTEXLIST.Add
                    (new Vertex
                        (
                            顶点开始位置 + new Vector2(90 * -player.direction, -90f).RotatedBy(or[i] * player.direction) - Main.screenPosition,
                            new Color(255,255,255,200),
                         new Vector3(i / 20, 1, 1)
                        )
                    );

                    VERTEXLIST.Add
                    (new Vertex
                        (
                            顶点开始位置 + new Vector2(20f, 90).RotatedBy(or[i] * player.direction) - Main.screenPosition,
                            Color.White,
                         new Vector3(1, i / 20, 1)
                        )
                    );
                }
            }
            sb.End();
            sb.Begin(
                SpriteSortMode.Immediate,
                BlendState.NonPremultiplied,
                SamplerState.AnisotropicClamp,
                DepthStencilState.None,
                RasterizerState.CullNone,
                null,
                Main.GameViewMatrix.TransformationMatrix);//屏幕矩阵

            if(VERTEXLIST.Count >= 3)
            {
                Main.graphics.GraphicsDevice.Textures[0] = MyTexture.Value;
                Main.graphics.GraphicsDevice.DrawUserPrimitives(PrimitiveType.TriangleStrip, VERTEXLIST.ToArray(), 0, VERTEXLIST.Count - 2);
            }
            Main.NewText(VERTEXLIST.Count);
            sb.End();
            sb.Begin();

            return false;
        }

        public override bool ShouldUpdatePosition()
        {
            return false;
        }
    }
}
