package MyProcessGame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import MyDataSource.MyTableModel;
import MyProcessServer.Common;
import MyProcessServer.ConsoleSRV;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MySportVote.Subscriber;
import MySportVote.DefineMT.MTType;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyLogger;
import MyUtility.MyConfig.Telco;

public class CheckTopProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(this.getClass().toString());
	Collection<MsgObject> ListMessOject = new ArrayList<MsgObject>();
	String MSISDN = "";
	MyConfig.Telco mTelco = Telco.NOTHING;
	String MTContent = "";
	String Info = "";
	String Keyword = "";
	int PID = 1;
	MsgObject mObject = null;
	String CurrentDate = "";

	Subscriber mSub = null;

	private void AddToList() throws Exception
	{
		try
		{
			mObject.setUsertext(MTContent);
			mObject.setContenttype(21);
			mObject.setMsgtype(1);

			ListMessOject.add(new MsgObject(mObject));
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{
		try
		{
			mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);

			CurrentDate = MyConfig.Get_DateFormat_InsertDB().format(Calendar.getInstance().getTime());

			mObject = msgObject;
			MSISDN = msgObject.getUserid();
			Info = msgObject.getUsertext();
			Keyword = msgObject.getKeyword();

			MSISDN = MyCheck.ValidPhoneNumber(MSISDN, "84");

			// lấy PID cho số điện thoại
			PID = Common.GetPIDByMSISDN(MSISDN);

			mTelco = MyCheck.GetTelco(MSISDN);

			// Kiem tra session có tồn tại hay không
			if (!GameCommon.CheckSession(ConsoleSRV.ListSessionObject, mObject.getTTimes()))
			{
				MTContent = Common.GetDefineMT_Message(MTType.Session_Expire);
				AddToList();
				return ListMessOject;
			}

			MyTableModel mTable_Sub = mSub.Select(3, MSISDN);

			if (mTable_Sub.GetRowCount() < 1)
			{
				MTContent = Common.GetDefineMT_Message(MTType.CheckTop_NotReg);
				AddToList();
				return ListMessOject;
			}

			MTContent = Common.GetDefineMT_Message(MTType.CheckTop);

			if (MTContent.contains("{Order}"))
			{
				MTContent = MTContent.replace("{Order}", mTable_Sub.GetValueAt(0, "RowIndex").toString());
			}

			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);

			ListMessOject.add(new MsgObject(msgObject));

			return ListMessOject;
		}
		catch (Exception ex)
		{
			mLog.log.error(Common.GetStringLog(mObject), ex);
			return Common.GetMTSystemError(mObject);
		}
		finally
		{
			mLog.log.debug(Common.GetStringLog(mObject));
		}
	}
}
