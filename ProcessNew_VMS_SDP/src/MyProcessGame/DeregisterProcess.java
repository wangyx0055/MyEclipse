package MyProcessGame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.activation.MailcapCommandMap;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyProcessServer.Common;
import MyProcessServer.ConsoleSRV;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MySportVote.ActionLog;
import MySportVote.Question;
import MySportVote.Subscriber;
import MySportVote.SubscriberObject;
import MySportVote.DefineMT.MTType;
import MySportVote.UnSubscriber;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyUtility.MyText;
import MyUtility.MyConfig.Telco;

public class DeregisterProcess extends ContentAbstract
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
	SubscriberObject mSubObj = new SubscriberObject();
	Question mQuestion = null;

	String CurrentDate = "";
	Subscriber mSub = null;
	UnSubscriber mUnSub = null;

	private void AddToList() throws Exception
	{
		try
		{
			mObject.setUsertext(MTContent);
			mObject.setContenttype(21);
			mObject.setMsgtype(2);

			ListMessOject.add(new MsgObject(mObject));
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private void Insert_ActionLog() throws Exception
	{
		try
		{
			ActionLog mActionLog = new ActionLog(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable_ActionLog = ConsoleSRV.mTableTemplate.mActionLog;
			mTable_ActionLog.Clear();
			MyDataRow mRow_ActionLog = mTable_ActionLog.CreateNewRow();

			mRow_ActionLog.SetValueCell("MSISDN", MSISDN);
			mRow_ActionLog.SetValueCell("TelcoID", mTelco.getValue());
			mRow_ActionLog.SetValueCell("LogDate", CurrentDate);
			mRow_ActionLog.SetValueCell("ActionTypeID", ActionLog.ActionLogType.DeRegister.GetValue());
			mRow_ActionLog.SetValueCell("ActionTypeName", ActionLog.ActionLogType.DeRegister.toString());
			mRow_ActionLog.SetValueCell("MO", mObject.getMO());
			mRow_ActionLog.SetValueCell("MT", mObject.getUsertext());
			mRow_ActionLog.SetValueCell("LogContent", "Huy Dang ky dich vu Vinh Danh The thao");
			mRow_ActionLog.SetValueCell("LogPID", MyConvert.GetPIDByDate(Calendar.getInstance().getTime()));
			mRow_ActionLog.SetValueCell("RequestID", mObject.getRequestid().toString());

			mTable_ActionLog.AddNewRow(mRow_ActionLog);

			mActionLog.Insert(0, mTable_ActionLog.GetXML());

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
			throw ex;
		}
	}

	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{
		try
		{
			mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);
			mUnSub = new UnSubscriber(LocalConfig.PoolName_Data_SQL);
			mQuestion = new Question(LocalConfig.PoolName_Data_SQL);

			CurrentDate = MyConfig.Get_DateFormat_InsertDB().format(Calendar.getInstance().getTime());

			mObject = msgObject;
			MSISDN = msgObject.getUserid();
			Info = msgObject.getUsertext();
			Keyword = msgObject.getKeyword();

			MSISDN = MyCheck.ValidPhoneNumber(MSISDN, "84");

			// lấy PID cho số điện thoại
			PID = Common.GetPIDByMSISDN(MSISDN);

			mTelco = MyCheck.GetTelco(MSISDN);

			// MT thanh cong
			MTContent = Common.GetDefineMT_Message(MTType.Register_Success);

			// Kiem tra session có tồn tại hay không
			if (!GameCommon.CheckSession(ConsoleSRV.ListSessionObject, mObject.getTTimes()))
			{
				MTContent = Common.GetDefineMT_Message(MTType.Session_Expire);
				AddToList();
				return ListMessOject;
			}

			// Kiem tra xem khach hang da vote truoc do hay chua

			MyTableModel mTable_Sub = mSub.Select(2, Integer.toString(PID), MSISDN);

			if (mTable_Sub.GetRowCount() == 0)
			{
				MTContent = Common.GetDefineMT_Message(MTType.DeRegister_NotRegister);
				AddToList();
				return ListMessOject;
			}

			mUnSub.Move(0, MSISDN);
			MTContent = Common.GetDefineMT_Message(MTType.DeRegister_Success);

			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(2);

			// Insert vao log
			Insert_ActionLog();

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
