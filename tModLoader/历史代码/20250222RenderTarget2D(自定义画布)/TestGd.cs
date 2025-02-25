using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Terraria;
using Terraria.GameContent.Drawing;
using Terraria.Graphics.Capture;
using Terraria.ModLoader;
using Terraria.UI;
using ThoriumMod.PlayerLayers;
using static GensokyoWPNACC.TestContent.TestGd;

namespace GensokyoWPNACC.TestContent
{ 
    //public class Layer : ModMapLayer //晶塔和床等


    public class TestGd : ModSystem
    {
        public delegate void DDoDraw(GameTime gamtime);
        public static MethodInfo DoDrawMethod { get; } = typeof(Main).GetMethod("DoDraw", BindingFlags.Instance | BindingFlags.NonPublic);
        //连new都要在主线程
        public static RenderTarget2D rt2d = new RenderTarget2D(Main.graphics.GraphicsDevice, Main.graphics.GraphicsDevice.PresentationParameters.BackBufferWidth, Main.graphics.GraphicsDevice.PresentationParameters.BackBufferHeight, mipMap: false, Main.graphics.GraphicsDevice.PresentationParameters.BackBufferFormat, DepthFormat.None);//new RenderTarget2D(Main.spriteBatch.GraphicsDevice, 200, 200, true, SurfaceFormat.Alpha8, DepthFormat.None, 1, RenderTargetUsage.PreserveContents);
        public static DDoDraw DoDraw = (DDoDraw)Delegate.CreateDelegate(typeof(DDoDraw), Main.instance, DoDrawMethod);
        public override void Load()
        {
            base.Load();
        }

        public override void ModifyInterfaceLayers(List<GameInterfaceLayer> layers)
        {
            var lgif =
            new LegacyGameInterfaceLayer("GenokyoWPNACC:DrawDusts", () =>
            {
                #region 垃圾就应该丢进垃圾桶里 2025 02 22
                //    var sb = Main.spriteBatch;
                //    var mms = Main.MouseScreen;
                //    var rec = new Rectangle((int)mms.X / 2, (int)mms.Y / 2, 590, 500);
                //    sb.End();
                //    sb.GraphicsDevice.SetRenderTarget(rt2d);

                //    sb.Begin(SpriteSortMode.Deferred, BlendState.Additive, SamplerState.LinearClamp, DepthStencilState.None, RasterizerState.CullNone, null, Main.GameViewMatrix.TransformationMatrix);
                //    sb.Draw(EffectAssets.Black.Value, rec/*drawInfo.drawPlayer.Center - Main.screenPosition*/, Color.Azure);
                //    sb.End();

                //    sb.Begin();//todo 后面家的
                //    sb.Draw(rt2d, rec, Color.Aqua);
                //    sb.GraphicsDevice.SetRenderTarget(null);
                #endregion

                //彻底覆盖任何图层 只显示EffectAssets.Black.Value
                var sb = Main.spriteBatch;
                sb.End(); //停止原来的覆盖

                #region 演示如何绘制一个方形
                //sb.GraphicsDevice.SetRenderTarget(rt2d); //将绘制目标改为自定义的 (黑底)
                //sb.GraphicsDevice.Clear(Color.Transparent); //清除原有内容

                ////开始绘制
                //sb.Begin(SpriteSortMode.Deferred, BlendState.Additive, SamplerState.LinearClamp, DepthStencilState.None, RasterizerState.CullNone, null, Main.GameViewMatrix.TransformationMatrix);
                ////sb.Draw(Main.instance.mapSectionTexture, new Rectangle(0, 0, 500, 500), Color.Azure);
                //sb.Draw(EffectAssets.Black.Value, new Rectangle(0, 0, 500, 500), Color.Azure);
                //sb.GraphicsDevice.SetRenderTarget(null); //在End()前设置为null
                //sb.End();
                #endregion 演示如何绘制一个方形
                sb.End();

                

                sb.Begin();//恢复原来的绘制
                return true;
            }, InterfaceScaleType.UI);
            layers.Insert(layers.Count, lgif);


            base.ModifyInterfaceLayers(layers);
        }

        //Main.tileTarget;
        //Main.blackTarget;
        //Main.tile2Target;
        //Main.wallTarget;
        //Main.backgroundTarget;
        //sb.Draw(Main.instance.tileTarget, Vector2.Zero, Color.White);
        //sb.Draw(Main.instance.tile2Target, new Rectangle(0,0,1000,1000), Color.White);
        //var r = Main.ContentThatNeedsRenderTargets;       //所有内容层
        //Main.instance.ResetAllContentBasedRenderTargets(); //重置内容层

        //照相机 无用
        //Main.instance.DrawCapture(new Rectangle(0,0,1000,1000), new Terraria.Graphics.Capture.CaptureSettings().);

        ///
    }
}
