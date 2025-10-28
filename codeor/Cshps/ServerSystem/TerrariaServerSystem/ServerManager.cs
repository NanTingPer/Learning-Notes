namespace TerrariaServerSystem;

public class ServerManager
{
    private readonly static CancellationTokenSource s_sourceToken = new();
    static ServerManager()
    {
        AppDomain.CurrentDomain.ProcessExit += (_, _) => ExitStop();
        Console.CancelKeyPress += (_, _) => ExitStop(); 
    }

    private static void ExitStop()
    {
        foreach (var item in s_serverManagers) {
            foreach (var item1 in item._servers.Values) {
                item1.Stop().Wait(3000);
            }
        }
        Environment.Exit(0);
    }

    private readonly static List<ServerManager> s_serverManagers = [];
    private readonly static object s_lock = new();
    /// <summary>
    /// 进程全部的ServerManager实例
    /// </summary>
    //private static IEnumerable<ServerManager> ServerManagers { get => s_serverManagers.AsEnumerable(); }

    private readonly object _appendlock = new();
    private long _serverCount = 0L; //此ServerManager管理了多少服务器实例，用于生成唯一ID
    private readonly Dictionary<long, Server> _servers = [];

    /// <summary>
    /// 获取此服务器管理器的所管理的全部服务器实例
    /// </summary>
    public IEnumerable<Server> Servers { get => _servers.Select(f => f.Value); }

    public ServerManager()
    {
        lock (s_lock) {
            try {
                s_serverManagers.Add(this);
            } finally { }
        }
    }

    /// <summary>
    /// 添加一个服务器实例到此管理器中，并返回其唯一ID
    /// </summary>
    public long Append(Server server, string name)
    {
        var count = 0L;
        lock (_appendlock) {
            _serverCount++;
            count = _serverCount;
        }

        var managedServer = server;
        managedServer.Id = count;
        managedServer.Name = name;
        _servers.Add(count, managedServer);
        return count;
    }

    /// <summary>
    /// 添加一个服务器实例到此管理器中，并返回其唯一ID
    /// </summary>
    public long Append(Server server) => Append(server, "未命名服务器");
}
