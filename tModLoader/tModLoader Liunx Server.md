# tModLoader Liunx Server

从Github仓库下载最新的正式版文件，或使用脚本下载，具体可以查看[Wiki](https://github.com/tModLoader/tModLoader/wiki/Starting-a-modded-server#using-the-management-script)

下载完成后，编写启动脚本，可使用参数可以依然可以查看上方文档。或下方内容。

默认的Mod存放位置为: `~/.local/share/Terraria/tModLoader/Mods`



# 启动脚本

```sh
#!/bin/bash
./start-tModLoaderServer.sh \
        -nosteam \
        -world "/root/.local/share/Terraria/tModLoader/Worlds/ts_server.wld" \
        -port 7777 \
        -ip 0.0.0.0
```



# 使用Screen

```sh
screen -S terraria # 开启会话
./start.sh #启动

#按下键盘CTRL + A 后按下 D, 脱离会话

#如要执行命令 可以使用 screen -r terraria 回到会话
```





# 参数

| 参数                                     | 描述                                                         |
| ---------------------------------------- | ------------------------------------------------------------ |
| `-config <配置文件>`                     | 指定要使用的配置文件。                                       |
| `-port <端口号>`                         | 指定监听的端口。                                             |
| `-players <数字>` / `-maxplayers <数字>` | 设置最大玩家数                                               |
| `-pass <密码>` / `-password <密码>`      | 设置服务器密码                                               |
| `-world <世界文件>`                      | 加载世界并自动启动服务器。                                   |
| `-autocreate <#>`                        | 如果在-world指定的路径中未找到世界，则创建一个世界。世界大小由以下指定：1(小型)，2(中型)，3(大型)。 |
| `-banlist <路径>`                        | 指定封禁列表的位置。默认为工作目录中的"banlist.txt"。        |
| `-worldname <世界名称>`                  | 设置使用-autocreate时的世界名称。                            |
| `-secure`                                | 为服务器添加额外的防作弊保护。                               |
| `-noupnp`                                | 禁用自动端口转发                                             |
| `-steam`                                 | 启用Steam支持                                                |
| `-nosteam`                               | 禁用Steam支持                                                |
| `-lobby <friends>` 或 `<private>`        | 允许好友加入服务器，或在启用Steam时将其设置为私人            |
| `-ip <ip地址>`                           | 设置服务器监听的IP地址                                       |
| `-forcepriority <优先级>`                | 设置此任务的进程优先级。如果使用此项，下面的"priority"设置将被忽略。 |
| `-disableannouncementbox`                | 禁用通告栏被电线触发时的文本通告。                           |
| `-announcementboxrange <数字>`           | 设置通告栏文本消息范围（像素），设为-1则为全服务器通告。     |
| `-seed <种子>`                           | 使用-autocreate时指定世界种子                                |
| `-tmlsavedirectory <路径>`               | 指定tModLoader的存档目录。模组和世界目录将由此存档目录派生。 |
| `-modpack <模组包名称>`                  | 设置要加载的模组包，这样只会加载指定的模组。                 |
| `-modpath <路径>`                        | 设置手动安装模组的加载文件夹。                               |
| `world=<世界文件>`                       | 加载世界并自动启动服务器。                                   |
| `autocreate=<#>`                         | 如果未找到世界则创建一个新世界。世界大小由以下指定：1(小型)，2(中型)，3(大型)。 |
| `seed=<种子>`                            | 使用autocreate时设置世界种子                                 |
| `worldname=<世界名称>`                   | 使用autocreate时设置世界名称                                 |
| `difficulty=<0/1>`                       | 使用autocreate时设置世界难度 0(普通), 1(专家)                |
| `maxplayers=<数字>`                      | 设置服务器允许的最大玩家数。数值必须在1到255之间             |
| `port=<端口号>`                          | 设置端口号                                                   |
| `password=<密码>`                        | 设置服务器密码                                               |
| `motd=<消息>`                            | 设置每日消息                                                 |
| `worldpath=<路径>`                       | 设置世界文件的存储文件夹                                     |
| `banlist=<路径>`                         | 封禁列表的位置。默认为工作目录中的"banlist.txt"。            |
| `secure=1`                               | 添加额外的防作弊保护。                                       |
| `language=<语言代码>`                    | 根据语言代码设置服务器语言。English = en-US, German = de-DE, Italian = it-IT, French = fr-FR, Spanish = es-ES, Russian = ru-RU, Chinese = zh-Hans, Portuguese = pt-BR, Polish = pl-PL |
| `upnp=1`                                 | 使用uPNP自动转发端口                                         |
| `npcstream=<数字>`                       | 减少怪物跳过但增加带宽使用。数字越低，跳过发生越少，但发送的数据越多。0为关闭。 |
| `priority=<0-5>`                         | 默认系统优先级 0:实时, 1:高, 2:高于正常, 3:正常, 4:低于正常, 5:空闲 |
| `modpath=<路径>`                         | 设置手动安装模组的加载文件夹。                               |
| `modpack=<模组包名称>`                   | 设置要加载的模组包，这样只会加载指定的模组。                 |

```sh
#这是一个tModLoader的示例配置文件
#tModLoader配置文件的工作方式与泰拉瑞亚完全相同，但启动的是.bat文件而不是.exe文件。
#注意，除非指定了世界，否则某些设置（如最大玩家数、端口和端口转发）将被忽略。
#此外，tModLoader配置文件支持tModLoader特有的选项。
#默认情况下，服务器会自动使用名为serverconfig.txt的配置文件。
#可以自定义使用的配置文件。使用命令'start-tModLoaderServer.bat -config configFileHere.txt'来使用特定的配置文件。
#更多细节请访问https://github.com/tModLoader/tModLoader/wiki/Command-Line 和 https://github.com/tModLoader/tModLoader/wiki/Starting-a-modded-server

#以下是可用的命令行参数列表：

#-config <配置文件>				            指定要使用的配置文件。
#-port <端口号>				              指定监听的端口。
#-players <数字> / -maxplayers <数字>	设置最大玩家数
#-pass <密码> / -password <密码>	      设置服务器密码
#-world <世界文件>				              加载世界并自动启动服务器。
#-autocreate <#>			                  如果在-world指定的路径中未找到世界，则创建一个世界。世界大小由以下指定：1(小型)，2(中型)，3(大型)。
#-banlist <路径>			                  指定封禁列表的位置。默认为工作目录中的"banlist.txt"。
#-worldname <世界名称>             			设置使用-autocreate时的世界名称。
#-secure			                        		为服务器添加额外的防作弊保护。
#-noupnp				                        	禁用自动端口转发
#-steam                         					启用Steam支持
#-nosteam                         				禁用Steam支持
#-lobby <friends> 或 <private>             允许好友加入服务器，或在启用Steam时将其设置为私人
#-ip <ip地址>	                              设置服务器监听的IP地址
#-forcepriority <优先级>	                  设置此任务的进程优先级。如果使用此项，下面的"priority"设置将被忽略。
#-disableannouncementbox                   禁用通告栏被电线触发时的文本通告。
#-announcementboxrange <数字>               设置通告栏文本消息范围（像素），设为-1则为全服务器通告。
#-seed <种子>                               使用-autocreate时指定世界种子

# tModLoader添加的命令行参数 (https://github.com/tModLoader/tModLoader/wiki/Command-Line)

#-tmlsavedirectory <路径>		                指定tModLoader的存档目录。模组和世界目录将由此存档目录派生。
#-modpack <模组包名称>			                  设置要加载的模组包，这样只会加载指定的模组。
#-modpath <路径>				                    设置手动安装模组的加载文件夹。

#移除命令前的#以启用它们。

#加载世界并自动启动服务器。
#world=C:\Users\你的用户名\My Documents\My Games\Terraria\Worlds\world1.wld

#如果未找到世界则创建一个新世界。世界大小由以下指定：1(小型)，2(中型)，3(大型)。
#autocreate=1

#使用autocreate时设置世界种子
#seed=AwesomeSeed

#使用autocreate时设置世界名称
#worldname=Terraria

#使用autocreate时设置世界难度 0(普通), 1(专家)
#difficulty=1

#设置服务器允许的最大玩家数。数值必须在1到255之间
#maxplayers=8

#设置端口号
#port=7777

#设置服务器密码
#password=p@55w0rd

#设置每日消息
#motd=请不要砍伐紫色的树！

#设置世界文件的存储文件夹
#worldpath=C:\Users\Defaults\My Documents\My Games\Terraria\Worlds\

#封禁列表的位置。默认为工作目录中的"banlist.txt"。
#banlist=banlist.txt

#添加额外的防作弊保护。
#secure=1

#根据语言代码设置服务器语言。
#English = en-US, German = de-DE, Italian = it-IT, French = fr-FR, Spanish = es-ES, Russian = ru-RU, Chinese = zh-Hans, Portuguese = pt-BR, Polish = pl-PL,
#language=en-US

#使用uPNP自动转发端口
#upnp=1

#减少怪物跳过但增加带宽使用。数字越低，跳过发生越少，但发送的数据越多。0为关闭。
#npcstream=60

#默认系统优先级 0:实时, 1:高, 2:高于正常, 3:正常, 4:低于正常, 5:空闲
priority=1

# tModLoader添加的服务器配置选项 (https://github.com/tModLoader/tModLoader/wiki/Command-Line)

#设置手动安装模组的加载文件夹。
#modpath=C:\Users\你的用户名\My Documents\My Games\Terraria\tModLoader\Mods\

#设置要加载的模组包，这样只会加载指定的模组。
#modpack=我的模组包
```

