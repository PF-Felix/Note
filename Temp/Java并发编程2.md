### volatile的内存语义

> volatile 的读内存语义：当读一个 volatile 变量时，JMM 将该线程对应的本地内存置为无效，从主内存中读取变量
> volatile 的写内存语义：当写一个 volatile 变量时，JMM 将该线程对应的本地内存中的共享变量值刷新到主内存

实现原理是使用内存屏障

> 在每个 volatile 写操作前插入 StoreStore 屏障，后插入 StoreLoad 屏障
> 在每个 volatile 读操作后插入 LoadLoad 屏障，后插入 LoadStore 屏障

PS：这里的内存屏障是 JVM 层面的，并非操作系统层面的，但是本质上还是使用操作系统的内存屏障

> 写内存屏障：促使处理器将当前 StoreBuffer 的值写回主存
> 读内存屏障：促使处理器处理 InvalidateQueue，避免 StoreBuffer 和 InvalidateQueue 的非实时性带来的问题

![image-20230405150552642](C:\Note\x.附件夹\image-20230405150552642.png)

内存屏障会限制重排序：

> 限制 volatile 变量之间的重排序
> 限制 volatile 变量与普通变量之间的重排序

![image-20230405150813921](C:\Note\x.附件夹\image-20230405150813921.png)

![image-20230405150822366](C:\Note\x.附件夹\image-20230405150822366.png)

### 锁的内存语义

> 线程释放锁前，JMM 将共享变量的最新值刷新到主内存中
> 线程获取锁时，JMM 将线程对应的本地内存置为无效，需要共享变量的时候必须去主内存中读取，同时保存在本地内存
> 可以看出，锁释放和 volatile 写具有相同的内存语义；锁获取和 volatile 读具有相同的内存语义

==实现原理==

内置锁（synchronized）

> 同步块：编译器会在同步块的入口位置和退出位置分别插入 monitorenter 和 monitorexit 字节码指令
> 同步方法：编译器会在 Class 文件的方法表中将该方法的 access_flags 字段中的 synchronized 标志位置 1，表示该方法是同步方法并使用调用该方法的对象

显式锁-以 ReentrantLock 公平锁为例

> 加锁：首先会调用 getState() 方法读 volatile 变量 state
> 解锁：setState(int newState) 方法写 volatile 变量 state

显式锁-以 ReentrantLock 非公平锁为例

> 加锁：首先会使用 CAS 更新 volatile 变量 state，更新不成功再去采用公平锁的方式（CAS保证了原子性）
> 解锁：setState(int newState) 方法写 volatile 变量   state  


## 锁优化

==减小锁的粒度==

比如 ConcurrentHashMap 只锁数组的某个元素所在链表

==使用读写锁==

比如 ReentrantReadWriteLock

==读写分离==

CopyOnWriteArrayList

## 锁的种类

==公平锁、非公平锁==

公平锁：如果有别的线程在排队等待锁了，那我就老老实实的往后排队
非公平锁：即使有别的线程在排队等待锁，我也要先尝试获得锁一下，拿到就插队成功，拿不到锁再排队

synchronized 是非公平锁
ReentrantLock、ReentrantReadWriteLock 可以实现公平锁和非公平锁

==悲观锁、乐观锁==

悲观锁：拿不到锁就阻塞等待；synchronized、ReentrantLock、ReentrantReadWriteLock 都是悲观锁
乐观锁：拿不到锁不阻塞，可以继续做别的事情；CAS 是乐观锁的一种实现

==可重入锁、不可重入锁==

==互斥锁、共享锁==

## ReentrantLock

### Condition

lock.newCondition() 得到的对象，提供了 await 和 signal 方法实现了类似 wait 和 notify 的功能
想执行 await、signal 就必须先持有 lock 锁

await：
1、将当前线程封装成 Node 添加到 Condition 单向链表中
2、当前线程释放锁 Node 脱离 AQS 双向链表

signal：
1、脱离 Condition 单向链表
2、Node 加入 AQS 双向链表

## ReentrantReadWriteLock

比 ReentrantLock 效率高
读读之间不互斥，可以读和读操作并发执行
涉及到了写操作就是互斥的

基于 AQS 实现，对 state 进行操作，读锁基于 state 的高16位进行操作，写锁基于 state 的低16位进行操作

# 并发工具类

## Semaphore

通常使用 Semaphore 限制同时并发的线程数量

线程执行操作时先通过 acquire 方法获得许可（计数器不为0，计数器减1），执行完毕再通过 release 方法释放许可（计数器加1）
如果无可用许可（计数器为0），acquire 方法将一直阻塞，直到其它线程释放许可

很像线程池，但线程池的线程数量是一定的可复用，Semaphore 并没有复用


# 异步编程

## FutureTask

FutureTask 是一个异步任务的类，一般配合 Callable 使用，可以取消任务，查看任务是否完成，获取任务的返回结果

状态流转：

```java
/**
 * NEW -> COMPLETING -> NORMAL           任务正常执行，并且返回结果也正常返回
 * NEW -> COMPLETING -> EXCEPTIONAL      任务正常执行，但是结果是异常
 * NEW -> CANCELLED                      任务被取消
 * NEW -> INTERRUPTING -> INTERRUPTED    任务被中断
 */
//记录任务的状态
private volatile int state;
//任务被构建之后的初始状态
private static final int NEW          = 0;
private static final int COMPLETING   = 1;
private static final int NORMAL       = 2;
private static final int EXCEPTIONAL  = 3;
private static final int CANCELLED    = 4;
private static final int INTERRUPTING = 5;
private static final int INTERRUPTED  = 6;
```

## CompletableFuture

```java
/**
 * CompletableFuture
 * 任务编排
 */
public class CompletableFutureTest {

    /**
     * runAsync 无返回值
     * supplyAsync 有返回值
     * 这两个方法都可以指定线程池，如果没有指定的话默认使用 ForkJoinPool
     */
    @Test
    public void testRunAsync() throws Exception {
        CompletableFuture.runAsync(() -> {
            System.out.println("任务1");
        });

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2");
            return "任务2的结果";
        });
        while (completableFuture.isDone()) {
            System.out.println(completableFuture.get());
            break;
        }
    }

    /**
     * accept 前置任务完成，就触发回调，回调能够得到前置任务的返回值
     * acceptAsync 能够指定线程池，如果不指定就用默认的线程池
     * thenRun thenRunAsync 同上 区别在于这个方法不能接收前置方法的返回值
     * thenApply thenApplyAsync 同上 区别在于这个方法能接收前置方法的返回值，自己也有返回值
     */
    @Test
    public void testAccept() throws Exception {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("任务1");
        }).thenAccept(c -> {
            System.out.println("任务2");
            System.out.println(c);
        }).get();

        System.out.println("=====");

        CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1");
            return "任务1的结果";
        }).thenAccept(c -> {
            System.out.println("任务2");
            System.out.println(c);
        }).get();
    }

    /**
     * acceptEither 任何一个任务完成，就触发回调
     * acceptEitherAsync 能够指定线程池，如果不指定就用默认的线程池
     * runAfterEither applyToEither 是类似的
     *
     * 同理：runAfterBoth thenAcceptBoth thenCombine 同上
     */
    @Test
    public void testAcceptEither() throws Exception {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("任务1");
        }).acceptEither(
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("任务2");
                }),
                (c) -> {
                    System.out.println("任务3");
                    System.out.println(c);
                }
        ).get();

        System.out.println("=====");

        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("任务1");
            return "任务1的结果";
        }).acceptEither(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("任务2");
                    return "任务2的结果";
                }),
                (c) -> {
                    System.out.println("任务3");
                    System.out.println(c);
                }
        ).get();
    }

    /**
     * exceptionally thenCompose handle 可以用于处理异常
     *
     * allOf 的方式是让内部编写多个 CompletableFuture 的任务，多个任务都执行完后，才会继续执行后续拼接的任务；无返回值
     * anyOf 的方式是让内部编写多个 CompletableFuture 的任务，只要有一个前置任务执行完毕就继续执行后续拼接的任务
     */
}
```

# !!!



# AQS

## 介绍

AQS 是一个抽象类
ReentrantLock、ThreadPoolExecutor、阻塞队列、CountDownLatch、Semaphore、CyclicBarrier 都是基于AQS实现

AQS 的核心实现：

- 一个 volatile 修饰的 int 类型的 state 变量（基于 CAS 修改），解决并发编程的三大问题：原子性、可见性、有序性
- 一个存储阻塞线程的双向链表，如果一个线程获取不到资源就会封装成一个 Node 对象并放入这个链表中

如果面试问到了这个问题，可以扩展谈谈 ReentrantLock

## 为什么用双向链表

是为了方便操作

线程在排队期间是可以取消的，取消某个节点需要将前继节点的 next 指向后继节点
如果是单向链表，只能找到前继节点或后继节点其中一个，要找到另一个需要遍历整个链表，这样效率低
所以采用双向链表的结构

## 为什么有一个虚拟的head节点

只是为了方便操作，没有虚拟head节点也可以实现 AQS

# ReentrantLock释放锁时为什么要从后往前找有效节点

因为线程排队加入链表不是原子性的（addWaiter 方法）

1. 将当前节点的前置节点指向 tail 代表的节点
2. CAS 将 tail 指向当前节点
3. 将前置节点的后继节点指向当前节点

如果从前往后遍历，此时恰好有个节点排队入链表尾部，很可能遍历的时候把这个节点漏掉（从后往前没有这个问题）