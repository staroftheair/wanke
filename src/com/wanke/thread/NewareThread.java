package com.wanke.thread;

import java.io.IOException;
import java.net.Socket;

import com.wanke.plc.PLCTrans;

public class NewareThread {

	public static Runnable StartCheckTask(Socket socket) {
		return new Runnable() {
			public void run() {
				try {
					PLCTrans.ScanPLCNewareIsStart(socket);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
