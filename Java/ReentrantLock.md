# 加锁

![zoom=35](70e91b93dbc7965d1d5b37edab124cd0.png)

![zoom=40](91374b6541e335acbdecd5b6c1550d33.png)

根据上图，**公平锁和非公平锁**唯一的区别就在这里：
- 非公平锁：无论何时都不会先排队，而是直接尝试 CAS 竞争锁
- 公平锁：队列不空就先排队，队列为空才会尝试 CAS 竞争锁

![zoom=45](d45da21938db9f285b3572ed2ecf5511.png)

相关的详细代码如下：

![zoom=60](d1b575a2a6ab27301c80016a27211627.png)

```java
//如果没有获得锁，addWaiter(Node.EXCLUSIVE)，将当前线程封装为Node节点，插入到AQS的双向链表的结尾
//acquireQueued：加入双向链表之后死循环遍历
//  如果我是第一个排队的节点就tryAcquire，如果加锁失败就继续往下执行
//  如果不是第一个排队的节点，继续往下执行shouldParkAfterFailedAcquire
//      如果前置节点是SIGNAL状态，返回true，接下来【阻塞当前线程】
//      如果前置节点是CANCELLED状态，将这个CANCELLED的节点从链表移除，继续循环
//      如果前置节点是其他状态，就会CAS将其改变为SIGNAL，继续循环
//  总之最终要么是加锁成功，要么是线程阻塞
final boolean acquireQueued(final Node node, int arg) {
    boolean interrupted = false;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node))
                interrupted |= parkAndCheckInterrupt();
        }
    } catch (Throwable t) {
        cancelAcquire(node);
        if (interrupted)
            selfInterrupt();
        throw t;
    }
}

private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    if (ws == Node.SIGNAL)
        return true;
    if (ws > 0) {
        do {
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } else {
        pred.compareAndSetWaitStatus(ws, Node.SIGNAL);
    }
    return false;
}

private final boolean parkAndCheckInterrupt() {
    LockSupport.park(this);
    return Thread.interrupted();
}

public static void park(Object blocker) {
    Thread t = Thread.currentThread();
    setBlocker(t, blocker);
    UNSAFE.park(false, 0L);
    setBlocker(t, null);
}
```

# 解锁

![zoom=40](0cd5830962c8df9a74ba8afa71d97e45.png)

![zoom=60](83450878676e833137ad7928ba78ccbc.png)

# VS synchronized

- 一个是类，一个是关键字
- ReentrantLock 功能更加丰富，支持公平锁和非公平锁，还可以指定等待锁资源的时
- ReentrantLock 的锁基于 AQS，利用一个 CAS 维护的 volatile 变量实现
  synchronized 是基于 ObjectMonitor

-----

#并发 #锁 