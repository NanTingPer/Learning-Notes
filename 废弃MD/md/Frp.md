---
title: "Frp"
date: 2024-12-28T10:06:00+08:00
draft: false
tags: ["Frp"]
---

[Frp](https://github.com/fatedier/frp)



### 服务端 : 

```sh
#在 frps.toml内添加
#xxxx是开放映射的端口
bindPort = xxxxx
```



### 客户端:

```sh
#服务端IP
serverAddr = "xxx.xxx.xxx.xxx"
#服务端运行时会爆出一个端口
serverPort = 7000

[[proxies]]
#名字
name = "test-tcp"
#类型
type = "tcp"
#映射本地的哪个IP
localIP = "127.0.0.1"
#映射本地的哪个端口
localPort = 7777
#映射到目标的哪个端口
remotePort = 7777
```





### 网络管理  开放端口

```sh
#开放
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT

#删除规则
sudo iptables -A INPUT -p tcp --dport 25511 -j DROP

#阻止
sudo iptables -A INPUT -p tcp --dport 80 -j REJECT

#保存
sudo iptables-save > /etc/iptables/rules.v4

#查看
sudo iptables -L -v -n
```



