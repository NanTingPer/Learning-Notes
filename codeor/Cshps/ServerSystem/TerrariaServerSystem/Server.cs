using System.Diagnostics;
using System.Text;
using TerrariaServerSystem.Interface;

namespace TerrariaServerSystem;

public class Server : IServer
{
    /// <summary>
    /// 读取进程的标准输出时触发
    /// </summary>
    public event Action<char>? ReadOutputEvent; 

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
        var configs = config.Configs;
        processStartInfo.Arguments = string.Join(' ', configs);
        processStartInfo.FileName = serverFileName;
        Process = Process.Start(processStartInfo)!;
        ProcessId = Process.Id;
    }

    public async Task Run()
    {
        await Task.Delay(1000);

        //进程不为Null 并且 程序未退出
        while (Process != null && !Process.HasExited) {
            int readChar = Process.StandardOutput.Read();
            if (readChar == -1)
                continue;

            ReadOutputEvent?.Invoke((char)readChar);
        }
    }
}
