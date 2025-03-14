| systemctl start docker        | 启动docker           |
| ----------------------------- | -------------------- |
| **systemctl enable docker**   | **开启开机自启**     |
|                               |                      |
| **docker search Name**        | **检索Docker镜像**   |
| **docker pull Name[:版本号]** | **下载Docker镜像**   |
| **docker images**             | **查看已有镜像**     |
| **docker rmi 唯一ID**         | **删除指定ID的镜像** |

[打算用这个](https://themes.gohugo.io/themes/hugo-theme-hello-friend-ng/)

> ### Docker容器操作

| docker run IMAGENAME                                     | 运行容器            |
| -------------------------------------------------------- | ------------------- |
| **docker ps [-a]** / -a查看全部容器                      | **查看容器**        |
| **docker stop ID** / ID是唯一id 使用名字也可以           | **停止容器**        |
| **docker start ID** / ID是唯一id 使用名字也可以          | **启动容器**        |
| **docker restart ID** / ID是唯一id 使用名字也可以        | **重启容器**        |
| **docker stats ID** / ID是唯一id 使用名字也可以          | **查看状态**        |
| **docker logs ID** / ID是唯一id 使用名字也可以           | **查看日志**        |
| **docker exec -it ID FilePath** / -it 是交互             | **进入docker**      |
| **docker rm [-f] ID** / ID是唯一id 使用名字也可以 -f强制 | **删除容器**        |
|                                                          |                     |
| **容器发布和保存**                                       |                     |
| **docker commit**                                        | **打包容器** 为镜像 |
| **docker save NAME:V** save -o mynginx.tar mynginx:v1.0  | **保存镜像为文件**  |

> ## 加载 save 的容器

> `docker load -i path` 加载位于path的镜像
>
> `docker tag `改名



 



> ### docker run

> run后面加

- -p 80:80 , 端口映射 本地80映射到docker 80 ,前面是主机的

- -d 后台运行
- --name Name , 指定名



> ### docker commit
>
> docker commit [Options] 镜像名称 [版本号]

- -m "消息" / 提交的内容
- `docker commit -m "update index.html" mynginx mynginx:v1.0`



[Docker Hub 如果是指定版本 这里搜索](https://hub.docker.com/)

- 镜像就是软件包
- 容器就是使用软件包启动的应用
  - 容器和容器之间互相隔离，轻量级的vm,共享操作系统内核



# 安装Docker(Liunx)

> 前置
>
> NTP服务
>
> - sudo apt-get install ntpdate
>
> 同步
>
> - nptdate time.windows.com
>
> 设置自动同步
>
> - sudo crontab -e
> - 0 0 * * * /usr/sbin/ntpdate ntp.ubuntu.com > /dev/null 2>&1
>
> 设置时区
>
> - timedatectl set-timezone Asia/Shanghai
>
> 拉取公钥
>
> - apt install gnupg
>
> - sudo apt-key adv  --keyserver keyserver.ubuntu.com --recv-keys 40976EAF437D05B5
> - sudo apt-key adv --keyserver  keyserver.ubuntu.com --recv-keys 3B4FE6ACC0B21F32

```sh
sudo apt-get install ntpdate
nptdate time.windows.com
timedatectl set-timezone Asia/Shanghai
apt install gnupgs
sudo apt-key adv  --keyserver keyserver.ubuntu.com --recv-keys 40976EAF437D05B5
sudo apt-key adv --keyserver  keyserver.ubuntu.com --recv-keys 3B4FE6ACC0B21F32
```







[Plugin | Docker Docs](https://docs.docker.com/compose/install/linux/)

1. 更换源 mv -f /etc/apt/sources.list /etc/apt/sources.list.bak (可以不做)

2. 创建源文件 `vim sources.list`

3. 输入源信息

   ```sh
   deb http://mirrors.aliyun.com/ubuntu/ trusty main restricted universe multiverse
   deb http://mirrors.aliyun.com/ubuntu/ trusty-security main restricted universe multiverse
   deb http://mirrors.aliyun.com/ubuntu/ trusty-updates main restricted universe multiverse
   deb http://mirrors.aliyun.com/ubuntu/ trusty-proposed main restricted universe multiverse
   deb http://mirrors.aliyun.com/ubuntu/ trusty-backports main restricted universe multiverse
   deb-src http://mirrors.aliyun.com/ubuntu/ trusty main restricted universe multiverse
   deb-src http://mirrors.aliyun.com/ubuntu/ trusty-security main restricted universe multiverse
   deb-src http://mirrors.aliyun.com/ubuntu/ trusty-updates main restricted universe multiverse
   deb-src http://mirrors.aliyun.com/ubuntu/ trusty-proposed main restricted universe multiverse
   deb-src http://mirrors.aliyun.com/ubuntu/ trusty-backports main restricted universe multiverse
   ```

   



安装Docker



Doucker Engine -> Install



> 设置Docker存储库

```SH
#移除原有docker
for pkg in docker.io docker-doc docker-compose podman-docker containerd runc; do sudo apt-get remove $pkg; done

# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl -y
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```



> 安装Docker

```sh
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```



> 国内加速源

```sh
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
	"registry-mirrors": ["https://mirror.ccs.tencentyun.com"]
}
EOF
#重启后台进程
sudo systemctl daemon-reload
#重启docker
sudo systemctl restart docker
```



# 实战 启动nginxWeb

> 1. 下载镜像
> 2. 启动容器
> 3. 修改页面
> 4. 保存镜像
> 5. 分享

```sh
docker pull nginx
docker start nginx
docker exec -it nginx /usr/share/nginx/html (bash)
#保存为镜像
docker commint mynginx
#保存为文件
docker save -o mynginx.tar mynginx:v1.0

docker load -i mynginx.tar
```





# Docker 存储

| docker volume ls          | 查看全**部卷**     |
| ------------------------- | ------------------ |
| **docker inspect ngconf** | **查看指定卷信息** |



> ### 快速卸载全部容器

```sh
#查看全部容器 只看id
docker ps -aq
#将结果传给命令
docker rm -f $(docker ps -aq)
```



## 目录挂载

```sh
#指定目录映射
docker run -d -p 80:80 -v /app/nghtml:/usr/share/nginx/html --name app01 nginx

#可以映射多个
docker run -d -p 80:80 -v /app/nghtml:/usr/share/nginx/html -v /app/ngconf:/etc/nginx --name app01 nginx
```



## 卷映射

```sh
#将v后面的路径更改为本地卷名
#存储在 /var/lib/docker/volumes/卷名
-v 卷名:/etc/nginx
```





# Dicker 网络

> #### 默认网卡 docker0

| docker inspect NAME | 查看指定容器网络信息 |
| ------------------- | -------------------- |



## 创建自定义网络

```sh
docker network create Name

#列出网络
docker network ls
```

> ### 加入自定义网络

```sh
docker run -d -p 88:80 --name app1 --network NETWORKNAME nginx
docker run -d -p 99:80 --name app2 --network NETWORKNAME nginx
```

> ### 访问

```sh
docker exec -it app1 bash
curl http://app2:99
```





# Redis集群

> #### master

```sh
REDIS_REPLICATION_MODE=master
REDIS_PASSWORD=123456
```

> #### slave

```sh
REDIS_REPLICATION_MODE=slave
REDIS_MASTER_HOST=redis01
REDIS_MASTER_PORT_NUMBER=6379
#访问master的密码
REDIS_MASTER_PASSWORD=123456
#本节点的访问密码 
REDIS_PASSWORD=123456
```



> #### 启动容器

```sh
docker run -d -p 6379;6379 \
-v /app/rd1:/bitnami/redis/data \
-e REDIS_REPLICATION_MODE=master \
-e REDIS_PASSWORD=123456 \
--network mynet \
--name redis01 \
bitnami/redis #镜像

#给目录权限
chmod -R 777 /app/rd1
```





# Docker Compose

```sh
#批量启动配置文件的容器
docker compose up -d

#批量关闭配置文件中的容器
docker compose down

#启动指定
docker compose start x1 x2 x3
#停止
docker compose start x1 x2 x3

#指定启动三个
docker compose scale x2=3
```



## 测试实验

> #### 编写WordPress 开源博客系统



> #### 创建网络

```sh
docker network create blog
```





> #### 创建MySQL容器

```sh
docker run -d -p 6666:3306 \
-e MYSQL_ROOT_PASSWORD=123456 \
-e MYSQL_DATABASE=wordpress \
-v mysql-data:/var/lib/mysql \ #卷映射
-v /app/myconf:/etc/mysql/conf.d \
--restart always --name mysql \ #开启开机自启并设置name
--network blog \ #指定网络
mysql
```



> ### 创建博客系统容器

```sh
docker run -d -p 80:80 \
-e WORDPRESS_DB_HOST=mysql \
-e WORDPRESS_DB_USER=root \
-e WORDPRESS_DB_PASSWORD=123456 \
-e WORDPRESS_DB_NAME=wordpress \
-v wordpress:/var/www/html \ #卷映射
--restart always --name wordpress-app \
--network blog \
wordpress
```





## 创建一键脚本

> compose.yam

```yaml
#创建应用的名
name:myblog
services:
	#应用名称 使用该名称可以单独只启动这个[NAME]
	mysql:
		#容器名称
		container_name: mysql
		#镜像
		image: mysql[:8.0]
		portes:
			- "3306:3306"
		#环境变量
		environment:
			- MYSQL_ROOT_PASSWORD=123456
			- MYSQL_DATABASE=wordpress
		#卷挂载 / 映射
		volumes:
			- mysql-data:/var/lib/mysql
			- /app/myconf:/etc/mysql/conf.d
		#重启策略
		restart: always
		#网络
		networks:
			- blog
	
	wordpress:
		container_name: wordpress
		image: wordpress
		portes:
			- "8888:80"
		environment:
			- WORDPRESS_DB_HOST=mysql
			- WORDPRESS_DB_USER=root
			- WORDPRESS_DB_PASSWORD=123456
			- WORDPRESS_DB_NAME=wordpress
		volumes:
			- wordpress:/var/www/html
		restart: always
		networks:
			- blog
		#依赖
		depends_on:
			- mysql[NAME]
	
#上面的卷只需要在这里出现就行了
volumes:
	mysql-data:
	wordpress:
	
networks:
	blog:
```



## 删除先前的全部

```sh
docker rm -f $(docker ps -aq)
docker volume ls
docker volume rm xxx xxx
docker network ls
docker network rm xx
```



## 启动

```sh
docker compose -f compose.yaml up -d
```



```sh
#彻底删除
docker compose -f compose.yaml down --rmi all -v
```



# Docker DockerFile

> ### 自建镜像

DockerFile + app.jar => Builder

镜像中应该包含全部的运行环境



| 常见指令   | 作用               |
| ---------- | ------------------ |
| FROM       | 指定镜像基础环境   |
| RUN        | 运行自定义命令     |
| CMD        | 容器启动命令或参数 |
| LABEL      | 自定义标签         |
| EXPOSE     | 指定暴露端口       |
| ENV        | 环境变量           |
| ADD        | 添加文件到镜像     |
| COPY       | 复制文件到镜像     |
| ENTRYPOINT | 容器固定启动命令   |
| VOLUME     | 数据卷             |
| USER       | 指定用户和用户组   |
| WORKDIR    | 指定默认工作目录   |
| ARG        | 指定构建参数       |



```sh
FROM openjdk:17

LABEL author=作者

COPY app.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["javr","-jar","/app.jar"]
```



```SH
from golang:latest

copy ./
EXPOSE 80
ENTRYPOINT ["./hugo","server","--bind","0.0.0.0","-p","80",-b,"http://122.11.29.203"]
```



> Windows 更改Docker文件位置

```SH
wsl --export docker-desktop "D:\\All\docker.tar"
wsl --unregister docker-desktop
wsl --import docker-desktop "D:\\All\\docker" "D:\\All\\docker.tar" --version 2
```

