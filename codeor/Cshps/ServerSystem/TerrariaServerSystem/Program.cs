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
        StandardInputEncoding = Encoding.Unicode, //需要Unicode 前面的问题 就是他！
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
        ProcessId = ServerProcess.Id;
        Start?.Invoke(ProcessId);

        ServerProcess.StandardInput.AutoFlush = true;
        return Task.Factory.StartNew(() => {
            StringBuilder line = new StringBuilder();
            string lineText = "";
            while (!ServerProcess.HasExited) {
                char[] @char = new char[1];
                ServerProcess.StandardOutput.Read(@char, 0, 1);
                if (@char[0] != Environment.NewLine[0]) { //不是换行符
                    line.Append(@char);
                    lineText = line.ToString();
                    if (lineText.StartsWith('\n') || lineText.StartsWith('\r')) {
                        lineText = lineText[1..];
                    }
                } else { //是换行符
                    if (!string.IsNullOrWhiteSpace(lineText)) {
                        logOutput.Add(lineText);
                    }
                    line.Clear();
                }

                Console.Write(@char);
                if (lineText.Equals("Choose World: "))
                    /*preRead = () => */WriteLine();
            }
        }, taskToken.Token);
    }

    private void WriteLine()
    {
        switch (status) {
            case WriteStatus.ChoiceWorld:
                ChoiceWorld();
                break;
        }
    }

    private async void ChoiceWorld()
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
        await Task.Delay(1000);
        ServerProcess!.StandardInput.WriteLine(index);

        await Task.Delay(500);
        //设置人数
        ServerProcess!.StandardInput.WriteLine(); //默认16

        await Task.Delay(500);
        //设置端口
        ServerProcess.StandardInput.WriteLine(Info.Port);

        await Task.Delay(500);
        //自动端口转发
        ServerProcess.StandardInput.WriteLine();

        await Task.Delay(500);
        //服务器密码
        ServerProcess.StandardInput.WriteLine(Info.Passwd);
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

    public TerrariaServerManager() {
    }

    private void Console_CancelKeyPress()
    {
        foreach (var item in Servers) {
            try {
                item.Stop();
            } catch (Exception) { }
        }
    }

    private void KillAll(object? sender, EventArgs e)
    {
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