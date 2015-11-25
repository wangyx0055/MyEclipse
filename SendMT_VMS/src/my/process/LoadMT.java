package my.process;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import my.define.MTObject;
import my.define.MTObject.Status;
import my.define.MTQueue;
import my.define.MySetting;
import MyDataSource.MyTableModel;
import MyGateway.ems_send_queue;
import MyUtility.MyLogger;

/**
 * Lấy MT bỏ lên queue
 * 
 * @author Administrator
 * 
 */
public class LoadMT extends Thread
{
	static MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), LoadMT.class.toString());
	MTQueue mQueue = null;
	int ProcessNumber = 1;
	int ProcessIndex = 1;

	ems_send_queue mSendQueue = null;

	static int TIME_DELAY_LOAD = 100;

	public LoadMT(MTQueue mQueue, int ProcessNumber, int ProcessIndex)
	{
		try
		{
			this.mQueue = mQueue;
			this.ProcessNumber = ProcessNumber;
			this.ProcessIndex = ProcessIndex;
			mSendQueue = new ems_send_queue(MySetting.GetProxoolConfigPath(), MySetting.PoolName_GateWay);
		}
		catch (Exception ex)
		{
			mLog.log.error("Error Contructor", ex);
		}
	}

	public void run()
	{
		while (MySetting.AllowRunning)
		{
			MTObject mObject = null;

			try
			{
				MyTableModel mTable = mSendQueue.Select(ProcessNumber, ProcessIndex, 20);

				String ListID_Delete = "";

				for (int i = 0; i < mTable.GetRowCount(); i++)
				{
					int ID = 0;
					try
					{
						ID = (Integer) mTable.GetValueAt(i, "ID");
						String USER_ID = mTable.GetValueAt(i, "USER_ID").toString();
						String SERVICE_ID = mTable.GetValueAt(i, "SERVICE_ID").toString();
						String MOBILE_OPERATOR = mTable.GetValueAt(i, "MOBILE_OPERATOR").toString();
						String COMMAND_CODE = mTable.GetValueAt(i, "COMMAND_CODE").toString();
						int CONTENT_TYPE = Integer.parseInt(mTable.GetValueAt(i, "CONTENT_TYPE").toString());
						String INFO = mTable.GetValueAt(i, "INFO").toString();
						Timestamp SUBMIT_DATE = new Timestamp(Calendar.getInstance().getTime().getTime());
						Timestamp DONE_DATE = new Timestamp(Calendar.getInstance().getTime().getTime());
						int PROCESS_RESULT = Integer.parseInt(mTable.GetValueAt(i, "PROCESS_RESULT").toString());
						int MESSAGE_TYPE = Integer.parseInt(mTable.GetValueAt(i, "MESSAGE_TYPE").toString());
						String REQUEST_ID = mTable.GetValueAt(i, "REQUEST_ID").toString();
						String MESSAGE_ID = mTable.GetValueAt(i, "MESSAGE_ID").toString();
						int TOTAL_SEGMENTS = Integer.parseInt(mTable.GetValueAt(i, "TOTAL_SEGMENTS").toString());
						int RETRIES_NUM = Integer.parseInt(mTable.GetValueAt(i, "RETRIES_NUM").toString());
						Timestamp INSERT_DATE = (Timestamp) mTable.GetValueAt(i, "INSERT_DATE");
						int CPID = mTable.GetValueAt(i, "CPID") == null ? 0 : Integer.parseInt(mTable.GetValueAt(i,
								"CPID").toString());

						String VMS_SVID = mTable.GetValueAt(i, "VMS_SVID") == null ? "" : mTable.GetValueAt(i,
								"VMS_SVID").toString();

						mObject = new MTObject(ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
								INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID,
								TOTAL_SEGMENTS, RETRIES_NUM, INSERT_DATE, CPID, VMS_SVID);

						mObject.CurrentStatus = Status.InMTQueue;
					}
					catch (Exception ex)
					{
						mLog.log.error("Error get info from db", ex);
					}

					try
					{
						if (!ListID_Delete.equals("")) ListID_Delete += ",";

						ListID_Delete += "" + ID;

						mQueue.add(mObject);

						mLog.log.info("LoadMT AddToQueue -->" + mObject.GetLog());
					}
					catch (Exception ex)
					{
						mLog.log.error("Error While", ex);
					}
				}

				// xóa cac dòng dã xu lý
				if (!ListID_Delete.equals(""))
				{
					mSendQueue.Delete(ListID_Delete);
					ListID_Delete = "";
				}
			}
			catch (SQLException ex)
			{
				mLog.log.error("Error run 1", ex);
			}
			catch (Exception ex)
			{
				mLog.log.error("Error run 2", ex);
			}

			try
			{
				sleep(TIME_DELAY_LOAD);
			}
			catch (InterruptedException ex)
			{
				mLog.log.error("Error run 3", ex);
			}
		}
	}
}
