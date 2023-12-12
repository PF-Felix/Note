package algorithm;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import org.junit.Test;

import java.util.Arrays;

public class Day003_选择排序冒泡排序插入排序 {

    @Test
    public void test01(){
        for (int p = 0; p < 10000; p++) {
            for (int i = 1; i < 4; i++) {
                int[] xx = MyArrayUtils.buildRandomArray(50, -30, 50);
                int[] xxClone = ArrayUtil.clone(xx);

                System.out.println(Arrays.toString(xx));
                System.out.println(Arrays.toString(xxClone));
                Assert.isTrue(ArrayUtil.equals(xx, xxClone));

                if (i == 1) {
                    sort1(xx);
                } else if (i == 2) {
                    sort2(xx);
                } else {
                    sort3(xx);
                }
                Arrays.sort(xxClone);

                System.out.println(Arrays.toString(xx));
                System.out.println(Arrays.toString(xxClone));
                Assert.isTrue(ArrayUtil.equals(xx, xxClone));
                System.out.println("=========================");
            }
        }
    }

    /**
     * 选择排序
     */
    private void sort1(int[] xx) {
        System.out.println("选择排序");
        for (int i = 0; i < xx.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < xx.length; j++) {
                if (xx[j] < xx[minIndex]) {
                    minIndex = j;
                }
            }

            swap(xx, i, minIndex);
        }
    }

    /**
     * 冒泡排序
     */
    private void sort2(int[] xx) {
        System.out.println("冒泡排序");
        for (int i = xx.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (xx[j] > xx[j + 1]) {
                    swap(xx, j, j + 1);
                }
            }
        }
    }

    /**
     * 插入排序
     */
    private void sort3(int[] xx) {
        System.out.println("插入排序");
        for (int i = 0; i < xx.length; i++) {
            for (int j = i; j - 1 >= 0; j--) {
                if (xx[j] < xx[j - 1]) {
                    swap(xx, j, j - 1);
                }
            }
        }
    }

    /**
     * 交换
     */
    private void swap(int[] xx, int x, int y) {
        int temp = xx[x];
        xx[x] = xx[y];
        xx[y] = temp;
    }
}
