package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import MyProcessServer.*;
import MyGateway.sms_receive_forward;

import MyUtility.MyLogger;
import WS.Iplay.Sms;

public class IPlayProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(this.getClass().toString());
	Collection<MsgObject> MessOject = new ArrayList<MsgObject>();

	@Override
	protected Collection<?> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{
		sms_receive_forward mForward = new sms_receive_forward(LocalConfig.PoolName_Gateway);

		String Result = "-1";
		String Receive_Date = "";
		int RetryCount = 1;
		String InsertDate = "";
		String ReceiveDate = "";

		try
		{

			Receive_Date = new SimpleDateFormat("yyyyMMddHHmmss").format(msgObject.getTTimes());
			InsertDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			ReceiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msgObject.getTTimes());
			
			WS.Iplay.Sms mSMS = new Sms();
			WS.Iplay.SmsSoap mSoapSMS = mSMS.getSmsSoap();
			
			try
			{
				Result = mSoapSMS.moForward(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getKeyword(), msgObject.getUsertext(), msgObject
						.getRequestid().toString(), Receive_Date, msgObject.getMobileoperator(), "iplayviethorizon", "E5F3D005A8922E9AC132767920C5E8E0");
			}
			catch (Exception ex)
			{
				Result = mSoapSMS.moForward(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getKeyword(), msgObject.getUsertext(), msgObject
						.getRequestid().toString(), Receive_Date, msgObject.getMobileoperator(), "iplayviethorizon", "E5F3D005A8922E9AC132767920C5E8E0");
				RetryCount++;
				mLog.log.error(ex);
			}

		

			return null;
		}
		catch (Exception ex)
		{
			mLog.log.error(Common.GetStringLog(msgObject), ex);
			return null;
		}
		finally
		{
			try
			{
				mForward.Insert(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getMobileoperator(), msgObject.getKeyword(),
						msgObject.getUsertext(), InsertDate, ReceiveDate, "0", msgObject.getRequestid().toString(), "", Integer.toString(msgObject.getCpid()),
						"0", Integer.parseInt(Result), RetryCount);
			}
			catch (Exception ex)
			{
				msgObject.setUsertext(LocalConfig.MT_SYSTEM_ERROR);
				msgObject.setContenttype(21);
				msgObject.setMsgtype(1);

				MessOject.add(new MsgObject(msgObject));

				mLog.log.error(Common.GetStringLog(msgObject), ex);

				return MessOject;
			}

			mLog.log.info(Common.GetStringLog(msgObject));
		}

	}
}
