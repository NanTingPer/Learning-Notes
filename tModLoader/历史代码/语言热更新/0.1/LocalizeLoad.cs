using System.Collections.Generic;
using Terraria.ModLoader.Config;

namespace ThoriumModzhcn.Systems
{
    public static class LocalizeLoad
    {
        public static List<string> HKHJson { get; } =
         [
            "zh-hk",
            "zh-hk_Mods.CalamityBardHealer.hjson",
            "zh-hk_Mods.RedemptionBardHealer.hjson",
            "zh-hk_Mods.SOTSBardHealer.hjson",
            "zh-hk_Mods.SpiritBardHealer.hjson",
            "zh-hk_Mods.SpookyBardHealer.hjson",
            "zh-hk_Mods.TerrariumHacks.hjson",
            "zh-hk_Mods.ThoriumRework.hjson",
            "zh-hk_Mods.UnlimitedBardEnergy.hjson"
        ];
        public static List<string> TWHJson { get; } =
         [
            "zh-tw",
            "zh-tw_Mods.CalamityBardHealer.hjson",
            "zh-tw_Mods.RedemptionBardHealer.hjson",
            "zh-tw_Mods.SOTSBardHealer.hjson",
            "zh-tw_Mods.SpiritBardHealer.hjson",
            "zh-tw_Mods.SpookyBardHealer.hjson",
            "zh-tw_Mods.TerrariumHacks.hjson",
            "zh-tw_Mods.ThoriumRework.hjson",
            "zh-tw_Mods.UnlimitedBardEnergy.hjson"
        ];

        public static List<string> ZHHJson { get; } = [];
        static LocalizeLoad()
        {
            Load(HKHJson, LocalizeUtil.Language.HongKong);
            Load(TWHJson, LocalizeUtil.Language.TaiWan);
            Load(ZHHJson, LocalizeUtil.Language.Chinese);
        }

        private static void Load(List<string> paths, LocalizeUtil.Language language)
        {
            foreach (var hjson in paths) {
                LocalizeUtil.LoadLocalizedKey(hjson, language, GetStartChars(hjson));
            }
        }

        private static string GetStartChars(string fileName)
        {
            var splitString = fileName.Split("_");
            if (splitString.Length == 1) return "";
            if (splitString[0] == "IL") return "";
            return splitString[1].Replace(".hjson", "");
        }
    }

    public class 语言切换 : ModConfig
    {
        public LocalizeUtil.Language 语言;

        public override ConfigScope Mode => ConfigScope.ClientSide;

        public override void OnChanged()
        {
            LocalizeUtil.CutLanguage(语言);
            base.OnChanged();
        }
    }
}
