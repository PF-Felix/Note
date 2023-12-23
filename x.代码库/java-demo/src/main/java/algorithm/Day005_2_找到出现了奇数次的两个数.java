package algorithm;

import cn.hutool.core.lang.Assert;
import org.junit.Test;

public class Day005_2_找到出现了奇数次的两个数 {

    @Test
    public void test01(){
        //两个数是8和9
        int[] xx = {1,2,3,4,5,6,7,8,9,9,8,7,6,5,4,3,2,1,1,2,3,4,5,6,7,8,9,8,7,6,5,4,3,2,1,8};
        int eor = 0;
        for (int i : xx) {
            eor = eor ^ i;
        }
        Assert.equals(eor, 8 ^ 9);

        //eor只保留一个是1的二进制位
        //例如：00000001 00001000 01000000
        int onlyOne = eor & (-eor);
        int one = 0;
        for (int j = 1; j < xx.length; j++) {
            //如果不等于0 说明是两个数字其中的一个 或者是出现了偶数次的数字 循环完毕得到ab中的其中一个
            if ((xx[j] & onlyOne) != 0) {
                one = one ^ xx[j];
            }
        }
        int another = one ^ eor;

        Assert.isTrue((one == 8 && another == 9) || (one == 9 && another == 8));
    }
}
