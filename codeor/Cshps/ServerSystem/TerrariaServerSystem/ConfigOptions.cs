using System.Reflection;
using System.Text.Json.Serialization;

namespace TerrariaServerSystem;

public class ConfigOptions
{
    private static readonly Dictionary<string, string> propMap = new()
    {
        { nameof(AutoCreate), "世界大小" },
        { nameof(Port), "端口" },
        { nameof(MaxPlayers), "最大玩家数量" },
        { nameof(Password), "密码" },
        { nameof(IP), "IP" },
        { nameof(Seed), "种子" },
        { nameof(Evil), "邪恶" },
        { nameof(WorldName), "世界名称" },

    };
    private static Dictionary<string, string> PropMapReverse => propMap.Reverse().ToDictionary();
    private static PropertyInfo[] PropertyInfos { get; } = typeof(ConfigOptions).GetProperties();

    /// <summary>
    /// <para> 当<see cref="World"/>不存在时，会使用此参数 </para>
    /// <para> 要创建的世界大小，1=小，2=中，3=大 </para> 
    /// </summary>
    public string AutoCreate { get; set; } = "1";

    /// <summary>
    /// 服务器的端口号
    /// </summary>
    public string Port { get; set; } = "7777";
    
    /// <summary>
    /// 服务器最大玩家数量
    /// </summary>
    public string MaxPlayers { get; set; } = "16";
    
    /// <summary>
    /// 服务器的密码
    /// </summary>
    public string Password { get; set; } = string.Empty;
    
    /// <summary>
    /// 服务器监听的IP地址
    /// </summary>
    public string IP { get; set; } = "0.0.0.0";

    /// <summary>
    /// 新世界的种子
    /// </summary>
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Seed { get; set; }

    /// <summary>
    /// 世界邪恶 默认随机
    /// </summary>
    public string? Evil { get; set; }

    /// <summary>
    /// 指定的世界文件路径
    /// </summary>
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? World { get; set; }

    /// <summary>
    /// 自动创建时，指定的世界名称
    /// </summary>
    public string WorldName { get; set; } = "新的世界";

    /// <summary>
    /// <para> 将此类型的值全部转换为命令行参数格式 </para> 
    /// <para> 当值为string.Empty / null时，将忽略 </para>
    /// </summary>
    [JsonIgnore]
    public List<string> Configs
    {
        get
        {
            List<string> parmsValue = [];
            foreach (var info in PropertyInfos) {
                var value = info.GetValue(this);
                if (!(value != null && (string)value != string.Empty)) { //值为null 并且值为 string.Empty
                    continue;
                }
                var configName = info.Name.ToLower();
                parmsValue.Add($"-{configName} {value}"); //-port 7777
            }

            return parmsValue;
        }
    }

    /// <summary>
    /// 将字符串解析为ConfigModel, 一行就是一个配置项目
    /// <code>
    /// 世界名称=新世界
    /// 端口=7777
    /// </code>
    /// </summary>
    public static ConfigOptions Parse(string strValue)
    {
        var nullConfigModel = new ConfigOptions();
        var propMap = PropMapReverse;

        strValue
            .Replace("\r\n", "\n")
            .Split('\n')
            .Where(line => line.Split('=').Length == 2)
            .Select(line => {
                string[] configValue = line.Split('=');
                return new KeyValuePair<string, string>(configValue[0], configValue[1]);
            })
            .ToList()
            .ForEach(kv => {
                var configNameCn = kv.Key;
                var configValue = kv.Value;
                if (!propMap.TryGetValue(configNameCn, out string? configNameEn)) {
                    return;
                }

                var propInfo = PropertyInfos.FirstOrDefault(p => p.Name == configNameEn);
                if (propInfo == null) {
                    return;
                }

                propInfo.SetValue(nullConfigModel, configValue);
            })
            ;

        return nullConfigModel;
    }
}