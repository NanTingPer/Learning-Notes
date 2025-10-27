using TerrariaServerSystem;

namespace TerrariaServerSystemTestRun;

public class Program
{
    private static ServerManager serverManager = new ();
    static async Task Main(string[] args)
    {
        Environment.SetEnvironmentVariable("TSPATH", "C:\\Users\\23759\\Downloads\\TShock-5.2.4-for-Terraria-1.4.4.9-win-amd64-Release\\TShock.Server.exe");
        //var serverInfo = new TerrariaServerInfo()
        //{
        //    Name = "测试",
        //    Passwd = "",
        //    Port = 7777,
        //    WorldName = "启动！"
        //};
        //await serverManager.AddServer(serverInfo);
        var info = new WorldInfo()
        {
            WorldDifficulty = 3,
            WorldEvil = 1,
            WorldSize = 2,
            WroldName = "测试世界",
            WroldSeed = "65a4wf"
        };
        await TerrariaServer.CreateWorld().CreateWorld(info);
        //var ser = new TerrariaServer(new TerrariaServerInfo() { Name ="a", Passwd = string.Empty, Port = 7777, WorldName = "不孤独的世界" });
        //_ = ser.RunServer();
        while (true) {

        }
    }
}