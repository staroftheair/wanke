package com.wanke.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PlcTransUtil {

	private static String PLC_HEAD = "FINS";

	/**
	 * 执行Socket通信，获取相应字符串
	 * 
	 * @param socket
	 * @param plccommand
	 *            PLC命令字符串
	 * @return 通信返回字符串
	 * @throws IOException
	 */
	public String PlcTrans(Socket socket, String plccommand) throws IOException {

		DataInputStream input = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		byte[] commandByte = UtilStr.hexStr2Bytes(plccommand);
		byte[] resultByte = new byte[1024];
		out.write(commandByte);
		out.flush();
		int res = input.read(resultByte);
		String result = "";
		if (res != -1) {
			result = UtilStr.byte2HexStr(resultByte);
		}
		return result;
	}

	/**
	 * 获取握手字符串
	 * 
	 * @param int
	 *            长度
	 * @param int
	 *            终端IP最后一位
	 * @return String 握手用HEX字符串
	 */
	public String GetPlcHandshakeHexStr(int length, int clientNo) {
		String handshake = "";
		handshake += UtilStr.str2HexStr(PLC_HEAD);
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(length));
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(0));
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(0));
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(clientNo));

		return handshake;
	}

	/**
	 * 获取握手校验用返回字符串
	 * 
	 * @param int
	 *            长度
	 * @param int
	 *            FINS命令代码
	 * @param int
	 *            错误代码
	 * @param int
	 *            PLC通信IP最后一位
	 * @param int
	 *            终端IP最后一位
	 * @return String 握手用返回HEX字符串
	 */
	public String GetHandshakeBackHexStr(int length, int comandno, int errorno, int plcno, int clientno) {
		String handshakeback = "";
		handshakeback += UtilStr.str2HexStr(PLC_HEAD);
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(length));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(comandno));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(errorno));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(clientno));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(plcno));

		return handshakeback;
	}

	/**
	 * 设置读取写入16进制字符串 例：将PLC的W3.10置ON
	 * 46494E530000001B0000000200000000800002000100000200FF01023100030A000101
	 * （1B:数据长度；01：PLC节点号；02：电脑节点号；0102：FINS命令码，表示写；31：W(Bit）区；0003：十六进制，这里是第3通道
	 * ；0A：十六进制，这里是第10位；0001：十六进制，通道数1个；01：要写入的数据1）
	 * 
	 * @param length
	 *            发送数据长度
	 * @param comandno
	 *            命令码
	 * @param errorno
	 *            错误码
	 * @param plcno
	 *            PLCIP
	 * @param clientno
	 *            客户机IP
	 * @param rw
	 *            0:读取;1:写入
	 * @param scope
	 *            区
	 * @param channel
	 *            通道
	 * @param bit
	 *            位数
	 * @param channelcount
	 *            通道数
	 * @param value
	 *            设置值
	 * @return
	 */
	public String SetReadOrWriteStr(int length, int comandno, int errorno, int plcno, int clientno, int rw,
			String scope, int channel, int bit, int channelcount, int value) {
		String transStr = "";
		transStr += UtilStr.str2HexStr(PLC_HEAD);
		transStr += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(length));
		transStr += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(comandno));
		transStr += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(errorno));
		transStr += "80000200";
		transStr += UtilStr.byte2HexStr(IntByteUtil.intToBytes(plcno)) + "0000"
				+ UtilStr.byte2HexStr(IntByteUtil.intToBytes(clientno));
		if (rw == 0) {
			transStr += "00FF0101";
		} else {
			transStr += "00FF0102";
		}
		transStr += scope + "00" + UtilStr.byte2HexStr(IntByteUtil.intToBytes(channel))
				+ UtilStr.byte2HexStr(IntByteUtil.intToBytes(bit));
		transStr += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(channelcount));

		if (rw != 0) {
			transStr += UtilStr.byte2HexStr(IntByteUtil.intToBytes(value));
		}

		return transStr;
	}

	/**
	 * 正确返回收到的响应： 46494E53000000160000000200000000C00002000200000100FF01020000
	 * 
	 * @param length
	 *            发送数据长度
	 * @param comandno
	 *            命令码
	 * @param errorno
	 *            错误码
	 * @param plcno
	 *            PLCIP
	 * @param clientno
	 *            客户机IP
	 * @param rw
	 *            0:读取;1:写入
	 * @return
	 */
	public String SetReadOrWriteStrBack(int length, int comandno, int errorno, int plcno, int clientno, int rw,
			int value) {
		String transStrBack = "";
		transStrBack += UtilStr.str2HexStr(PLC_HEAD);
		transStrBack += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(length));
		transStrBack += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(comandno));
		transStrBack += UtilStr.byte2HexStr(IntByteUtil.intToBytes4(errorno));
		transStrBack += "C0000200";
		transStrBack += UtilStr.byte2HexStr(IntByteUtil.intToBytes(clientno)) + "0000"
				+ UtilStr.byte2HexStr(IntByteUtil.intToBytes(plcno));

		if (rw == 0) {
			transStrBack += "00FF0101";
		} else {
			transStrBack += "00FF0102";
		}
		transStrBack += "0000";

		if (rw == 0) {
			transStrBack += UtilStr.byte2HexStr(IntByteUtil.intToBytes(value));
		}
		return transStrBack;
	}

}
