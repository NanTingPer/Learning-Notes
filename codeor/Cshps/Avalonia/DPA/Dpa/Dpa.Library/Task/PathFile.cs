namespace Dpa.Library.Task;

public class PathFile
{
    private static string getPath()
    {
        return Path.Combine(System.Environment.CurrentDirectory,"Dpa");
    }

    /// <summary>
    /// 创建并返回用户应用目录下的这个文件路径
    /// </summary>
    /// <param name="fileName"> 要创建的文件 如果已经存在 不创建 </param>
    /// <returns></returns>
    public static string GetFilePath(string fileName)
    {
        if (!Directory.Exists(getPath()))
        {
            Directory.CreateDirectory(getPath());
        }

        // if (!File.Exists(Path.Combine(getPath(), fileName)))
        // {
        //     File.Create(Path.Combine(getPath(), fileName)).Close();
        // }
        
        return Path.Combine(getPath(), fileName);
    }
}