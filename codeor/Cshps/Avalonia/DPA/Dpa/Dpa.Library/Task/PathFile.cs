namespace Dpa.Library.Task;

public class PathFile
{
    /// <summary>
    /// 返回应用的文件存放的目录
    /// </summary>
    /// <returns> 目录 </returns>
    public static string getPath()
    {
        return Path.Combine(System.Environment.CurrentDirectory,"Dpa");
    }

    /// <summary>
    /// 返回用户应用目录下的这个文件路径 不创建文件
    /// </summary>
    /// <param name="fileName"> 要返回的文件  </param>
    /// <returns></returns>
    public static string GetFilePath(string fileName)
    {
        if (!Directory.Exists(getPath()))
        {
            Directory.CreateDirectory(getPath());
        }
        
        return Path.Combine(getPath(), fileName);
    }

    /// <summary>
    /// 返回用户应用目录下的这个文件路径，会创建目录和文件
    /// </summary>
    /// <param name="fileName"> 要返回的文件 </param>
    /// <returns></returns>
    public static string GetFileOrCreate(string fileName)
    {
        if (!Directory.Exists(getPath()))
        {
            Directory.CreateDirectory(getPath());
        }
        
        if (!File.Exists(Path.Combine(getPath(), fileName)))
        {
            File.Create(Path.Combine(getPath(), fileName)).Close();
        }
        
        return Path.Combine(getPath(), fileName);
    }
}