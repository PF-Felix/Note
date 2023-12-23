是一种乐观锁，能保证对一个变量的替换是原子性的

缺点：

- 无法保证多个变量的原子性
  解决方案可以是 ReentrantLock 锁（AQS）基于 volatile 和 CAS 实现
  可以使用 AtomicReference 将多个变量放到一个对象中操作
- ABA问题：可以引入版本号来解决，有一个这样的实现 AtomicStampeReference
- 长时间自旋不成功浪费 CPU：控制好自旋的次数

-----

#锁 