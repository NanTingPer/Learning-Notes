using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using Terraria;

namespace KillLife;

public static class AssetExtension
{
    public static Vector2 Center(this Asset<Texture2D> asset)
    {
        var height = asset.Height();
        var width = asset.Width();

        return new Vector2(width / 2, height / 2);
    }
}

#region Del
//public class LifePlayerLayer : PlayerDrawLayer
//{
//    public override void Load()
//    {
//        base.Load();
//    }

//    public override Position GetDefaultPosition()
//    {
//        //return new AfterParent(PlayerDrawLayers.FrontAccBack);
//        //return new AfterParent(PlayerDrawLayers.FrontAccFront);
//        //return new AfterParent(PlayerDrawLayers.HeldItem);

//        return new BeforeParent(PlayerDrawLayers.HeldItem);
//    }

//    public override bool GetDefaultVisibility(PlayerDrawSet drawInfo)
//    {
//        return !(FrontDraw == null);
//    }

//    /// <summary>
//    /// 在玩家双手之间绘制
//    /// </summary>
//    public static event Action FrontDraw;
//    protected override void Draw(ref PlayerDrawSet drawInfo)
//    {
//        FrontDraw?.Invoke();
//        foreach (var item in FrontDraw.GetInvocationList()) {
//            FrontDraw -= (Action)item;
//        }
//    }
//}
#endregion