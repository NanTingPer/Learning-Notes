using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using Terraria;

namespace GensokyoWPNACC.TestContent.Dusts.DrawDusts
{
    public abstract class DrawDust : IDisposable
    {
        public static List<DrawDust> DrawDustList = new List<DrawDust>();
        protected SpriteBatch SB => Main.spriteBatch;
        protected GraphicsDevice GD => Main.graphics.GraphicsDevice;
        protected Vector2 position;

        public bool Active = true;

        public virtual void Draw()
        {

        }

        public static DrawDust NewDrawDust(DrawDust dust)
        {
            DrawDustList.Add(dust);
            return dust;
        }

        public void Dispose()
        {
            GC.SuppressFinalize(this);
        }
    }
}
