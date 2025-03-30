using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Terraria.ModLoader;
using Terraria.UI;

namespace InfiniteL.TestUI
{
    public class TestUI_allUI : ModSystem
    {
        public static UserInterface UserInterface { get; } = new UserInterface();
        public static Asset<Texture2D> Texture { get; } = ModContent.Request<Texture2D>("InfiniteL/TestUI/icon");
        public static OneUI OneUI { get; } = new OneUI();
        public static NewGameInterface NewGameInterface { get; } = new NewGameInterface("InfiniteL:TestNGI");

        public override void UpdateUI(GameTime gameTime)
        {
            UserInterface.Update(gameTime);
            base.UpdateUI(gameTime);
        }

        public override void Load()
        {
            OneUI.Activate();
            base.Load();
        }


    }
}
