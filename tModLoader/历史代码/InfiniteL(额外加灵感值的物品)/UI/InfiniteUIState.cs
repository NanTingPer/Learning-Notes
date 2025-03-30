using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Reflection;
using Terraria.GameContent.UI.Elements;
using Terraria.ModLoader;
using Terraria.UI;
using ThoriumMod.UI.ResourceBars;

namespace InfiniteL.UI
{
    /// <summary>
    /// 克脑，肉山，三王后，世花后，石巨后，月总后，三灾后，神后，龙后，女巫
    ///if (Main.ingameOptionsWindow || Main.inFancyUI || !Main.playerInventory)
    /// </summary>
    public class InfiniteUIState : UIState
    {
        public static Asset<Texture2D> Infinite;
        private static MethodInfo DrawInfo;
        private UIImageButton but;
        static InfiniteUIState()
        {
            DrawInfo = typeof(InformationalIcons).GetMethod("DisplayIconNearEquips", BindingFlags.Static | BindingFlags.NonPublic);
            Infinite = ModContent.Request<Texture2D>("InfiniteL/UI/" + nameof(Infinite));
        }
        public override void OnInitialize()
        {
            but = new UIImageButton(Infinite);
            but.Top.Pixels = 0f;
            but.Left.Pixels = 0f;
            but.Width.Pixels = 20;
            but.Height.Pixels = 20;
            but.OnLeftClick += new MouseEvent(IsV);
            Append(but);
            base.OnInitialize();
        }

        private void IsV(UIMouseEvent evt, UIElement listeningElement)
        {
            LoadBossUIState.isShow = !LoadBossUIState.isShow;
            return;
        }

        public override void Draw(SpriteBatch spriteBatch)
        {
            //Main.spriteBatch, BardIcon_Texture, 140, ThoriumConfigClient.Instance.BardIconOffsetY - 0.7f, iconText3, " sec", new Vector2(30f, -2f), mouseText3
            //SpriteBatch spriteBatch, Texture2D tex, int yOffDefault, float yOffFactor, string iconText, string iconTextSuf, Vector2 iconTextOffset, string mouseText
            //DrawInfo.Invoke(null, [spriteBatch, Infinite.Value, 140, 2f, "", "无限\n灵感", new Vector2(30f, -2f), ""]);

            if (ingameOptionsWindow || inFancyUI || !playerInventory)
                return;

            //spriteBatch.Draw(Infinite.Value, new Vector2(screenWidth - 220, screenHeight - 100), Color.White);

            base.Draw(spriteBatch);
        }

        public override void Recalculate()
        {
            foreach (var item1 in Elements) {
                item1.Recalculate();
            }
            base.Recalculate();
        }
        public override void Update(GameTime gameTime)
        {
            Left.Pixels = screenWidth - 220;
            Top.Pixels = screenHeight - 100;
            Recalculate();
            base.Update(gameTime);
        }
    }

    public sealed class InfiniteUIStateSystem : ModSystem
    {
        private static UserInterface userIf;
        private static InfiniteUIState InUI;
        private static LegacyGameInterfaceLayer UiLayer { get; } = new LegacyGameInterfaceLayer(
            name: "InfiniteL:Enable",
            drawMethod: () => {
                InUI.Draw(spriteBatch);
                return true;
            },
            InterfaceScaleType.UI
        );
        public override void UpdateUI(GameTime gameTime)
        {
            if (ingameOptionsWindow || inFancyUI || !playerInventory){
                userIf.SetState(null);
                return;
            }
            userIf.SetState(InUI);
            userIf?.Update(gameTime);
            base.UpdateUI(gameTime);
        }
        public override void ModifyInterfaceLayers(List<GameInterfaceLayer> layers)
        {
            int r = layers.FindIndex(f => f.Name.Contains("Mouse"));
            r = r == -1 ? 0 : r;
            layers.Insert(r, UiLayer);
            base.ModifyInterfaceLayers(layers);
        }
        public override void Load()
        {
            InUI = new InfiniteUIState();
            InUI.Activate();

            userIf = new UserInterface();
            userIf.SetState(InUI);
            base.Load();
        }
    }

}
