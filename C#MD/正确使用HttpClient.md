正确使用HttpClient

​	`HttpClient`在释放时，真正释放的是底层的连接池，频繁释放将会导致连接池耗尽导致后面新的`HttpClient`无法继续进行通信。`SocketsHttpHandler`

​	如果要使用静态单例`HttpClient`建议手动创建`SocketsHttpHandler` ，然后设置循环时间。