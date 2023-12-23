package algorithm;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 生成随机数
 */
public class Day002_随机数生成器 {

    /**
     * 生成一个随机数
     * Math.random() 范围 [0,1)
     * Math.random() * N 范围 [0,N)
     * (int)(Math.random() * N) 范围 [0,N-1] 等概率随机整数
     * -----
     * 下面是随机 [0,10] 出现 10 的概率
     */
    @Test
    public void test01() {
        int num;
        int count = 0;
        int max = 10000;
        for (int i = 0; i < max; i++) {
            num = (int) ((10 + 1) * Math.random());
            if (num == 10) {
                count++;
            }
        }
        System.out.println("随机 [0,10] 出现 10 的理论概率：" + divide(1, 11));
        System.out.println("随机 [0,10] 出现 10 的实际概率：" + divide(count, max));
    }

    private BigDecimal divide(int i, int j) {
        return BigDecimal.valueOf(i).divide(BigDecimal.valueOf(j), 5, RoundingMode.HALF_DOWN);
    }

    /**
     * 从 1-5 随机到 1-7 随机
     * @see #f1() 这个方法能得到 1-5 的等概率随机整数 概率是 1/5
     * 希望生成一个 1-7 的一个等概率随机整数 概率为 1/7
     * 解决方案如下
     * 0 是 000
     * 1 是 001
     * 2 是 010
     * 3 是 011
     * 4 是 100
     * 5 是 101
     * 6 是 110
     * 7 是 111
     * @see #f2() 这是利用 f1 做的一个等概率的 0 1 生成器
     * @see #f3() 调用 f2 生成三次 0 1 随机数 组装成每个二进制数的概率是 1/8 去掉 000 的情况就是 1/7
     */
    @Test
    public void test02() {
        int count = 0;
        int max = 10000;
        for (int i = 0; i < max; i++) {
            if (f1() == 4) {
                count++;
            }
        }
        System.out.println("1-5出现4的理论概率是：0.2");
        System.out.println("1-5出现4的实际概率是：" + divide(count, max));

        count = 0;
        for (int i = 0; i < max; i++) {
            if (f3() == 4) {
                count++;
            }
        }
        System.out.println("1-7出现4的理论概率是：" + divide(1, 7));
        System.out.println("1-7出现4的实际概率是：" + divide(count, max));
    }

    private int f1() {
        int num = (int) (5 * Math.random());
        return num + 1;
    }

    private int f2() {
        int num1 = f1();
        if (num1 == 1 || num1 == 2) {
            return 0;
        } else if (num1 == 3 || num1 == 4) {
            return 1;
        } else {
            return f2();
        }
    }

    private int f3() {
        int sum = (f2() << 2) + (f2() << 1) + f2();
        if (sum == 0) {
            return f3();
        } else {
            return sum;
        }
    }

    /**
     * @see #f4() 是一个概率不等的 0 1 发生器，如何转化为等概率随机
     * @see #f5() 是解决方案
     * 请求两次
     * 0,1 和 1,0 概率相等
     * 0,1 返回 0
     * 1,0 返回 1
     * 遇到 0,0 和 1,1 就重做
     */
    @Test
    public void test03() {
        int count = 0;
        int max = 10000;
        for (int i = 0; i < max; i++) {
            if (f4() == 0) {
                count++;
            }
        }
        System.out.println("不等概率随机：0出现的实际概率是：" + divide(count, max));

        count = 0;
        for (int i = 0; i < max; i++) {
            if (f5() == 0) {
                count++;
            }
        }
        System.out.println("等概率随机：0出现的实际概率是：" + divide(count, max));
    }

    private int f4() {
        int num1 = f1();
        if (num1 == 1 || num1 == 2) {
            return 0;
        } else {
            return 1;
        }
    }

    private int f5() {
        int sum = (f4() << 1) + f4();
        if (sum == 1) {
            return 0;
        } else if (sum == 2) {
            return 1;
        } else {
            return f5();
        }
    }
}
