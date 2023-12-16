package algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 支持通过坐标找到元素 也能通过元素找到坐标
 */
public class Day007_2_堆_加强堆<T> {
	private ArrayList<T> heap;
	private HashMap<T, Integer> indexMap;
	private int heapSize;
	private Comparator<? super T> comp;

	public Day007_2_堆_加强堆(Comparator<T> c) {
		heap = new ArrayList<>();
		indexMap = new HashMap<>();
		heapSize = 0;
		comp = c;
	}

	public boolean isEmpty() {
		return heapSize == 0;
	}

	public int size() {
		return heapSize;
	}

	public boolean contains(T obj) {
		return indexMap.containsKey(obj);
	}

	public T peek() {
		return heap.get(0);
	}

	public T pop() {
		T ans = heap.get(0);  //返回头节点
		swap(0, heapSize - 1); //交换头尾节点
		indexMap.remove(ans);  //删除尾元素
		heap.remove(--heapSize);  //删除尾元素
		heapCasWithMaxChild(0); //从头节点重新调整
		return ans;
	}

	public void push(T obj) {
		heap.add(obj);
		indexMap.put(obj, heapSize);
		heapCasWithParent(heapSize++); //插入新元素，和父元素比较重新调整
	}

	public void remove(T obj) {
		T replace = heap.get(heapSize - 1);
		int index = indexMap.get(obj);
		indexMap.remove(obj);
		heap.remove(--heapSize);
		if (obj != replace) {
			heap.set(index, replace); //最后一个元素填补删除的元素
			indexMap.put(replace, index); //维护索引
			resign(replace); //重新调整
		}
	}

	public void resign(T obj) {
		heapCasWithParent(indexMap.get(obj));
		heapCasWithMaxChild(indexMap.get(obj));
	}

	public List<T> getAllElements() {
		return new ArrayList<>(heap);
	}

	/**
	 * 参考另一个类的同名方法
	 */
	private void heapCasWithParent(int index) {
		while (comp.compare(heap.get(index), heap.get((index - 1) / 2)) < 0) {
			swap(index, (index - 1) / 2);
			index = (index - 1) / 2;
		}
	}

	/**
	 * 参考另一个类的同名方法
	 */
	private void heapCasWithMaxChild(int index) {
		int left = index * 2 + 1;
		while (left < heapSize) {
			int best = left + 1 < heapSize && comp.compare(heap.get(left + 1), heap.get(left)) < 0 ? (left + 1) : left;
			best = comp.compare(heap.get(best), heap.get(index)) < 0 ? best : index;
			if (best == index) {
				break;
			}
			swap(best, index);
			index = best;
			left = index * 2 + 1;
		}
	}

	private void swap(int i, int j) {
		T o1 = heap.get(i);
		T o2 = heap.get(j);
		heap.set(i, o2);
		heap.set(j, o1);
		indexMap.put(o2, i);
		indexMap.put(o1, j);
	}
}
