LanguageManager

```cs
System.IO.IOException: 当文件已存在时，无法创建该文件。
   at System.IO.FileSystem.MoveFile(String sourceFullPath, String destFullPath, Boolean overwrite)
   at Terraria.ModLoader.LocalizationLoader.UpdateLocalizationFilesForMod(Mod mod, String outputPath, GameCulture specificCulture) in tModLoader\Terraria\ModLoader\LocalizationLoader.cs:line 510
   at Terraria.ModLoader.LocalizationLoader.UpdateLocalizationFiles() in tModLoader\Terraria\ModLoader\LocalizationLoader.cs:line 306
   at Terraria.ModLoader.LocalizationLoader.FinishSetup() in tModLoader\Terraria\ModLoader\LocalizationLoader.cs:line 297
   at Terraria.ModLoader.ModContent.Load(CancellationToken token) in tModLoader\Terraria\ModLoader\ModContent.cs:line 379
   at Terraria.ModLoader.ModLoader.Load(CancellationToken token) in tModLoader\Terraria\ModLoader\ModLoader.cs:line 134

```



- 设置当前的全部值为 `Key` (本地化键)

```cs
private void SetAllTextValuesToKeys()
{
	foreach (KeyValuePair<string, LocalizedText> localizedText in _localizedTexts) {
		localizedText.Value.SetValue(localizedText.Key);
	}
}
```



- 获取原版的本地化文件

```cs
GetLanguageFilesForCultureprivate string[] GetLanguageFilesForCulture(GameCulture culture)
{
	Assembly.GetExecutingAssembly();
	return Array.FindAll(typeof(Program).Assembly.GetManifestResourceNames(), (string element) => element.StartsWith("Terraria.Localization.Content." + culture.CultureInfo.Name.Replace('-', '_')) && element.EndsWith(".json"));
}
```



- 加载模组本地化

```cs
public static void LoadModTranslations(GameCulture culture)
{
	var lang = LanguageManager.Instance;
	foreach (var mod in ModLoader.Mods) {
		foreach (var (key, value) in LoadTranslations(mod, culture)) {
			lang.GetText(key).SetValue(value); // can only set the value of existing keys. Cannot register new keys.
		}
	}
}

```

