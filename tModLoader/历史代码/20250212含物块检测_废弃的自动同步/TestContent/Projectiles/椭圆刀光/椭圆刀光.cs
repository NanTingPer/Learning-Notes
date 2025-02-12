using GensokyoWPNACC.MyUtils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Collections.Generic;
using Terraria;
using Terraria.GameContent;
using Terraria.ID;
using Terraria.ModLoader;
using static Microsoft.Xna.Framework.MathHelper;
using static System.Math;

namespace GensokyoWPNACC.TestContent.Projectiles.椭圆刀光
{
    public class 椭圆刀光 : ModProjectile
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

        public override bool PreDraw(ref Color lightColor)
        {
            Projectile.timeLeft = 9;
            VERTEXLIST.Clear();
            var sb = Main.spriteBatch;
            sb.End();
            sb.Begin(SpriteSortMode.Immediate, BlendState.NonPremultiplied, SamplerState.PointClamp, DepthStencilState.None, RasterizerState.CullNone, null, Main.GameViewMatrix.TransformationMatrix);
            
            for(float i = 0; i < PI; i+= 0.01f)
            {
                Vector2 point = new Vector2((float)Cos(i) * 200, (float)Sin(i) * 70);
                VERTEXLIST.Add
                (
                    new Vertex
                    (
                        point + Main.MouseScreen,
                        Color.White,
                        new Vector3(i / Pi, 1f, 0f)
                    )
                );
                VERTEXLIST.Add
                (
                    new Vertex
                    (
                        point + new Vector2(0, 100) + Main.MouseScreen,
                        Color.White,
                        new Vector3(1f, i / Pi, 0f)
                    )
                );

            }
            Main.graphics.GraphicsDevice.Textures[0] = TextureAssets.Projectile[Type].Value;
            Main.graphics.GraphicsDevice.DrawUserPrimitives(PrimitiveType.TriangleStrip, VERTEXLIST.ToArray(), 0, VERTEXLIST.Count - 2);

            sb.End();
            sb.Begin();
            return false;
        }

        public override void AI()
        {
            base.AI();
        }

        //本体是否造成伤害
        public override bool MinionContactDamage()
        {
            return true;
        }

        private List<Vertex> VERTEXLIST = new List<Vertex>();
    }
}
