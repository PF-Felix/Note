# 安装

![zoom=40](a86d2ec7390cac2c565247dc1f1db94b.png)

![zoom=40](d41d1cba3e91fe015477665d13cb7c1d.png)

`wget -O /etc/yum.repos.d/docker-ce.repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo`

![](3f9ba346787236a6efb8b7cda238db6b.png)

```shell
#安装docker-ce
yum -y install docker-ce

#启动
systemctl start docker
docker version
#设置开机自启
systemctl enable docker

#测试是否可用
docker ps
```

**遇到的问题与解决方案**

![](431651e715bfd13fcbb62c217439bc70.png)
1. 检查`/etc/docker/daemon.json`文件
2. 添加内容：`{"registry-mirrors":["https://registry.docker-cn.com"]}`
3. 重启 docker，脚本如下：

```shell
systemctl daemon-reload
systemctl restart docker.service
```

# 容器镜像加速器

![zoom=40](fc5a81d157dd46d94dfd07a04185b35e.png)

```shell
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://o4osh0q0.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

# 常用命令

镜像相关：

```shell
#查看镜像
docker images
#搜索镜像
docker search nginx
#下载镜像
docker pull nginx
#删除镜像
docker rmi 5d0da3dc9764
docker rmi nginx

#将内存镜像保存到本地
docker save -o nginx.tar nginx:v1
#加载本地镜像到内存
docker load -i nginx.tar

#登录/退出，登录之后再下载和上传镜像
docker login/logout
#重新为镜像打标
docker tag centos:latest dockersmartmsb/centos:v1
#上传/下载镜像
docker push/pull dockersmartmsb/centos:v1
```

容器相关：

```shell
#查看容器
docker ps
docker ps -a
#运行容器 -i交互式 -t提供终端 -name容器名称 bash在容器中执行命令
docker run -i -t --name c1 centos bash
#查看网络信息
ip a s
#查看进程
ps aux

#进入容器
docker exec -it container bash
#进入容器，退出容器时，如不需要容器再运行exit退出，如需要容器继续运行可使用ctrl+p+q
docker attach container
#启动/停止/删除容器
docker start/stop/rm 359efebd70d2

#查看容器的日志
docker logs ID

#容器与宿主机文件传输
docker cp [host_path] [container_id]:[container_path]
docker cp [container_id]:[container_path] [host_path]
```

容器与镜像：

```shell
#把容器提交为一个镜像
docker commit 7dXXXXX n1:v1

#将容器导出
docker export -o centos.tar 7dXXXX
#导入镜像
docker import centos.tar centos:v1

#批量清理镜像
docker rmi $(docker images -q)
#批量清理停止的容器
docker rm $(docker ps -a -q)
#批量停止正在运行的容器
docker stop $(docker ps -q)
```

# 自建镜像仓库

这里使用 harbor

```shell
#下载docker-compose
wget https://dn-dao-github-mirror.daocloud.io/docker/compose/releases/download/1.25.0/docker-compose-Linux-x86_64
#移动到/usr/bin目录，并更名为docker-compose
mv docker-compose-Linux-x86_64 /usr/bin/docker-compose
#添加可执行权限
chmod +x /usr/bin/docker-compose
#查看版本
docker-compose -v

#下载harbor
wget https://github.com/goharbor/harbor/releases/download/v2.4.1/harbor-offline-installer-v2.4.1.tgz
tar xxx harbor-offline-installer-v2.4.1.tgz
cd harbor/

#配置文件修改下面几点
hostname: my.harbor.com
certificate: /data/cert/my.harbor.com.crt
private_key: /data/cert/my.harbor.com.key

./prepare
./install.sh
```

验证：

```shell
docker ps

IMAGE                                COMMAND                  CREATED          STATUS
goharbor/nginx-photon:v2.4.1         "nginx -g 'daemon of…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/harbor-jobservice:v2.4.1    "/harbor/entrypoint.…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/harbor-core:v2.4.1          "/harbor/entrypoint.…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/harbor-registryctl:v2.4.1   "/home/harbor/start.…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/harbor-db:v2.4.1            "/docker-entrypoint.…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/registry-photon:v2.4.1      "/home/harbor/entryp…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/redis-photon:v2.4.1         "redis-server /etc/r…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/harbor-portal:v2.4.1        "nginx -g 'daemon of…"   37 minutes ago   Up 37 minutes (healthy)
goharbor/harbor-log:v2.4.1           "/bin/sh -c /usr/loc…"   37 minutes ago   Up 37 minutes (healthy)
```

在物理机访问浏览器：
![](0ffa3ba0afa56c4196b7eb33cbf59fed.png)

也可以用下面的方法验证：

```shell
vim /etc/hosts
10.0.0.11 my.harbor.com

docker login my.harbor.com
```

[配置证书参考](https://www.jianshu.com/p/7766759ab071)
![](bb9cc6c7833e4642f5a643276fafc3bd.png)

修改 /etc/docker/daemon.json 使用 harbor：

```shell
{
    "insecure-registries": ["my.harbor.com"]
}
```

```shell
systemctl daemon-reload
systemctl restart docker

docker tag centos:v1 my.harbor.com/peoject_name/centos:v2
docker push
docker pull my.harbor.com/peoject_name/centos:v2
```

harbor 怎么做高可用？两个 harbor 可以使用同一个存储卷

# 面试题

## Docker与虚拟机有啥不同

Docker 是轻量级的沙盒，在其中运行的只是应用
虚拟机里面还有额外的系统

## Dockerfile中copy和add指令区别

copy：文件复制
add：复制文件并解压缩，支持URL

## 本地的镜像文件存放在哪里

与 Docker 相关的本地资源都存放在`/var/lib/docker/`目录下
其中 container 目录存放容器信息，graph 目录存放镜像信息，aufs 目录下存放具体的内容文件

## 迁移Docker到另一台宿主机

将本地资源全部迁移过去即可，即`/var/lib/docker/`目录

## 如何查看镜像运行的环境变量

docker run 镜像名称 env

## 如何退出一个镜像的bash而不终止它

先按 Ctrl+p，后按 Ctrl+q，如果按 Ctrl+c 会使容器内的应用进程终止，进而会使容器终止

## 退出容器的时候自动删除

docker run 的时候加上参数 -rm，例如：docker run –rm -it ubuntu

## 可以在一个容器运行多个应用进程吗

一般不推荐

## 如何控制容器占用资源的大小

docker run 运行容器时，用 -c 来调整容器使用 CPU 的权重，用 -m 来调整容器使用内存的大小