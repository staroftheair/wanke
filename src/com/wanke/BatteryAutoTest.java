package com.wanke;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.sun.jna.platform.win32.WinNT.HANDLE;
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

	public void NewareStart(Socket socket) {
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
				byte[] handshake = handshakeStr.getBytes();
				out.write(handshake);
				out.flush();
				byte[] handshakeResuletb = new byte[50];
				if (input.read(handshakeResuletb) != -1) {
					String handshakeResulet = UtilStr.byte2HexStr(handshakeResuletb);
					String handshakeResuletback = plcutil.GetHandshakeBackHexStr(16, 1, 0, 2, 250);
					if (handshakeResulet.indexOf(handshakeResuletback) > 0) {
						while (true) {
							// 检测启动位
							String plccommand = plcutil.SetReadOrWriteStr(26, 2, 0, 2, 250, 0, "W", 10, 1, 1, 0);
							String plccommandback = plcutil.PlcTrans(socket, plccommand);
							String plccommandCheck = plcutil.SetReadOrWriteStrBack(27, 2, 0, 2, 250, 0, 1);
							if (plccommandback.indexOf(plccommandCheck) > 0) {
								NewareStart(socket);
							}
						}

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
