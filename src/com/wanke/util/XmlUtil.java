package com.wanke.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import org.xml.sax.InputSource;

import com.wanke.bean.neware.NewareInquireBean;
import com.wanke.bean.neware.NewareInquireResultBean;
import com.wanke.bean.neware.NewareStartBean;
import com.wanke.bean.neware.NewareStartStatusBean;

public class XmlUtil {

	/**
	 * 生成开启新威检测Xml字符串
	 * 
	 * @param batteryList
	 * @param xmlgbPath
	 * @return 开启新威检测Xml字符串
	 */
	@Test
	// List<NewareBatteryBean> batteryList,String xmlgbPath
	public String createStartXml(List<NewareStartBean> batteryList, String xmlgbPath) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		Element bts = new Element("bts");
		bts.setAttribute("version", "1.0");
		Element cmd = new Element("cmd");
		cmd.setText("start");
		bts.addContent(cmd);
		Element list = new Element("list");
		list.setAttribute("count", String.valueOf(batteryList.size()));
		for (NewareStartBean nbb : batteryList) {
			Element start = new Element("start");
			start.setAttribute("ip", nbb.getIp());
			start.setAttribute("devtype", nbb.getDevtype());
			start.setAttribute("devid", nbb.getDevid());
			start.setAttribute("subdevid", nbb.getSubdevid());
			start.setAttribute("chlid", nbb.getChlid());
			start.setAttribute("barcode", nbb.getBarcode());
			start.setText(xmlgbPath);
			list.addContent(start);
		}
		bts.addContent(list);
		ByteArrayOutputStream byteRsp = new ByteArrayOutputStream();
		XMLOutputter out = new XMLOutputter(FormatXML());
		try {
			out.output(bts, byteRsp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xml += byteRsp.toString() + "\n\n";
		return xml;
	}

	/**
	 * 创建查询电池状态Xml字符串
	 * 
	 * @param batteryList
	 * @return 查询电池状态Xml字符串
	 */
	public String createInquireXml(List<NewareInquireBean> batteryList) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		Element bts = new Element("bts");
		bts.setAttribute("version", "1.0");
		Element cmd = new Element("cmd");
		cmd.setText("inquire");
		bts.addContent(cmd);
		Element list = new Element("list");
		list.setAttribute("count", String.valueOf(batteryList.size()));
		for (NewareInquireBean nbb : batteryList) {
			Element start = new Element("inquire");
			start.setAttribute("ip", nbb.getIp());
			start.setAttribute("devtype", nbb.getDevtype());
			start.setAttribute("devid", nbb.getDevid());
			start.setAttribute("subdevid", nbb.getSubdevid());
			start.setAttribute("chlid", nbb.getChlid());
			start.setAttribute("aux", nbb.getAux());
			start.setText("true");
			list.addContent(start);
		}
		bts.addContent(list);
		ByteArrayOutputStream byteRsp = new ByteArrayOutputStream();
		XMLOutputter out = new XMLOutputter(FormatXML());
		try {
			out.output(bts, byteRsp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xml += byteRsp.toString() + "\n\n";
		// System.out.print(xml);
		return xml;
	}

	/**
	 * 
	 * 解析电池检测返回Xml
	 * 
	 * @param xml
	 * @return 电池检测结果集
	 */
	@SuppressWarnings("rawtypes")
	public List<NewareInquireResultBean> parseInquireXml(String xml) {
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);
		SAXBuilder sax = new SAXBuilder();
		List<NewareInquireResultBean> inquireResult = new ArrayList<NewareInquireResultBean>();
		try {
			Document doc = sax.build(source);
			Element root = doc.getRootElement();
			Element el = null;
			List node = root.getChildren();
			for (int i = 0; i < node.size(); i++) {
				el = (Element) node.get(i);
				if (el.getName().equals("list")) {

					List inquireList = el.getChildren();
					for (int j = 0; j < inquireList.size(); j++) {
						Element inquire = (Element) inquireList.get(j);
						NewareInquireResultBean inquireBean = new NewareInquireResultBean();
						inquireBean.setDev(inquire.getAttributeValue("dev"));
						inquireBean.setWorkstatus(inquire.getAttributeValue("workstatus"));
						String current = inquire.getAttributeValue("current");
						current = current.replace("-", "");
						inquireBean.setCurrent(current);
						inquireBean.setVoltage(inquire.getAttributeValue("voltage"));
						inquireBean.setCapacity(inquire.getAttributeValue("capacity"));
						inquireBean.setEnergy(inquire.getAttributeValue("energy"));
						inquireBean.setTotaltime(inquire.getAttributeValue("totaltime"));
						inquireBean.setRelativetime(inquire.getAttributeValue("relativetime"));
						inquireBean.setAuxtemp(inquire.getAttributeValue("auxtemp"));
						inquireBean.setAuxvol(inquire.getAttributeValue("auxvol"));
						inquireResult.add(inquireBean);
					}
				}
			}

		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		return inquireResult;
	}

	/**
	 * 解析检测状态
	 * 
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<NewareStartStatusBean> parseStatusXml(String xml) {
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);
		SAXBuilder sax = new SAXBuilder();
		List<NewareStartStatusBean> statusResult = new ArrayList<NewareStartStatusBean>();
		try {
			Document doc = sax.build(source);
			Element root = doc.getRootElement();
			Element el = null;
			List node = root.getChildren();
			for (int i = 0; i < node.size(); i++) {
				el = (Element) node.get(i);
				if (el.getName().equals("list")) {

					List statusList = el.getChildren();
					for (int j = 0; j < statusList.size(); j++) {
						Element status = (Element) statusList.get(j);
						NewareStartStatusBean statusBean = new NewareStartStatusBean();
						statusBean.setIp(status.getAttributeValue("ip"));
						statusBean.setDevtype(status.getAttributeValue("devtype"));
						statusBean.setDevid(status.getAttributeValue("devid"));
						statusBean.setSubdevid(status.getAttributeValue("subdevid"));
						statusBean.setChlid(status.getAttributeValue("chlid"));
						statusBean.setStatus(status.getText());
						statusResult.add(statusBean);
					}
				}
			}

		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return statusResult;
	}

	/**
	 * 格式化Xml文档
	 * 
	 * @return
	 */
	public static Format FormatXML() {
		// 格式化生成的xml文件，如果不进行格式化的话，生成的xml文件将会是很长的一行...
		Format format = Format.getCompactFormat();
		format.setEncoding("utf-8");
		// format.setIndent(" ");
		return format;
	}

}
