package algorithm;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class Day006_1_归并排序 {

    @Test
    public void test() {
        for (int i = 0; i < 100; i++) {
            mergeSort();
        }
    }

    public void mergeSort() {
        int[] array = MyArrayUtils.buildRandomArray(1000, 0, 20);
        List<Integer> list = ListUtil.toList(Arrays.stream(array).boxed().toArray(Integer[]::new));
        System.out.println("原数组:" + list);

        //排序
        mergeSort(array, 0, array.length - 1, "");
        System.out.println("新数组:" + ListUtil.toList(Arrays.stream(array).boxed().toArray(Integer[]::new)));

        Assert.isTrue(MyArrayUtils.isSorted(array));
    }

    private void mergeSort(int[] array, int startIndex, int endIndex, String blank) {
        int length = endIndex - startIndex + 1;
        if (length < 2) {
            return;
        }

        if (length == 2) {
            if (array[startIndex] > array[endIndex]) {
                //元素交换位置
                MyArrayUtils.swap(array, startIndex, endIndex);
            }
            return;
        }

        System.out.println(blank + startIndex + "->" + endIndex);

        int middleIndex = (startIndex + endIndex) / 2;

        mergeSort(array, startIndex, middleIndex, blank + "  ");
        mergeSort(array, middleIndex + 1, endIndex, blank + "  ");

        int leftIndex = startIndex;
        int rightIndex = middleIndex + 1;
        int[] tempArray = new int[length];
        int tempIndex = 0;
        while (leftIndex <= middleIndex || rightIndex <= endIndex) {
            if (leftIndex > middleIndex) {
                //左边安排完了，安排右边，下次还会进来这里
                tempArray[tempIndex] = array[rightIndex];
                rightIndex++;
                tempIndex++;
                System.out.println(blank + Arrays.toString(tempArray));
                continue;
            }
            if (rightIndex > endIndex) {
                //右边安排完了，安排左边，下次还会进来这里
                tempArray[tempIndex] = array[leftIndex];
                leftIndex++;
                tempIndex++;
                System.out.println(blank + Arrays.toString(tempArray));
                continue;
            }

            if (array[leftIndex] > array[rightIndex]) {
                //左右都有的时候比较，左比右大的时候 取右边
                tempArray[tempIndex] = array[rightIndex];
                rightIndex++;
            } else {
                //左右都有的时候比较，左比右小的时候 取左边
                tempArray[tempIndex] = array[leftIndex];
                leftIndex++;
            }
            tempIndex++;
        }

        System.arraycopy(tempArray, 0, array, startIndex, length);
    }
}
