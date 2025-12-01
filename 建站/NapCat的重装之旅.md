# NapCat的重装之旅

## 网络备份

​	在之前的版本中，由于使用了自己写的后端，依赖于老的配置文件，因此需要将网络配置拷贝一份，用于后面部署后直接替换。网络配置文件路径在`/root/Napcat/opt/QQ/resources/app/app_launcher/napcat/config` [文档](https://napneko.github.io/config/basic)

## 使用CLI

​	NapCat官方提供了一键[脚本](https://github.com/NapNeko/NapCat-Installer)按提示安装并替换配置文件即可

```sh
[2025-11-25 08:32:40]: 安装位置: 
[2025-11-25 08:32:40]:   /root/Napcat 
[2025-11-25 08:32:40]:  
[2025-11-25 08:32:40]: 启动 Napcat (无需 sudo): 
[2025-11-25 08:32:40]:   xvfb-run -a /root/Napcat/opt/QQ/qq --no-sandbox  
[2025-11-25 08:32:40]:  
[2025-11-25 08:32:40]: 后台运行 Napcat (使用 screen, 无需 sudo): 
[2025-11-25 08:32:40]:   启动: screen -dmS napcat bash -c "xvfb-run -a /root/Napcat/opt/QQ/qq --no-sandbox " 
[2025-11-25 08:32:40]:   带账号启动: screen -dmS napcat bash -c "xvfb-run -a /root/Napcat/opt/QQ/qq --no-sandbox  -q QQ号码" 
[2025-11-25 08:32:40]:   附加到会话: screen -r napcat (按 Ctrl+A 然后按 D 分离) 
[2025-11-25 08:32:40]:   停止会话: screen -S napcat -X quit 
[2025-11-25 08:32:40]:  
[2025-11-25 08:32:40]: Napcat 相关信息: 
[2025-11-25 08:32:40]:   插件位置: /root/Napcat/opt/QQ/resources/app/app_launcher/napcat 
[2025-11-25 08:32:40]:   WebUI Token: 查看 /root/Napcat/opt/QQ/resources/app/app_launcher/napcat/config/webui.json 文件获取 
[2025-11-25 08:32:40]:  
[2025-11-25 08:32:40]: 未安装 TUI-CLI 工具。如需使用便捷命令管理, 请重新运行安装脚本并选择安装 TUI-CLI (--cli y)。 
[2025-11-25 08:32:40]: -- 
[2025-11-25 08:32:40]: Shell (Rootless) 安装流程完成。
```

```sh
nohup xvfb-run -a /root/Napcat/opt/QQ/qq --no-sandbox &
```

```sh
cd /opt/hugo/
bash start.sh
cd /opt/terrariaWikiImage/
bash start.sh
```

```sh
#登录二维码位置
/root/Napcat/opt/QQ/resources/app/app_launcher/napcat/cache/qrcode.png
#后台执行命令 日志不会重定向
screen -dmS napcat bash -c "xvfb-run -a /root/Napcat/opt/QQ/qq --no-sandbox " >> /opt/napcat/log.log 2>&1
```

