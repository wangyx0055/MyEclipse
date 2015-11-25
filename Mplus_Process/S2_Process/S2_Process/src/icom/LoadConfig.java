package icom;

import icom.common.Util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class LoadConfig extends Thread {
	private Hashtable keywords;
	public Vector vtKeyword;
	public static Hashtable hServices;
	// private Hashtable prices;
	public Vector vtPrices;
	public boolean LOADED = false;

	public LoadConfig() {

	}

	public Keyword getKeyword(String keyword, String serviceid) {

		Keyword retobj = new Keyword();

		retobj.setKeyword(Constants.INV_KEYWORD);

		retobj.setCpmo(Constants.INV_CP);
		retobj.setCpmt(Constants.INV_CP);
		retobj.setPartner_mo(Constants.INV_CP + "");
		retobj.setPartner_mt(Constants.INV_CP + "");

		retobj.setServiceid(serviceid);
		retobj.setClass_mo(Constants.INV_MO_CLASS);
		retobj.setClass_mt(Constants.INV_MT_CLASS);
		String keytosearch = serviceid + "@" + keyword;
		boolean bCheck = true;

		String[] arrKeyNotE = Constants._prop.getProperty("keyword_config")
				.split(";");

		for (int i = 0; i < arrKeyNotE.length; i++) {
			if (keyword.toUpperCase().startsWith(arrKeyNotE[i]))
				bCheck = false;
		}

		String strkey = Constants.INV_KEYWORD;
		for (Iterator it = vtKeyword.iterator(); it.hasNext();) {
			String currLabel = (String) it.next();
			// neu nhu start bang dk hoac huy se bat dung keywords neu khong se
			// check start with
			if (bCheck) {
				if (keytosearch.toLowerCase().equalsIgnoreCase(
						currLabel.toLowerCase())
						|| keytosearch.equals(currLabel)) {
					strkey = currLabel;
					retobj = (Keyword) keywords.get(strkey);
					Util.logger
							.info("{LoadConfig.getKeyword}@ search success{Keytosearh="
									+ keytosearch
									+ "} {keyword="
									+ retobj.getKeyword() + "}");
					
					return retobj;
				}
			} else if (keytosearch.toLowerCase().equalsIgnoreCase(
					currLabel.toLowerCase())
					|| keytosearch.startsWith(currLabel)) {
				strkey = currLabel;
				retobj = (Keyword) keywords.get(strkey);
				Util.logger
						.info("{LoadConfig.getKeyword}@ search success{Keytosearh="
								+ keytosearch
								+ "} {keyword="
								+ retobj.getKeyword() + "}");
				return retobj;
			}
		}
		Util.logger.info("{LoadConfig.getKeyword}{Keytosearh=" + keytosearch
				+ "} {keyword=" + retobj.getKeyword() + "}");
		retobj.setService_ss_id(strkey);
		
		return retobj;
	}

	public Prices getPrice(String services_ssid) {
		Prices retobj = new Prices();
		String keytosearch = services_ssid;
		String strkey = "";
		for (Iterator it = vtPrices.iterator(); it.hasNext();) {
			String currLabel = (String) it.next();
			if (keytosearch.equalsIgnoreCase(currLabel)) {
				strkey = currLabel;
				// retobj = (Prices) prices.get(strkey);
				Util.logger.info("{LoadConfig.getPrice}{Keytosearh="
						+ keytosearch + "} {services="
						+ retobj.getService_ss_id() + "}");
				return retobj;
			}
		}
		// Util.logger.info("{LoadConfig.getPrice}{Keytosearh=" + keytosearch
		// + "} {keyword=" + retobj.getPrice_new() + "}");
		return null;
	}

	public void updateKeyword(String serviceName, String serviceId,
			Keyword keywordObj) {

		String strKey = serviceId + "@" + serviceName;
		keywords.put(strKey, keywordObj);
	}

	@Override
	public void run() {
		Util.logger.info("LoadConfig - Start");
		while (Sender.processData && !LOADED) {
			try {
				keywords = Keyword.retrieveKeyword();
				// prices = Prices.retrieve();
				LOADED = true;

				// try {
				// sleep(1000 * 30 * 1);
				//
				// } catch (InterruptedException ex3) {
				// }

			} catch (Exception ex3) {
				Util.logger.crisis("Loi khi doc cau hinh:" + ex3.toString());
				Util.logger.printStackTrace(ex3);
			}
		}
	}
}
