package algorithm;

import cn.hutool.core.lang.Assert;
import org.junit.Test;

public class Day005_1_找到出现了奇数次的数 {

    @Test
    public void test01(){
        int[] xx = {1,2,3,4,5,6,7,8,9,9,8,7,6,5,4,3,2,1,1,2,3,4,5,6,7,8,9,8,7,6,5,4,3,2,1};
        int eor = xx[0];
        for (int j = 1; j < xx.length; j++) {
            eor = eor ^ xx[j];
        }
        Assert.equals(eor, 9);
    }
}
