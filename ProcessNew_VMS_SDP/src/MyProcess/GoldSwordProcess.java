package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import MyCallWebservice.GoldSword;
import MyGateway.sms_receive_forward;
import MyProcessServer.Common;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MyUtility.MyLogger;

public class GoldSwordProcess extends ContentAbstract
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
			InsertDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			ReceiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msgObject.getTTimes());
			Receive_Date = new SimpleDateFormat("yyyyMMddHHmmss").format(msgObject.getTTimes());

			try
			{
				while (Result == "-1" && RetryCount++ < 3)
				{
					Result = GoldSword.CallWebservice(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getKeyword(), msgObject.getUsertext(),
							msgObject.getRequestid().toString(), Receive_Date, msgObject.getMobileoperator(), "PartnerFRQWE",
							"977CE426A4FABE0AC6F0FC39044831F3");
				}
			}
			catch (Exception ex)
			{
				Result = GoldSword.CallWebservice(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getKeyword(), msgObject.getUsertext(),
						msgObject.getRequestid().toString(), Receive_Date, msgObject.getMobileoperator(), "PartnerFRQWE", "977CE426A4FABE0AC6F0FC39044831F3");
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
				mForward.Insert_VMS(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getMobileoperator(), msgObject.getKeyword(),
						msgObject.getUsertext(), InsertDate, ReceiveDate, "0", msgObject.getRequestid().toString(), "", Integer.toString(msgObject.getCpid()),
						"0", Integer.parseInt(Result), RetryCount,msgObject.getVMS_SVID());
			}
			catch(Exception ex)
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
