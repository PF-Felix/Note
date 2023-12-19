存储结构：数组+链表+红黑树

通过 CAS 和 synchronized 互斥锁实现的**线程安全**
- CAS：在没有hash冲突时（Node要放在数组上时）
- synchronized：在出现hash冲突时（Node存放的位置已经有数据了）

# put

返回值是 put 之前 key 的 value
put 会覆盖原值，putIfAbsent 不会

```java
public V put(K key, V value) {
    return putVal(key, value, false);
}

public V putIfAbsent(K key, V value) {
    return putVal(key, value, true);
}
```

## 添加数据到数组

```java
final V putVal(K key, V value, boolean onlyIfAbsent) {
    //不允许key或者value出现null（HashMap 支持 null value 与 null key）
    if (key == null || value == null) throw new NullPointerException();

    //(h ^ (h >>> 16)) & HASH_BITS; HashMap中没有HASH_BITS
    //h ^ (h >>> 16) 是扰动计算，目的是尽量使元素分布均匀减少 hash 碰撞，下面举例说明
    //    假设数组的初始化容量为16即10000，length-1=15即1111
    //    假设几个对象的 hashCode 为 1100 10010、1110 10010、11101 10010，不做扰动计算将发生 hash 碰撞（取模值相等）
    //HASH_BITS = ‭01111111111111111111111111111111‬
    //HASH_BITS 让 hash 的最高位肯定为0代表正数（其他位不变），因为 hash 值为负数时有特殊的含义
    //static final int MOVED     = -1; //代表当前位置正在扩容
    //static final int TREEBIN   = -2; //代表当前位置是一棵红黑树
    //static final int RESERVED  = -3; // hash for transient reservations
    int hash = spread(key.hashCode());

    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        //n：数组长度
        //i：索引位置
        //f：i索引位置的Node对象
        //fh：i索引位置上数据的hash值
        Node<K,V> f; int n, i, fh;

        //需要的话先初始化数组
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();

        //（取模运算）定位元素在数组哪个索引：hash & (length -1)，运算结果最大值为 length -1，不会出现数组下标越界的情况
        //如果这个位置没有数据，就把数据放在这个位置（CAS）
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))
                break;
        }

        //如果索引位置有数据，且正在扩容，就协助扩容
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);

        //索引位置有数据，且没有在扩容，把元素插入链表或红黑树，加锁，互斥锁锁住一个索引，其他索引可以正常访问
        //然后如果链表长度>=8，转化为红黑树或扩容
        else {
            V oldVal = null;
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    //（元素的hash>=0）遍历链表添加元素
                    if (fh >= 0) {
                        binCount = 1; //...遍历期间binCount++
                    }

                    //如果fh<0（元素的hash<0），且索引位置是红黑树，向树中插入元素
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key, value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            if (binCount != 0) {
                //链表长度>=8，转为红黑树或扩容
                //为何是8？根据泊松分布，链表长度到8的概率非常低，源码中是0.00...6，尽量在避免生成红黑树使写入成本过高
                //            数组长度<64，扩容，容量翻倍
                //            数组长度>=64，转为红黑树
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }

    //数量+1 且判断是否需要扩容
    addCount(1L, binCount);
    return null;
}
```

## 初始化

```java
//sizeCtl：是数组在初始化和扩容操作时的一个控制变量
//0：代表数组还没初始化
//大于0：代表当前数组的扩容阈值，或者是当前数组的初始化大小
//-1：代表当前数组正在初始化
//小于-1：低16位代表当前数组正在扩容的线程个数（如果1个线程扩容，值为-2，如果2个线程扩容，值为-3）
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; int sc;
    while ((tab = table) == null || tab.length == 0) {
        if ((sc = sizeCtl) < 0)
            Thread.yield();

        //CAS将SIZECTL改为-1，尝试初始化
        else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
            try {
                if ((tab = table) == null || tab.length == 0) {
                    //如果sizeCtl > 0 就初始化sizeCtl长度的数组；如果sizeCtl == 0，就初始化默认的长度16
                    int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                    Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                    table = tab = nt;
                    //将sc赋值为下次扩容的阈值
                    sc = n - (n >>> 2);
                }
            } finally {
                //sizeCtl为下次扩容的阈值
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```

## treeifyBin

```java
private final void treeifyBin(Node<K,V>[] tab, int index) {
    Node<K,V> b; int n, sc;
    if (tab != null) {
        if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
            tryPresize(n << 1);

        //转红黑树需要加锁
        else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
            synchronized (b) {
                //省略一大段代码
            }
        }
    }
}

private final void tryPresize(int size) {
    int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY :
    tableSizeFor(size + (size >>> 1) + 1);
    int sc;

    //数组没有在初始化，也没有在扩容
    while ((sc = sizeCtl) >= 0) {
        Node<K,V>[] tab = table; int n;

        //初始化，同上
        if (tab == null || (n = tab.length) == 0) {
            //省略
        }

        //容量已经到了最大，就不扩容了
        else if (c <= sc || n >= MAXIMUM_CAPACITY)
            break;

        //扩容，帮助其他线程扩容 或 自己扩容
        else if (tab == table) {
            int rs = resizeStamp(n);
            if (sc < 0) {
                Node<K,V>[] nt;
                //判断是否可以协助扩容
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                    sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                    transferIndex <= 0)
                    break;
                //sizeCtl+1，表示扩容的线程数量+1，并协助扩容
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                    transfer(tab, nt);
            }

            //自己扩容，sizeCtl+2，表示当前有一个线程在扩容
            else if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2))
                transfer(tab, null);
        }
    }
}
```

## addCount

```java
private final void addCount(long x, int check) {
    CounterCell[] as; long b, s;
    //不重要
    if ((as = counterCells) != null ||
        !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
        //省略一大段代码
    }

    if (check >= 0) {
        Node<K,V>[] tab, nt; int n, sc;
        //当前元素个数大于等于扩容阈值，且数组不为null，且数组长度没有达到最大值，协助其他线程扩容或自己扩容
        while (s >= (long)(sc = sizeCtl) && (tab = table) != null && (n = tab.length) < MAXIMUM_CAPACITY) {
            int rs = resizeStamp(n);
            //省略一段和 tryPresize 一样的代码
            s = sumCount();
        }
    }
}
```

## 扩容

支持多线程并发扩容

触发扩容的三个点：

- 链表转红黑树前，会判断是否需要扩容
- addCount 方法中，如果元素数量超过阈值，触发扩容
- putAll 方法中，根据传入的 map.size 判断是否需要扩容

```java
//tab：老数组
//nextTab：新数组
private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
    int n = tab.length, stride;

    //基于CPU内核数计算每个线程一次性迁移多少数据
    //每个线程迁移数组长度最小值是MIN_TRANSFER_STRIDE=16
    if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
        stride = MIN_TRANSFER_STRIDE;

    //没有新数组的话，创建一个容量翻倍的新数组
    if (nextTab == null) {
        try {
            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
            nextTab = nt;
        } catch (Throwable ex) {
            sizeCtl = Integer.MAX_VALUE;
            return;
        }
        nextTable = nextTab;

        //扩容总进度，>=transferIndex的索引都已分配出去
        transferIndex = n;
    }
    int nextn = nextTab.length;
    ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab); //MOVED节点
    boolean advance = true;
    boolean finishing = false; // to ensure sweep before committing nextTab
    for (int i = 0, bound = 0;;) {
        Node<K,V> f; int fh;
        while (advance) {
            int nextIndex, nextBound;

            //第一次循环不会进来
            //之后领取了任务之后就可以进来了，按索引顺序从后往前一个个处理
            if (--i >= bound || finishing)
                advance = false;

            //transferIndex <=0 表示所有索引都迁移完成
            else if ((nextIndex = transferIndex) <= 0) {
                i = -1;
                advance = false;
            }

            //当前线程尝试领取任务，领取一段数组的数据迁移
            else if (U.compareAndSwapInt(this, TRANSFERINDEX, nextIndex,
                      nextBound = (nextIndex > stride ? nextIndex - stride : 0))) {
                bound = nextBound;
                //标记索引位置
                i = nextIndex - 1;
                advance = false;
            }
        }

        //i < 0，即线程没有接收到任务，扩容线程数量-1，结束扩容操作
        if (i < 0 || i >= n || i + n >= nextn) {
            int sc;
            if (finishing) {
                nextTable = null;
                table = nextTab;
                sizeCtl = (n << 1) - (n >>> 1);
                return;
            }
            if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                    return;
                finishing = advance = true;
                i = n; // recheck before commit
            }
        }

        //如果当前索引位置没数据，无需迁移，标记为MOVED
        else if ((f = tabAt(tab, i)) == null)
            advance = casTabAt(tab, i, null, fwd);

        //如果当前索引位置的 hash 是 MOVED，表示已经迁移过了
        else if ((fh = f.hash) == MOVED)
            advance = true;

        //迁移数据，将 oldTable 的数据迁移到 newTable，加锁
        else {
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    Node<K,V> ln, hn;

                    //正常情况，链表
                    if (fh >= 0) {
                        //运算结果是0或n：比如16（即10000）&任何hash 只有10000（hash=X1XXXX）和0（hash=X0XXXX）两种结果
                        //hash&15（即01111）的结果是A
                        //hash&31（即11111）的结果是B
                        //A和B只有两种情况：A==B（hash=X0XXXX） 或者 A+16==B（hash=X1XXXX）
                        //因此扩容后索引位置不变 或 索引位置+n
                        int runBit = fh & n;
                        Node<K,V> lastRun = f;

                        //找出最后一段 hash&n 连续不变的链表
                        //    即从 lastRun 开始后面的这一段数据就不用重新创建节点了，前面的数据还需要创建节点
                        //runBit == 0 表示扩容前后索引位置不变，其他情况表示索引位置需要变动
                        for (Node<K,V> p = f.next; p != null; p = p.next) {
                            //这里也是0或n
                            int b = p.hash & n;
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        }
                        else {
                            hn = lastRun;
                            ln = null;
                        }

                        //lastRun 之前的结点重新创建节点
                        for (Node<K,V> p = f; p != lastRun; p = p.next) {
                            int ph = p.hash; K pk = p.key; V pv = p.val;
                            if ((ph & n) == 0)
                                ln = new Node<K,V>(ph, pk, pv, ln);
                            else
                                hn = new Node<K,V>(ph, pk, pv, hn);
                        }
                        //低位链表
                        setTabAt(nextTab, i, ln);
                        //高位链表
                        setTabAt(nextTab, i + n, hn);
                        //设置当前索引为MOVED
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }

                    //红黑树
                    else if (f instanceof TreeBin) {
                        //忽略一大段代码
                    }
                }
            }
        }
    }
}
```

# HashMap线程不安全的原因

- 在 Java7 中，并发执行扩容操作时会造成环形链表和数据丢失的情况
- 在 Java8 中，并发执行put操作时会发生数据覆盖的情况

# HashMap VS HashTable VS ConcurrentHashMap

|                  | HashMap    | HashTable                                        | ConcurrentHashMap |
| ---------------- | ---------- | ------------------------------------------------ | ----------------- |
| 线程安全性       | 非线程安全 | 线程安全                                         | 线程安全          |
| 是否允许KV为null | 允许       | 不允许                                           | 不允许            |
| 其他             |            | HashTable锁粒度太粗，没有ConcurrentHashMap性能好 | 建议替代HashTable |

**HashMap VS ConcurrentHashMap**

存储结构一样，都是数组+链表+红黑树

链表转换为红黑树的时机一样，都是链表长度>=8
红黑树转换为链表的时机一样，都是树上节点数量<=6

扩容都是变为原来的两倍

扩容时机不一样：

- ConcurrentHashMap：链表长度>=8且数组长度<64 或 当前元素个数大于等于扩容阈值即数组长度（满了再扩容）
- HashMap：元素个数超过负载因子与容量的乘积，负载因子用来衡量何时自动扩容
  例如负载因子默认为 0.75（意味着元素数量达到当前容量的的 3/4 时扩容）

# TreeMap

支持根据 key 自定义排序（默认升序），key 必须实现 Comparable 接口或者在构造方法传入自定义的 Comparator

# 源码

## lastRun机制

## 数组长度为什么是2的n次幂

> 同HashMap

为了数据分布均匀，如果数组长度不是2的n次幂，会破坏咱们散列算法，导致hash冲突增加

还会破坏后面很多的算法，比如lastRun机制

## 如何保证数组初始化线程安全

使用了 DCL（双重检查锁）

锁的实现是基于 CAS 的，初始化数组时，CAS 将 sizeCt 改为负1

外层 while 循环判断数组未初始化，基于 CAS 加锁，然后在内层再次判断数组未初始化

-----

#并发 