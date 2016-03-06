package com.wanke.plc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.wanke.util.UtilStr;

public class PLCTrans {

	public Socket ConnectPLC(String addr, int port) {

		Socket socket = null;
		try {
			socket = new Socket(addr, port);

			while (true) {
				// 读取服务器端数据
				DataInputStream input = new DataInputStream(socket.getInputStream());
				// 向服务器端发送数据
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				byte[] b = UtilStr.hexStr2Bytes("46494E530000000C0000000000000000000000FA");
				out.write(b);
				out.flush();
				byte[] a = new byte[50];
				if (input.read(a) != -1) {
					String aa = UtilStr.byte2HexStr(a);
					if (a[23] == 2) {
						while (true) {
							// 读取服务器端数据
							input = new DataInputStream(socket.getInputStream());
							// 向服务器端发送数据
							out = new DataOutputStream(socket.getOutputStream());
							byte[] bb = UtilStr.hexStr2Bytes(
									"46494E530000001B000000020000000080000200020000FA00FF010231000A05000101");
							// byte[] bb = UtilStr.hexStr2Bytes(
							// "46494E530000001C000000020000000080000200020000FA00FF0102820064000001FFFF");
							a = new byte[1024];
							out.write(bb);
							out.flush();
							int res = input.read(a);
							if (res != -1) {
								aa = UtilStr.byte2HexStr(a);
								System.out.print(aa + "\r\n");
							}
						}

					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return null;
	}

}
