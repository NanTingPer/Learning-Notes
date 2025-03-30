using Microsoft.Xna.Framework.Graphics;
using Terraria.ModLoader;
using Terraria.UI;

namespace InfiniteL.UI;

/// <summary>
/// 克脑，肉山，三王后，世花后，石巨后，月总后，三灾后，神后，龙后，女巫
///if (Main.ingameOptionsWindow || Main.inFancyUI || !Main.playerInventory)
/// </summary>
public class BossUIState : UIState
{
    public override void OnInitialize()
    {
        var _nvwu = ModContent.Request<Texture2D>("InfiniteL/UI/_nvwu");
        var _sanzai = ModContent.Request<Texture2D>("InfiniteL/UI/_sanzai");
        var _shen = ModContent.Request<Texture2D>("InfiniteL/UI/_shen");
        var _long = ModContent.Request<Texture2D>("InfiniteL/UI/_long");

        Append(new BossButton(_nvwu, "克苏鲁之脑"));
        Append(new BossButton(_nvwu, "肉山"));
        Append(new BossButton(_nvwu, "三王"));
        Append(new BossButton(_nvwu, "世花"));
        Append(new BossButton(_nvwu, "石巨人"));
        Append(new BossButton(_nvwu, "月总"));
        Append(new BossButton(_nvwu, "女巫"));
        Append(new BossButton(_sanzai, "三灾"));
        Append(new BossButton(_shen, "神"));

        base.OnInitialize();
    }
    public override void Update(GameTime gameTime)
    {
        Top.Set(0, 0.8f);
        Left.Set(0, 0.6f);
        Recalculate();
        base.Update(gameTime);
    }

    public override void Recalculate()
    {
        base.Recalculate();
        foreach (var uiel in Elements) {
            uiel.Recalculate();
        }
    }

    public override void OnActivate()
    {
        base.OnActivate();
    }

    public override void Draw(SpriteBatch spriteBatch)
    {
        foreach (var item1 in Elements) {
            item1.Draw(spriteBatch);
        }
        //base.Draw(spriteBatch);
    }

}

public sealed class LoadBossUIState : ModSystem
{
    public static bool isShow = false;
    private static UserInterface userIf;
    private static BossUIState bossUi;
    private static LegacyGameInterfaceLayer UiLayer { get; } = new LegacyGameInterfaceLayer(
        name: "InfiniteL:Enable",
        drawMethod: () => {
            bossUi.Draw(spriteBatch);
            return true;
        },
        InterfaceScaleType.UI
    );
    public override void UpdateUI(GameTime gameTime)
    {
        //bossUi = new BossUIState();
        userIf?.Update(gameTime);
        base.UpdateUI(gameTime);
    }
    public override void ModifyInterfaceLayers(List<GameInterfaceLayer> layers)
    {
        if (!isShow) {
            userIf.SetState(null);
            return;
        }
        userIf.SetState(bossUi);
        int r = layers.FindIndex(f => f.Name.Contains("Mouse"));
        r = r == -1 ? 0 : r;
        layers.Insert(r, UiLayer);
        base.ModifyInterfaceLayers(layers);
    }
    public override void Load()
    {
        bossUi = new BossUIState();
        bossUi.Activate();

        userIf = new UserInterface();
        userIf.SetState(bossUi);
        base.Load();
    }
}
