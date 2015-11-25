package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import icom.common.Queue;

import java.text.SimpleDateFormat;
import java.util.*;

public class WriteLogQueue extends Thread {
	private Queue toSMSC = null;
	private Queue fromSMSC = null;
	private Queue respondSMSC = null;
	private Queue EMSQueue = null;
	private Queue SendLogQueue = null;
	private Queue ResendQueue = null;

	private Map wait4ResponseTable = null;

	public WriteLogQueue(Queue toSMSC, Queue fromSMSC, Queue respondSMSC,
			Queue EMSQueue, Queue SendLogQueue, Queue ResendQueue,
			Map wait4ResponseTable) {
		this.toSMSC = toSMSC;
		this.fromSMSC = fromSMSC;
		this.respondSMSC = respondSMSC;
		this.EMSQueue = EMSQueue;
		this.SendLogQueue = SendLogQueue;
		this.ResendQueue = ResendQueue;
		this.wait4ResponseTable = wait4ResponseTable;

	}

	public void run() {
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////
		while (Gateway.running) {

			synchronized (wait4ResponseTable) {
				Logger.info(Preference.mobileOperator + "-toSMSQueueTX: "
						+ toSMSC.size() + "\n" + Preference.mobileOperator
						+ "-fromSMSQueueRX: " + fromSMSC.size() + "\n"
						+ Preference.mobileOperator + "-RespondSMSQueueRX: "
						+ respondSMSC.size() + "\nWait4ResponseTable: "
						+ wait4ResponseTable.size() + "\nSendLogQueue: "
						+ SendLogQueue.size() + "\nGetMT2Queue: "
						+ EMSQueue.size() + "\nResendQueue: "
						+ ResendQueue.size());
			}

			if ((toSMSC.size() > 2) || (fromSMSC.size() > 2)
					|| (respondSMSC.size() > 2) || (SendLogQueue.size() > 2)
					|| (EMSQueue.size() > 2) || (ResendQueue.size() > 2)
					|| (wait4ResponseTable.size() > 2)) {
				synchronized (wait4ResponseTable) {
					Logger.info(Preference.mobileOperator + "-toSMSQueueTX: "
							+ toSMSC.size() + "\n" + Preference.mobileOperator
							+ "-fromSMSQueueRX: " + fromSMSC.size() + "\n"
							+ Preference.mobileOperator
							+ "-RespondSMSQueueRX: " + respondSMSC.size()
							+ "\nWait4ResponseTable: "
							+ wait4ResponseTable.size() + "\nSendLogQueue: "
							+ SendLogQueue.size() + "\nGetMT2Queue: "
							+ EMSQueue.size() +

							"\nResendQueue: " + ResendQueue.size());
				}
				// DBTools.Alert2YM("Gateway Start!!!");
			}

			if ((toSMSC.size() > Preference.ALERT_TOSMSC)
					|| (fromSMSC.size() > Preference.ALERT_FROMSMSC)
					|| (respondSMSC.size() > Preference.ALERT_RESPONSESMSC)
					|| (EMSQueue.size() > Preference.ALERT_EMSQUEUE)
					|| (wait4ResponseTable.size() > Preference.ALERT_RESPONSETABLE)) {
				String sinfo = "";
				synchronized (wait4ResponseTable) {
					sinfo = Preference.Channel + "-toSMSC: " + toSMSC.size()
							+ "\n" + "fromSMSC: " + fromSMSC.size() + "\n"
							+ "responseSMSC: " + respondSMSC.size()
							+ "\nGetMT2Queue: " + EMSQueue.size()
							+ "\nWait4ResponseTable: "
							+ wait4ResponseTable.size();
				}

				DBTools.ALERT("Queue", "Queue", Constants.ALERT_MAJOR, sinfo,
						Preference.ALERT_CONTACT);

			}

			// DBTools.Alert2YM("Gateway Start!!!");

			Logger.info("-----Auto Update Log for " + Preference.Channel);
			try {
				sleep(60 * 1000);
			} catch (InterruptedException ex) {
			}
		} // while
		// ////////////////////
		this.destroy();
		// ////////////////////
	}

	public void destroy() {
		Gateway.removeThread(this);
	}
}
