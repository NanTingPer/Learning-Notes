using System.Text.Json.Serialization;

namespace Dpa.Library.Models;

public class ToDayPoetry
{
    /// <summary>
    /// 名字
    /// </summary>
    public string Name { get; set; } = string.Empty;
    
    /// <summary>
    /// 作者
    /// </summary>
    public string Author { get; set; } = string.Empty;
    
    /// <summary>
    /// 来源
    /// </summary>
    public string Source { get; set; } = string.Empty;
    
    /// <summary>
    /// 朝代
    /// </summary>
    public string Dynasty { get; set; } = string.Empty;

    /// <summary>
    /// 正文
    /// </summary>
    public string Content { get; set; } = string.Empty;
    
    /// <summary>
    /// 第一句
    /// </summary>
    public string Snippet { get; set; } = string.Empty;
}