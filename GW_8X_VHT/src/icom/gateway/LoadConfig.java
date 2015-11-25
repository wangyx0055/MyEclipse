package icom.gateway;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;



public class LoadConfig extends Thread {

	private Hashtable keywords;
	public Vector vtKeyword;
	public boolean isLoaded = false;

	public LoadConfig() {

	}

	public Keyword getKeyword(String keyword, String serviceid) {
		Keyword retobj = new Keyword();
		retobj.setClassname("INV");
		retobj.setKeyword("INV");
		retobj.setServiceid(serviceid);
		String keytosearch = serviceid + "@" + keyword;
		String strkey = "INV";
		for (Iterator it = vtKeyword.iterator(); it.hasNext();) {
			String currLabel = (String) it.next();
			if (keytosearch.startsWith(currLabel)) {
				strkey = currLabel;
				retobj = (Keyword) keywords.get(strkey);
				Logger.info("{LoadConfig.getKeyword}{Keytosearh="
						+ keytosearch + "} {keyword=" + retobj.getKeyword()
						+ "}");
				return retobj;
			}
		}
		Logger.info("{LoadConfig.getKeyword}{Keytosearh=" + keytosearch
				+ "} {keyword=" + retobj.getKeyword() + "}");
		return retobj;
	}

	public void run() {

		Logger.info("LoadConfig - Start");
		isLoaded = false;
		while (Gateway.running) {

			try {
				keywords = Keyword.retrieveKeyword();
				isLoaded = true;

				try {
					sleep(2*1000 * 60);

				} catch (InterruptedException ex3) {

				}
			} catch (Exception ex3) {
				Logger.crisis("Load config error:" + ex3.toString());
				
				DBTools.ALERT("Load config error", "Load config error",
						Constants.ALERT_SERIOUS, Preference.Channel
								+ "Load config error: " + ex3.getMessage()
								,
						Preference.ALERT_CONTACT);

			}

		}

	}
}
