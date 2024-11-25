using System.Text.Json.Serialization;

namespace Dpa.Library.Models;

public class FinalJsonData
{
    /// <summary>
    /// 下面的Data
    /// </summary>
    [JsonPropertyName("data")]
    public Data Data { get; set; }
}

public class Data
{
    /// <summary>
    /// 第一句
    /// </summary>
    [JsonPropertyName("content")]
    public string Content { get; set; }
    
    public Origin origin { get; set; }
}

public class Origin
{
    /// <summary>
    /// 标题
    /// </summary>
    [JsonPropertyName("title")]
    public string Title { get; set; }
    
    /// <summary>
    /// 朝代
    /// </summary>
    [JsonPropertyName("dynasty")]
    public string Dynasty { get; set; }
    
    /// <summary>
    /// 作者
    /// </summary>
    [JsonPropertyName("author")]
    public string Author { get; set; }
    
    /// <summary>
    /// 正文
    /// </summary>
    [JsonPropertyName("content")]
    public string[] Content { get; set; }
}