using System.Diagnostics;
using System.Text;

namespace TerrariaServerSystem;

public class Program
{
    private static TerrariaServerManager serverManager = new ();
    static async Task Main(string[] args)
    {
        var serverInfo = new TerrariaServerInfo()
        {
            Name = "测试",
            Passwd = "",
            Port = 7777,
            WorldName = "启动！"
        };
        var server = new TerrariaServer(serverInfo);
        await serverManager.Run(server);
    }
}

public class WorldText(string text)
{
    public string Text { get; set; } = text;
    public (int index, string name)? IndexName
    {
        get
        {
            var indexText = Text[0..1].Trim();
            if(!int.TryParse(indexText, out int index)) {
                return null;
            }

            var worldName = Text[2..].Trim();
            return (index, worldName);
        }
    }
}

public class TerrariaServer
{
    private enum WriteStatus
    {
        ChoiceWorld
    }


    /// <summary>
    /// 传入ProcessId
    /// </summary>
    public event Action<int>? Start;

    #region Private Field
    private bool isRun = false;
    private WriteStatus status = WriteStatus.ChoiceWorld;
    private readonly static ProcessStartInfo startInfo = new ProcessStartInfo()
    {
        UseShellExecute = false, //不使用命令行执行
        CreateNoWindow = true,
        RedirectStandardOutput = true,
        RedirectStandardInput = true,
        RedirectStandardError = true,
        StandardErrorEncoding = Encoding.UTF8,
        StandardInputEncoding = Encoding.UTF8,
        StandardOutputEncoding = Encoding.UTF8,
        FileName = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Terraria\\TerrariaServer.exe"
    };

    public Process? ServerProcess { get; private set; }
    private List<string> logOutput = [];
    #endregion

    public TerrariaServerInfo Info { get; init; }
    public TerrariaServer(TerrariaServerInfo info)
    {
        Info = info;
    }

    public int ProcessId { get; private set; }

    public DateTime readTime = DateTime.Now;
    private CancellationTokenSource taskToken = new CancellationTokenSource();
    public Task RunServer()
    {
        if (isRun)
            return Task.CompletedTask;

        isRun = true;
        ServerProcess = Process.Start(startInfo)!;
        ServerProcess.OutputDataReceived += ReadProcessOutput;
        ProcessId = ServerProcess.Id;
        Start?.Invoke(ProcessId);

        Task.Factory.StartNew(() => {
            while (!ServerProcess.HasExited) { 
                if((DateTime.Now - readTime).TotalSeconds > 3) {
                    WriteLine();
                    readTime = DateTime.Now;
                }
            }
        }, taskToken.Token);
        //ServerProcess.OutputDataReceived += WriteLine;

        ServerProcess.StandardInput.AutoFlush = true;
        return Task.Factory.StartNew(() => {
            while (!ServerProcess.HasExited) {
                var lineText = ServerProcess.StandardOutput.ReadLine();
                readTime = DateTime.Now;
                Console.WriteLine(lineText);
                if (string.IsNullOrWhiteSpace(lineText)) {
                    continue;
                }
                logOutput.Add(lineText);
            }
        }, taskToken.Token);
    }

    private void ReadProcessOutput(object sender, DataReceivedEventArgs e)
    {
        _ = e.Data;
    }

    private void WriteLine()
    {
        switch (status) {
            case WriteStatus.ChoiceWorld:
                ChoiceWorld();
                break;
        }
    }

    private void ChoiceWorld()
    {
        //计算是否有世界，没有就返回空
        var startIndex = logOutput.FindIndex(f => f.StartsWith("Terraria Server"));
        var lastIndex = logOutput.FindIndex(f => f.StartsWith("d <number>"));
        if (startIndex + 2 == lastIndex) {
            throw new WorldListIsNullException();
        }

        //计算最后一个世界的索引
        var oneWorld = logOutput[lastIndex - 2];
        (int worldCount, string _) s = new WorldText(oneWorld).IndexName 
            ?? throw new WorldListIsNullException();

        var worldCount = s.worldCount;

        Dictionary<int, string> worldList = [];
        //根据最后一个世界的索引 遍历获取世界列表
        for (int i = 0; i < worldCount; i++) {
            var worldText = logOutput[lastIndex - 2 - i];
            (int worldIndex, string worldName)? @in = new WorldText(worldText).IndexName;
            if (@in == null) continue;
            worldList.Add(@in.Value.worldIndex, @in.Value.worldName);
        }

        if (!worldList.ContainsValue(Info.WorldName)) 
            throw new NotWorldException($"未找到给定世界: {Info.WorldName}");

        //选择世界
        var index = worldList.FirstOrDefault(kv => kv.Value.Equals(Info.WorldName)).Key;
        ServerProcess!.StandardInput.Write(index);
        Console.WriteLine(index);
    }

    public void Stop()
    {
        ServerProcess?.Kill();
        ServerProcess?.Dispose();
        taskToken.Cancel();
    }
}

public class WorldListIsNullException : Exception
{
    public override string Message => "世界列表为空";
}

public class NotWorldException : Exception
{
    private string mesg { get; init; }
    public NotWorldException(string message)
    {
        mesg = message;
    }

    public override string Message => mesg;
}

public class TerrariaServerInfo
{
    public string Name { get; set; } = "未命名";
    public string WorldName { get; set; } = "";
    public int Port { get; set; } = 7777;
    public string Passwd { get; set; } = "";
}

public class TerrariaServerManager
{
    private List<TerrariaServer> Servers { get; init; } = [];
    private readonly List<int> processIds = [];

    public TerrariaServerManager()
    {
        AppDomain.CurrentDomain.ProcessExit += KillAll;
        Console.CancelKeyPress += Console_CancelKeyPress;
    }

    private void Console_CancelKeyPress(object? sender, ConsoleCancelEventArgs e)
    {
        foreach (var item in Servers) {
            try {
                item.Stop();
            } catch (Exception) { }
        }
        //processIds.ForEach(id => {
        //    try {
        //
        //        //var pro = Process.GetProcessById(id);
        //        //pro.Kill();
        //        //pro.WaitForExit(1000);
        //    } catch (Exception) { }
        //});
    }

    private void KillAll(object? sender, EventArgs e)
    {
        //processIds.ForEach(id => Process.GetProcessById(id).Kill());
    }

    public Task Run(TerrariaServer server)
    {
        Servers.Add(server);
        server.Start += GetProId;
        return server.RunServer();
    }

    private void GetProId(int obj)
    {
        processIds.Add(obj);
    }
}