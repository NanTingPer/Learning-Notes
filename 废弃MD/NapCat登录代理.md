# NapCat登录代理

​	由于配置本机代理，因此使用`NapCatShell` [github](https://github.com/NapNeko/NapCat-Installer) 这边是香港服务器

​	`dnf` / `yum` 清华源配置 [CentOS-stream](https://mirrors.tuna.tsinghua.edu.cn/help/centos-stream/)

```sh
curl -o napcat.sh https://raw.githubusercontent.com/NapNeko/NapCat-Installer/refs/heads/main/script/install.sh && sudo bash napcat.sh
```



## 1. `TUI-CIL`安装失败

> 因为`ffmpeg`安装失败

[进入](https://github.com/BtbN/FFmpeg-Builds/releases)选择最新构建版本，然后使用`wget`进行下载

```sh
wget https://github.com/BtbN/FFmpeg-Builds/releases/download/latest/ffmpeg-master-latest-linux64-gpl.tar.xz
```

然后使用`xz -d`进行解压，解压后的后缀是`tar`还需要进行解压

```sh
xz -d ./ffmpeg-master-latest-linux64-gpl.tar.xz
tar -vxf ./ffmpeg-master-latest-linux64-gpl.tar
```

随后进入目标文件夹，进入`bin`目录，内有`ffmpeg` 尝试运行

- 设置软链

```sh
cd /usr/bin
ln -s {filepath}/bin/ffmpeg ffmpeg
ln -s {filepath}/bin/ffprobe ffprobe
```



随后再次运行`napcat.sh`安装脚本



## 2. 设置环境变量

```sh
wget -N --no-check-certificate https://raw.githubusercontent.com/XTLS/Xray-install/main/install-release.sh
bash install-release.sh
```





```sh
export NAPCAT_PROXY_PORT=1965 #代理端口
export NAPCAT_PROXY_ADDRESS=127.0.0.1 #代理地址
```

