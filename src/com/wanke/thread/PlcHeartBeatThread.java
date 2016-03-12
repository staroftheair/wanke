package com.wanke.thread;

import java.io.IOException;
import java.net.Socket;

import com.wanke.plc.PLCTrans;

public class PlcHeartBeatThread {
	/*
	 * 定义了一个简单的任务(打印ID)
	 */
	public static Runnable HeartBeatTask(Socket socket) {
		return new Runnable() {
			public void run() {
				try {
					PLCTrans.PLCHeartBeat(socket);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}

}
