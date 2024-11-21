using System;
using System.IO;
using System.Runtime.Serialization;
using Dpa.Library.Task;

namespace Dpa.Library.ConfigFile;

public class Config : IConfig
{
    /// <summary>
    /// 写入配置数据
    /// </summary>
    /// <param name="key"> 配置名 </param>
    /// <param name="value"> 写入的值 </param>
    private void SetData(string key, string value)
    {
        string filePath = PathFile.GetFileOrCreate(key);
        File.WriteAllText(filePath,value);
    }

    /// <summary>
    /// 读取配置数据
    /// </summary>
    /// <param name="key"> 键 </param>
    /// <returns></returns>
    private String Get(string key) => File.ReadAllText(PathFile.GetFileOrCreate(key));

    public void Set(string key, string value)
    {
        SetData(key,value);
    }

    public string Get(string key, string value)
    {
        if(Get(key) == null) return value;
        return Get(key);
    }

    public void Set(string key, int value)
    {
        SetData(key,value.ToString());
    }

    public int Get(string key, int value)
    {
        if (Get(key).Equals(""))
        {
            SetData(key, value.ToString());
            return value;
        }
        return int.Parse(Get(key));
    }

    public void Set(string key, DateTime value)
    {
        SetData(key,value.ToString());
    }

    public DateTime Get(string key, DateTime value)
    {
        if (Get(key).Equals(""))
        {
            SetData(key, value.ToString());
            return value;
        }
        return Convert.ToDateTime(Get(key));
    }
}