package com.wanke.plc;

import java.io.IOException;
import java.net.Socket;

import com.wanke.neware.NewareTrans;
import com.wanke.util.PlcTransUtil;

public class PLCTrans {

	/**
	 * 启动心跳
	 * 
	 * @param socket
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void PLCHeartBeat(Socket socket) throws IOException, InterruptedException {

		PlcTransUtil plcutil = new PlcTransUtil();
		int beatValue = 0;
		while (true) {
			// 检测停止位
			String plccommand = plcutil.SetReadOrWriteStr(27, 2, 0, 2, 250, 1, "31", 11, 2, 1, beatValue);
			String plccommandback = plcutil.PlcTrans(socket, plccommand);
			String plccommandCheck = plcutil.SetReadOrWriteStrBack(22, 2, 0, 2, 250, 1, 1);
			if (plccommandback.startsWith(plccommandCheck)) {
				if (beatValue == 0) {
					beatValue = 1;
				} else {
					beatValue = 0;
				}
			}

			Thread.sleep(2000);
		}

	}

	/**
	 * 扫描错误位
	 * 
	 * @param socket
	 * @return 新威检测是否有错误
	 * @throws IOException
	 */
	public static boolean ScanPLCNewareIsError(Socket socket) throws IOException {

		PlcTransUtil plcutil = new PlcTransUtil();
		boolean errflag = false;
		while (true) {
			// 检测停止位
			String plccommand = plcutil.SetReadOrWriteStr(26, 2, 0, 2, 250, 0, "31", 10, 0, 2, 0);
			String plccommandback = plcutil.PlcTrans(socket, plccommand);
			String plccommandCheck = plcutil.SetReadOrWriteStrBack(23, 2, 0, 2, 250, 0, 1);
			if (plccommandback.startsWith(plccommandCheck)) {
				errflag = true;
				break;
			}
		}

		return errflag;
	}

	/**
	 * 启动新威检测
	 * 
	 * @param socket
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void ScanPLCNewareIsStart(Socket socket) throws IOException, InterruptedException {

		PlcTransUtil plcutil = new PlcTransUtil();
		// while (true) {
		// 检测启动位
		String plccommand = plcutil.SetReadOrWriteStr(26, 2, 0, 2, 250, 0, "31", 10, 0, 1, 0);
		String plccommandback = plcutil.PlcTrans(socket, plccommand);
		String plccommandCheck = plcutil.SetReadOrWriteStrBack(23, 2, 0, 2, 250, 0, 1);
		if (plccommandback.startsWith(plccommandCheck)) {
			NewareTrans.NewareStart(socket);
		}
		// Thread.sleep(5000);
		// }

	}

}
