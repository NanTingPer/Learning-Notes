---
title: "acme.sh的基础使用"
date: 2026-05-11T12:45:00+08:00
draft: false
tags: ["Linux"]
---

## 下载

```sh
git clone https://github.com/acmesh-official/acme.sh.git
```

## 安装

```sh
cd ./acme.sh
bash acme.sh
./acme.sh --install -m youremail@xx.com
```

## 从nginx申请

这里有个坑，你的这个nginx站点必须能够供80端口访问。需要一条配置`server_name`为申请的域名，同时此服务能正常访问。

指定配置不存在使用 `nginx_conf` 配置，而是`--nginx confPath`。

```sh
acme.sh --issue -d yourdns --nginx your_Nginx_conf_path --keylength 2048
```

## 安装证书

> 部署路径需要精确到文件名

```sh
acme.sh --install-cert -d yourdns \
--key-file       部署路径  \
--fullchain-file 部署路径 \
--reloadcmd     "nginx -s reload"
```

例:

```sh
acme.sh --install-cert -d www.nantingya.top \
--key-file       ssl/www.nantingya.top.key  \
--fullchain-file ssl/www.nantingya.top.pem \
--reloadcmd     "nginx -s reload"
```



## 其他

申请完成后的内容，包括配置文件，会被保存在`~/.acme.sh`目录下。