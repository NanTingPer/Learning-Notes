using Microsoft.Xna.Framework;
using System.Collections.Generic;
using System.Threading.Tasks;
using Terraria;
using Terraria.DataStructures;
using Terraria.ModLoader;

namespace 打字机
{
	// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
	public class 打字机 : Mod
	{
		
	}

    public class ModPlayerLayer : PlayerDrawLayer
    {
        private string drawString = "要被显示的全部文本，你猜怎么着";

        public override bool GetDefaultVisibility(PlayerDrawSet drawInfo)
        {
            return true;
        }
        public override Position GetDefaultPosition()
        {
            return new BeforeParent(PlayerDrawLayers.HandOnAcc);
        }

        private int timer = 0;
        private bool IsViewShu = true;
        protected override void Draw(ref PlayerDrawSet drawInfo)
        {
            timer++;
            int length = drawString.Length;                                                 //文本长度
            var DrawPos = drawInfo.drawPlayer.Center - Main.screenPosition;
            var viewCount = timer / 10 % (length + 1);                                   //显示数量
            string draw = drawString.Substring(0, viewCount);                      //真实显示的文本
            DrawPos += new Vector2(-viewCount * (50 - viewCount), -100f);                 //绘制位置调整 需要微调 / 计算

            #region 最后的 | 符号
            if (IsViewShu) draw += "I";
            if (timer % 30 == 0) IsViewShu = false;
            if (timer % 60 == 0) IsViewShu = true;
            #endregion
            Utils.DrawBorderStringBig(Main.spriteBatch, draw, DrawPos, Color.Red, 1.5f);
        }
    }
}
