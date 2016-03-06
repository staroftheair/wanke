package com.wanke.neware;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.wanke.bean.neware.NewareInquireResultBean;
import com.wanke.bean.neware.NewareStartStatusBean;
import com.wanke.util.XmlUtil;

public class NewareTrans {

	// 管道名
	private static String pipeName = "\\\\.\\pipe\\" + "NewareBtsAPI";

	/**
	 * 建立管道连接
	 * 
	 * @return
	 */
	public HANDLE ConnectNewareAPI() {
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
	public List<NewareStartStatusBean> StartDrive(String xml, HANDLE newarePipe) {
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
	public List<NewareInquireResultBean> StartInquire(String xml, HANDLE newarePipe) {
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
	public void WriteFile(String xml, HANDLE newarePipe) {
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
	public String ReadFile(HANDLE newarePipe) {
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
	public boolean checkStatus(List<NewareStartStatusBean> nssblist) {

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
	public void CloseNewarePipe(HANDLE newarePipe) {
		Kernel32.INSTANCE.CloseHandle(newarePipe);
	}

}
