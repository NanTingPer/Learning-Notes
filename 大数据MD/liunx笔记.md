# 目录

- #### 只有一个树，所有文件的顶级目录都是 /

# 命令

### ls 目录显示

- ls -a 隐藏的显示
- ls -l 列表显示
- ls -al 都有
- ls -lh 文件大小显示为带单位

### cd 目录切换

- cd 直接回到home
- cd 路径 => 去往指定路径
- cd . => 返回上一级
- cd .. => 返回上两级
- cd ~ => 从HOME目录开始指定
- cd ./ => 当前目录

### pwd 打印当前所在

- pwd 直接打印当前所在目录

### mkdir 创建文件夹

- mkdir -p 路径 => 创建层级目录

### touch 创建文件

### cat 查看文件内容

### 删除 移动 复制文件

- cp [-r] 参数1 参数2
  
  - -r  => 复制文件夹
  - 参数1 => 要复制的文件
  - 参数2  => 目的地

- mv 参数1 参数2 => 可以用来更改名字

- rm [-r -f] 参数.....
  
  - -r 删除文件夹
  - -f 强制删除

### 查找

- which => 查找命令文件位置
- find 起始路径 -name "文件名"
  - su - root 切换到root用户
  - find 起始 -size +|- n [KMG]
    - -size 文件大小
    - +=>大于
    - n=>大小
    - KMG=>KB,MB,GB

### 关键字过滤

- grep [-n] 关键字 文件路径
  
  - -n => 表示 显示匹配的行号
  - 关键字 => 查找内容

- wc [-c -m -l -w]文件路径
  
  - -c => 统计bytes数量
  - -m => 字符数量
  - -l => 行数
  - -w => 单词数

### 管道符 |

- #### 把左边的结果 作为右边的输入

### echo 输文件内容

### ‘’ 飘号

- ##### 被包裹的内容 作为命令执行

### ">"重定向符号

- ">" 将左边的结果 覆盖写入 右边的文件
- ">>" 将左边的结果 追加写入右边的文件

### tail 查看文件尾部内容

- tail [-f -num] 路径
  - -f 表示持续跟踪
  - -unm 表示查看多少行 默认10

### su [-] [用户名]

- "-" 是否加载该用户的环境变量

### 用户组

- groupadd 用户组名
  - 创建用户组
- gropudel 用户组名
  - 删除用户组

### 用户

- useradd [-g -d] 用户名
  - -g 指定用户的组,-g的组必须存在
  - -d 指定用户HOME路径，不指定就在/HOME/用户名
- userdel [-r] 用户名
  - -r 删除用户的HOME目录
- id [用户名]
  - 查看用户所属组
- usermod -aG 用户组 用户名
  - 指定用户加入组

### getent passwd 查看用户

### getent group 查看组

### chmod 修改权限信息

#### rwx =>

- r读权限
- w写权限
- x表示执行权限

只有文件的所属用户或者root用户可以修改

- chmod [-R] 权限 文件或文件夹
  - -R => 对文件内的全部内容都这样做、
- chmod -R u=rwx,g=rwx,o=rwx hello.txt

### 权限的数字表达

- ##### 751 => r4 w2 x1
  
  - rwx(7)
  - r-x(5)
  - --x(1)

- chmod简写 chmod 777 文件名

### chown 修改文件所属用户

chow [-R] [用户] [:] [用户组] 文件或文件夹

- -R 对里面的所有文件都做操作

### 软件安装

- yum [-y] [install | remove | search] 软件名称

### systemctl控制程序

- systemctl start | stop | status | enable | disable 服务名称
  - start 启动
  - stop 关闭
  - status 查看状态
  - enable 开启开机自启
  - disable 关闭开机自启

### 软链接(快捷方式)

- ln -s 参数1 参数2
  - -s 创建软链接
  - 被链接的文件
  - 要链接的目的地

# 固定IP地址

- #### 使用vim编辑/etc/sysconfig/network-scripts/网卡名称

- #### 新增:
  
  - IPADDR="IP"
  - NETMASK="子网掩码"
  - GATEWAY="网关"
  - DNS1="DNS1"

# env查看环境变量

## 永远生效

- ##### 当前用户 ~/bashrc

- ##### 全部用户 /etc/profile

- ##### 生效 source 配置文件

- ### export MYNAME="内容"
  
  - 指定一个名称到什么内容

- export 变量名=$PATH:路径

# 脚本编写

```shell
#!/bin/bash #表示指定执行程序 为bashShell
case $1 in #条件判断 $1 是用户根在脚本后的一个参数
"start")
    for i in master selave1 selave2
    do
        #ssh $i 路径 -daemon(后台) 配置文件路径
        ssh slave1 "/opt/module/kafka/bin/kafka-server-start.sh -daemon /opt/module/kafka/config/server.properties"
    done
;;
"stop")
    for i in master selave1 selave2
    do
        ssh xxxxx
    done
;;
esac
```

# 1.0 乌班图apt无法自动下载依赖的问题

```sh
sudo apt install aptitude
```

- 后面使用apt安装软件的时候改用aptitude就行了

```sh
sudo aptitude install xxx
```

# 2.0 乌班图桌面安装搜狗输入法

- 官网下载deb安装包

- 乌班图默认好像是fcitx5 但是我后面下载的时候要的是fcitx4

- 安装 aptitude

```sh
sudo apt install aptitude
```

- 安装fcitx
  - tip : 不带任何东西

```sh
sudo aptitude fcitx
```

- - 如果让你选择Y你就选择Y 会卸载自带的fcitx5

- 好了后左下角打开那个软件选择的界面 打开语言支持

- 然后帮助上面的那个选择选成fcitx

- 添加或删除语言里面把**汉语(中国)**安装一下

- 在上面的那个框框 拖动 把汉语拖到最上面

- reboot重启

- 然后去下载的路径

```sh
sudo dpkg -i sogo.deb
```

- 重启就好了

# 3.0 乌班图桌面安装Clash Verge

libicu70_70.1-2_amd64 这是第一个要装的

libicu67 这三第二个要安装的
https://packages.debian.org/zh-cn/bullseye/libicu67

libjavascriptcoregtk-4.0-18 (= 2.36.0-2ubuntu1) 这个是第三个要安装的
https://ubuntu.pkgs.org/22.04/ubuntu-main-amd64/libjavascriptcoregtk-4.0-18_2.36.0-2ubuntu1_amd64.deb.html

libwebkit2gtk-4.0-37_2.36.0-2ubuntu1_amd64.deb 这三第4个要安装的

如果不显示界面
直接原因:安装了 gpaste
解决办法:
vim /usr/share/applications/clash-verge.desktop 
新版本文件夹名
/usr/share/applications/Clash\Verge.desktop
将 Exec 改为:Exec=env WEBKIT_DISABLE_COMPOSITING_MODE=1 clash-verge %u

# 3.0 乌班图桌面安装.NET SDK

sudo apt-get update; 
sudo apt-get install -y apt-transport-https
sudo apt-get update
sudo apt-get install -y dotnet-sdk-8.0

注意只能安装以发布并受到支持的版本

# 4.0 乌班图桌面安装Java JDK8

sudo apt install openjdk-8-jdk

# 5.0 乌班图桌面安装Scala

sudo apt install curl

curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup

# 5.0 乌班图卸载软件

apt-get remove PackageName // 删除软件包, 保留配置文件

apt-get --purge remove PackageName // 删除软件包, 同时删除配置文件
apt-get purge PackageName // 删除软件包, 同时删除配置文件

apt-get autoremove PackageName // 删除软件包, 同时删除为满足依赖
// 而自动安装且不再使用的软件包

apt-get --purge autoremove PackageName // 删除软件包, 删除配置文件,
// 删除不再使用的依赖包

apt-get clean && apt-get autoclean // 清除 已下载的软件包 和 旧软件包

sudo apt-get --purge remove <programname>

# 6.0 乌班图软件包名

下载一个软件synaptic
状态 -> 已安装(手动)
状态 -> 已安装(本地)
netease-cloud-music 网易云音乐
yesplaymusic - 第三方播放器 网易云等（已安装）
linuxqq - QQ (已安装)
wechat - 微信(已安装)

marktext -> 一款md编辑器

[GitHub - marktext/marktext: 📝A simple and elegant markdown editor, available for Linux, macOS and Windows.](https://github.com/marktext/marktext)

中文特别版

[GitHub - topabomb/marktext-specialedition: marktext0.17.1分叉的中文特别版](https://github.com/topabomb/marktext-specialedition)

[GitHub - chinayangxiaowei/marktext-chinese-language-pack: Simplified Chinese language pack for marktext](https://github.com/chinayangxiaowei/marktext-chinese-language-pack)



GithubDesktop Liunx

[GitHub - shiftkey/desktop at release-3.4.8-linux1](https://github.com/shiftkey/desktop/tree/release-3.4.8-linux1)
