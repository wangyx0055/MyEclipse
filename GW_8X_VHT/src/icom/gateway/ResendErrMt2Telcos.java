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

import java.sql.Timestamp;

public class ResendErrMt2Telcos extends Thread {
	final static int MAX_SMS_IN_QUEUE = 1000;
	private EMSData sms = null;
	private Queue ResendQueue = null;
	private Queue EMSQueue = null;

	public ResendErrMt2Telcos(Queue ResendQueue, Queue EMSQueue) {
		this.ResendQueue = ResendQueue;
		this.EMSQueue = EMSQueue;
		this.setPriority(Thread.MIN_PRIORITY);
	}

	public void run() {
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////
		while (Gateway.running) {
			try {
				// Not over Max messages in queue
				if (ResendQueue.size() > 0) {
					for (int i = 0; i < ResendQueue.size(); i++) {
						sms = (EMSData) ResendQueue.dequeue();
						if (sms != null)
							this.ResendMT(sms);
						else
							Gateway.util
									.log(this.getClass().getName(),
											"{Can't resend sms get from ResendQueue is null}");
						this.sleep(100);
					}
					this.sleep(Preference.timeResend * 60 * 1000);
				} else {
					this.sleep(Preference.timeResend * 60 * 1000);
				}
			} catch (Exception ex) {
				Gateway.util.logErr(this.getClass().getName(),
						"ResendErrMt2Telcos:" + ex.getMessage());
			}
		}
		// /////////////////////////////
		this.destroy();
		// /////////////////////////////
	}

	public void destroy() {
		Gateway.removeThread(this);
	}

	public void ResendMT(EMSData sms) {
		try {
			if (isTimeResend(sms.getSubmitDate())) {
				Gateway.util.log(this.getClass().getName(),
						"{ResendMT:add2 SendQueue}{User_ID=" + sms.getUserId()
								+ "}{Service_ID=" + sms.getServiceId()
								+ "}{Info=" + sms.getText() + "}{Request_ID"
								+ sms.getsRequestID() + "}");
				Timestamp time = new Timestamp(System.currentTimeMillis());
				sms.setDoneDate(time);
				EMSQueue.enqueue(sms);
			} else {
				ResendQueue.enqueue(sms);
			}
		} catch (Exception ex) {
			Gateway.util.logErr(this.getClass().getName(),
					"{ERR: ResendMT}{User_ID=" + sms.getUserId()
							+ "}{Service_ID=" + sms.getServiceId() + "}{Info="
							+ sms.getText() + "} ERR=" + ex.getMessage());

		}

	}

	public boolean isTimeResend(Timestamp time) {
		long currTime = System.currentTimeMillis();
		if ((currTime - time.getTime()) > Preference.timeResend * 60 * 1000) {
			return true;
		}
		return false;
	}

}
