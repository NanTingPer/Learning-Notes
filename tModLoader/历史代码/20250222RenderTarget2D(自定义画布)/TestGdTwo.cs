//using Microsoft.Xna.Framework;
//using Microsoft.Xna.Framework.Graphics;
//using System.Collections.Generic;
//using Terraria;
//using Terraria.ModLoader;
//using Terraria.UI;

//namespace GensokyoWPNACC.TestContent
//{ 
//    //public class Layer : ModMapLayer //晶塔和床等


//    public class TestGdTwo : ModSystem
//    {
//        public float uTime = 0f;    //控制透明度
//        public bool isSub = false;  //透明度是加还是剑
//        public static int width = Main.spriteBatch.GraphicsDevice.PresentationParameters.BackBufferWidth; //绘制宽度
//        public static int height = Main.spriteBatch.GraphicsDevice.PresentationParameters.BackBufferHeight; //绘制高度
//        public static GraphicsDevice Graphics = Main.instance.GraphicsDevice;//new GraphicsDevice(GraphicsAdapter.DefaultAdapter, GraphicsProfile.HiDef, Main.instance.GraphicsDevice.PresentationParameters);
//        public static SpriteBatch sb = new SpriteBatch(Graphics); //画笔
//        public static RenderTarget2D RT2 = new RenderTarget2D(Graphics, width, height); //绘制大小
//        public override void UpdateUI(GameTime gameTime)
//        {
//            //透明度算法
//            if (uTime > 1f) isSub = true;
//            if (isSub) uTime -= 0.01f;
//            else uTime += 0.01f;
//            if (uTime <= 0f) isSub = false;

//            //绘制位置和大小算法，这里是绘制在屏幕中心，绘制大小是渲染大小
//            var drawWidth = width;
//            var drawHeight = height;
//            var xOffset = drawWidth / 2;
//            var yOffset = drawHeight / 2;
//            var drawRectangle = new Rectangle((drawWidth / 2) - xOffset, (drawHeight / 2) - yOffset, drawWidth, drawHeight);
           
//            //设置渲染画布
//            Graphics.SetRenderTarget(RT2);
//            Graphics.Clear(Color.Black * 0.5f); //如果画布大小不足够覆盖 就变为这个颜色
//            sb.Begin(SpriteSortMode.Immediate, BlendState.NonPremultiplied, SamplerState.LinearClamp, DepthStencilState.None, RasterizerState.CullNone, null, Main.GameViewMatrix.TransformationMatrix);
//            EffectAssets.ApplayAlphaAdd(uTime);
//            EffectAssets.ApplayAlphaAdd(0.3f);
//            sb.Draw(EffectAssets.BlackCircle反.Value, drawRectangle, null, Color.White);
//            Graphics.SetRenderTarget(null);
//            sb.End();
//            base.UpdateUI(gameTime);
//        }

//        public override void ModifyInterfaceLayers(List<GameInterfaceLayer> layers)
//        {
//            layers.Insert(0, new LegacyGameInterfaceLayer(
//                "w",
//                () => {
//                    Main.spriteBatch.Draw(RT2, new Rectangle(0, 0, width, height), null, Color.White);
//                    return true;
//                }, InterfaceScaleType.Game));
//            base.ModifyInterfaceLayers(layers);
//        }
//    }
//}
