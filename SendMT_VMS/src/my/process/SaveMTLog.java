package my.process;

import my.define.MTObject;
import my.define.MTQueue;
import my.define.MySetting;
import my.define.MTObject.Status;
import MyGateway.ems_send_log;
import MyUtility.MyFile;
import MyUtility.MyLogger;

/**
 * Lấy MT và lưu xuống MT Log
 * 
 * @author Administrator
 * 
 */
public class SaveMTLog extends Thread
{
	MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), SaveMTLog.class.toString());

	int ProcessNumber = 1;
	int ProcessIndex = 1;
	
	MTQueue mQueue_CDR = null;
	MTQueue mQueue_MTLog = null;

	ems_send_log mSendLog = null;
	public SaveMTLog(MTQueue mQueue_MTLog, MTQueue mQueue_CDR, int ProcessNumber, int ProcessIndex) throws Exception
	{
		this.ProcessNumber = ProcessNumber;
		this.ProcessIndex = ProcessIndex;
		this.mQueue_CDR = mQueue_CDR;
		this.mQueue_MTLog = mQueue_MTLog;
		mSendLog = new ems_send_log(MySetting.GetProxoolConfigPath(), MySetting.PoolName_GateWay);
	}

	public void run()
	{
		MTObject mObject = null;
		while (MySetting.AllowRunning)
		{
			try
			{
				mObject = (MTObject) mQueue_MTLog.remove();
				mObject.CurrentStatus = Status.SaveMTLog;

				if (mSendLog.Insert(mObject.USER_ID, mObject.SERVICE_ID, mObject.MOBILE_OPERATOR, mObject.COMMAND_CODE,
						mObject.CONTENT_TYPE.toString(), mObject.INFO, mObject.SUBMIT_DATE, mObject.DONE_DATE,
						mObject.PROCESS_RESULT.toString(), mObject.MESSAGE_TYPE.toString(), mObject.REQUEST_ID, mObject.MESSAGE_ID,
						mObject.TOTAL_SEGMENTS.toString(), mObject.RETRIES_NUM.toString(), "", mObject.CPID.toString(), mObject.VMS_MT_ID,
						mObject.SEND_RESULT.toString(),mObject.VMS_SVID))
				{
					//Nếu gửi thành công thì add vào queue ghi cdr
					mObject.CurrentStatus = Status.SaveCDR;
				}
				else
				{
					//Nếu gửi không thành công thì ghi ra log
					MyFile.WriteToFile(MySetting.MTNotSaveToLog_Path_Save, mObject.GetLog());
				}
				
				mQueue_CDR.add(mObject);
				mLog.log.info("SAVE TO MT LOG -->"+mObject.GetShortLog());
			}
			catch (Exception ex)
			{
				mQueue_MTLog.add(mObject);
				mLog.log.error("Loi gui MT, MT nay se duoc add lai queue", ex);
			}
		}
	}
}
