# 数据持久化

[内存数据持久化的两种方式](内存数据持久化的两种方式.md)

HDFS 中也有这两种方式
- 日志：EditsLog
- 镜像、快照：FsImage

但是 HDFS 并没有单独使用任何一种持久化方式，而是结合了两种方式的优点
数据恢复时：先加载最近时点的 FsImage + 再加载增量的 EditsLog

**举例说明 FsImage 是怎么做滚动更新的？**

- 由 NameNode 备份
- 假设 8点备份、9点备份、10点备份
- 8点：NameNode 第一次开机，此时只写一次 FsImage
- 9点：EditsLog 记录的是 8~9 点的日志，只需要将这部分日志更新到 8点的 FsImage 中就能得到9点的 FsImage
- 写日志的是 NameNode #待办事项 寻求另外一台机器来做？Secondary NameNode

# 数据恢复与安全模式

![zoom=40](2afaec369e64602daab21576a1baca85.png)

-----

#分布式 #大数据