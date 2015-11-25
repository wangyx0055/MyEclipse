package my.process;

import java.sql.Timestamp;
import java.util.Calendar;

import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSmsE;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE;

import cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeaderE;

import MyUtility.MyFile;
import MyUtility.MyLogger;

import my.define.MTObject;
import my.define.MTObject.Status;
import my.define.MTQueue;
import my.define.MySetting;
import my.send.sms.SendResultObject;
import my.send.sms.SendWappush;

/**
 * Thead tiến hành gửi MT sang VMS
 * 
 * @author Administrator
 * 
 */
public class SendMT extends Thread
{
	MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), SendMT.class.toString());

	int ProcessNumber = 1;
	int ProcessIndex = 1;

	
	MTQueue mQueue_MT = null;
	MTQueue mQueue_MTLog = null;

	public SendMT(MTQueue mQueue_MT, MTQueue mQueue_MTLog,int ProcessNumber, int ProcessIndex)
	{
		this.ProcessNumber = ProcessNumber;
		this.ProcessIndex = ProcessIndex;
		this.mQueue_MT = mQueue_MT;
		this.mQueue_MTLog = mQueue_MTLog;
	}

	public void run()
	{
		MTObject mObject = null;
		while (MySetting.AllowRunning)
		{
			try
			{
				mObject = (MTObject) mQueue_MT.remove();
				mObject.CurrentStatus = Status.SendMT;
				SendResultObject mResult = new SendResultObject();

				if (mObject.CONTENT_TYPE == 8) // bản tin là wappsuh
				{
					mResult = SendSMS(mObject);
				}
				else
				// gửi bản tin sms thường
				{
					mResult = SendSMS(mObject);
				}
				
				mObject.VMS_MT_ID=mResult.VMS_MT_ID;
				
				// Nếu gửi không thành công
				if (!mResult.Result())
				{
					mObject.RETRIES_NUM++;
					if (mObject.RETRIES_NUM > MySetting.MaxRetry_SendMT)
					{
						// nếu vượt quá số lần retry cho phép thì lưu MT vào
						// file
						MyFile.WriteToFile(MySetting.MTNotSend_Path_Save, mObject.GetLog());
					}
					else
					{
						mQueue_MT.add(mObject);
					}
					continue;
				}

				mObject.DONE_DATE = new Timestamp(Calendar.getInstance().getTime().getTime());

				mObject.CurrentStatus = Status.SaveMTLog;
				// Add lao queu_log để lưu vào table MT log trong DB
				mQueue_MTLog.add(mObject);

				// Ghi log
				mLog.log.info("SEND MT TO VMS -->" + mObject.GetShortLog());
			}
			catch (Exception ex)
			{
				mQueue_MT.add(mObject);
				mLog.log.error("Loi gui MT, MT nay se duoc add lai queue", ex);
			}
		}
	}

	private SendResultObject SendSMS(MTObject mObject)
	{
		SendResultObject mResult = new SendResultObject();
		RequestSOAPHeaderE mHeader = my.send.sms.SendSMS.createHeader(mObject.USER_ID,mObject.VMS_SVID);
		SendSmsE mBody = my.send.sms.SendSMS.createBody(mObject.USER_ID, mObject.SERVICE_ID, mObject.INFO);
		mResult = my.send.sms.SendSMS.sendSms(mHeader, mBody);
		return mResult;
	}

	private SendResultObject SendWappush(MTObject mObject)
	{
		SendResultObject mResult = new SendResultObject();
		String[] arr = mObject.INFO.split("http");

		if (arr.length == 2)
		{
			String Subject = arr[0];
			String LinkDown = "http" + arr[1];
			RequestSOAPHeaderE mHeader = my.send.sms.SendSMS.createHeader(mObject.USER_ID,mObject.VMS_SVID);
			SendPushMessageE mBody = SendWappush.createBody(mObject.USER_ID, mObject.SERVICE_ID, Subject, LinkDown);
			mResult = SendWappush.sendPush(mHeader, mBody);
		}
		else
		// gửi sms bình thường
		{
			mResult = SendSMS(mObject);
		}
		return mResult;
	}
}
