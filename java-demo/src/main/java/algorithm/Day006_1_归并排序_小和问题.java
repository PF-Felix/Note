package algorithm;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class Day006_1_归并排序_小和问题 {

    @Test
    public void test() {
        for (int i = 0; i < 10000; i++) {
            mergeSort();
        }
    }

    public void mergeSort() {
        int[] array = MyArrayUtils.buildRandomArray(1000, 0, 5);
        List<Integer> list = ListUtil.toList(Arrays.stream(array).boxed().toArray(Integer[]::new));
        System.out.println("原数组:" + list);
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] < array[j]) {
                    sum = sum + array[i];
                }
            }
        }

        //排序
        int mergeSortSum = mergeSort(array, 0, array.length - 1);
        System.out.println("新数组:" + ListUtil.toList(Arrays.stream(array).boxed().toArray(Integer[]::new)));

        Assert.isTrue(mergeSortSum == sum);
    }

    private int mergeSort(int[] array, int startIndex, int endIndex) {
        int length = endIndex - startIndex + 1;
        if (length < 2) {
            return 0;
        }

        if (length == 2) {
            if (array[startIndex] > array[endIndex]) {
                //元素交换位置
                MyArrayUtils.swap(array, startIndex, endIndex);
            } else if (array[startIndex] < array[endIndex]) {
                return array[startIndex];
            }
            return 0;
        }

        int middleIndex = (startIndex + endIndex) / 2;

        int x1 = mergeSort(array, startIndex, middleIndex);
        int x2 = mergeSort(array, middleIndex + 1, endIndex);

        int x3 = 0;
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
                continue;
            }
            if (rightIndex > endIndex) {
                //右边安排完了，安排左边，下次还会进来这里
                tempArray[tempIndex] = array[leftIndex];
                leftIndex++;
                tempIndex++;
                continue;
            }

            if (array[leftIndex] > array[rightIndex]) {
                //左右都有的时候比较，左比右大的时候 取右边
                tempArray[tempIndex] = array[rightIndex];
                rightIndex++;
            } else if (array[leftIndex] < array[rightIndex]) {
                //左右都有的时候比较，左比右小的时候 取左边
                tempArray[tempIndex] = array[leftIndex];

                x3 = x3 + (endIndex - rightIndex + 1) * array[leftIndex];

                leftIndex++;
            } else {
                //相等的时候 取右边 不能取左边
                tempArray[tempIndex] = array[rightIndex];
                rightIndex++;
            }
            tempIndex++;
        }

        System.arraycopy(tempArray, 0, array, startIndex, length);

        return x1 + x2 + x3;
    }
}
