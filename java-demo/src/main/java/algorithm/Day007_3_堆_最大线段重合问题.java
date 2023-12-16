package algorithm;

import cn.hutool.core.lang.Assert;
import org.junit.Test;

import java.util.*;

public class Day007_3_堆_最大线段重合问题<T> {

    @Test
    public void test1() {
        for (int i = 0; i < 100000; i++) {
            int[][] array = generateLines(100, 0, 200);
            Arrays.sort(array, Comparator.comparingInt(a -> a[0]));

            //小根堆
            PriorityQueue<Integer> heap = new PriorityQueue<>();
            int max = 0;
            for (int[] line : array) {
                while (!heap.isEmpty() && heap.peek() <= line[0]) {
                    heap.poll();
                }
                heap.add(line[1]);
                max = Math.max(max, heap.size());
            }

            Assert.equals(max, maxCover(array));
        }
    }

    public int[][] generateLines(int N, int L, int R) {
        int size = (int) (Math.random() * N) + 1;
        int[][] ans = new int[size][2];
        for (int i = 0; i < size; i++) {
            int a = L + (int) (Math.random() * (R - L + 1));
            int b = L + (int) (Math.random() * (R - L + 1));
            if (a == b) {
                b = a + 1;
            }
            ans[i][0] = Math.min(a, b);
            ans[i][1] = Math.max(a, b);
        }
        return ans;
    }

    /**
     * 笨方法 比较器
     */
    public static int maxCover(int[][] lines) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int[] line : lines) {
            min = Math.min(min, line[0]);
            max = Math.max(max, line[1]);
        }
        int cover = 0;
        for (double p = min + 0.5; p < max; p += 1) {
            int cur = 0;
            for (int[] line : lines) {
                if (line[0] < p && line[1] > p) {
                    cur++;
                }
            }
            cover = Math.max(cover, cur);
        }
        return cover;
    }
}
