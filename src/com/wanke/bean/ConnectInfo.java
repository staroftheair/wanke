package com.wanke.bean;

import java.net.Socket;

public class ConnectInfo {

	// 返回字符串
	String result;
	// Socket连接
	Socket socket;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
