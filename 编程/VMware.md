# 安装虚拟机

## 操作步骤

![](Pasted%20image%2020231210234530.png)
![](Pasted%20image%2020231210234638.png)
![](Pasted%20image%2020231210234647.png)
![](Pasted%20image%2020231210234653.png)
![](Pasted%20image%2020231210234700.png)
![](Pasted%20image%2020231210234709.png)
![](Pasted%20image%2020231210234722.png)
![](Pasted%20image%2020231210234731.png)
![](Pasted%20image%2020231210234738.png)
![](Pasted%20image%2020231210234748.png)
![](Pasted%20image%2020231210234754.png)
![](Pasted%20image%2020231210234800.png)

最后一定记得设置网络和用户密码
## 问题与解决

以前的华为笔记本，装虚拟机用的是**VMware 15.5**+**CentOS-7-Minimal-1810.iso**

此次换了零刻小主机之后，还按华为笔记本的方式安装虚拟机发现虚拟机安装失败了，报错如下：

```shell
Kernel panic - not syncing: Fatal exception
```

折腾了许久，最终的解决方式是用**VMware 16**+**CentOS-7-x86_64-Minimal-2003.iso**

# 设置固定IP地址

编辑→虚拟网络编辑器，点右下角更改设置，为 VMnet8 更改子网 IP，如下图
![](Pasted%20image%2020231211020218.png)

**虚拟机的 IP 总是变化怎么办？**

点击 DHCP 设置，将租用时间都改为最长天数

**配置固定IP地址**

```shell
vi /etc/sysconfig/network-scripts/ifcfg-ens33
```

删除UUID行，删除DHCP行，再配置如下：
![](Pasted%20image%2020231211020731.png)

# 制作模板机

**安装必备软件**

```shell
yum -y install net-tools
yum -y install wget
yum -y install vim
```

**同步时钟**

同步时钟可以配置阿里云的服务器
可以配置多个时间同步服务器来提高时间同步的可靠性和准确性

```shell
yum -y install ntp
vi /etc/ntp.conf

server ntp1.aliyun.com
server ntp2.aliyun.com
server ntp3.aliyun.com
server ntp4.aliyun.com

service ntpd start
chkconfig ntpd on
```

![](Pasted%20image%2020231211020532.png)

**关闭防火墙**

之后重启检查防火墙是否启动
关闭selinux
```shell
vi /etc/selinux/config
```

![](Pasted%20image%2020231211020612.png)