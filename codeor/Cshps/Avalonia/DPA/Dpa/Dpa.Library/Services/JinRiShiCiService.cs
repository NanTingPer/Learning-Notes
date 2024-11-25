using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using Dpa.Library.ConfigFile;
using Dpa.Library.Models;
using Dpa.Library.Task;

namespace Dpa.Library.Services;

public class JinRiShiCiService : IToDayPoetryStyService
{
    private readonly IConfig _config;
    private readonly IAlertService _alertService;
    private readonly IPoetryStyService _poetryStyService;
    private string Server = "服务器错误";
    private string JsonError = "Json解析错误";
    public string Source_Web = "网络服务器";
    public string Source_DBSQL = "本地数据库";
    private string _ToKen;
    private Func<Task<string>> _loadToKen;
    public JinRiShiCiService(IConfig config,IAlertService alertService,IPoetryStyService poetryStyService)
    {
        _loadToKen = () => GetTokenAsync(JinRiShiCi_Config.GetToKenUrl);
        _alertService = alertService;
        _config = config;
        _poetryStyService = poetryStyService;
        ToKen = GetToken();
    }

    public string ToKen
    {
        get => _ToKen;
        private set => _ToKen = value;
    }

    /// <summary>
    /// 从本地获取ToKen
    /// </summary>
    /// <returns> 如果没有 返回null </returns>
    public string GetToken()
    {
        string e = _config.Get(JinRiShiCi_Config.ToKenConfgKey);
        if (string.IsNullOrEmpty(e))
        {
            return null;
        }
        ToKen = e;
        return e;
    }
    
    /// <summary>
    /// 获取诗 从服务器
    /// </summary>
    /// <returns> 得到的诗 </returns>
    public async Task<ToDayPoetry> GetToDayPoetryAsync()
    {
        if (string.IsNullOrEmpty(ToKen)) ToKen = await _loadToKen();
        if (string.IsNullOrEmpty(ToKen)) return await RandomGetPortryAsync();
        using HttpClient httpClient = new HttpClient();
        HttpResponseMessage Message;
        httpClient.DefaultRequestHeaders.Add("X-User-Token",ToKen);
        
        //包裹错误信息 => 404或请求超时
        try
        {
            Message = await httpClient.GetAsync(JinRiShiCi_Config.GetToDayPoetryUrl);
            Message.EnsureSuccessStatusCode();
        }
        catch (Exception e)
        {
            await _alertService.AlertAsync(Server, e.Message);
            return await RandomGetPortryAsync();
        }

        string json = await Message.Content.ReadAsStringAsync();
        
        //捕获错误 => Json格式
        FinalJsonData Poetry;
        try
        {
            Poetry = JsonSerializer.Deserialize<FinalJsonData>(json) ?? throw new Exception(JsonError);
        }
        catch (Exception e)
        {
            await _alertService.AlertAsync(Server, e.Message);
            return await RandomGetPortryAsync();
        }
        
        //转为字符串
        StringBuilder str = new StringBuilder();
        try
        {
            foreach (string value in Poetry.Data?.origin?.Content ?? throw new Exception(JsonError))
            {
                str.Append(value);
            }
        }
        catch (Exception e)
        {
            await _alertService.AlertAsync("Error", e.Message);
            return await RandomGetPortryAsync();
        }
        
        //捕获错误 Json 空解析
        ToDayPoetry toDayPoetry;
        try
        {
            toDayPoetry =  new ToDayPoetry()
            {
                Author = Poetry.Data?.origin?.Author ?? throw new Exception(JsonError),
                Content = str.ToString(),
                Dynasty = Poetry.Data?.origin?.Dynasty ?? throw new Exception(JsonError), 
                Name = Poetry.Data?.origin?.Title ?? throw new Exception(JsonError),
                Source = Source_Web
            };
        }
        catch (Exception e)
        {
            await _alertService.AlertAsync("Error", e.Message);
            return await RandomGetPortryAsync();
        }

        return toDayPoetry;
    }

    /// <summary>
    /// 随机从数据库获取一首 
    /// </summary>
    /// <returns></returns>
    public async Task<ToDayPoetry> RandomGetPortryAsync()
    {
        Random rom = new Random();
        int next = rom.Next(30);
        var list = await _poetryStyService.GetPoetryAsync(f => true, next, 1);
        Poetry po = list[0];
        // Poetry po = await _poetrySty.GetPoetryAsync("10001");
        return new ToDayPoetry()
        {
            Author = po.Author,
            Content = po.Content,
            Dynasty = po.Dynasty,
            Name = po.Name,
            Snippet = po.Content.Split("。")[0],
            Source = Source_DBSQL
        };
    }
    
    
    /// <summary>
    /// 获取今日诗词的Token 会访问网站
    /// </summary>
    /// <param name="url"> 从url获取Token </param>
    public async Task<String> GetTokenAsync(string url)
    {
        using HttpClient httpClient = new HttpClient() ;
        try
        {
            HttpResponseMessage Message = await httpClient.GetAsync(url);
            //404等抛出异常
            Message.EnsureSuccessStatusCode();
            
            //获取数据返回的原始Json
            string ToKenJson = await Message.Content.ReadAsStringAsync();
            
            //将Json对象反序列化为ToKenJson对象
            TokenJson ToKen = JsonSerializer.Deserialize<TokenJson>(ToKenJson);
            
            //将ToKen保存到本地
            _config.Set(JinRiShiCi_Config.ToKenConfgKey,ToKen.data);
            
            //将Token保存到内存
            this.ToKen = ToKen.data;
            
            //如果ToKen是空的 报错
            if (string.IsNullOrEmpty(this.ToKen)) throw new Exception(ErrorMessage.HttpRequestFileError);
            return ToKen.data;
        }
        catch (Exception e)
        {
            await _alertService.AlertAsync("今日诗词服务器", e.Message);
            return null;
        }
    }
    
}
public class JinRiShiCi_Config
{
    public static readonly string GetToKenUrl = "https://v2.jinrishici.com/token";
    public static readonly string GetToDayPoetryUrl = "https://v2.jinrishici.com/sentence";
    public const string ToKenConfgKey = "JinRiToKen";
}
public class TokenJson
{
    [JsonPropertyName("data")]
    public string data{get; set; }
}