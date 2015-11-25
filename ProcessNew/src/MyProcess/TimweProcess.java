package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import MyProcessServer.*;
import MyGateway.sms_receive_forward;

import MyUtility.MyLogger;

public class TimweProcess extends ContentAbstract
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
		String URL = "";
		try
		{

			Receive_Date = new SimpleDateFormat("yyyyMMddHHmmss").format(msgObject.getTTimes());
			InsertDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			ReceiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msgObject.getTTimes());
			
			String infor = java.net.URLEncoder.encode(msgObject.getUsertext().replace(msgObject.getKeyword(), ""), "UTF-8");
			String CommandCode = java.net.URLEncoder.encode(msgObject.getKeyword(), "UTF-8");

			String Para = "User_ID=" + msgObject.getUserid() + "&Service_ID=" + msgObject.getServiceid() + "&Command_Code=" + CommandCode + "&Info=" + infor
					+ "&Request_ID=" + msgObject.getRequestid() + "&Receive_Date=" + Receive_Date + "&Operator=" + msgObject.getMobileoperator()
					+ "&UserName=PartnerFRQWE&Password=977CE426A4FABE0AC6F0FC39044831F3";

			URL = "http://xc.emea.timwe.com/mg/vn/hbcom/recmo?" + Para;

			try
			{
				Result = MyUtility.MyText.ReadFromURL(URL);
			}
			catch (Exception ex)
			{
				Result = MyUtility.MyText.ReadFromURL(URL);
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
