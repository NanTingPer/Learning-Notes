using System.Diagnostics;
using System.Text;

namespace TerrariaServerSystem;

public abstract class Server
{
    /// <summary>
    /// 此服务器进程
    /// </summary>
    public Process Process { get; init; }

    /// <summary>
    /// 此服务器进程ID
    /// </summary>
    public int ProcessId { get; init; }

    public readonly ProcessStartInfo processStartInfo = new ProcessStartInfo()
    {
        UseShellExecute = false, //不使用命令行执行
        CreateNoWindow = true,
        RedirectStandardOutput = true,
        RedirectStandardInput = true,
        RedirectStandardError = true,
        StandardErrorEncoding = Encoding.UTF8,
        StandardInputEncoding = Encoding.Unicode, //需要Unicode 前面的问题 就是他！
        StandardOutputEncoding = Encoding.UTF8
    };

    public Server(string serverFileName, ConfigOptions config)
    {
        config.Configs.ForEach(processStartInfo.ArgumentList.Add);
        processStartInfo.FileName = serverFileName;
        Process = Process.Start(processStartInfo)!;
        ProcessId = Process.Id;
    }
}
