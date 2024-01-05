- full gc = young gc + old gc + metaspace gc
- minor gc = young gc
- major gc = old gc

**什么时候触发 FullGC？FullGC 期间全过程 STW，如何减少 Full GC？**

> 1、老年代有效空间不足以满足一个内存分配时，比如新生代晋升到老年代时发现空间不够用
> 解决方案：通过调整堆的大小以及调整参数来减少 FullGC 的发生
> 2、元空间区域空间不足
> 解决方案：除非本地内存不够用，概率极小
> 3、之前每次晋升的对象的平均大小大于老年代的剩余空间
> 解决方案：同1
> 4、System.gc()
> 解决方案：不建议使用