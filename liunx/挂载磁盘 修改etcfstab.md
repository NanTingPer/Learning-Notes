修改`/etc/fstab`

```sh
UUID=382c0a95-3f59-475f-a27f-f27f9dd5682e /                       xfs     defaults        0 0
UUID=5fd2566d-16e5-42b0-8729-680f67825a63 /mnt/data               ext4    defaults        0 0
```

UUID使用 `lsblk -f`  文件系统也是

```sh
NAME   FSTYPE  FSVER LABEL  UUID                                 FSAVAIL FSUSE% MOUNTPOINTS
sr0    iso9660       cidata 2025-10-26-19-14-10-00                              
vda                                                                             
└─vda1 xfs                  382c0a95-3f59-475f-a27f-f27f9dd5682e   28.4G    43% /
vdb                                                                             
└─vdb1 ext4    1.0          5fd2566d-16e5-42b0-8729-680f67825a63              
```

随后挂载`mount -a`





elep安装 `sudo dnf install -y epel-release`

`https://gitlab.com/virt-viewer/virt-viewer/-/releases` virt-viewer



## Qemu-Sytem

先去装一下quickemu`https://github.com/quickemu-project/quickemu/wiki/04-Create-Windows-virtual-machines`

可以省很多事

安装管理工具

```sh
yum install libvirt
# 启用socket服务
systemctl enable virtqemud.socket
```

编写配置 保存为 `windowsKVM.xml` 运行使用 `virsh define windowsKVM.xml` 定义域，如要删除可以使用`virsh undefine windows11` 名称取决于配置文件中的`name`。

如要运行 可以使用`virsh start windows11` 终止使用 `virsh destroy windows11`

如果要查看全部define可以使用`virsh list --all`
下列配置中的disk.qcow2需要自行创建`qemu-img create -f qcow2 ./windows-10-Chinese-Simplified/disk.qcow2 40G`

如果要重新安装引导可以使用 `yum reinstall edk2-ovmf`

替换引导 `cp /usr/share/edk2/ovmf/OVMF_VARS.fd /var/lib/libvirt/qemu/nvram/windows11_VARS.fd`

|                |          |
| -------------- | -------- |
| virsh destroy  | 停止     |
| virsh undefine | 取消定义 |
| virsh define   |          |



```sh
yum reinstall edk2-ovmf
cp /usr/share/edk2/ovmf/OVMF_VARS.fd /var/lib/libvirt/qemu/nvram/windows11_VARS.fd
qemu-img create -f qcow2 ./windows-10-Chinese-Simplified/disk.qcow2 40G
virsh define windows11.xml
virsh start windows11
```

```sh
virsh destroy windows11
virsh undefine windows11 --nvram
virsh define windowsKVM.xml
```





## 启用了

```xml
<domain type='kvm'>
    <name>windows11</name>
    <memory unit='GiB'>8</memory>
    <vcpu placement='static'>4</vcpu>
    <os>
        <type arch='x86_64' machine='q35'>hvm</type>
        <loader readonly='yes' type='pflash'>/usr/share/edk2/ovmf/OVMF_CODE.secboot.fd</loader>
        <nvram template='/usr/share/edk2/ovmf/OVMF_VARS.secboot.fd'/>
    </os>
    <features>
        <acpi/>
        <apic/>
        <hyperv>
            <relaxed state='on'/>
            <vapic state='on'/>
            <spinlocks state='on' retries='8191'/>
        </hyperv>
        <kvm>
            <hidden state='on'/>
        </kvm>
        <vmport state='off'/>
    </features>
    <cpu mode='host-passthrough' check='none'/>
    <clock offset='localtime'>
        <timer name='rtc' tickpolicy='catchup'/>
        <timer name='pit' tickpolicy='delay'/>
        <timer name='hpet' present='no'/>
        <timer name='hypervclock' present='yes'/>
    </clock>
    <devices>
        <emulator>/usr/bin/qemu-system-x86_64</emulator>
        <disk type='file' device='cdrom'>
            <driver name='qemu' type='raw'/>
            <source file='/mnt/data/quickemu/windows-11-Chinese-Simplified/windows-11.iso'/>
            <target dev='sda' bus='sata'/>
            <boot order='1'/>
            <readonly/>
        </disk>
        <disk type='file' device='disk'>
            <driver name='qemu' type='qcow2'/>
            <source file='/mnt/data/quickemu/windows-11-Chinese-Simplified/disk.qcow2'/>
            <target dev='vda' bus='virtio'/>
        </disk>
        <disk type='file' device='cdrom'>
            <driver name='qemu' type='raw'/>
            <source file='/mnt/data/quickemu/windows-11-Chinese-Simplified/virtio-win.iso'/>
            <target dev='sdb' bus='sata'/>
            <readonly/>
        </disk>
        <controller type='usb' model='ich9-ehci1' index='0'/>
        <input type='tablet' bus='usb'/>
        <video>
        	<model type='qxl' vram='65536' heads='1' primary='yes'/>
        </video>
        <channel type='spicevmc'>
        	<target type='virtio' name='com.redhat.spice.0'/>
        </channel>
        <graphics type='vnc' port='5901' autoport='no' listen='0.0.0.0'/>
        <graphics type='spice' port='5002' autoport='no' listen='0.0.0.0'>
          	<listen type='address' address='0.0.0.0'/>
        </graphics>
        <tpm model='tpm-crb'>
            <backend type='emulator' version='2.0'/>
        </tpm>
        <memballoon model='virtio'/>
    </devices>
</domain>
```



## 没有启用

```xml
<domain type='kvm'>
    <name>windows11</name>
    <memory unit='GiB'>8</memory>
    <vcpu placement='static'>4</vcpu>
    <os>
        <type arch='x86_64' machine='q35'>hvm</type>
        <loader readonly='yes' type='pflash'>/usr/share/edk2/ovmf/OVMF_CODE.fd</loader>
        <nvram template='/usr/share/edk2/ovmf/OVMF_VARS.fd'/>
    </os>
    <features>
        <acpi/>
        <apic/>
        <hyperv>
            <relaxed state='on'/>
            <vapic state='on'/>
            <spinlocks state='on' retries='8191'/>
        </hyperv>
        <kvm>
            <hidden state='on'/>
        </kvm>
        <vmport state='off'/>
    </features>
    <cpu mode='host-passthrough' check='none'/>
    <clock offset='localtime'>
        <timer name='rtc' tickpolicy='catchup'/>
        <timer name='pit' tickpolicy='delay'/>
        <timer name='hpet' present='no'/>
        <timer name='hypervclock' present='yes'/>
    </clock>
    <devices>
        <emulator>/usr/bin/qemu-system-x86_64</emulator>
        <disk type='file' device='cdrom'>
            <driver name='qemu' type='raw'/>
            <source file='/mnt/data/quickemu/windows-11-Chinese-Simplified/windows-11.iso'/>
            <target dev='sda' bus='sata'/>
            <boot order='1'/>
            <readonly/>
        </disk>
        <disk type='file' device='disk'>
            <driver name='qemu' type='qcow2'/>
            <source file='/mnt/data/quickemu/windows-11-Chinese-Simplified/disk.qcow2'/>
            <target dev='vda' bus='virtio'/>
        </disk>
        <disk type='file' device='cdrom'>
            <driver name='qemu' type='raw'/>
            <source file='/mnt/data/quickemu/windows-11-Chinese-Simplified/virtio-win.iso'/>
            <target dev='sdb' bus='sata'/>
            <readonly/>
        </disk>
        <controller type='usb' model='ich9-ehci1' index='0'/>
        <input type='tablet' bus='usb'/>
        <graphics type='vnc' port='5901' autoport='no' listen='0.0.0.0'/>
        <memballoon model='virtio'/>
    </devices>
    <tpm model='tpm-crb'>
        <backend type='emulator' version='2.0'/>
    </tpm>
</domain>
```





```sh
qemu-system-x86_64 \
  -machine q35,accel=kvm \
  -cpu host \
  -smp 4 \
  -m 8G \
  -drive file=windows-11-Chinese-Simplified/disk.qcow2,if=virtio,format=qcow2 \
  -cdrom windows-11-Chinese-Simplified/windows-11.iso \
  -drive file=windows-11-Chinese-Simplified/virtio-win.iso,media=cdrom \
  #-bios /usr/share/edk2/ovmf/OVMF_CODE.fd \
  -drive if=pflash,format=raw,unit=0,file=/usr/share/edk2/ovmf/OVMF_CODE.fd,readonly=on \
  -drive if=pflash,format=raw,unit=1,file=./vars.fd \
  -device intel-hda -device hda-duplex \
  -device ich9-usb-ehci1,id=usbctrl \
  -device usb-tablet,bus=usbctrl.0 \
  -device usb-host,bus=usbctrl.0,vendorid=0x1234,productid=0x5678 \
  -vga qxl \
  #-spice port=25564,addr=0.0.0.0,disable-ticketing=on \
  -vnc :1 \
  -device virtio-serial-pci \
  -device virtserialport,chardev=spicechannel0,name=com.redhat.spice.0 \
  -chardev spicevmc,id=spicechannel0,name=vdagent \
  -monitor stdio
```

查看当前运行的

```sh
ps -ef | grep qemu-system-x86_64
```





## 网络NAT

启用网络服务

```sh
sudo systemctl enable --now virtnetworkd.socket
sudo systemctl enable --now virtqemud.service

#启用默认网卡
virsh net-start default

#编辑
<interface type='network'>
  <source network='default'/>
  <model type='virtio'/>
</interface>
```

