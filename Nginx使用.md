# Nginx使用

## 安装编译依赖

```sh
sudo yum install gcc make
sudo yum install pcre-devel zlib-devel openssl-devel
```

## 克隆仓库

```sh
git clone https://github.com/nginx/nginx.git
```

一些默认的位置信息

> https://nginx.org/en/docs/configure.html

| 参数           | 说明                                    | 位置              |
| -------------- | --------------------------------------- | ----------------- |
| --prefix       | 默认路径使用的前缀                      | 自定义            |
| --sbin-path    | nginx可执行文件的名称<br>仅在安装时使用 | prefix/sbin/nginx |
| --modules-path | 安装动态模块的目录                      | prefix/modules    |



## 生成mark配置

> 使用pcre会在 /usr/include or /usr/lib64

```sh
#在nginx源码根目录下
./auto/configure --prefix=/opt/nginx --with-http_ssl_module
```

使用make安装

> 上面的执行完成后，会在源代码目录下生成 objs/Makefile，日志里显示为`creating objs/Makefile`

```sh
sudo make install # 在代码根目录下，ls / ll能看到objs文件夹
```

## 测试

```sh
`prefix`/sbin/nginx
curl localhost
```

```html
[root@nanting nginx-source]# curl localhost
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
html { color-scheme: light dark; }
body { width: 35em; margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, nginx is successfully installed and working.
Further configuration is required for the web server, reverse proxy, 
API gateway, load balancer, content cache, or other features.</p>

<p>For online documentation and support please refer to
<a href="https://nginx.org/">nginx.org</a>.<br/>
To engage with the community please visit
<a href="https://community.nginx.org/">community.nginx.org</a>.<br/>
For enterprise grade support, professional services, additional 
security features and capabilities please refer to
<a href="https://f5.com/nginx">f5.com/nginx</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```



## 加入环境变量

```sh
vim /etc/profile # 滚到最下面
export PATH=$PATH:/opt/nginx/sbin
source /etc/profile
```





## 自动ssl续费

```sh
curl https://get.acme.sh | sh -s email=youremail@xx.com
git clone https://github.com/acmesh-official/acme.sh.git
cd ./acme.sh
bash acme.sh
./acme.sh --install -m youremail@xx.com


bash acme.sh  --issue  -d www.nantingya.top  --nginx /usr/local/nginx/conf/nginx.conf #可以指定配置文件

# 安装证书
acme.sh --install-cert -d www.nantingya.top \
--key-file       /usr/local/nginx/ssl/www.nantingya.top.key  \
--fullchain-file /usr/local/nginx/ssl/www.nantingya.top.pem \
--reloadcmd     "nginx -s reload"
```



## nginx配置https

```nginx
server {  
    listen 80;
    server_name www.exp.com;
    return 301 https://www.exp.com;
}
server {
   listen       443 ssl;
   server_name  www.exp.com;

   ssl_certificate      /usr/local/nginx/ssl/www.exp.com.pem;
   ssl_certificate_key  /usr/local/nginx/ssl/www.exp.com.key;

   ssl_session_cache    shared:SSL:1m;
   ssl_session_timeout  5m;

   ssl_ciphers  HIGH:!aNULL:!MD5;
   ssl_prefer_server_ciphers  on;

   location / {
		proxy_pass http://127.0.0.1:1313;
   }
}
```

