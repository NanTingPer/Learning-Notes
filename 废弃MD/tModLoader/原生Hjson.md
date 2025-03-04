根据现有逻辑，规划行动路径

# 原生`Hjson`

​	对于能使用原生`Hjson`进行汉化的文件，直接创建`Hjson`副本，然后读取解析保存为字典即可，需要保存两份，一份是`Key,Value`用来存储本地化`Key`和`值`，一份是`List`用来存储需要被替换的全部`Key`

```cs
//读取文件并赋值
using Stream file = GetFileStream("zh-tw_Mods.LocalText.hjson");
using StreamReader fileRead = new StreamReader(file);
//本Hjson的全部内容，调用 Method(hjsonValue)之后就会返回KV了
string hjsonValue = fileRead.ReadToEnd();
```

1. 获取KV

```cs
List<(string, string)> AllKV = Method(hjsonValue);
```

2. 存储为字典与集合

```cs
public static Dictionary<string, string> KeyValue = [];
public static List<String> Key = [];
public override void PostSetupContent(){
    AllKV.foreach(kv => {
        KeyValue.Add(kv.Item1, kv.Item2);
        Key.Add(Kv.Item1);
    });
}
```

3. 替换原内容，使用反射获取`Set`方法，还有全部的被本地化的内容，也可以写在`PostSetupContent`

```cs
public static Dictionary<string, LocalizedText> LocalizedTexts; //这个是全部的内容
public static MethodInfo LocalizedTextSet;	//这个是Set方法
public override void Load(){
	Type LocalizedText = typeof(LocalizedText);
	//全部字段
	FieldInfo Field = GameCulture.GetField("_localizedTexts", BindingFlags.NonPublic | BindingFlags.Instance);
    //获取实例-全部内容
	LocalizedTexts = (Dictionary<string, LocalizedText>)Field.GetValue(LanguageManager);
    //获取Set方法
	LocalizedTextSet = LocalizedText.GetProperty("Value").GetSetMethod(true);
}
```

```cs
//修改内容
Key.foreach(f => {
    if(LocalizedTexts.TryGetValue(f, out var text) && KeyValue.TryGetValue(f, out var newText)){
        LocalizedTextSet.Invoke(text, [newText]);
    }
});
```









# 公共方法

```cs
/// <summary>
/// 传入Hjson的内容
/// LocalizationLoader.LoadTranslations
/// </summary>
public List<(string, string)> Method(string translationFileContents)
{
    // Parse HJSON and convert to standard JSON
    var flattened = new List<(string, string)>();
    string jsonString;
    try {
        jsonString = HjsonValue.Parse(translationFileContents).ToString();
    } catch (Exception e) {
        string additionalContext = "";
        if (e is ArgumentException && Regex.Match(e.Message, "At line (\\d+),") is Match { Success: true } match && int.TryParse(match.Groups[1].Value, out int line)) {
            string[] lines = translationFileContents.Replace("\r", "").Replace("\t", "    ").Split('\n');
            int start = Math.Max(0, line - 4);
            int end = Math.Min(lines.Length, line + 3);
            var linesOutput = new StringBuilder();
            for (int i = start; i < end; i++) {
                if (line - 1 == i)
                    linesOutput.Append($"\n{i + 1}[c/ff0000:>" + lines[i] + "]");
                else
                    linesOutput.Append($"\n{i + 1}:" + lines[i]);
            }
            additionalContext = "\nContext:" + linesOutput.ToString();
        }
        throw new Exception(/*$"The localization file \"{translationFile.Name}\" is malformed and failed to load:{additionalContext} ", e*/);
    }

    // Parse JSON
    var jsonObject = JObject.Parse(jsonString);

    foreach (JToken t in jsonObject.SelectTokens("$..*")) {
        if (t.HasValues) {
            continue;
        }

        // Due to comments, some objects can by empty
        if (t is JObject obj && obj.Count == 0)
            continue;

        // Custom implementation of Path to allow "x.y" keys
        string path = "";
        JToken current = t;

        for (JToken parent = t.Parent; parent != null; parent = parent.Parent) {
            path = parent switch
            {
                JProperty property => property.Name + (path == string.Empty ? string.Empty : "." + path),
                JArray array => array.IndexOf(current) + (path == string.Empty ? string.Empty : "." + path),
                _ => path
            };
            current = parent;
        }

        // removing instances of .$parentVal is an easy way to make this special key assign its value
        //  to the parent key instead (needed for some cases of .lang -> .hjson auto-conversion)

        ////修改的地方，因为没有完全按照TML的来
        ////path.Replace(".$parentVal", "")
        path = "Mods.LocalText." + path.Replace(".$parentVal", "");

        flattened.Add((path, t.ToString()));
    }


	return flattened;
}
```

