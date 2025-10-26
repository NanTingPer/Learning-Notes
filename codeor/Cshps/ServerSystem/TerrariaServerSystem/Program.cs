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