using GensokyoWPNACC.Content.Projectiles.KoishiSatoriProjectiles;
using static Terraria.ModLoader.ModContent;
using Microsoft.Xna.Framework;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;
using System.Collections.Generic;
using GensokyoWPNACC.MyUtils;
using Microsoft.Xna.Framework.Graphics;

namespace GensokyoWPNACC.TestContent.Projectiles
{
    public class Bza : ModProjectile
    {
        public static int 控制点X;
        public static int 控制点Y;
        public override string Texture => GetModProjectile(ProjectileType<Koishi_Projectile>()).Texture;

        #region 无关紧要
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
            Projectile.netImportant = true;             //同步给新玩家
            Projectile.minionSlots = 0;                 //占用召唤栏
            Projectile.minion = true;                   //是仆从

            base.SetDefaults();
        }

        public override void SetStaticDefaults()
        {
            ProjectileID.Sets.MinionSacrificable[Type] = false; //牺牲自己召唤新的
            ProjectileID.Sets.CultistIsResistantTo[Type] = true; //自导 同时不攻击邪教徒
            ProjectileID.Sets.MinionTargettingFeature[Type] = true; //右键指定
            base.SetStaticDefaults();
        }
        #endregion 无关紧要

        private Player player => Main.player[Projectile.owner];
        private bool IsOneInit = false;
        private Vector2 起点;
        private Vector2 控制点;
        private Vector2 终点;
        private int 计时器 = 0;
        
        public override void AI()
        {
            计时器++;
            Projectile.timeLeft = 10;
            //var lerp1 = Vector2.Lerp(起点, 控制点, 计时器 % 100f / 100f);
            //var lerp2 = Vector2.Lerp(控制点, 终点, 计时器 % 100f / 100f);
            //var lerp3 = Vector2.Lerp(lerp1, lerp2, 计时器 % 100f / 100f);
            //Projectile.velocity = (lerp3 - Projectile.Center).SafeNormalize(Projectile.velocity) * 10f;


            base.AI();
        }

        public override bool PreDraw(ref Color lightColor)
        {
            var sb = Main.spriteBatch;
            控制点 = player.Center + new Vector2(控制点X, 控制点Y);
            终点 = player.Center + new Vector2(300, -500);
            起点 = Projectile.Center;
            Projectile.timeLeft = 6;

            var tlerp1 = Vector2.One;
            var tlerp2 = Vector2.One;
            var tlerp3 = Vector2.One;

            List<Vertex> VERTEXLIST = new List<Vertex>();
            #region 可视化贝塞尔
            for (float i = 0f; i <= 1f; i += 0.01f)
            {
                tlerp1 = Vector2.Lerp(起点, 控制点, i);
                tlerp2 = Vector2.Lerp(控制点, 终点, i);
                tlerp3 = Vector2.Lerp(tlerp1, tlerp2, i);

                VERTEXLIST.Add(new Vertex(tlerp3 - Main.screenPosition, Color.White, Vector3.Zero));
                VERTEXLIST.Add(new Vertex(tlerp3 - Main.screenPosition + new Vector2(10,10), Color.White, Vector3.One));

                Dust.NewDustPerfect(控制点, DustID.YellowTorch).noGravity = true;
            }

            sb.End();
            sb.Begin(SpriteSortMode.Immediate, BlendState.NonPremultiplied, SamplerState.AnisotropicClamp, DepthStencilState.None, RasterizerState.CullNone, null, Main.GameViewMatrix.TransformationMatrix);

            if(VERTEXLIST.Count > 3)
            {
                Main.graphics.GraphicsDevice.DrawUserPrimitives(PrimitiveType.LineList, VERTEXLIST.ToArray(), 0, VERTEXLIST.Count - 2);
            }
            sb.End();
            sb.Begin();
            #endregion

            return false;
        }

        //本体是否造成伤害
        public override bool MinionContactDamage()
        {
            return true;
        }
    }
}
