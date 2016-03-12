package com.wanke;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.junit.Test;
import com.wanke.thread.NewareThread;
import com.wanke.thread.PlcHeartBeatThread;
import com.wanke.thread.ThreadPool;
import com.wanke.util.PlcTransUtil;
import com.wanke.util.UtilStr;

public class BatteryAutoTest {

	public static final String IP_ADDR = "192.168.1.2";// 服务器地址
	public static final int PORT = 9600;// 服务器端口号

	@Test
	public void PlcTest() {
		Socket socket = null;
		try {
			socket = new Socket(IP_ADDR, PORT);
			PlcTransUtil plcutil = new PlcTransUtil();
			while (true) {
				// PLC握手
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				String handshakeStr = plcutil.GetPlcHandshakeHexStr(12, 250);
				byte[] handshake = UtilStr.hexStr2Bytes(handshakeStr);
				out.write(handshake);
				out.flush();
				byte[] handshakeResuletb = new byte[50];
				if (input.read(handshakeResuletb) != -1) {
					String handshakeResulet = UtilStr.byte2HexStr(handshakeResuletb);
					String handshakeResuletback = plcutil.GetHandshakeBackHexStr(16, 1, 0, 2, 250);
					if (handshakeResulet.startsWith(handshakeResuletback)) {
						ThreadPool threadPool = new ThreadPool(2); // 创建线程池
						threadPool.execute(PlcHeartBeatThread.HeartBeatTask(socket));// 启动心跳线程
						threadPool.execute(NewareThread.StartCheckTask(socket));// 启动新威检测线程
						threadPool.join(); // 等待工作线程完成所有的任务 //
						threadPool.close();
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			PlcTest();
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
	}

}
