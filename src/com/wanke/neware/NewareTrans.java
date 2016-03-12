package com.wanke.neware;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.wanke.bean.neware.NewareInquireBean;
import com.wanke.bean.neware.NewareInquireResultBean;
import com.wanke.bean.neware.NewareStartBean;
import com.wanke.bean.neware.NewareStartStatusBean;
import com.wanke.util.XmlUtil;

public class NewareTrans {

	// 管道名
	private static String pipeName = "\\\\.\\pipe\\" + "NewareBtsAPI";
	private static double VOL_CHANGE = 0.03;
	private static double CURR_CHANGE = 0.01;

	/**
	 * 启动新威检测
	 * 
	 * @param socket
	 */
	public static void NewareStart(Socket socket) {
		HANDLE pipe = ConnectNewareAPI();
		XmlUtil xu = new XmlUtil();
		System.out.println(System.currentTimeMillis());
		try {
			List<NewareStartBean> batteryList = new ArrayList<NewareStartBean>();
			// for (int j = 1; j < 5; j++) {
			for (int i = 1; i < 9; i++) {
				NewareStartBean nsb = new NewareStartBean();
				nsb.setIp("127.0.0.1"); // 设置地址
				nsb.setDevtype("22");// 设置类别
				nsb.setDevid("50");// 设置下位机号
				nsb.setSubdevid("" + 1);// 设置通道号
				nsb.setChlid("" + i);// 设置映射号
				nsb.setBarcode("D1234567890" + i);// 设置序列号
				batteryList.add(nsb);
			}
			// }
			List<NewareStartStatusBean> statusList = new ArrayList<NewareStartStatusBean>();
			String startxml = xu.createStartXml(batteryList, "e:/test1.xml");
			statusList = StartDrive(startxml, pipe);
			if (CheckStatus(statusList)) {
				List<NewareInquireBean> niblist = new ArrayList<NewareInquireBean>();
				// for (int j = 1; j < 5; j++) {
				for (int i = 1; i < 9; i++) {
					NewareInquireBean nib = new NewareInquireBean();
					nib.setIp("127.0.0.1");// 设置地址
					nib.setDevtype("22");// 设置类别
					nib.setDevid("50");// 设置下位机号
					nib.setSubdevid("" + 1);// 设置通道号
					nib.setChlid("" + i);// 设置映射号
					nib.setAux("0");
					niblist.add(nib);
				}
				// }
				Thread.sleep(500);
				NewareInquireResultBean nirb_old = new NewareInquireResultBean();
				for (int i = 0; i < 5; i++) {
					String inquirexml = xu.createInquireXml(niblist);
					List<NewareInquireResultBean> nisblist = StartInquire(inquirexml, pipe);
					int j = 1;
					for (NewareInquireResultBean nirb : nisblist) {
						if (i != 0) {
							String voltage = nirb.getVoltage();
							String voltage_old = nirb_old.getVoltage();
							double volChange = (Double.valueOf(voltage_old) - Double.valueOf(voltage));
							String current = nirb.getCurrent();
							String current_old = nirb_old.getCurrent();
							double currChange = (Double.valueOf(current_old) - Double.valueOf(current));
							if (volChange > VOL_CHANGE) {
								System.out.println("电压" + j + ":" + nirb.getVoltage());
							}
							if (currChange > CURR_CHANGE) {
								System.out.println("电流" + j + ":" + nirb.getCurrent());
							}
						}
						j++;
						nirb_old = nirb;
					}
					Thread.sleep(900);
				}
			} else {
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally { // clean up
			CloseNewarePipe(pipe);
		}
	}

	/**
	 * 建立管道连接
	 * 
	 * @return
	 */
	public static HANDLE ConnectNewareAPI() {
		HANDLE newarePipe = Kernel32.INSTANCE.CreateFile(pipeName, WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0, null,
				WinNT.OPEN_EXISTING, 0, null);
		return newarePipe;
	}

	/**
	 * 开启检测
	 * 
	 * @param xml
	 * @return
	 */
	public static List<NewareStartStatusBean> StartDrive(String xml, HANDLE newarePipe) {
		// xml = xml.replace("\"", "'");
		WriteFile(xml, newarePipe);
		String resultXml = ReadFile(newarePipe);
		XmlUtil util = new XmlUtil();
		List<NewareStartStatusBean> statusList = new ArrayList<NewareStartStatusBean>();
		statusList = util.parseStatusXml(resultXml);
		return statusList;
	}

	/**
	 * 开启查询
	 * 
	 * @param xml
	 * @return
	 */
	public static List<NewareInquireResultBean> StartInquire(String xml, HANDLE newarePipe) {
		// xml = xml.replace("\"", "'");
		WriteFile(xml, newarePipe);
		String resultXml = ReadFile(newarePipe);
		XmlUtil util = new XmlUtil();
		List<NewareInquireResultBean> InquireList = new ArrayList<NewareInquireResultBean>();
		InquireList = util.parseInquireXml(resultXml);
		return InquireList;
	}

	/**
	 * 写入命令
	 * 
	 * @param xml
	 * @param newarePipe
	 * @return
	 */
	public static void WriteFile(String xml, HANDLE newarePipe) {
		byte[] writeBuffer = xml.getBytes();
		IntByReference lpNumberOfBytesWritten = new IntByReference(0);
		boolean isOk = Kernel32.INSTANCE.WriteFile(newarePipe, writeBuffer, writeBuffer.length, lpNumberOfBytesWritten,
				null);
		if (!isOk) {
			CloseNewarePipe(newarePipe);
		}
	}

	/**
	 * 返回xml结果
	 * 
	 * @param newarePipe
	 * @return
	 */
	public static String ReadFile(HANDLE newarePipe) {
		String xml = "";
		IntByReference lpNumberOfBytesRead = new IntByReference(0);
		byte[] readBuffer = new byte[1024 * 64];
		boolean isOk = Kernel32.INSTANCE.ReadFile(newarePipe, readBuffer, readBuffer.length, lpNumberOfBytesRead, null);
		if (!isOk) {
			CloseNewarePipe(newarePipe);
		}
		int readlen = lpNumberOfBytesRead.getValue();
		xml = new String(readBuffer, 0, readlen);
		return xml;
	}

	/**
	 * 检测开启状态 任意一个失败返回False
	 * 
	 * @param nssblist
	 * @return
	 */
	public static boolean CheckStatus(List<NewareStartStatusBean> nssblist) {

		for (NewareStartStatusBean nssb : nssblist) {
			if (nssb.getStatus().equals("false")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 关闭管道
	 * 
	 * @param newarePipe
	 */
	public static void CloseNewarePipe(HANDLE newarePipe) {
		Kernel32.INSTANCE.CloseHandle(newarePipe);
	}

}
