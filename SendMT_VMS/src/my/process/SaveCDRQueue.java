package my.process;

import java.util.Date;

import my.define.MTObject;
import my.define.MTQueue;
import my.define.MySetting;
import my.define.MTObject.Status;
import MyGateway.cdr_queue;
import MyUtility.MyConfig;
import MyUtility.MyFile;
import MyUtility.MyLogger;

public class SaveCDRQueue extends Thread
{
	MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), SendMT.class.toString());

	int ProcessNumber = 1;
	int ProcessIndex = 1;
	
	MTQueue mQueue_CDR = null;

	cdr_queue mCDRQueue = null;
	public SaveCDRQueue(MTQueue mQueue_CDR, int ProcessNumber, int ProcessIndex) throws Exception
	{
		this.ProcessNumber = ProcessNumber;
		this.ProcessIndex = ProcessIndex;
		this.mQueue_CDR = mQueue_CDR;
		mCDRQueue = new cdr_queue(MySetting.GetProxoolConfigPath(), MySetting.PoolName_GateWay);
	}
	public void run()
	{
		MTObject mObject = null;
		while (MySetting.AllowRunning)
		{
			try
			{
				mObject = (MTObject) mQueue_CDR.remove();
				mObject.CurrentStatus = Status.SaveCDR;

				// USER_ID, SERVICE_ID,MOBILE_OPERATOR, COMMAND_CODE, INFO, "
				// "SUBMIT_DATE, DONE_DATE,
				// TOTAL_SEGMENTS,Message_Type,process_result,request_id,cpid
				if (mCDRQueue.Insert(mObject.USER_ID, mObject.SERVICE_ID, mObject.MOBILE_OPERATOR,
						mObject.COMMAND_CODE, mObject.INFO,
						MyConfig.Get_DateFormat_InsertDB().format(new Date(mObject.SUBMIT_DATE.getTime())), MyConfig
								.Get_DateFormat_InsertDB().format(new Date(mObject.DONE_DATE.getTime())),
						mObject.TOTAL_SEGMENTS.toString(), mObject.MESSAGE_TYPE.toString(), mObject.PROCESS_RESULT
								.toString(),
						mObject.REQUEST_ID))
				{
					// Nếu gửi thành công thì add vào queue ghi cdr
					mObject.CurrentStatus = Status.Complete;
				}
				else
				{
					// Nếu gửi không thành công thì ghi ra log
					MyFile.WriteToFile(MySetting.MTNotSaveToCDRQueue_Path_Save, mObject.GetLog());
				}
				
				mLog.log.info("SAVE TO CDR QUEUE -->" + mObject.GetShortLog());
			}
			catch (Exception ex)
			{
				mQueue_CDR.add(mObject);
				mLog.log.error("Loi Ghi vao CDR Queue, CDR nay se duoc add lai queue", ex);
			}
		}
	}
}
