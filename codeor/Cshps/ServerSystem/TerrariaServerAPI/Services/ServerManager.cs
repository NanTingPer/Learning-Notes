using System;
using System.Threading.Tasks;
using TerrariaServerAPI.Models;
using TerrariaServerSystem;

namespace TerrariaServerAPI.Services;

public class ServerManager
{
    private readonly Dictionary<int, TerrariaServer> _servers = [];
    private readonly Dictionary<int, TerrariaServerInfo> _infos = [];
    public List<TerrariaServerInfoDto> Servers => [.. _infos.Select(f => TerrariaServerInfoDto.Create(f.Key, f.Value))];
    private readonly SemaphoreSlim _editSlim = new SemaphoreSlim(1, 1);

    private async void StopEventMethod(TerrariaServer t)
    {
        await IfThrowExple(() => {
            int key = -1;
            foreach (var item in _servers) {
                if (item.Value.Equals(t)) {
                    key = item.Key;
                    break;
                }
            }
            _servers.Remove(key);
            _infos.Remove(key);
        });
        
    }

    /// <summary>
    /// 运行此info的服务器，并返回唯一标识
    /// </summary>
    public async Task<int> AddServer(TerrariaServerInfo info)
    {
        var newKey = -1;
        await IfThrowExple(() => {
            TerrariaServer server = CreateTerrariaServer(info);
            if (_servers.Count != 0) {
                newKey = _servers.Keys.Max() + 1;
            } else {
                newKey = 0;
            }
            WhenThrowTask(newKey, server.RunServer());
            _servers.Add(newKey, server);
            _infos.Add(newKey, info);
        }, async () => {
            await StopServer(newKey);
        });
        return newKey;
    }

    /// <summary>
    /// 如果执行<paramref name="action"/>过程中发生错误，那么释放信号量并执行<paramref name="catchAction"/>
    /// </summary>
    private async Task IfThrowExple(Action action, Action? catchAction = null)
    {
        bool isExcep = false;
        Exception? exception = null;
        await _editSlim.WaitAsync();
        try {
            action.Invoke();
        } catch (Exception excep) {
            isExcep = true;
            exception = excep;
            _editSlim.Release(); //? 避免catchaction执行过长 提前释放，如果catchaction有获取锁也可能正常获取
            try {
                catchAction?.Invoke();
            } catch { }
        }
        //_editSlim.Release();
        if (isExcep) {
            throw exception!;
        } else {                    //?     正常执行完成后的释放
            _editSlim.Release();    //?     正常执行完成后的释放
        }                           //?     正常执行完成后的释放
    }


    private TerrariaServer CreateTerrariaServer(TerrariaServerInfo info)
    {
        var server = new TerrariaServer(info);
        server.StopEvent += StopEventMethod;
        return server;
    }

    private async Task Cover(int key, TerrariaServerInfo info)
    {
        await IfThrowExple(() => {
            TerrariaServer server = CreateTerrariaServer(info);
            WhenThrowTask(key, server.RunServer());
            _servers[key] = server;
            _infos[key] = info;
        }, async () => {
            await StopServer(key);
        });
    }

    private async void WhenThrowTask(int key, Task task)
    {
        try {
            await task;
        } catch {
            await StopServer(key);
        }
    }

    /// <summary>
    /// 停止指定标识的服务器
    /// </summary>
    public async Task StopServer(int identity)
    {
        await IfThrowExple(async () => {
            if (_servers.TryGetValue(identity, out var server)) {
                await server.Stop();
                _servers.Remove(identity);
            }
        }, () => {
            _servers.Remove(identity);
        });
    }

    public async IAsyncEnumerable<Task> StopAll()
    {
        await Task.CompletedTask;
        foreach (var item in _servers.Values) {
            yield return Task.Run(() => item.Stop());
        }
        _servers.Clear();
    }

    /// <summary>
    /// 更新并启动
    /// </summary>
    public async Task UpdateInfo(int identity, TerrariaServerInfo newInfo)
    {
        await IfThrowExple(async () => {
            if (_servers.TryGetValue(identity, out var server)) {
                await server.Stop();
                _servers.Remove(identity);
                _infos.Remove(identity);
            }
        });

        await Cover(identity, newInfo);
    }

    /// <summary>
    /// 重启此标识代表的服务器
    /// </summary>
    public async Task ReStart(int identity)
    {
        if(_servers.TryGetValue(identity, out var server)) {
            await server.ReStart();
        }
    }
}

public class ExitServer(ServerManager server) : BackgroundService
{
    private readonly ServerManager _manager = server;
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (!stoppingToken.IsCancellationRequested) {
            await Task.Delay(10000, stoppingToken);
        }
    }

    public override async Task StopAsync(CancellationToken cancellationToken)
    {
        List<Task> tasks = [];
        await foreach (var item in _manager.StopAll()) {
            tasks.Add(item);
        }
        ;

        Task.WaitAll(tasks.ToArray(), cancellationToken);
    }
}
