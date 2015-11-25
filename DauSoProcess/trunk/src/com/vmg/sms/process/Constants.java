package com.vmg.sms.process;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import com.vmg.sms.common.Util;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class Constants {
	public static int NUM_THREAD = 10;
	public static int NUM_THREAD_LOAD_MO = 2;
	public static int NUM_THREAD_INSERTLOG = 1;

	public static int MAX_RETRIES = 10;

	public static String LOAD_MO_MODE = "DB";
	public static String MO_DIR = "Z:/";
	public static String LOGFILE = "${yyyy-MM-dd}.log";
	public static String LOGPATH = "log/";

	public static String LOGLEVEL = "info,warn,error,crisis";
	public static String[] RUNCLASS = null;

	public static String[] TELCOS = { "VIETTEL", "VMS", "GPC", "EVN" };
	public static String TELCOLIST = "VIETTEL,VMS,GPC,EVN";
	public static int TIME_DELAY_LOAD_MO = 100;

	public static Properties _prop;

	public static String MT_CHARGING = "1";
	public static String MT_NOCHARGE = "0";
	public static String MT_PUSH = "3";
	public static String MT_REFUND = "2";
	public static String MT_REFUND_SYNTAX = "21";
	public static String MT_REFUND_CONTENT = "22";

	public static String INV_CLASS = "com.vmg.sms.process.InvalidSMS";
	public static String INV_KEYWORD = "INV";
	public static String INV_INFO = "Tin nhan sai cu phap";

	public Constants() {
	}

	public static boolean loadProperties(String propFile) {
		Properties properties = new Properties();
		Util.logger.info("Reading configuration file " + propFile);
		try {
			FileInputStream fin = new FileInputStream(propFile);
			properties.load(fin);
			_prop = properties;
			fin.close();

			NUM_THREAD = Integer.parseInt(properties.getProperty("NUM_THREAD",
					"10"));
			NUM_THREAD_LOAD_MO = Integer.parseInt(properties.getProperty(
					"NUM_THREAD_LOAD_MO", "2"));
			LOAD_MO_MODE = properties.getProperty("LOAD_MO_MODE", "DB");
			MO_DIR = properties.getProperty("MO_DIR", MO_DIR);
			LOGFILE = properties.getProperty("LOGFILE", LOGFILE);
			LOGLEVEL = properties.getProperty("LOGLEVEL", LOGLEVEL);

			TIME_DELAY_LOAD_MO = Integer.parseInt(properties.getProperty(
					"TIME_DELAY_LOAD_MO", "" + TIME_DELAY_LOAD_MO));

			String runclass = properties.getProperty("RUNCLASS", "");
			RUNCLASS = parseString(runclass, ",");

			INV_CLASS = properties.getProperty("INV_CLASS", INV_CLASS);
			INV_KEYWORD = properties.getProperty("INV_KEYWORD", INV_KEYWORD);
			INV_INFO = properties.getProperty("INV_INFO", INV_INFO);

			MAX_RETRIES = Integer.parseInt(properties.getProperty(
					"MAX_RETRIES", "10"));

			TELCOLIST = properties.getProperty("TELCOLIST", TELCOLIST);
			TELCOS = parseString(TELCOLIST, ",");
			return true;

		} catch (Exception e) {
			Util.logger.info("Reading configuration file " + propFile + "@"
					+ e.toString());
			return false;
		}

	}

	public static int getintproperties(String text, int defaultval) {
		try {
			return Integer.parseInt(_prop.getProperty(text, defaultval + ""));

		} catch (Exception e) {
			// TODO: handle exception
		}
		return defaultval;
	}

	public static String getstringproperties(String text, String defaultval) {
		try {
			return (_prop.getProperty(text, defaultval + ""));

		} catch (Exception e) {
			// TODO: handle exception
		}
		return defaultval;
	}

	public static String[] parseString(String text, String seperator) {
		Vector vResult = new Vector();
		if (text == null || "".equals(text)) {
			return null;
		}
		String tempStr = text.trim();
		String currentLabel = null;
		int index = tempStr.indexOf(seperator);
		while (index != -1) {
			currentLabel = tempStr.substring(0, index).trim();

			if (!"".equals(currentLabel)) {
				vResult.addElement(currentLabel);
			}
			tempStr = tempStr.substring(index + 1);
			index = tempStr.indexOf(seperator);
		} // Last label
		currentLabel = tempStr.trim();
		if (!"".equals(currentLabel)) {
			vResult.addElement(currentLabel);
		}
		String[] re = new String[vResult.size()];
		Iterator it = vResult.iterator();
		index = 0;
		while (it.hasNext()) {
			re[index] = (String) it.next();
			index++;
		}
		return re;
	}

}
