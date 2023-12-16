package algorithm;

import cn.hutool.core.lang.Assert;
import org.junit.Test;

import java.util.Arrays;

public class Day007_1_堆_大根堆_堆排序 {

    @Test
    public void test() {
        int num = 1;
        for (int i = 0; i < num; i++) {
            int[] array = MyArrayUtils.buildRandomArray(1000, 1, 20);
            System.out.println(Arrays.toString(array));
            buildHeapAsc(array);
            Assert.isTrue(isHeap(array));
        }

        for (int i = 0; i < num; i++) {
            int[] array = MyArrayUtils.buildRandomArray(1000, 1, 20);
            System.out.println(Arrays.toString(array));
            buildHeapDesc(array);
            Assert.isTrue(isHeap(array));
        }

        for (int i = 0; i < num; i++) {
            int[] array = MyArrayUtils.buildRandomArray(1000, 1, 20);
            heapSort(array);
            Assert.isTrue(MyArrayUtils.isSorted(array));
        }
    }

    /**
     * 正序遍历数组建立大根堆
     */
    public void buildHeapAsc(int[] array) {
        if (array.length == 0 || array.length == 1) {
            return;
        }

        //遍历数组建立大根堆
        for (int i = 1; i < array.length; i++) {
            heapCasWithParent(array, i);
        }
    }

    /**
     * 倒序遍历数组建立大根堆
     */
    public void buildHeapDesc(int[] array) {
        if (array.length == 0 || array.length == 1) {
            return;
        }

        //倒序遍历，针对每个节点都建立大根堆
        for (int i = array.length - 1; i >= 0; i--) {
            heapCasWithMaxChild(array, i, array.length);
        }
    }

    /**
     * 堆排序
     */
    public void heapSort(int[] array) {
        if (array.length == 0 || array.length == 1) {
            return;
        }

//        buildHeapAsc(array);
        buildHeapDesc(array);

        int heapSize = array.length;
        while (heapSize > 1) {
            //交换堆顶最大的元素到尾部
            MyArrayUtils.swap(array, 0, heapSize - 1);
            //调整重新生成大根堆
            heapSize--;
            heapCasWithMaxChild(array, 0, heapSize);
        }
    }

    /**
     * 节点建立大根堆
     * 和父节点比较交换
     */
    public void heapCasWithParent(int[] array, int index) {
        int parentIndex = (index - 1) / 2;
        //如果当前节点大于父节点就交换
        while (array[index] > array[parentIndex]) {
            MyArrayUtils.swap(array, index, parentIndex);
            index = parentIndex;
            parentIndex = (index - 1) / 2;
        }
    }

    /**
     * 节点建立大根堆
     * 和最大的子节点比较交换
     */
    public void heapCasWithMaxChild(int[] array, int index, int heapSize) {
        int left = 2 * index + 1;
        //只要有孩子就一直遍历
        while (left < heapSize) {
            int right = left + 1;

            //得到最大的孩子
            //如果有右孩子 且 右孩子 > 左孩子 时 右孩子最大
            //如果有右孩子 且 右孩子 < 左孩子 时 左孩子最大
            //如果没有右孩子 直接取左孩子
            int maxChildIndex = right < heapSize && array[left] < array[right] ? right : left;

            //如果当前节点小于子节点就交换
            if (array[maxChildIndex] > array[index]) {
                MyArrayUtils.swap(array, index, maxChildIndex);
                index = maxChildIndex;
                left = 2 * index + 1;
            } else {
                break;
            }
        }
    }

    /**
     * 是否是大根堆
     */
    public boolean isHeap(int[] array) {
        if (array.length == 0 || array.length == 1) {
            return true;
        }

        for (int i = 0; i < array.length; i++) {
            //如果有右孩子 且 右孩子大于父节点
            if (2 * i + 2 < array.length && array[2 * i + 2] > array[i]) {
                return false;
            }

            //如果有左孩子 且 左孩子大于父节点
            if (2 * i + 1 < array.length && array[2 * i + 1] > array[i]) {
                return false;
            }
        }

        return true;
    }
}
