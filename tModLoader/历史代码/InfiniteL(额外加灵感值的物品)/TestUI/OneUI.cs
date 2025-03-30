using Terraria.GameContent.UI.Elements;
using Terraria.UI;

namespace InfiniteL.TestUI
{
    public class OneUI : UIState
    {
        public override void OnInitialize()
        {
            Top.Pixels = 100;
            Left.Pixels = 100;

            var button = new UIImageButton(Texture);
            button.Width.Pixels = 50;
            button.Height.Pixels = 50;
            Append(button);
            base.OnInitialize();
        }
    }
}
