namespace TerrariaServerSystem;

public class Program
{
    private static ServerManager serverManager = new ();
    static async Task Main(string[] args)
    {
        var serverInfo = new TerrariaServerInfo()
        {
            Name = "测试",
            Passwd = "",
            Port = 7777,
            WorldName = "启动！"
        };
        await serverManager.AddServer(serverInfo);
    }
}