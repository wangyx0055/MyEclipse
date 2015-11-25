package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import MyGateway.sms_receive_forward;
import MyProcessServer.Common;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MyUtility.MyLogger;
import MyUtility.MyText;

public class TSTechProcess extends ContentAbstract
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

			String infor = java.net.URLEncoder.encode(msgObject.getUsertext(), "UTF-8");
			String CommandCode = java.net.URLEncoder.encode(msgObject.getKeyword(), "UTF-8");
			String RequestID = java.net.URLEncoder.encode(msgObject.getRequestid().toString(), "UTF-8");

			mForward.Insert_VMS(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getMobileoperator(), msgObject
					.getKeyword(), msgObject.getUsertext(), InsertDate, ReceiveDate, "0", msgObject.getRequestid()
					.toString(), "", Integer.toString(msgObject.getCpid()), "0", Integer.parseInt(Result), RetryCount,msgObject.getVMS_SVID());

			HttpClient client = new HttpClient();
			PostMethod httppost = new PostMethod("http://api.tstech.co/trieuphu/connect/sms_charging.php");

			httppost.setParameter("User_ID", msgObject.getUserid());
			httppost.setParameter("Service_ID", msgObject.getServiceid());
			httppost.setParameter("Command_Code", CommandCode);
			httppost.setParameter("Info", infor);
			httppost.setParameter("Request_ID", RequestID);
			httppost.setParameter("Receive_Date", Receive_Date);
			httppost.setParameter("Operator", msgObject.getMobileoperator());
			httppost.setParameter("UserName", "PartnerFRQWE");
			httppost.setParameter("Password", "977CE426A4FABE0AC6F0FC39044831F3");
			try
			{
				client.executeMethod(httppost);
				byte[] response = httppost.getResponseBody();

				Result = new String(response);
			}
			catch (Exception ex)
			{
				mLog.log.error(ex);
			}

			if (!Result.equalsIgnoreCase("0") && !Result.equalsIgnoreCase("1") && !Result.equalsIgnoreCase("-1"))
				Result = "-1";

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
				mForward.Update_RetryCount(msgObject.getRequestid().toString(), RetryCount, Result);
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