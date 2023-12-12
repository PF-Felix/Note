package algorithm;

import cn.hutool.core.lang.Assert;
import org.junit.Test;

import java.util.Arrays;

public class Day004_2_局部最小值与二分查找 {

    @Test
    public void test01(){
        for (int p = 0; p < 100000; p++) {
            //生成数组 相邻的元素不相等
            int[] xx = 随机等概率生成一个数组.invoke1(10, 1, 20);
            System.out.println(Arrays.toString(xx));

            int index;
            if (xx[0] < xx[1]) {
                index = 0;
            } else if (xx[xx.length - 1] < xx[xx.length - 2]) {
                index = xx.length - 1;
            } else {
                index = find1(xx, 0, xx.length - 1);
            }
            System.out.println(index);

            Assert.isTrue(check(xx, index));
        }
    }

    private int find1(int[] xx, int leftIndex, int rightIndex) {
        int middle = (rightIndex + leftIndex) / 2;
        if (xx[middle] < xx[middle - 1] && xx[middle] < xx[middle + 1]) {
            return middle;
        } else if (xx[middle] > xx[middle - 1]){
            return find1(xx, leftIndex, middle - 1);
        } else {
            return find1(xx, middle + 1, rightIndex);
        }
    }

    private boolean check(int[] xx, int index) {
        if (index == 0) {
            return xx[0] < xx[1];
        }
        if (index == xx.length - 1) {
            return xx[xx.length - 1] < xx[xx.length - 2];
        }
        return xx[index] < xx[index - 1] && xx[index] < xx[index + 1];
    }
}
