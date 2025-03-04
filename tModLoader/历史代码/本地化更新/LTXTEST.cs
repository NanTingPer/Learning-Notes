using Microsoft.Xna.Framework;
using MonoMod.Cil;
using MonoMod.RuntimeDetour;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Reflection.Emit;
using System.Text;
using System.Threading.Tasks;
using Terraria.Localization;
using Terraria.ModLoader;

namespace LTXTEST
{
	// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
	public class LTXTEST : Mod
	{
        /// <summary>
        /// LanguagerManager的实例
        /// </summary>
        public static object LanguageManager;
        /// <summary>
        /// 全部的本地化键与值
        /// </summary>
        public static Dictionary<string, LocalizedText> LocalizedTexts;
        /// <summary>
        /// 本地化文本的Set方法
        /// </summary>
        public static MethodInfo LocalizedTextSet;

        public override void PostSetupContent()
        {
            Type GameCulture = typeof(LanguageManager);
            Type LocalizedText = typeof(LocalizedText);
            #region 获取LanguageManager的实例
            //在LanguageManager类下有个Instance字段，是自己的实例 公共的
            FieldInfo Instance = GameCulture.GetField("Instance", BindingFlags.Public | BindingFlags.Static);
            LanguageManager = Instance.GetValue(null);
            #endregion

            #region 获取LocalizedTexts
            //全部的KeyValue
            FieldInfo Field = GameCulture.GetField("_localizedTexts", BindingFlags.NonPublic | BindingFlags.Instance);
            LocalizedTexts = (Dictionary<string, LocalizedText>)Field.GetValue(LanguageManager);
            #endregion
            #region 修改值
            LocalizedTextSet = LocalizedText.GetProperty("Value").GetSetMethod(true);
            #endregion

            base.PostSetupContent();
        }
    }

    public class Update : ModSystem
    {
        public override void UpdateUI(GameTime gameTime)
        {
            LocalizedText Text = LTXTEST.LocalizedTexts["ItemName.CopperShortsword"];
            LTXTEST.LocalizedTextSet.Invoke(Text, ["非常好的铜短剑令我脑袋飞转"]);
            base.UpdateUI(gameTime);
        }
    }
}
