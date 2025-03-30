using Terraria.UI;

namespace InfiniteL.TestUI;

/// <summary>
/// 此类承载进度修改UI
/// </summary>
public class NewGameInterface : GameInterfaceLayer
{
    public NewGameInterface(string name) : base(name, InterfaceScaleType.Game)
    {
        
    }
    public NewGameInterface(string name, InterfaceScaleType scaleType) : base(name, scaleType)
    {
    }

    protected override bool DrawSelf()
    {
        TestUI_allUI.UserInterface.CurrentState.Draw(spriteBatch);
        return base.DrawSelf();
    }
}
