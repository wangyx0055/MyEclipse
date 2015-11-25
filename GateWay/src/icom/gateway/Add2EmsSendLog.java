package icom.gateway;

/**
 * <p>Title:IT-R&D VMG</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2007</p>
 * <p>Company: VMG</p>
 * @author Duong Kien Trung
 * @version 1.0
 */

import icom.common.*;

import java.math.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Add2EmsSendLog extends Thread {
	final static int MAX_SMS_IN_QUEUE = 1000;
	private EMSData sms = null;
	private Queue logQueue = null;
	private DBTools dbTools = null;
	private int Results = 0;

	public Add2EmsSendLog(Queue InvQueue) {
		this.logQueue = InvQueue;
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
				if (logQueue.size() > 0) {
					this.AddMT2SendLog();
					this.sleep(100);
				} else {
					this.sleep(10 * 1000); // 10s
				}
			} catch (Exception ex) {
				try {
					Gateway.util.log(this.getClass().getName(),
							"MoveMO2Lottery:" + ex.getMessage());
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

	public void AddMT2SendLog() {

		sms = (EMSData) logQueue.dequeue();
		int logID1 = 0;
		try {
			logID1 = dbTools.add2EMSSendLog(sms);
			if (logID1 != 0) {
				Gateway.util.log(this.getClass().getName(),
						"{AddMT2Log}{User_ID=" + sms.getUserId()
								+ "}{Service_ID=" + sms.getServiceId()
								+ "}{Info=" + sms.getText() + "}{Request_ID"
								+ sms.getsRequestID() + "}{emsID="
								+ sms.getId() + "}");

				if (sms.getMessageType() == Constants.CDR_CHARGE) {
					if (dbTools.add2CdrQueueb(sms, "1")) {
						Gateway.util.log(this.getClass().getName(),
								"{CDR write=yes}{User_ID=" + sms.getUserId()
										+ "}{Service_ID=" + sms.getServiceId()
										+ "}{MessageType=1}{RequestID="
										+ sms.getRequestId() + "}");
					} else {
						Gateway.util.logConsole(this.getClass().getName(),
								"{CDR write Err}{User_ID=" + sms.getUserId()
										+ "}{Service_ID=" + sms.getServiceId()
										+ "}{MessageType=1}{RequestID="
										+ sms.getRequestId() + "}");

						DBTools.ALERT("Add2CDRDB", "Add2CDRDB",
								Constants.ALERT_SERIOUS, Preference.Channel
										+ "Exception: "
										+ "{CDR write Err}{User_ID="
										+ sms.getUserId()
										+ "}{Service_ID="
										+ sms.getServiceId()
										+ "}{MessageType=1}{RequestID="
										+ sms.getRequestId()
										+ "}"
										,
								Preference.ALERT_CONTACT);
					}
				} else if ((sms.getMessageType() + "").startsWith(""
						+ Constants.CDR_REFUND)) {
					if (dbTools.add2CdrQueueb(sms, sms.getMessageType() + "")) {
						Gateway.util.log(this.getClass().getName(),
								"{CDR write=yes}{User_ID=" + sms.getUserId()
										+ "}{Service_ID=" + sms.getServiceId()
										+ "}{MessageType=0}{RequestID="
										+ sms.getRequestId() + "}");
					} else {
						Gateway.util.logConsole(this.getClass().getName(),
								"{CDR write Err}{User_ID=" + sms.getUserId()
										+ "}{Service_ID=" + sms.getServiceId()
										+ "}{MessageType=0}{RequestID="
										+ sms.getRequestId() + "}");

						DBTools.ALERT("Add2CDRDB", "Add2CDRDB",
								Constants.ALERT_SERIOUS, Preference.Channel
										+ "Exception: "
										+ "{CDR write Err}{User_ID="
										+ sms.getUserId()
										+ "}{Service_ID="
										+ sms.getServiceId()
										+ "}{MessageType=0}{RequestID="
										+ sms.getRequestId()
										+ "}"
										,
								Preference.ALERT_CONTACT);
					}

				}

			} else {
				Gateway.util.logErr(this.getClass().getName(),
						"{ERR: Add MT2Log and CDR}{User_ID=" + sms.getUserId()
								+ "}{Service_ID=" + sms.getServiceId()
								+ "}{Info=" + sms.getText() + "}{Request_ID"
								+ sms.getsRequestID() + "}{emsID="
								+ sms.getId() + "}");
			}
		} catch (Exception ex) {
			Gateway.util.logErr(this.getClass().getName(),
					"{ERR: Add MT2Log and CDR}{User_ID=" + sms.getUserId()
							+ "}{Service_ID=" + sms.getServiceId() + "}{Info="
							+ sms.getText() + "} ERR=" + ex.getMessage());

		}

	}
}
