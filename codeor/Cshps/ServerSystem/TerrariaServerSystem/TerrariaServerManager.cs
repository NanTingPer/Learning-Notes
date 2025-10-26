namespace TerrariaServerSystem;

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
        server.StartEvent += GetProId;
        return server.RunServer();
    }

    private void GetProId(int obj)
    {
        processIds.Add(obj);
    }
}