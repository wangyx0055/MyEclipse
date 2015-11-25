package MyProcessGame;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

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
import MySportVote.Sportman;
import MySportVote.SportmanObject;
import MySportVote.Subscriber;
import MySportVote.SubscriberObject;
import MySportVote.UnSubscriber;
import MySportVote.VoteLog;
import MySportVote.DefineMT.MTType;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyUtility.MyText;
import MyUtility.MyConfig.Telco;

public class RegisterProcess extends ContentAbstract
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
			mObject.setMsgtype(1);

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
			mRow_ActionLog.SetValueCell("ActionTypeID", ActionLog.ActionLogType.Register.GetValue());
			mRow_ActionLog.SetValueCell("ActionTypeName", ActionLog.ActionLogType.Register.toString());
			mRow_ActionLog.SetValueCell("MO", mObject.getMO());
			mRow_ActionLog.SetValueCell("MT", mObject.getUsertext());
			mRow_ActionLog.SetValueCell("LogContent", "Dang ky dich vu Vinh Danh The thao");
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

	private String Update_Sub() throws Exception
	{
		try
		{
			String QuestionContent = "";
			int QuestionID = 0;
			if (mSubObj.IsNull())
			{
				// Neu chua dang ky thi ko can xu ly
				return QuestionContent;
			}
			QuestionID = mSubObj.LastQuestionID;

			MyTableModel mTable_Question = mQuestion.Select(1, Integer.toString(QuestionID));

			if (mTable_Question.GetRowCount() > 0)
			{
				QuestionContent = " Cau hoi " + mSubObj.QuestionCount + ": " + mTable_Question.GetValueAt(0, "Content").toString();
			}
			// Lấy câu hỏi chưa trả lời, trả về cho khách hàng
			return QuestionContent;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private String Insert_Sub() throws Exception
	{
		try
		{
			String QuestionContent = "";
			int QuestionID = 0;

			// Nếu như khách hàng chưa có trong table Subscriber
			if (!mSubObj.IsNull())
			{
				// Nếu đã đăng ký rồi thì không trả về câu hỏi nữa
				return QuestionContent;
			}

			// Nếu chưa đăng ký thì Insert vào table Sub, và lấy câu hỏi trả về
			// cho khách hàng

			MyTableModel mTable_Question = mQuestion.Select(3, "0");

			if (mTable_Question.GetRowCount() > 0)
			{
				QuestionContent = " Cau hoi 1: " + mTable_Question.GetValueAt(0, "Content").toString();
				QuestionID = Integer.parseInt(mTable_Question.GetValueAt(0, "QuestionID").toString());
			}

			MyTableModel mTable_Sub = ConsoleSRV.mTableTemplate.mSubscriber;
			mTable_Sub.Clear();

			// Tạo row để insert vào Table Sub
			MyDataRow mRow_Sub = mTable_Sub.CreateNewRow();

			// Kiểm tra xem khách hàng đã hủy đăng ký trước đó hay chưa.
			MyTableModel mTable_UnSub = mUnSub.Select(2, Integer.toString(PID), MSISDN);

			mRow_Sub.SetValueCell("MSISDN", MSISDN);
			mRow_Sub.SetValueCell("TelcoID", mTelco.getValue());
			mRow_Sub.SetValueCell("RegisterDate", CurrentDate);
			mRow_Sub.SetValueCell("FirstQuestionDate", CurrentDate);
			mRow_Sub.SetValueCell("LastQuestionID", QuestionID);
			mRow_Sub.SetValueCell("LastQuestionDate", CurrentDate);
			mRow_Sub.SetValueCell("PreQuestionID", 0);
			mRow_Sub.SetValueCell("LastAnswerDate", "");
			mRow_Sub.SetValueCell("LastAnswer", "");
			mRow_Sub.SetValueCell("RightAnswerAgain", 0);
			mRow_Sub.SetValueCell("WrongAnswerAgain", 0);
			mRow_Sub.SetValueCell("LastMark", 0);
			mRow_Sub.SetValueCell("TotalMark", 1000);
			mRow_Sub.SetValueCell("PID", PID);
			mRow_Sub.SetValueCell("StatusID", Subscriber.Status.NewRegister.GetValue());
			mRow_Sub.SetValueCell("RemindDate", "");
			mRow_Sub.SetValueCell("RemindCount", 0);
			mRow_Sub.SetValueCell("ListQuestionID", QuestionID + "");
			mRow_Sub.SetValueCell("QuestionCount", "1");

			if (mTable_UnSub.GetRowCount() > 0)
			{
				java.util.Date LastDate = null;

				if (mTable_UnSub.GetValueAt(0, "LastAnswerDate") != null)
					LastDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_UnSub.GetValueAt(0, "LastAnswerDate").toString());
				else
					LastDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_UnSub.GetValueAt(0, "LastQuestionDate").toString());

				if (GameCommon.CheckSession(ConsoleSRV.ListSessionObject, new Timestamp(LastDate.getTime())))
				{
					mRow_Sub.SetValueCell("TotalMark", mTable_UnSub.GetValueAt(0, "TotalMark").toString());
				}
			}

			mTable_Sub.AddNewRow(mRow_Sub);

			if (!mSub.Insert(0, mTable_Sub.GetXML()))
			{
				mLog.log.info("Insert vao table Subscriber KHONG THANH CONG: XML Insert-->" + mTable_Sub.GetXML());
				return "";
			}

			return QuestionContent;
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

			if (mTable_Sub.GetRowCount() > 0)
			{
				MTContent = Common.GetDefineMT_Message(MTType.Register_SuccessAgain);
			}

			mSubObj = Subscriber.Convert(mTable_Sub);

			// Kiểm tra khach hàng đã đăng ký dịch vụ chưa
			if (mSubObj.IsNull())
			{
				MTContent += Insert_Sub();
			}
			else
			{
				MTContent += Update_Sub();
				if (MTContent.contains("{TotalMark}"))
				{
					// lấy tổng số Điểm nếu đã chơi trước đó
					MTContent = MTContent.replace("{TotalMark}", MyText.RemoveSignVietnameseString(Integer.toString(mSubObj.TotalMark)));
				}
			}

			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);

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
