---
title: "C#Web开发"
date: 2024-12-26T16:33:00+08:00
draft: false
tags: ["C#","Web"]
---

# C# Web开发

## 1 接收WebSocket消息

```csharp
//创建WebSocket客户端
ClientWebSocket _clientWebSocket = new ClientWebSocket();

//创建目标Uri
Uri uri = new Uri("ws://127.0.0.1:7777");

//建立连接
//对于CancellationToken可以自己New一个
await _clientWebSocket.ConnectAsync(uri,CancellationToken.None);

//如果连接成功 _clientWebSocket的 State就不等于None 做操作前可以判断一下

//使用_clientWebSocket接收消息
//这个字节数组用来存储接收到的数据
byte[] bytes = new byte[1024 * 10];
//接收数据不是直接到位的 不同数据需要隔开所以需要包装一下
ArraySegment<byte> data = new ArraySegment<byte>(bytes);

//要一直接收
while(true)
{
	//接收
	WebSocketReceiveResult result = await 		_clientWebSocket.ReceiveAsync(data,CancellationToken.None);
	
	//使用 EndOfMessage属性可以判断是否完整接收
    if(result.EndOfMessage)
    {
        Console.WriteLine(Endoding.UTF-8.GetString(bytes,data.Offset,data.Count));
        break;
    }
}

//关闭连接
_clientWebSocket.CloseAsync(WebSocketCloseStatus.EndpointUnavailable, "hand" ,CancellationToken.None);
```



>### 我的一个例子


```cs
using System.Net.WebSockets;

namespace NapCatSprcit.WebSocketConnection
{
    /// <summary>
    /// 建立连接
    /// </summary>
    public class Connection
    {
        public string Uri = string.Empty;
        /// <summary>
        /// ws的 状态标识符
        /// </summary>
        public static CancellationToken WebSocketCT { get; } = new CancellationToken();
        
        private ClientWebSocket _clientWebSocket = new ClientWebSocket();

        public string HttpUri = string.Empty;

        public Connection(string uri)
        {
            Uri = uri;
            InitionWebSocket();
        }

        

        public ClientWebSocket PublicConnection 
        {
            get
            {
                if(_clientWebSocket.State != WebSocketState.None)
                {
                    return _clientWebSocket;
                }
                else if(_clientWebSocket.State == WebSocketState.None)
                {
                    InitionWebSocket();
                }
                return _clientWebSocket;
            } 
            private set => _clientWebSocket = value;
        }
        public WebSocketState State
        {
            get => getState();
        }

        /// <summary>
        /// 初始化连接
        /// </summary>
        private async void InitionWebSocket()
        {
            //Uri不正确
            if(string.IsNullOrEmpty(Uri))
            {
                return;
            }
            await _clientWebSocket.ConnectAsync(new Uri(Uri), WebSocketCT);
        }

        /// <summary>
        /// 关闭WebSocket连接
        /// </summary>
        public async void CloseWebSocket()
        {
            await PublicConnection.CloseAsync(WebSocketCloseStatus.EndpointUnavailable, "hand" ,WebSocketCT);
        }

        /// <summary>
        /// 返回连接状态
        /// </summary>
        private WebSocketState getState()
        {
            return PublicConnection.State;
        }
    }
}
```



```cs
using NapCatSprcit.WebSocketConnection;
using System.Net.WebSockets;
using System.Text;

namespace NapCatSprcit.MessagesService
{
    public partial class Messages
    {
        public Messages(Connection cws) 
        {
            _connection = cws;
            _clientWebSocket = _connection.PublicConnection;
        }

        /// <summary>
        /// 接收并返回数据
        /// </summary>
        /// <returns></returns>
        public async IAsyncEnumerable<string> ReceiveMes()
        {
            byte[] dataByte = new byte[1024 * 10];
            ArraySegment<byte> data = new ArraySegment<byte>(dataByte);
            while (true)
            {
                if(_connection.State == WebSocketState.None)
                {
                    yield break;
                }

                WebSocketReceiveResult result = await _clientWebSocket.ReceiveAsync(data, WebSocketConnection.Connection.WebSocketCT);
                if (result.EndOfMessage)
                {
                    yield return Encoding.UTF8.GetString(dataByte, data.Offset, data.Count);
                }
            }
        }
    }
}

```



## 2 发送POST请求

1. 要求请求头为 application/json

   

```cs
//创建HttpClient连接 加上using 以自动释放
using HttpClient client = new HttpClient();

//构建消息体 Hello 一般为Json格式的请求
StringContent sc = new StringContent("Hello",Encoding.UTF-8,"application/json");

//发送请求
//会返回结果
HttpResponseMessage HRM = await client.PostAsync("http://127.0.0.1", sc);
//响应内容使用 Content
await HRM.Content.ReadAsStringAsync();
```



> ### 我的例子

```cs
/// <summary>
/// 发送Post请求 并返回回应消息
/// <para></para>
/// </summary>
/// <param name="user_id"> 目标id </param>
/// <param name="text"> 要发的内容 </param>
/// <returns></returns>
public async Task<string> SendPostMessagesAsync(string httpUri, string msg, Encoding? enc ,string body = "application/json")
{
    Encoding encoding = enc ?? Encoding.UTF8;
    using HttpClient client = new HttpClient();

    //构建消息
    StringContent sc = new StringContent(msg, encoding, body);

    //发送并接收
    HttpResponseMessage HRM = await client.PostAsync(httpUri, sc);

    return await HRM.Content.ReadAsStringAsync();
}
```

