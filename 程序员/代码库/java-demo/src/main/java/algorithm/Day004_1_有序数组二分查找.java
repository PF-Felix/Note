package algorithm;

import cn.hutool.core.lang.Assert;
import org.junit.Test;

import java.util.Arrays;

public class Day004_1_有序数组二分查找 {

    @Test
    public void test01(){
        for (int p = 0; p < 100000; p++) {
            //生成一个排序数组
            int[] xx = MyArrayUtils.buildRandomArray(10, 1, 10);
            Arrays.sort(xx);
            System.out.println(Arrays.toString(xx));
            Assert.equals(find1(xx, 0, xx.length - 1, 8), find1Check(xx, 8));
            Assert.equals(find2(xx, 0, xx.length - 1, 8, -1), find2Check(xx, 8));
            Assert.equals(find3(xx, 0, xx.length - 1, 8, -1), find3Check(xx, 8));
        }
    }

    /**
     * 二分查找 =num 的数字
     */
    private boolean find1(int[] xx, int leftIndex, int rightIndex, int num) {
        if (leftIndex > rightIndex) {
            System.out.println("找不到");
            return false;
        }
        int middle = (rightIndex + leftIndex) / 2;
        if (xx[middle] > num) {
            return find1(xx, leftIndex, middle - 1, num);
        } else if (xx[middle] < num) {
            return find1(xx, middle + 1, rightIndex, num);
        } else {
            return true;
        }
    }

    /**
     * 二分查找 =num 的最右边的数字的位置，找不到返回 -1
     */
    private int find3(int[] xx, int leftIndex, int rightIndex, int num, int index) {
        if (leftIndex > rightIndex) {
            return index;
        }
        int middle = (rightIndex + leftIndex) / 2;
        if (xx[middle] > num) {
            return find3(xx, leftIndex, middle - 1, num, index);
        } else if (xx[middle] < num) {
            return find3(xx, middle + 1, rightIndex, num, index);
        } else {
            //如果找到了，继续往右边找，看看有没有相等的
            return find3(xx, middle + 1, rightIndex, num, middle);
        }
    }

    /**
     * 对数器
     */
    private int find3Check(int[] xx, int num) {
        for (int i = xx.length - 1; i >= 0; i--) {
            if (xx[i] == num)
                return i;
        }
        return -1;
    }

    /**
     * 二分查找 =num 的最左边的数字的位置，找不到返回 -1
     */
    private int find2(int[] xx, int leftIndex, int rightIndex, int num, int index) {
        if (leftIndex > rightIndex) {
            return index;
        }
        int middle = (rightIndex + leftIndex) / 2;
        if (xx[middle] > num) {
            return find2(xx, leftIndex, middle - 1, num, index);
        } else if (xx[middle] < num) {
            return find2(xx, middle + 1, rightIndex, num, index);
        } else {
            //如果找到了，继续往左边找看看有没有相等的
            return find2(xx, leftIndex, middle - 1, num, middle);
        }
    }

    private int find2Check(int[] xx, int num) {
        for (int i = 0; i < xx.length; i++) {
            if (xx[i] == num)
                return i;
        }
        return -1;
    }

    /**
     * 验证
     */
    private boolean find1Check(int[] xx, int num) {
        for (int j : xx) {
            if (j == num)
                return true;
        }
        System.out.println("找不到");
        return false;
    }
}
