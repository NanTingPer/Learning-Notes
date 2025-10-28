using TerrariaServerSystem;

namespace TerrariaServerSystemTestRun;

public class Program
{
    static async Task Main(string[] args)
    {
        var co = new ConfigOptions()
        {
            AutoCreate = "1",
            WorldName = "testworld",
        };

        co.World = $@"C:\TShock-5.2.4\Worlds\{co.WorldName}.wld";

        var server = new Server(@"C:\TShock-5.2.4\TShock.Installer.exe", co);
        server.ReadOutputEvent += CWLoging;
        await server.Run();

        while (true) {

        }
    }

    private static void CWLoging(char obj)
    {
        Console.Write(obj);
    }
}