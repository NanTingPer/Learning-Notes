using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.Dusts.DrawDusts
{
    public class DrawDustSystem : ModSystem
    {
        public override void UpdateUI(GameTime gameTime)
        {
            DrawDust.DrawDustList.RemoveAll(f => f.Active == false);

            foreach (var item in DrawDust.DrawDustList)
            {
                item.Draw();
            }

            base.UpdateUI(gameTime);
        }
    }
}
