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
import java.util.Map;
import java.util.Enumeration;
import java.util.Vector;

public class CheckTimeOutWait4Response extends Thread
{
	// final static int MAX_SMS_IN_QUEUE = 1000;
	private EMSData sms = null;
	private Queue SendLogQueue = null;
	private Map Wait4ResponseTable = null;

	public CheckTimeOutWait4Response(Map Wait4ResponseTable, Queue SendLogQueue)
	{
		this.Wait4ResponseTable = Wait4ResponseTable;
		this.SendLogQueue = SendLogQueue;
		this.setPriority(Thread.MIN_PRIORITY);
	}

	public void run()
	{
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////

		while (Gateway.running)
		{

			try
			{
				if (Wait4ResponseTable.size() > 0)
				{
					Thread.sleep(60 * 1000);
					// this.sleep(Preference.timeOut * 60 * 1000);
					// Not over Max messages in queue
					synchronized (Wait4ResponseTable)
					{
						Logger.info(this.getClass().getName() + "@" + "CheckTimeOutWait4Response");
						for (Enumeration e = new Vector(Wait4ResponseTable.keySet()).elements(); e.hasMoreElements();)
						{

							String key = (String) e.nextElement(); // key=messageId
							Logger.info(this.getClass().getName() + "@" + "CheckTimeOutWait4Response& key=" + key);

							EMSData ems = (EMSData) Wait4ResponseTable.get(key);
							if (ems == null)
							{
								Logger.info(this.getClass().getName() + "@" + "emsid=" + key + " @ems=null");
								Wait4ResponseTable.remove(key);
								continue;
							}

							Logger.info(this.getClass().getName() + "@" + "CheckTimeOutWait4Response& key=" + key + "A:" + ems.getSubmitDate());

							if (isTimeOut(ems.getSubmitDate()))
							{
								Logger.info(this.getClass().getName() + "@" + "emsid=" + key + " @ems=istimeout");
								ems.setProcessResult(4);
								ems.setNotes("No ACK return for this message!!!");
								if (ems.getMessageType() == Constants.CDR_CHARGE)
								{
									ems.setMessageType(Constants.CDR_CHARGE);
								}

								SendLogQueue.enqueue(ems);
								Wait4ResponseTable.remove(key);
								continue;
							}
							// Wait4ResponseTable.put(key,ems);

						}
					}

					// this.sleep(30 * 60*1000);
				}
				else
				{
					Thread.sleep(Preference.timeOut * 60 * 1000);
				}
			}
			catch (Exception ex)
			{
				Utilities.logErr(this.getClass().getName(), "CheckTimeOutWait4Response:" + ex.getMessage());

			}
		}
		// /////////////////////////////
		this.destroy();
		// /////////////////////////////
	}

	public void destroy()
	{
		Gateway.removeThread(this);
	}

	public boolean isTimeOut(Timestamp time)
	{
		long currTime = System.currentTimeMillis();
		if ((currTime - time.getTime()) > Preference.timeOut * 60 * 1000)
		{
			return true;
		}
		return false;
	}

}
