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