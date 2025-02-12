using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Terraria;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.Dusts.DrawDusts
{
    public class DrawDustTest : DrawDust
    {
        public Asset<Texture2D> Texture;
        public DrawDustTest(Vector2 position, Asset<Texture2D> texture)
        {
            this.position = position;
            Texture = texture;
            Texture = ModContent.Request<Texture2D>("GensokyoWPNACC/TestContent/Dusts/螺旋上升/螺旋上升粒子");
            DrawDustList.Add(this);
        }
        public override void Draw()
        {
            SB.Begin();
            SB.Draw(Texture.Value, position - Main.screenPosition, null, Color.White, 0f, Vector2.Zero, 3f, SpriteEffects.None, 1f);
            Main.NewText("1");
            SB.End();
        }
    }
}
