package algorithm;

import org.junit.Test;

public class Day001_位移运算_原码反码补码 {

	@Test
	public void test01() {
		//调用 JDK 的自带方法 toBinaryString 输出二进制数
		test01(false);
		//自己写的打印，结果与上面是一样的
		test01(true);
	}

	private void test01(boolean print) {
		int num_5 = 5;
		int num_5_2 = -5;

		System.out.println("无符号右移：正数");
		for (int i = 0; i < 33; i++) {
			print(num_5 >>> i, print);
		}
		System.out.println("无符号右移：负数");
		for (int i = 0; i < 33; i++) {
			print(num_5_2 >>> i, print);
		}
		System.out.println("有符号右移：正数");
		for (int i = 0; i < 33; i++) {
			print(num_5 >> i, print);
		}
		System.out.println("有符号右移：负数");
		for (int i = 0; i < 33; i++) {
			print(num_5_2 >> i, print);
		}
		System.out.println("有符号左移：正数");
		for (int i = 0; i < 33; i++) {
			print(num_5 << i, print);
		}
		System.out.println("有符号左移：负数");
		for (int i = 0; i < 33; i++) {
			print(num_5_2 << i, print);
		}
	}

	private void print(int x, boolean print) {
		if (print) {
			print(x);
		} else {
			System.out.println(Integer.toBinaryString(x));
		}
	}

	@Test
	public void test02() {
		int num = 5;
		System.out.println("正数5的补码是原码本身");
		print(num);
		System.out.println("正数5取反就是其相反数-5的反码");
		print(~num);
		System.out.println("-5用补码表示，是反码+1");
		print(-num);
	}

	/**
	 * 整型的最大值最小值
	 */
	@Test
	public void test03() {
		System.out.println("整型最大值是正 2的31次方-1 补码就是其本身");
		print(Integer.MAX_VALUE);
		System.out.println();
		System.out.println("整型最小值的补码");
		print(Integer.MIN_VALUE);
		System.out.println("补码取反");
		print(~Integer.MIN_VALUE);
		System.out.println("补码取反+1 就是整型最小值的相反数 即2的31次方 因此整型最小值是负 2的31次方");
	}

	/**
	 * 输出十进制数的二进制格式
	 */
	private void print(int num) {
		for (int i = 31; i >= 0; i--) {
			System.out.print((num & (1 << i)) == 0 ? "0" : "1");
		}
		System.out.println();
	}
}
