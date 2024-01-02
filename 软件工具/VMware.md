# 安装虚拟机

## 操作步骤

![](49ea19e8af43d0d18a352a641faa4de0.png)
![](8fd61a9ba22b37f98d726bf973bead0d.png)
![](d710018d9d2f2dae97308a066e5e666c.png)
![](f85d6a363d808001fa7f6f2d40b2a7f7.png)
![](14405f231ee21ee8c7b6d30ff3163421.png)
![](7fa1b612b04c11a9ed94636a5fc84a24.png)
![](98ee2dc1ea6f196e8f76424496af6548.png)
![](dd98deb825e50ccd352161e1b47c6016.png)
![](cb6cc631043d855efbf85f841f974c4e.png)
![](7ea259ea2e888dab29c3e69857ae10d8.png)
![](1706915a76d4d7ad89b821cebc726573.png)
![](2bb650bf641a95cefe5053e27bbfee69.png)

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
![](9e07eead387d622eb86d7efb6b979e06.png)

**虚拟机的 IP 总是变化怎么办？**

点击 DHCP 设置，将租用时间都改为最长天数

**配置固定IP地址**

```shell
vi /etc/sysconfig/network-scripts/ifcfg-ens33
```

删除UUID行，删除DHCP行，再配置如下：
![](1a19b78220bc38962a10554df7560180.png)

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

![](0f20639e0d6403a7b731740955f51667.png)

**关闭防火墙**

之后重启检查防火墙是否启动
关闭selinux
```shell
vi /etc/selinux/config
```

![](2f57d92a85298e09b1e4d01c7af5c7ff.png)