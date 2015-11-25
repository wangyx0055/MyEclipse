package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author ICom
 * @version 1.0
 */

import icom.common.*;
import icom.common.Queue;

import java.text.SimpleDateFormat;
import java.util.*;

public class GetMt2EmsQueue extends Thread {
	private Queue EMSQueue = null;
	private DBTools dbTools = null;
	private String iMod = "";
	private String iNum = "";
	public int iDB = 0;

	/*
	 * GetMt2EmsQueue
	 * 
	 * @param EMSQueue
	 * 
	 * @param iMod
	 * 
	 * @param iNum
	 */
	public GetMt2EmsQueue(Queue EMSQueue, String iMod, String iNum, int _iDB) {
		this.EMSQueue = EMSQueue;
		this.iMod = iMod;
		this.iNum = iNum;
		this.dbTools = new DBTools();
		this.iDB = _iDB;
	}

	public void run() {
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////
		while (Gateway.running) {
			if (Gateway.bound) {
				try {
					this.AddEMS2QueueEx();
					this.sleep(100);
				}

				catch (InterruptedException ex) {
					Gateway.util.log(this.getClass().getName(),
							"{InterruptedException}" + ex.getMessage());

					DBTools
							.ALERT("Getmt2emsqueue", "Getmt2emsqueue",
									Constants.ALERT_WARN, Preference.Channel
											+ "InterruptedException: "
											+ ex.getMessage(),
									Preference.ALERT_CONTACT);

				}

				catch (DBException ex) { // when lost connection to db
					Gateway.util.log(this.getClass().getName(), "{DBException}"
							+ ex.getMessage());
					DBTools.ALERT("Getmt2emsqueue", "Getmt2emsqueue",
							Constants.ALERT_WARN, Preference.Channel
									+ "DBException: " + ex.getMessage(),
							Preference.ALERT_CONTACT);
				}

				catch (Exception ex) {
					Gateway.util.log(this.getClass().getName(),
							"GetMt2EmsQueue:: " + ex.getMessage());

					DBTools.ALERT("Getmt2emsqueue", "Getmt2emsqueue",
							Constants.ALERT_WARN, Preference.Channel
									+ "Exception: " + ex.getMessage(),
							Preference.ALERT_CONTACT);
				}
			}
			else {
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		} // while

		// ///////////////////
		Gateway.util.log(this.getClass().getName(), "{"
				+ this.getClass().getName() + " stopped}");
		this.destroy();

		// ///////////////////
	}

	public void destroy() {
		Gateway.removeThread(this);
	}

	public int AddEMS2QueueEx() throws DBException, EMSException {
		int numOfEms = 0;
		try {
			if (!(dbTools.getAllEMSSendQueue(Preference.SEND_MODE, this.iMod,
					this.iNum, EMSQueue, iDB))) {
				Gateway.util.logErr(this.getClass().getName(),
						"Loi load MT tu DB");
			}
		} catch (Exception ex) {
			Gateway.util.logErr(this.getClass().getName(), "{Exception:}"
					+ ex.getMessage());
		}
		return numOfEms;
	}
}
