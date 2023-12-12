package algorithm;

public class 随机等概率生成一个数组 {

    /**
     * 相邻两个元素不相等
     */
    public static int[] invoke1(int max, int min, int length) {
        int[] xx = new int[length];
        for (int i = 0; i < xx.length; i++) {
            //生成一个 [0,N] 的整数
            do {
                int i1 = (int) (Math.random() * (max + 1 - min));
                int i2 = i1 + min;
                if (i2 > max || i2 < min) {
                    throw new RuntimeException("报错啦");
                }
                xx[i] = i2;
            } while (i > 0 && xx[i] == xx[i - 1]);
        }
        return xx;
    }
}
