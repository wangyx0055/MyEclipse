package com.vmg.sms.process;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.common.Utilities;

public class LoadConfig extends Thread {

	private Hashtable keywords;
	public Vector vtKeyword;
	public boolean isLoaded = false;

	public LoadConfig() {

	}

	public Keyword getKeyword(String keyword, String serviceid) {
		Keyword retobj = new Keyword();
		retobj.setClassname(Constants.INV_CLASS);
		retobj.setKeyword(Constants.INV_KEYWORD);
		retobj.setServiceid(serviceid);
		String keytosearch = serviceid + "@" + keyword;
		keytosearch = keytosearch.toUpperCase();
		String strkey = Constants.INV_KEYWORD;
		for (Iterator it = vtKeyword.iterator(); it.hasNext();) {

			String currLabel = (String) it.next();

			if (keytosearch.startsWith(currLabel)) {
				strkey = currLabel;
				retobj = (Keyword) keywords.get(strkey);
				Util.logger.info("{LoadConfig.getKeyword}{Keytosearh="
						+ keytosearch + "} {keyword=" + retobj.getKeyword()
						+ "}");
				return retobj;

			}
		}
		Util.logger.info("{LoadConfig.getKeyword}{Keytosearh=" + keytosearch
				+ "} {keyword=" + retobj.getKeyword() + "}");
		return retobj;
	}

	public Keyword getKeywordInvalid(String keyword, String serviceid) {
		Keyword retobj = new Keyword();
		String newkeyword = Utilities.replaceWhiteLetter(keyword);
		retobj.setClassname(Constants.INV_CLASS);
		retobj.setKeyword(Constants.INV_KEYWORD);
		retobj.setServiceid(serviceid);

		String keytosearch = serviceid + "@" + newkeyword;
		keytosearch = keytosearch.toUpperCase();
		String strkey = Constants.INV_KEYWORD;
		for (Iterator it = vtKeyword.iterator(); it.hasNext();) {
			String currLabel = (String) it.next();
			if (keytosearch.startsWith(currLabel)) {
				strkey = currLabel;
				retobj = (Keyword) keywords.get(strkey);
				Util.logger.info("{LoadConfig.getKeyword}{Keytosearh="
						+ keytosearch + "}{msg_old=" + keyword + "} {keyword="
						+ retobj.getKeyword() + "}");
				return retobj;

			}
		}
		Util.logger.info("{LoadConfig.getKeyword}{Keytosearh=" + keytosearch
				+ "}{msgold=" + keyword + "} {keyword=" + retobj.getKeyword()
				+ "}");
		return retobj;
	}

	public Keyword getKeywordInvalidLast(String keyword, String serviceid) {
		Keyword retobj = new Keyword();
		String newkeyword = Utilities.replaceWhiteLetter(keyword);

		newkeyword = newkeyword.replace(".", "");

		newkeyword = newkeyword.replace(" ", "");

		retobj.setClassname(Constants.INV_CLASS);
		retobj.setKeyword(Constants.INV_KEYWORD);
		retobj.setServiceid(serviceid);

		String keytosearch = serviceid + "@" + newkeyword;
		keytosearch = keytosearch.toUpperCase();
		String strkey = Constants.INV_KEYWORD;
		for (Iterator it = vtKeyword.iterator(); it.hasNext();) {
			String currLabel = (String) it.next();
			if (keytosearch.startsWith(currLabel)) {
				strkey = currLabel;
				retobj = (Keyword) keywords.get(strkey);
				Util.logger.info("{LoadConfig.getKeyword}{Keytosearh="
						+ keytosearch + "}{msg_old=" + keyword + "} {keyword="
						+ retobj.getKeyword() + "}");
				return retobj;

			}
		}
		Util.logger.info("{LoadConfig.getKeyword}{Keytosearh=" + keytosearch
				+ "}{msgold=" + keyword + "} {keyword=" + retobj.getKeyword()
				+ "}");
		return retobj;
	}

	public void run() {

		Util.logger.info("LoadConfig - Start");
		while (ConsoleSRV.processData) {

			try {
				keywords = Keyword.retrieveKeyword();
				isLoaded = true;

				try {
					sleep(1000 * 60);

				} catch (InterruptedException ex3) {

				}
			} catch (Exception ex3) {
				Util.logger.crisis("Loi khi doc cau hinh:" + ex3.toString());

			}

		}

	}
}
