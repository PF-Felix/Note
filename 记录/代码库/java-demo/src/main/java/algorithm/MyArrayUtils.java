package algorithm;

/**
 * 数组工具类
 */
public class MyArrayUtils {

    /**
     * 随机生成乱序数组
     */
    public static int[] buildRandomArray(int max, int min, int length) {
        int[] xx = new int[length];
        for (int i = 0; i < xx.length; i++) {
            //生成一个 [0,N] 的整数
            int i1 = (int) (Math.random() * (max + 1 - min));
            int i2 = i1 + min;
            if (i2 > max || i2 < min) {
                throw new RuntimeException("报错啦");
            }
            xx[i] = i2;
        }
        return xx;
    }

    /**
     * 元素交换位置
     */
    public static void swap(int[] xx, int x, int y) {
        int temp = xx[x];
        xx[x] = xx[y];
        xx[y] = temp;
    }

    /**
     * 是否从小到大排序
     */
    public static boolean isSorted(int[] xx) {
        if (xx.length == 0 || xx.length == 1) {
            return true;
        }

        for (int i = 1; i < xx.length; i++) {
            if (xx[i] < xx[i - 1]) {
                return false;
            }
        }

        return true;
    }
}
