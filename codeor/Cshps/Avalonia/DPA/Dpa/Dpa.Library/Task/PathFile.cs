using System.IO;

namespace Dpa.Library.Task;

public class PathFile
{
    /// <summary>
    /// 删除全部文件
    /// </summary>
    public static void Del() => Directory.Delete(PathFile.getPath());
    
    /// <summary>
    /// 返回应用的文件存放的目录 会创建文件夹
    /// </summary>
    /// <returns> 目录 </returns>
    public static string getPath()
    {
        string url = Path.Combine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData), "Dpa");
        if (Directory.Exists(url) == false)
        {
            Directory.CreateDirectory(url);
        }
        return url;
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