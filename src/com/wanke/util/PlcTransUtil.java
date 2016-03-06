package com.wanke.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.wanke.bean.ConnectInfo;

public class PlcTransUtil {

	private static String PLC_HEAD = "FINS";

	/**
	 * 获取握手字符串
	 * 
	 * @param int
	 *            长度
	 * @param int
	 *            终端IP最后一位
	 * @return String 握手用HEX字符串
	 */
	public static String GetPlcHandshakeHexStr(int length, int clientNo) {
		String handshake = "";
		handshake += UtilStr.str2HexStr(PLC_HEAD);
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(length));
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(0));
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(0));
		handshake += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(clientNo));

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
	public static String GetHandshakeBackHexStr(int length, int comandno, int errorno, int plcno, int clientno) {
		String handshakeback = "";
		handshakeback += UtilStr.str2HexStr(PLC_HEAD);
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(length));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(comandno));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(errorno));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(clientno));
		handshakeback += UtilStr.byte2HexStr(IntByteUtil.intToBytes2(plcno));

		return handshakeback;
	}

	public static String GetReadPLCStr() {
		String status = "";
		return status;
	}

	public static String GetWritePLCStr() {
		String status = "";
		return status;
	}

	public ConnectInfo ConnectPLC(String ADDR_IP, String PORT, String client_last_IP, String PLC_last_IP) {
		ConnectInfo conni = new ConnectInfo();
		Socket socket = null;

		try {
			while (true) {
				// 读取服务器端数据
				DataInputStream input = new DataInputStream(socket.getInputStream());
				// 向服务器端发送数据
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("PLC连接 异常:" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("PLC连接 异常::" + e.getMessage());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					socket = null;
					System.out.println("客户端 finally 异常:" + e.getMessage());
				}
			}
		}
		return conni;

	}

}
