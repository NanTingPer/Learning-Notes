# MCP连接笔记

我是作为Server的，但是目标给了我Socket端点，所以我是以服务器的身份去链接他。
我一连上，他就给我发消息了

```json
{
    "id": 0,
    "jsonrpc": "2.0",
    "method": "initialize",
    "params": {
        "protocolVersion": "2024-11-05",
        "capabilities": {
            "sampling": {},
            "roots": {
                "listChanged": false
            }
        },
        "clientInfo": {
            "name": "xz-mcp-broker",
            "version": "0.0.1"
        }
    }
}
```

这个消息是说，现在要进行初始化了，所以我要马上回复一个初始化的内容过去

```cs
var tools = new JsonObject()
{

};

var capabilities = new JsonObject()
{
    //{ "resources", resources },
    { "tools", tools }
};

var resources = new JsonObject()
{

};

var serverInfo = new JsonObject()
{
    { "name", "茶园管理服务" },
    { "version", "1.0.0" }
};

var resultj = new JsonObject()
{
    { "protocolVersion", initializeReceive?.Params.ProtocolVersion ?? "2024-11-05" },
    { "capabilities", capabilities },
    { "serverInfo", serverInfo }
};

var rootJson = new JsonObject()
{
    { "jsonrpc", initializeReceive?.Jsonrpc ?? "2.0" },
    { "id", initializeReceive?.Id ?? 0 },
    { "result", resultj }
};
#endregion
var initJsonBytes = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(rootJson));
await mcpSocket.SendAsync(initJsonBytes, WebSocketMessageType.Text, true, cancelToken);
```

发完还不行，还要马上发一个`Notifications`

```cs
/// <summary>
/// 链接后立即发送
/// </summary>
public class NotificationModel
{
    /// <summary>
    /// rpc版本
    /// </summary>
    public string Jsonrpc { get; set; } = "2.0";
    /// <summary>
    /// 方法
    /// </summary>
    public string Method { get; set; } = "notifications/initialized";
    private static JsonSerializerOptions jsonSerializerOptions = new JsonSerializerOptions() { PropertyNameCaseInsensitive = true };
    /// <summary>
    /// 使用<see cref="JsonSerializer"/>
    /// </summary>
    /// <param name="not"></param>
    public static implicit operator string(NotificationModel not)
        => JsonSerializer.Serialize(not, jsonSerializerOptions);
}
var notifJsonBytes = Encoding.UTF8.GetBytes(new AIChats.MCPModel.NotificationModel());
await mcpSocket.SendAsync(notifJsonBytes, WebSocketMessageType.Text, true, cancelToken);
```



然后他就会发送一个找你要Tool列表的请求

```json
{"id":1,"jsonrpc":"2.0","method":"tools/list","params":{}}
```

我们发过去就行。格式文档有 https://mcp-docs.apifox.cn/6175699m0



然后就是一直发ping包，回应就好
```cs
收到消息: {"id":8,"jsonrpc":"2.0","method":"ping","params":{}}
```

```cs
var response = new JsonObject
{
    ["jsonrpc"] = "2.0",
    ["id"] = mp.Id,
    ["result"] = new JsonObject()
};
await Send(response.ToJsonString(), cancelToken);
```



今晚就做到了这，明天要做工具调用了。
```json
收到消息: {"id":7,"jsonrpc":"2.0","method":"tools/call","params":{"name":"Tools","arguments":{},"clientId":null,"serialNumber":null,"_meta":{"clientId":null,"serialNumber":null,"agentId":1251946}}}
```

