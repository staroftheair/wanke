package com.wanke;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.wanke.bean.neware.NewareInquireBean;
import com.wanke.bean.neware.NewareInquireResultBean;
import com.wanke.bean.neware.NewareStartBean;
import com.wanke.bean.neware.NewareStartStatusBean;
import com.wanke.neware.NewareTrans;
import com.wanke.util.PlcTransUtil;
import com.wanke.util.UtilStr;
import com.wanke.util.XmlUtil;

public class BatteryAutoTest {

	public static final String IP_ADDR = "192.168.1.2";// 服务器地址
	public static final int PORT = 9600;// 服务器端口号

	@Test
	public void testGetStr() {
		String aa = PlcTransUtil.GetPlcHandshakeHexStr(12, 250);

		System.out.println(aa);
	}

	@Test
	public void testNamedPipeServerAPI() {
		NewareTrans nt = new NewareTrans();
		HANDLE pipe = nt.ConnectNewareAPI();
		XmlUtil xu = new XmlUtil();
		System.out.println(System.currentTimeMillis());
		try {
			List<NewareStartBean> batteryList = new ArrayList<NewareStartBean>();
			for (int i = 1; i < 6; i++) {
				NewareStartBean nsb = new NewareStartBean();
				nsb.setIp("127.0.0.1");
				nsb.setDevtype("22");
				nsb.setDevid("50");
				nsb.setSubdevid("1");
				nsb.setChlid("" + i);
				nsb.setBarcode("D1234567890" + i);
				batteryList.add(nsb);
			}
			List<NewareStartStatusBean> statusList = new ArrayList<NewareStartStatusBean>();
			String startxml = xu.createStartXml(batteryList, "e:/test1.xml");
			statusList = nt.StartDrive(startxml, pipe);
			if (nt.checkStatus(statusList)) {
				List<NewareInquireBean> niblist = new ArrayList<NewareInquireBean>();
				for (int j = 1; j < 6; j++) {
					NewareInquireBean nib = new NewareInquireBean();
					nib.setIp("127.0.0.1");
					nib.setDevtype("22");
					nib.setDevid("50");
					nib.setSubdevid("1");
					nib.setChlid("" + j);
					nib.setAux("0");
					niblist.add(nib);
				}
				Thread.sleep(500);
				NewareInquireResultBean nirb_old = new NewareInquireResultBean();
				for (int i = 0; i < 5; i++) {
					String inquirexml = xu.createInquireXml(niblist);
					List<NewareInquireResultBean> nisblist = nt.StartInquire(inquirexml, pipe);
					int j = 1;
					for (NewareInquireResultBean nirb : nisblist) {
						if (i != 0) {
							String voltage = nirb.getVoltage();
							String voltage_old = nirb_old.getVoltage();
							double volChange = (Double.valueOf(voltage_old) - Double.valueOf(voltage));
							String current = nirb.getCurrent();
							String current_old = nirb_old.getCurrent();
							double currChange = (Double.valueOf(current_old) - Double.valueOf(current));
							if (volChange > 0.03) {
								System.out.println("电压" + j + ":" + nirb.getVoltage());
							}
							if (currChange > 0.01) {
								System.out.println("电流" + j + ":" + nirb.getCurrent());
							}
						}
						j++;
						nirb_old = nirb;
					}
					Thread.sleep(900);
				}

				System.out.println(System.currentTimeMillis());
			} else {
				System.out.println("is not OK");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { // clean up
			nt.CloseNewarePipe(pipe);
		}
	}

	@Test
	public void testcc() {
		Socket socket = null;
		try {
			socket = new Socket(IP_ADDR, PORT);

			while (true) {
				// 读取服务器端数据
				DataInputStream input = new DataInputStream(socket.getInputStream());
				// 向服务器端发送数据
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				byte[] b = UtilStr.hexStr2Bytes("46494E530000000C0000000000000000000000FA");
				out.write(b);
				out.flush();
				int i = 1;
				byte[] a = new byte[50];
				if (input.read(a) != -1) {
					i++;
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

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			testcc();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			testcc();
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
