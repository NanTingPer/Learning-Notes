## Liunx磁盘映射到Windows

1. 安装 Samba `yum install samba -y`

2. 配置samba 

   `cd /etc/samba/`

   `vim smb.conf`

   | workgroup     | 工作组名称                               |
   | ------------- | ---------------------------------------- |
   | **security**  | **认证方式 默认 user, share 不需要秘密** |
   | load printers | 是否加载打印机 yes or no                 |

3. 添加内容

   ```sh
   [dir]
   	comment = All File
   	path = / #路径
   	writable = yes #可写
   	public = yes #公开
   	read only = no #只读
   	available = yes #可用
   ```

4. 创建可访问用户

   `useradd -m userName` 创建用户

   `passwd userName` 设置密码

   `smbpasswd -a userName` 添加为smb用户

5. 重启服务

   `sudo systemctl restart smb`

   `sudo systemctl restart nmb`

6. 开机自启

   `sudo systemctl enable  xxxx`

7. 关闭自启

   `sudo systemctl disable xxx`

8. 卸载

   `sudo yum remove samba samba-client samba-common`

   `sudo yum autoremove`

   