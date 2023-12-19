# CountDownLatch

- 核心实现就是一个计数器
- 对象创建的时候定义任务个数比如三个任务，在三个任务全部处理完毕后，再执行后续操作，这三个任务是并行处理的
- 执行`await`方法，判断 state 是否为0，如果为0直接执行后续任务，如果不为0插入到AQS的双向链表并挂起线程
- 执行`countDown`方法，代表一个任务结束，计数器减1，如果计数器变为0就唤醒阻塞的线程

![zoom=70](Pasted%20image%2020231219104606.png)

代码举例如下：CountDownLatch 的工作用 join 也可以做到

```java
//join() 将调用者合并入当前线程，当前线程等待 join 线程执行结束
public class JoinTest {
    public static void main(String[] args) throws InterruptedException {
        Thread parser1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("部件一生产完成");
            }
        });

        Thread parser2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("部件二生产完成");
            }
        });

        parser1.start();
        parser2.start();
        parser1.join();
        parser2.join();
        System.out.println("机器人组装完毕");
    }
}

public class CountDownLatchTest {
    static CountDownLatch c = new CountDownLatch(2);
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("部件1生产完成");
                c.countDown();
                System.out.println("部件2生产完成");
                c.countDown();
            }
        }).start();

        c.await();
        System.out.println("机器人组装完成");
    }
}
```

输出：

```
部件一生产完成
部件二生产完成
机器人组装完毕
```

# CyclicBarrier

原理：多个线程等待，直到线程数量达到临界点再继续执行

1. 构造函数中传入的参数为线程数量
2. 调用`await`方法的线程进入阻塞状态
3. 最后一个线程调用`await`方法会唤醒其他所有阻塞线程

PS：CountDownLatch 的计数器只能设置一次，而 CyclicBarrier 的计数器可以使用 reset() 方法重置

-----

#并发 