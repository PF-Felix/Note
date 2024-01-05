# 🥇核心概念

## 各种容器编排工具

**Docker Compose**
是一个用于定义和运行多容器的工具，使用 YAML 作为配置文件，使用一个命令就可以根据配置创建并启动所有服务
局限是适合于单主机，不适用多主机分布式集群环境

**Docker Swarm**
内置于 Docker，可以进行集群级别的管理，使用 YAML 作为配置文件
服务规模可扩大可缩小，支持服务发现、负载均衡、滚动更新
2019年阿里云宣布弃用

**Mesos+Marathon**
Mesos 是一个分布式系统内核的开源集群管理器，Marathon 是一个基于容器的应用程序的编排框架
Mesos 能够在同样的集群机器上运行多种分布式系统类型，可以更加动态高效的共享资源
Mesos 提供服务失败检查、服务发布、服务伸缩、服务跟踪、服务监控、资源管理、资源共享
Mesos 可以扩展伸缩到数千个节点，适合于如果你拥有很多的服务器而且想构建一个大的集群的时候
但是大而全，往往就是对应的复杂和困难，使用户快速学习应用变得更加困难
2019年 Twitter 宣布弃用

**kubernetes**
目标是让部署容器化的应用变得简单且高效，提供了应用部署、规划、更新、维护一整套完整的机制
除了 Docker 之外还支持其他多种容器，如 Containerd、rkt、CoreOS 等
可以实现容器调度、资源管理、服务发现、健康检查、自动伸缩、更新升级

优点：容器编排、轻量级、开源、快速部署、弹性伸缩、负载均衡、自动部署、自动重启、自动伸缩

## 节点

**master节点**
K8S集群的管理节点，提供集群的资源访问入口
可以拥有分布式高可用的 etcd 存储服务，运行了 ApiServer、Scheduler、ControllerManager 服务

**worker节点**
也叫 node 节点，是运行 Pod 服务的节点、运行守护进程 Kubelet、负载均衡器 kube-proxy

## 组件

**ApiServer**
K8s集群内部各功能模块的通信（master节点）

**Scheduler**
负责集群资源调度，将 Pod 调度到相应的 node 节点上（master节点）

**ControllerManager**
维护集群状态，比如程序部署安排、故障检测、自动扩展、滚动更新（master节点）

**Etcd**
分布式数据库（master节点）

**Kubelet**
worker节点
向 apiserver 注册节点自身信息，处理 apiserver 下发到本节点的指令，管理 Pod 的生命周期，定期向 master 汇报节点资源的使用情况（使用 cAdvisor 监控节点资源）

**kubectl**
是一个命令行工具，可以控制K8S集群管理器，如查看资源，创建、删除和更新组件（master节点）

**kube-proxy**
是Service的负载均衡器，将某个Service的访问请求转发到后端的多个Pod实例上（worker节点）

## kubectl常用命令

`kubectl -h`查看帮助
![](829e67048981a0d55a2c7ce7e59edc15.png)
![](3b1e0e79e1f92a9789a179690c23cdd0.png)

```shell
#查看资源支持的版本
kubectl api-resources | grep deployment
kubectl api-resources | grep pod
kubectl api-resources | grep service

#查看资源详细信息
kubectl describe ns kube-system
kubectl describe pod nginx1
kubectl describe node master1

########################## Namespace ##############################
#创建/删除namespace，删除会删除其下所有的资源
kubectl create/delete namespace test

#查看所有namespace
kubectl get namespace/ns

#查看命名空间kube-system下的资源
kubectl get all -n kube-system
kubectl get pods/pod -n kube-system
kubectl get pods/pod -n kube-system -o wide
kubectl get services/service/svc -n kube-system

########################## Pod ##############################
#创建pod
kubectl run nginx1 --image=nginx
kubectl run nginx1 --image=nginx:1.15 -n test
kubectl apply -f xxx.yml
#删除pod
kubectl delete pod pod1 pod2 pod3
kubectl delete -f xxx.yml

#在容器中执行命令，如果不指定容器默认是Pod中的第一个
kubectl exec -it pod-nginx1 -n tt -- ls
kubectl exec -it pod-nginx1 -n tt -- hostname
kubectl exec -it pod-nginx1 -n tt -c c2 -- hostname

#进入容器
kubectl exec -it pod-nginx1 -n tt bash

########################## Controller ##############################
#删除deployment
kubectl delete deployment deploy-nginx1

#版本升级
kubectl set image deployment deploy-nginx1 c1=nginx:1.16 --record
#statefulset独有的，partition=2表示只有编号大于等于2的Pod才更新，默认partition=0
kubectl patch sts web -p '{"spec":{"updateStrategy":{"rollingUpdate":{"partition":2}}}}'
kubectl set image sts statefulset-nginx1 c1=nginx:1.16 --record

#扩容缩容
kubectl scale deployment deploy-nginx1 --replicas=2
kubectl scale sts statefulset-nginx1 --replicas=10

########################## Label ##############################
#查看资源标签信息
kubectl get node --show-labels

#给资源打标签
kubectl label node worker1 region=huanai env=test
kubectl label node worker1 region=huanai env=test --overwrite=true

#删除标签
kubectl label node worker1 region-

#查看资源，标签过滤
kubectl get node -l kubernetes.io/hostname=worker2
```

## Namespace

命名空间，用作资源隔离
常见的资源比如 pod、service、deployment 都是属于某一个 namespace 的

## Pod

Pod 是 K8S 中 中最小的计算单元，包含一个或多个容器，容器中运行着应用程序
Pod 的 IP 不是固定的，集群外不能直接访问
同一个 Pod 中所有容器网络共享

**YML创建Pod**

```yaml
apiVersion: v1
kind: Pod                  #资源类型
metadata:                  #数据元
  name: pod-nginx1         #资源名
  namespace: tt            #命名空间，默认是default
  labels:                  #标签
    env: dev
    sex: man
spec:
  containers:              #容器
    - name: c1
      image: nginx:1.15    #镜像
```

**资源限制**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-stress1
  namespace: tt
spec:
  containers:
    - name: c1
      image: polinux/stress
      resources:
        limits:
          memory: "200Mi"
        requests:
          memory: "150Mi"
      command: ["stress"]  #启动容器时执行的命令
      #生成一个进程分配150M内存1秒后释放
      args: ["--vm", "1", "--vm-bytes", "250M", "--vm-hang", "1"]
```

这个重启是无法启动的，如果把 250 改为 100 就可以成功启动了

**部署到特定Node**

`spec.nodeName`定义主机名可以将 pod 部署到特定的 node
`spec.nodeSelector`定义一些标签用于将 pod 调度到匹配标签的 node

**探针**

| 检查方式 | |
| --- | --- |
| exec | 执行命令，返回码是0表示健康 |
| httpget | 请求某个URL，响应码是 2XX or 3XX 表示健康 |
| tcp | 连接某个端口，若能建立连接表示健康 |

| 探针种类 |  |
| ---- | ---- |
| liveness | 如果探测失败，容器将被杀死 |
| readiness | 如果探测失败，与 pod 匹配的服务的端点列表将删除这个 pod 的 IP |
| startup | 如果提供了此探针，其他探针暂时禁用，直到此探针成功；如果探测失败，容器将被杀死 |
|  |  |

~~liveness-exec~~

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-liveness-exec
spec:
  containers:
  - name: c1
    image: nginx:1.15
    args:
    - /bin/sh
    - -c
    - touch /tmp/healthy; sleep 30; rm -rf /tmp/healthy
    livenessProbe:
      exec:
        command:
        - cat
        - /tmp/healthy
      initialDelaySeconds: 5    #pod启动延迟5秒后探测
      periodSeconds: 5          #每5秒探测1次

```

`watch kubectl get pods`

~~liveness-httpget~~

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-liveness-httpget
spec:
  containers:
  - name: c1
    image: nginx:1.15
    livenessProbe:
      httpGet:
        port: 80
        path: /
      initialDelaySeconds: 5
      periodSeconds: 5
```

`watch kubectl get pods`监控状态与重启次数
`kubectl exec -it pod-liveness-httpget -- rm -rf /usr/share/nginx/html/index.html`删除这个文件后发现 Pod 重启了一次

~~liveness-tcp~~

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-liveness-tcp
spec:
  containers:
  - name: c1
    image: nginx:1.15
    livenessProbe:
      tcpSocket:
        port: 80
      initialDelaySeconds: 5
      periodSeconds: 5
```

监控重启次数
`kubectl exec -it pod-liveness-tcp -- /usr/sbin/nginx -s stop`停掉 nginx 之后 Pod 重启一次

**postStart&preStop**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-poststart
spec:
  containers:
  - name: c1
    image: nginx:1.15
    lifecycle:
      postStart:
        exec:
          command:
          - mkdir
          - -p
          - /tmp/xxx
```

检查 xxx 目录是否存在`kubectl exec -it pod-poststart -- ls /tmp`

## Controller

控制器管理和运行 Pod，与 Pod 通过标签建立关系
控制器监控集群状态，致力于将当前状态转变为期望状态，比如删除 Pod 马上就有新 Pod 创建出来
如果控制器被删除，相关的 replicaset、service、pod 也将自动删除
因 Pod 都在同网段，因此可以通过 ip 互相通信

### Deployment

包含了 ReplicaSet
支持扩容缩容，查看《常用命令》，关注 Pod 的数量变化
支持升级版本，查看《常用命令》，先升级一部分再升级一部分，滚动更新，默认的升级策略
删除 Pod 马上就能看到又一个 Pod 被创建出来，先创建再删除

部署的是无状态应用
1、所有 Pod 无差别、无顺序之分、命名无规则
2、数据持久化：所有 Pod 共享存储
3、Pod 之间无需通信；Pod 名称随机IP随机因此做不到稳定通信（可以通过IP通信因为都在同网段）

**部署**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deploy-nginx1
spec:
  replicas: 1          #副本数量即pod的数量，deployment默认用了replicaset
  selector:
    matchLabels:
      env: dev         #表示deployment和控制器匹配带有此标签的pod
  template:            #pod的配置模板
    metadata:
      name: pod-nginx1
      labels:
        env: dev
    spec:
      containers:
      - name: c1
        image: nginx:1.15
```

部署之后查看如下图：
![](db3f73f039f2f0b40d7c200f7cd3a5ad.png)

在任意一个节点 curl PodIP 都能访问到 nginx 的默认页面，但在外网（比如宿主机）就无法访问

### StatefulSet

**同 Deployment 相比**
支持扩容缩容，查看《常用命令》
支持版本升级，查看《常用命令》，不同点是 deploy 一次性全升级，statefulset 支持灰度发布只升级一部分
删除 Pod 马上就能看到又一个 Pod 被创建出来（名称等不变），先删除再创建

部署的是有状态应用：
1、Pod 是有序的、命名有规则且名称固定不变
2、数据持久化：每个 pod 都有自己的存储保存数据，通过 volumeClaimTemplates 实现
3、Pod 之间有可能需要通信，Pod IP随机但名称固定，DNS可以将名称解析为IP，因此可以稳定通信

**部署**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx
  labels:
    app: nginx
spec:
  ports:
  - port: 80
    name: web
  clusterIP: None
  selector:
    app: nginx
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  selector:
    matchLabels:
      app: nginx
  serviceName: "nginx"
  replicas: 2
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        ports:
        - containerPort: 80
          name: web
        volumeMounts:
        - name: disk-ssd
          mountPath: /data
  volumeClaimTemplates:
  - metadata:
      name: disk-ssd
    spec:
      accessModes: [ "ReadWriteOnce" ]
      storageClassName: "nfs-client"
      resources:
        requests:
          storage: 20Gi
```

### DaemonSet

集群中每个 worker 都有且只有一个 pod，因此没有 replicas 字段，且不支持扩容缩容
当有 worker 节点加入集群，pod 就被调度到该节点；节点从集群移除，该节点的 pod 将被删除
可以理解为是特殊的 deployment

### Job

对于非耐久性任务，任务完成后，pod 需要结束运行，这个时候就要用到 Job

**一个简单例子**

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: job-nginx1
spec:
  completions: 10                               #执行job的次数
  parallelism: 1                                #执行job的并发数
  template:
    metadata:
      name: pod-nginx1
    spec:
      containers:
      - name: c1
        image: nginx:1.15
        command: ["echo", "xxx"]
      restartPolicy: Never
```

![](4fb7587f0c157d1c4d0aeb77b16636d8.png)

### CronJob

定期执行任务

**一个简单例子**

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cronjob-nginx1
spec:
  schedule: "*/1 * * * *"
  jobTemplate:
    spec:
      template:
        metadata:
          name: pod-nginx1
        spec:
          containers:
          - name: c1
            image: nginx:1.15
            command: ["echo", "xxx"]
          restartPolicy: Never
```

每分钟执行一次任务

## Service

- Pod 经常用后即焚，IP地址经常变化，无法稳定直接访问 Pod 提供的服务，于是有了 Service
- Service 把 Pod 的 IP地址加入端点列表（Endpoints），通过 Service 代理访问 Pod
- Service 和 Pod 通过标签关联，Service 通过标签感知 Pod IP地址的变化

Pod 通过 Service 实现**负载均衡**，底层实现是 kube-proxy 提供的**代理模式**，有三种
1. ~~userspace~~第一代，性能不高不推荐使用了
2. ~~iptables~~第二代，比第一代性能高
    1. 原理：通过 apiserver 的 watch 接口实时跟踪 service 与 Endpoint 的变更信息，并更新对应的iptables 规则，请求通过 iptables 的 NAT 机制路由到目标 Pod
3. ~~ipvs~~专门用于高性能负载均衡，第三代优于第二代，缺点是低版本的内核无法使用
    1. 原理：使用 iptables 的扩展 ipset，而不是直接调用 iptables 来生成规则链。iptables 规则链是一个线性的数据结构，ipset 则引入了带索引的数据结构，因此当规则很多时，也可以很高效地查找和匹配
4. 1.10版本前用 iptables
5. 1.11版本后可同时用 iptables、ipvs，默认ipvs，如果ipvs没有加载，会自动降级至iptables

service 有下面几种类型：

### ClusterIP

普通的 ClusterIP

```yaml
apiVersion: v1
kind: Service
metadata:
  name: sts-ip
spec:
  selector:
    env: dev
  ports:
  - port: 80
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: sts-ip
spec:
  serviceName: sts-ip
  replicas: 3
  selector:
    matchLabels:
      env: dev
  template:
    metadata:
      name: pod
      labels:
        env: dev
    spec:
      containers:
      - name: c1
        image: nginx:1.15
```

提供集群内部（Pod）可以访问的虚拟IP，通过 kube-proxy 做负载均衡访问各个 Pod（curl clusterIP）
deployment 情况下：Pod 的全限定域名就是 Pod 名称，通过 hostname -f 命令查看
statefulset 情况下：Pod 的全限定域名与用 headless service 是一样的

### Headless Service

没有IP，提供固定服务名，没有负载均衡功能，DNS 将其直接解析为 Pod IP 列表，可用做服务发现
集群内无法访问到 pod

举例1：搭配 deployment，pod 的全限定域名是随机的

```yaml
apiVersion: v1
kind: Service
metadata:
  name: deploy-headless
spec:
  type: ClusterIP
  clusterIP: None
  selector:
    env: dev
  ports:
  - port: 8080
    targetPort: 80
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deploy-headless
spec:
  replicas: 3
  selector:
    matchLabels:
      env: dev
  template:
    metadata:
      name: busybox
      labels:
        env: dev
    spec:
      containers:
      - name: c1
        image: busybox:1.28.3
        command: ["/bin/sh", "-c", "tail -f /dev/null"]
```

![](b04175fe91c80943141f3fb0f672e084.png)

举例2：搭配 statefulset，pod 的全限定域名是固定不变的

```yaml
apiVersion: v1
kind: Service
metadata:
  name: sts-headless
spec:
  clusterIP: None
  selector:
    env: dev
  ports:
  - port: 80
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: sts-headless
spec:
  serviceName: sts-headless
  replicas: 3
  selector:
    matchLabels:
      env: dev
  template:
    metadata:
      name: pod
      labels:
        env: dev
    spec:
      containers:
      - name: c1
        image: busybox:1.28.3
        command: ["/bin/sh", "-c", "tail -f /dev/null"]

```

![](a8393b806712ef35a785b0d47bbc27f7.png)

### NodePort

```yaml
apiVersion: v1
kind: Service
metadata:
  name: deploy-nodeport
spec:
  type: NodePort
  selector:
    env: dev
  ports:
  - port: 80
    targetPort: 80
    nodePort: 30001
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deploy-nodeport
spec:
  replicas: 3
  selector:
    matchLabels:
      env: dev
  template:
    metadata:
      name: pod
      labels:
        env: dev
    spec:
      containers:
      - name: c1
        image: nginx:1.15
```

在 ClusterIP 的基础上，在每个 Node 上分配一个端口作为集群访问入口
首先它是一个普通的 service
而且集群内`curl 10.0.0.14:30001`可以访问到 Pod，也支持负载均衡，能够给外网访问了

### LoadBalance

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx
spec:
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.15
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 80

---
apiVersion: v1
kind: Service
metadata:
  name: nginx
spec:
  ports:
  - port: 8090
    protocol: TCP
    targetPort: 80
  selector:
    app: nginx
  type: LoadBalancer
```

需要搭配云服务负载均衡器
![](b7d604c3a0f913cdc6f9b76d15d898ce.png)

首先它是一个普通的 service，而且它是一个 NodePort Service 只不过端口不能指定
使用外接负载均衡器完成到服务的分发，需要`spec.status.loadBalancer`指定外接IP地址

### ExternalName

可以将集群外部的服务引入，实现了集群内部 Pod 和集群外部服务通信

## Label

标签是一组 KV，可以附加到各种资源上如 node、service、pod，用于关联资源

```shell
#查看Pod的标签
kubectl get pods --show-labels -n kube-system

#查看Pod用标签过滤
kubectl get pods -n kube-system -l env=test
kubectl get pods -n kube-system -l "zone in (A,B,C)"

#给Pod打标签
kubectl label pod pod-nginx1 -n kube-system region=huanai zone=A env=test bussiness=game
```

## Ingress

Ingress 用于将不同 URL 的访问请求转发到后端不同的 Service
入口控制器接收所有访问请求，基于 Ingress 规则，将客户端请求直接转发到 Service 对应的 Endpoint 上，从而跳过 kube-proxy 的转发功能

## Ingress最佳实践

```shell
#下载ingress-nginx控制器
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.0/deploy/static/provider/cloud/deploy.yaml

#修改service.type=NodePort 80端口暴露的端口为30080

#开启
kubectl apply -f deploy.yaml

#扩容成五个
kubectl scale deployment ingress-nginx-controller -n ingress-nginx --replicas=5

#最终健康的状态如下图所示
```

![](1b23ef23a6e14f42481c5d59148739b6.png)

### 一个应用

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx
  namespace: ingress-nginx
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: c1
        image: nginx:1.15
        imagePullPolicy: IfNotPresent
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
  namespace: ingress-nginx
  labels:
    app: nginx
spec:
  ports:
  - port: 80
    targetPort: 80
  selector:
    app: nginx
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ingress-nginx
  namespace: ingress-nginx
  annotations:
    ingressclass.kubernetes.io/is-default-class: "true"
    kubernetes.io/ingress.class: nginx
spec:
  rules:
  - host: www.kubemsb1.com
    http:
      paths:
      - pathType: Prefix
        path: "/"
        backend:
          service:
            name: nginx-service
            port:
              number: 80
```

上述代码 apply 之后，得到的状态如下图

![](d01fafa4772abeeeb66e851f95797974.png)
![](c95b8dc3f97b5c3fa875c88450347972.png)

先验证 service/nginx-service 是负载均衡的

![](738aa98258e2caad25bd8c1efe7e94f1.png)
![](c1ed23f546dd7aa2f4bf6429f735daa1.png)

宿主机访问测试，也是负载均衡

```shell
#配置hosts
10.0.0.14 www.kubemsb.com
10.0.0.15 www.kubemsb.com

#刷新DNS缓存
ipconfig /flushdns
```

![](5e4af1eef6453f0496d36e0ddc99ac34.png)

### 两个应用两个域名

在一个应用的基础上，增加第二个应用

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-1
  namespace: ingress-nginx
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx-1
  template:
    metadata:
      labels:
        app: nginx-1
    spec:
      containers:
      - name: c1
        image: nginx:1.15
        imagePullPolicy: IfNotPresent
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-service-1
  namespace: ingress-nginx
  labels:
    app: nginx-1
spec:
  ports:
  - port: 80
    targetPort: 80
  selector:
    app: nginx-1
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ingress-nginx-1
  namespace: ingress-nginx
  annotations:
    ingressclass.kubernetes.io/is-default-class: "true"
    kubernetes.io/ingress.class: nginx
spec:
  rules:
  - host: www.kubemsb1.com
    http:
      paths:
      - pathType: Prefix
        path: "/"
        backend:
          service:
            name: nginx-service-1
            port:
              number: 80
```

![](10620502407f99d4e7b9a3b5793aba0d.png)
![](e916645338916804cd219088c834dbc9.png)

宿主机访问测试，也是负载均衡

```
#hosts
10.0.0.14 www.kubemsb.com
10.0.0.15 www.kubemsb.com
10.0.0.14 www.kubemsb1.com
10.0.0.15 www.kubemsb1.com
```

![](d6abb8a11bef5d66eb75619fca0c24b2.png)

### 一个应用两个URL

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-1
  namespace: ingress-nginx
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx-1
  template:
    metadata:
      labels:
        app: nginx-1
    spec:
      containers:
      - name: c1
        image: nginx:1.15
        imagePullPolicy: IfNotPresent
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-service-1
  namespace: ingress-nginx
  labels:
    app: nginx-1
spec:
  ports:
  - port: 80
    targetPort: 80
  selector:
    app: nginx-1
```
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-2
  namespace: ingress-nginx
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx-2
  template:
    metadata:
      labels:
        app: nginx-2
    spec:
      containers:
      - name: c1
        image: nginx:1.15
        imagePullPolicy: IfNotPresent
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-service-2
  namespace: ingress-nginx
  labels:
    app: nginx-2
spec:
  ports:
  - port: 80
    targetPort: 80
  selector:
    app: nginx-2
```
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ingress-nginx
  namespace: ingress-nginx
  annotations:
    ingressclass.kubernetes.io/is-default-class: "true"
    kubernetes.io/ingress.class: nginx
spec:
  rules:
  - host: www.kubemsb.com
    http:
      paths:
      - pathType: Prefix
        path: "/svc1"
        backend:
          service:
            name: nginx-service-1
            port:
              number: 80
      - pathType: Prefix
        path: "/svc2"
        backend:
          service:
            name: nginx-service-2
            port:
              number: 80
```

再在四个 Pod 中做下面的操作
![](db92c9e74d172d4214b55cb25ad6b9cf.png)

然后访问`http://www.kubemsb.com:30080/svc1`和`http://www.kubemsb.com:30080/svc2`就可以查看负载均衡效果了

-----

如果使用上面的`service.type=NodePort`的 ingress-nginx 控制器，在前面加一个负载均衡的代理

# 🥇高可用集群部署

> k8s版本：v1.21

## 主机规划

| 角色&主机名 | IP地址 |
| --- | --- |
| master1 | 10.0.0.11 |
| master2 | 10.0.0.12 |
| master3 | 10.0.0.13 |
| worker1 | 10.0.0.14 |
| worker2 | 10.0.0.15 |
| LB1 | 10.0.0.16 |
| LB2 | 10.0.0.17 |
| node8 | 10.0.0.18 |
| VIP | 192.168.0.100 |
| Pod | 10.244.0.0/16 |
| Service | 10.96.0.0/12 |

## 前置准备

### 主机名与IP映射

> 所有节点都需要配置

```shell
10.0.0.11 master1
10.0.0.12 master2
10.0.0.13 master3
10.0.0.14 worker1
10.0.0.15 worker2
10.0.0.16 lb1
10.0.0.17 lb2
```

### 关闭防火墙

> 所有节点都需要配置

```shell
systemctl disable firewalld
systemctl stop firewalld
firewall-cmd --state
```

### 关闭selinux

> 所有节点都需要配置

```shell
sed -ri 's/SELINUX=enforcing/SELINUX=disabled/' /etc/selinux/config
```

### 关闭交换分区

> 所有节点都需要配置

```shell
vi /etc/fstab

#把下面这行代码注释掉
# /dev/mapper/centos-swap swap xxxxx

reboot
```

### 时间同步

> 所有节点都需要配置

```shell
yum -y install ntpdate
ntpdate cn.pool.ntp.org | ntp[1-7].aliyun.com
```

### 系统优化

> 所有节点都需要配置

```shell
ulimit -SHn 65535

cat <<EOF >> /etc/security/limits.conf
* soft nofile 655360
* hard nofile 131072
* soft nproc 655350
* hard nproc 655350
* soft memlock unlimited
* hard memlock unlimited
EOF
```

### 内核升级

> 所有节点都需要配置

```shell
#导入elrepo gpg key
rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org
#安装elrepo YUM源仓库
yum -y install https://www.elrepo.org/elrepo-release-7.0-4.el7.elrepo.noarch.rpm
#安装kernel-ml版本，ml为长期稳定版本，lt为长期维护版本
yum --enablerepo="elrepo-kernel" -y install kernel-ml.x86_64
#设置grub2默认引导为0
grub2-set-default 0
#重新生成grub2引导文件
grub2-mkconfig -o /boot/grub2/grub.cfg

#重启后，需要验证内核是否为更新对应的版本
reboot
uname -r
```

### 内核优化

> 所有节点都需要配置

```shell
cat <<EOF > /etc/sysctl.d/k8s.conf
net.ipv4.ip_forward = 1
net.bridge.bridge-nf-call-iptables = 1
net.bridge.bridge-nf-call-ip6tables = 1
fs.may_detach_mounts = 1
vm.overcommit_memory=1
vm.panic_on_oom=0
fs.inotify.max_user_watches=89100
fs.file-max=52706963
fs.nr_open=52706963
net.netfilter.nf_conntrack_max=2310720

net.ipv4.tcp_keepalive_time = 600
net.ipv4.tcp_keepalive_probes = 3
net.ipv4.tcp_keepalive_intvl =15
net.ipv4.tcp_max_tw_buckets = 36000
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_max_orphans = 327680
net.ipv4.tcp_orphan_retries = 3
net.ipv4.tcp_syncookies = 1
net.ipv4.tcp_max_syn_backlog = 16384
net.ipv4.ip_conntrack_max = 131072
net.ipv4.tcp_max_syn_backlog = 16384
net.ipv4.tcp_timestamps = 0
net.core.somaxconn = 16384
EOF
```
```shell
sysctl --system
reboot
```

### 禁用NetworkManager

> 所有节点都需要配置

```shell
systemctl stop NetworkManager
systemctl disable NetworkManager
```

### IPVS管理工具安装及模块加载

> 所有集群节点安装，负载均衡节点不用安装

```shell
yum -y install ipvsadm ipset sysstat conntrack libseccomp
```

```shell
cat > /etc/sysconfig/modules/ipvs.modules <<EOF
#!/bin/bash
modprobe -- ip_vs
modprobe -- ip_vs_rr
modprobe -- ip_vs_wrr
modprobe -- ip_vs_sh
modprobe -- nf_conntrack
EOF
```

```shell
cat >/etc/modules-load.d/ipvs.conf <<EOF
ip_vs
ip_vs_lc
ip_vs_wlc
ip_vs_rr
ip_vs_wrr
ip_vs_lblc
ip_vs_lblcr
ip_vs_dh
ip_vs_sh
ip_vs_fo
ip_vs_nq
ip_vs_sed
ip_vs_ftp
ip_vs_sh
nf_conntrack
ip_tables
ip_set
xt_set
ipt_set
ipt_rpfilter
ipt_REJECT
ipip
EOF
```

```shell
#设置为开机启动
systemctl enable --now systemd-modules-load.service

#授权、运行、检查是否加载
chmod 755 /etc/sysconfig/modules/ipvs.modules && bash /etc/sysconfig/modules/ipvs.modules && lsmod | grep -e ip_vs -e nf_conntrack
```

### 安装docker

> 所有集群节点安装，负载均衡节点不用安装

```shell
wget -O /etc/yum.repos.d/docker-ce.repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
yum -y install docker-ce
systemctl enable docker
systemctl start docker

vi /etc/docker/daemon.json
{
    "exec-opts": ["native.cgroupdriver=systemd"],
    "registry-mirrors": ["https://o4osh0q0.mirror.aliyuncs.com"]
}
systemctl daemon-reload
systemctl restart docker
```

### 传输文件免密

> 只在master节点

```shell
ssh-keygen
ssh-copy-id root@master2
ssh-copy-id root@master3
ssh-copy-id root@worker1
ssh-copy-id root@worker2
ssh-copy-id root@lb1
ssh-copy-id root@lb2
```

## HAProxy+Keepalived

> master节点的高可用，两个主机都得配置
> kublet、kube-proxy 配置中静态指定了某个 kube-apiserver 实例的 IP，如果该实例挂掉可能服务异常

[软件负载均衡选项指南](https://github.com/kubernetes/kubeadm/blob/main/docs/ha-considerations.md#options-for-software-load-balancing)

```shell
yum -y install haproxy keepalived
```

```shell
#修改haproxy配置，两个节点完全相同
vi /etc/haproxy/haproxy.cfg

#添加下面的配置
frontend apiserver
    bind *:6443
    mode tcp
    option tcplog
    default_backend apiserver

backend apiserver
    option httpchk GET /healthz
    http-check expect status 200
    mode tcp
    option ssl-hello-chk
    balance     roundrobin
    server  master1 10.0.0.11:6443 check
    server  master2 10.0.0.12:6443 check
    server  master3 10.0.0.13:6443 check

#两个节点都需要执行
systemctl enable haproxy
systemctl start haproxy
```

```shell
#在lb1修改keepalived配置
vi /etc/keepalived/keepalived.conf

#新增下面配置
vrrp_script chk_apiserver {
    script "/etc/keepalived/check_apiserver.sh"
    interval 5
    weight -5
    fall 2
    rise 1
}

#修改下面配置
vrrp_instance VI_1 {
    state MASTER #主节点，lb2改为备节点BACKUP
    interface ens33 #改网卡
    virtual_router_id 51 #两个节点相同
    priority 99 #排序，两个节点不能相同
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.0.100  #虚拟IP，两个节点不能相同
    }
    track_script {
       chk_apiserver
    }
}


vi /etc/keepalived/check_apiserver.sh

#!/bin/bash

err=0
for k in $(seq 1 3)
do
    check_code=$(pgrep haproxy)
    if [[ $check_code == "" ]]; then
        err=$(expr $err + 1)
        sleep 1
        continue
    else
        err=0
        break
    fi
done

if [[ $err != "0" ]]; then
    echo "systemctl stop keepalived"
    /usr/bin/systemctl stop keepalived
    exit 1
else
    exit 0
fi

chmod +x /etc/keepalived/check_apiserver.sh

#上面两个文件scp传给lb2，然后修改lb2的keepalived.conf

#两个节点都需要执行
systemctl enable keepalived
systemctl start keepalived
```

验证：`ip addr`一个可以看到 VIP 一个看不到
[https://10.0.0.100:6443/healthz](https://10.0.0.100:6443/healthz) 这个链接可以访问了，这也是 kubeadm 初始化 k8s 集群需要测试的链接

## kubeadm部署K8S集群v1.21

### 软件安装

> 所有集群节点安装，负载均衡节点不用安装

```shell
#准备YUM源，前后使用yum repolist验证
vi /etc/yum.repos.d/k8s.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg

#软件安装
#kubeadm 初始化集群、管理集群
yum -y install kubeadm-1.21.0 kubelet-1.21.0 kubectl-1.21.0
kubeadm version
kubectl version
systemctl enable kubelet

#配置kubelet，为了实现docker使用的cgroupdriver与kubelet使用的cgroup的一致性
vi /etc/sysconfig/kubelet
KUBELET_EXTRA_ARGS="--cgroup-driver=systemd"
```

### 下载镜像

> 所有集群节点安装，负载均衡节点不用安装

```shell
#查看远程镜像列表，使用kubeadm version查到的版本号
kubeadm config images list --kubernetes-version=v1.21.0

#从阿里云下载镜像
docker pull registry.aliyuncs.com/google_containers/kube-apiserver:v1.21.0
docker pull registry.aliyuncs.com/google_containers/kube-controller-manager:v1.21.0
docker pull registry.aliyuncs.com/google_containers/kube-scheduler:v1.21.0
docker pull registry.aliyuncs.com/google_containers/kube-proxy:v1.21.0
docker pull registry.aliyuncs.com/google_containers/pause:3.4.1
docker pull registry.aliyuncs.com/google_containers/etcd:3.4.13-0
docker pull registry.aliyuncs.com/google_containers/coredns:v1.8.0
```

### 集群初始化

```shell
#在master1初始化集群
kubeadm init \
  --apiserver-advertise-address=10.0.0.11 \
  --image-repository registry.aliyuncs.com/google_containers \
  --kubernetes-version v1.21.0 \
  --service-cidr=10.96.0.0/12 \
  --pod-network-cidr=10.244.0.0/16 \
  --control-plane-endpoint 10.0.0.100 --upload-certs \
  --ignore-preflight-errors=all

#查看输出中最后的几个命令并执行

#查看已经启动的集群节点
kubectl get nodes

#重新生成token，输出的命令是xxx
kubeadm token create --print-join-command
#生成证书用于新master加入集群，输出的证书是yyy
kubeadm init phase upload-certs --upload-certs
#新master加入集群
xxx --control-plane --certificate-key yyy
#新worker加入集群
xxx
```

![](81e401645c89b8c41f16db79a37180e1.png)

### 集群网络准备

> 只在master1做就可以了，使用 Calico

[官网](https://docs.tigera.io/archive/v3.22/getting-started/kubernetes/self-managed-onprem/onpremises#install-calico-with-kubernetes-api-datastore-50-nodes-or-less)

安装 calico

```shell
#注意K8S与calico插件和兼容版本
curl https://projectcalico.docs.tigera.io/archive/v3.22/manifests/calico.yaml -O

#改为使用集群初始化时pod的CIDR
- name: CALICO_IPV4POOL_CIDR
  value: "10.244.0.0/16"

kubectl apply -f calico.yaml
#直接执行可能部署失败，可以先下载镜像再执行
#如果部署失败需要重新部署可以将apply改为delete先执行一遍
docker pull docker.io/calico/cni:v3.22.5
docker pull docker.io/calico/pod2daemon-flexvol:v3.22.5
docker pull docker.io/calico/node:v3.22.5
docker pull docker.io/calico/kube-controllers:v3.22.5

#安装成功看下面图片
```

![zoom=65](24089f38a2a5406f56f435ac316a87c4.png)

![zoom=60](515e7c11be31fc6b23baed57f144f9a0.png)

![zoom=60](25e17bc47a224bd333c61da65df3d244.png)

[kubectl get cs 组件不健康的解决办法](https://www.cnblogs.com/xhg-Cathy/p/15714949.html)

安装 calicoctl

```shell
wget https://github.com/projectcalico/calico/releases/download/v3.22.5/calicoctl-linux-amd64

mv calicoctl-linux-amd64 /usr/bin/calicoctl
chmod +x /usr/bin/calicoctl

calicoctl version
```

## 安装kuboard

```shell
docker run -d \
  --restart=always \
  --name=kuboard \
  -p 81:80/tcp \
  -p 10081:10081/tcp \
  -e KUBOARD_ENDPOINT="http://10.0.0.18:81" \
  -e KUBOARD_AGENT_SERVER_TCP_PORT="10081" \
  -v /root/kuboard-data:/data \
  eipwork/kuboard:v3
```

# 🥇面试题

## K8S和Docker的关系

Docker 管理容器的生命周期，将应用程序运行所需的设置和依赖项打包到一个容器中，从而实现了可移植性
K8S 用于关联和编排在多个主机上运行的容器

## K8S集群

### 部署方式

kubeadm：也是推荐的一种部署方式
二进制：较深入的部署方式，但是比较麻烦
sealos：极速部署，封装了 kubeadm

### 如何添加节点和删除节点

添加节点：

```shell
#先查看有没有有效的token
kubeadm token list

#没有token就创建一个
kubeadm token create --print-join-command

#将新节点加入
kubeadm join 10.0.0.100:6443 --token d8q8he.ormit5mgteofldmc --discovery-token-ca-cert-hash sha256:1959a43f007ffef5e2f078245b05be5f71d53fd31b831656f3312254bf24ea54
```

删除节点：

先使用`kubectl drain`将该节点的 Pod 进行驱逐，再用`kubectl delete node xx`移除此节点
