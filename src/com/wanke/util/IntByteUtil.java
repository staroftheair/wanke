package com.wanke.util;

public class IntByteUtil {

	/**
	 * 将int数值转换为占1个字节的byte数组，
	 * 
	 * @param value
	 *            要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytes(int value) {
		byte[] src = new byte[1];
		src[0] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * 将int数值转换为占2个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
	 */
	public static byte[] intToBytes2(int value) {
		byte[] src = new byte[2];
		src[0] = (byte) ((value >> 8) & 0xFF);
		src[1] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */
	public static byte[] intToBytes4(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) ((value >> 24) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
	 * 
	 * @param src
	 *            byte数组
	 * @param offset
	 *            从数组的第offset位开始
	 * @return int数值
	 */
	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16)
				| ((src[offset + 3] & 0xFF) << 24));
		return value;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
	 */
	public static int bytesToInt2(byte[] src, int offset) {
		int value;
		value = (int) (((src[offset] & 0xFF) << 24) | ((src[offset + 1] & 0xFF) << 16) | ((src[offset + 2] & 0xFF) << 8)
				| (src[offset + 3] & 0xFF));
		return value;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
	 * 
	 * @param value
	 *            要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytes3(int value) {
		byte[] byte_src = new byte[4];
		byte_src[3] = (byte) ((value & 0xFF000000) >> 24);
		byte_src[2] = (byte) ((value & 0x00FF0000) >> 16);
		byte_src[1] = (byte) ((value & 0x0000FF00) >> 8);
		byte_src[0] = (byte) ((value & 0x000000FF));
		return byte_src;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
	 * 
	 * @param ary
	 *            byte数组
	 * @param offset
	 *            从数组的第offset位开始
	 * @return int数值
	 */
	public static int bytesToInt3(byte[] ary, int offset) {
		int value;
		value = (int) ((ary[offset] & 0xFF) | ((ary[offset + 1] << 8) & 0xFF00) | ((ary[offset + 2] << 16) & 0xFF0000)
				| ((ary[offset + 3] << 24) & 0xFF000000));
		return value;
	}
}
