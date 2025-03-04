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
        /// LanguagerManager��ʵ��
        /// </summary>
        public static object LanguageManager;
        /// <summary>
        /// ȫ���ı��ػ�����ֵ
        /// </summary>
        public static Dictionary<string, LocalizedText> LocalizedTexts;
        /// <summary>
        /// ���ػ��ı���Set����
        /// </summary>
        public static MethodInfo LocalizedTextSet;

        public override void PostSetupContent()
        {
            Type GameCulture = typeof(LanguageManager);
            Type LocalizedText = typeof(LocalizedText);
            #region ��ȡLanguageManager��ʵ��
            //��LanguageManager�����и�Instance�ֶΣ����Լ���ʵ�� ������
            FieldInfo Instance = GameCulture.GetField("Instance", BindingFlags.Public | BindingFlags.Static);
            LanguageManager = Instance.GetValue(null);
            #endregion

            #region ��ȡLocalizedTexts
            //ȫ����KeyValue
            FieldInfo Field = GameCulture.GetField("_localizedTexts", BindingFlags.NonPublic | BindingFlags.Instance);
            LocalizedTexts = (Dictionary<string, LocalizedText>)Field.GetValue(LanguageManager);
            #endregion
            #region �޸�ֵ
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
            LTXTEST.LocalizedTextSet.Invoke(Text, ["�ǳ��õ�ͭ�̽������Դ���ת"]);
            base.UpdateUI(gameTime);
        }
    }
}
