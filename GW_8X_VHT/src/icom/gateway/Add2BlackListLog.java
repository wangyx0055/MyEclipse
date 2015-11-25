package icom.gateway;


import icom.common.*;

import java.math.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Add2BlackListLog extends Thread {
	final static int MAX_SMS_IN_QUEUE = 1000;
	private EMSData sms = null;
	private Queue blacklistQueue = null;
	private DBTools dbTools = null;
	private int Results = 0;

	public Add2BlackListLog(Queue blQueue) {
		this.blacklistQueue = blQueue;
		this.dbTools = new DBTools();
		this.setPriority(Thread.NORM_PRIORITY);
	}

	public void run() {
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////
		while (Gateway.running) {
			try {
				// Not over Max messages in queue
				if (blacklistQueue.size() > 0) {
					this.AddMT2BlacklistLog();
					this.sleep(100);
				} else {
					this.sleep(10 * 1000); // 10s
				}
			} catch (Exception ex) {
				try {
					Gateway.util.log(this.getClass().getName(),
							"AddMT2BlacklistLog:" + ex.getMessage());
				} catch (Exception e) {
				}
			}

		}
		// /////////////////////////////
		this.destroy();
		// /////////////////////////////
	}

	public void destroy() {
		Gateway.removeThread(this);
	}

	public void AddMT2BlacklistLog() {

		sms = (EMSData) blacklistQueue.dequeue();
		int logID1 = 0;
		try {
			
			logID1 = dbTools.add2BlacklistLog(sms);
			if (logID1 != 0) {
				Gateway.util.log(this.getClass().getName(),
						"{add2BlacklistLog}{User_ID=" + sms.getUserId()
								+ "}{Service_ID=" + sms.getServiceId()
								+ "}{Info=" + sms.getText() + "}{Request_ID"
								+ sms.getsRequestID() + "}{emsID="
								+ sms.getId() + "}");

			} else {
				Gateway.util.logErr(this.getClass().getName(),
						"{ERR: add2BlacklistLog}{User_ID=" + sms.getUserId()
								+ "}{Service_ID=" + sms.getServiceId()
								+ "}{Info=" + sms.getText() + "}{Request_ID"
								+ sms.getsRequestID() + "}{emsID="
								+ sms.getId() + "}");
			}
		} catch (Exception ex) {
			Gateway.util.logErr(this.getClass().getName(),
					"{ERR: add2BlacklistLog}{User_ID=" + sms.getUserId()
							+ "}{Service_ID=" + sms.getServiceId() + "}{Info="
							+ sms.getText() + "} ERR=" + ex.getMessage());

		}

	}
}
