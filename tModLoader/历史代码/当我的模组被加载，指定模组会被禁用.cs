using Terraria.ModLoader;

namespace ModLoadrUnLoad
{
	// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
	public class ModLoadrUnLoad : Mod
	{
        public override void Load()
        {
            #region IL挂钩法 失败
            //静态构造函数

            //public static BindingFlags BPS = BindingFlags.NonPublic | BindingFlags.Static;
            //public static ILHook LoadHook;

            //MethodInfo methodinfo = typeof(ModLoader).GetMethod("Load", BPS);
            ////"IL_0039: ldarg.0"
            ////"IL_003a: call System.Collections.Generic.List`1<Terraria.ModLoader.Core.LocalMod> Terraria.ModLoader.Core.ModOrganizer::SelectAndSortMods(System
            ////"IL_003f: ldarg.0"
            ////"IL_0040: call System.Collections.Generic.List`1<Terraria.ModLoader.Mod> Terraria.ModLoader.Core.AssemblyManager::InstantiateMods(System.Collecti

            //Func<Instruction, bool> MethodFunc = ins => {
            //    ins.MatchCall(out MethodReference value);
            //    if (value.Name == "SelectAndSortMods")
            //        return true;
            //    return false;
            //};
            //Type LocalMod = typeof(Main).Assembly.GetTypes().FirstOrDefault(type => type.FullName.Contains("Terraria.ModLoader.Core.LocalMod"));
            //Type GenericList = typeof(List<>).MakeGenericType([LocalMod]);
            //MethodInfo LocalModListClear = GenericList.GetMethod("Clear");


            //LoadHook =
            //new ILHook(methodinfo, ilc => {
            //    var r = ilc.Body.Instructions;
            //    ILCursor ilCursor = new ILCursor(ilc);

            //    if (ilCursor.TryGotoNext(MoveType.After, f => f.MatchLdarg0(), MethodFunc)) {
            //        ilCursor.Emit(OpCodes.Ldloc_1);
            //        ilCursor.Emit(OpCodes.Call, LocalModListClear);
            //    }
            //});
            //LoadHook.Apply();
            #endregion

            #region 暴力卸载法(卸载modsByName中对应的内容) 失败
            //var ModLoadrField = typeof(ModLoader).GetFields(BindingFlags.NonPublic | BindingFlags.Static).FirstOrDefault(F => F.Name == "modsByName");
            //IDictionary<string, Mod> ALoadMod = (IDictionary<string, Mod>)ModLoadrField.GetValue(null);
            //ALoadMod.Remove("GensokyoWPNACC");
            #endregion

            #region 暴力卸载法(调用Mod的 Close/Unload 方法) 失败
            ////PostSetupContent
            //if (ModLoader.TryGetMod("GensokyoWPNACC", out Mod mod)) {
            //    mod.Close();
            //    mod.Unload();
            //}
            #endregion

            #region 文件替换法("更改enabled.json") 失败
            //string allEnableMod = File.ReadAllText("C:\\Users\\23759\\Documents\\My Games\\Terraria\\tModLoader\\Mods\\enabled.json");
            //JsonNode r = JsonValue.Parse(allEnableMod);
            //JsonArray array = r.AsArray();
            //array.Remove("GensokyoWPNACC");
            //string co = array.ToJsonString().Replace("\"GensokyoWPNACC\",", "");
            //File.WriteAllText("C:\\Users\\23759\\Documents\\My Games\\Terraria\\tModLoader\\Mods\\enabled.json", co);
            #endregion

            #region 调用ModLoader卸载法 失败
            ////PostSetupContent
            //BindingFlags BNS = BindingFlags.NonPublic | BindingFlags.Static;
            //typeof(ModLoader).GetMethod("Unload", BindingFlags.NonPublic | BindingFlags.Static).Invoke(null, null);
            //typeof(ModLoader).GetMethod("Mods_Unload", BNS).Invoke(null, null);
            //typeof(ModLoader).GetMethod("Load", BNS).Invoke(null, null);

            #endregion

            #region 调用UIMods的返回 失败
            //Type type = typeof(Main).Assembly.GetTypes().FirstOrDefault(type => type.FullName.Contains("Terraria.ModLoader.UI.Interface"));
            //FieldInfo field = type.GetField("modsMenu", BindingFlags.NonPublic | BindingFlags.Static);
            //object modsMenu = field.GetValue(null);//UIMods

            //modsMenu.GetType().GetMethod("HandleBackButtonUsage").Invoke(modsMenu, null);
            #endregion

            #region 调用强制重新加载 失败
            //BindingFlags BIN = BindingFlags.Instance | BindingFlags.NonPublic;

            //typeof(ModLoader).GetMethod("DisableMod", BindingFlags.NonPublic | BindingFlags.Static)
            //    .Invoke(null, ["GensokyoWPNACC"]);
            //if (ModLoader.TryGetMod("GensokyoWPNACC", out _)) {
            //    Type type = typeof(Main).Assembly.GetTypes().FirstOrDefault(type => type.FullName.Contains("Terraria.ModLoader.UI.Interface"));
            //    FieldInfo field = type.GetField("modsMenu", BindingFlags.NonPublic | BindingFlags.Static);
            //    object modsMenu = field.GetValue(null);//UIMods
            //    modsMenu.GetType().GetMethod("ReloadMods", BIN).Invoke(modsMenu, [null, null]);
            //}
            #endregion

            #region 调用ModLoader的DisableMod然后调用 ModLoader的Load方法 成功
            ////此行在Load
            //typeof(ModLoader).GetMethod("DisableMod", BindingFlags.NonPublic | BindingFlags.Static)
            //    .Invoke(null, ["GensokyoWPNACC"]);

            ////此行在PostSetupContent
            //if (ModLoader.TryGetMod("GensokyoWPNACC", out _)) {
            //    //ModLoader.Reload();
            //    BindingFlags BSN = BindingFlags.Static | BindingFlags.NonPublic;
            //    typeof(ModLoader).GetField("isLoading", BSN).SetValue(null, false);
            //    typeof(ModLoader).GetMethod("Load"/*"Reload"*/, BSN).Invoke(null, [null]);
            //}
            #endregion
            base.Load();
        }

        public override void PostSetupContent()
        {
        }
    }
}
