package MyProcessGame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyProcessServer.Common;
import MyProcessServer.ConsoleSRV;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MySportVote.AnswerLog;
import MySportVote.Question;
import MySportVote.SportmanObject;
import MySportVote.Subscriber;
import MySportVote.DefineMT.MTType;
import MySportVote.SubscriberObject;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyUtility.MyText;
import MyUtility.MyConfig.Telco;

public class AnswerProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(this.getClass().toString());
	Collection<MsgObject> ListMessOject = new ArrayList<MsgObject>();
	String MSISDN = "";
	MyConfig.Telco mTelco = Telco.NOTHING;
	String MTContent = "";
	String Info = "";
	String Keyword = "";

	int Answer = 0;
	int PID = 1;
	MsgObject mObject = null;
	SportmanObject mSportmanObj = null;
	String CurrentDate = "";

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

	private void AddToList(int MessateType) throws Exception
	{
		try
		{
			mObject.setUsertext(MTContent);
			mObject.setContenttype(21);
			mObject.setMsgtype(MessateType);

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
			CurrentDate = MyConfig.Get_DateFormat_InsertDB().format(Calendar.getInstance().getTime());

			mObject = msgObject;
			MSISDN = msgObject.getUserid();
			Info = msgObject.getUsertext();
			Keyword = msgObject.getKeyword();

			MSISDN = MyCheck.ValidPhoneNumber(MSISDN, "84");

			// lấy PID cho số điện thoại
			PID = Common.GetPIDByMSISDN(MSISDN);

			mTelco = MyCheck.GetTelco(MSISDN);
			String Temp_Answer = MyText.RemoveSpecialLetter(1, Info);

			if (!Temp_Answer.equalsIgnoreCase("1") && !Temp_Answer.equalsIgnoreCase("2"))
			{
				MTContent = Common.GetDefineMT_Message(MTType.Answer_Invalid);
				AddToList();
				return ListMessOject;
			}

			Answer = Integer.parseInt(Temp_Answer);

			// Kiem tra session có tồn tại hay không
			if (!GameCommon.CheckSession(ConsoleSRV.ListSessionObject, mObject.getTTimes()))
			{
				MTContent = Common.GetDefineMT_Message(MTType.Session_Expire);
				AddToList();
				return ListMessOject;
			}

			Subscriber mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable_Sub = mSub.Select(2, Integer.toString(PID), MSISDN);

			if (mTable_Sub.GetRowCount() < 1)
			{
				MTContent = Common.GetDefineMT_Message(MTType.Answer_NotSub);
				AddToList();
				return ListMessOject;
			}

			SubscriberObject mSubObject = Subscriber.Convert(mTable_Sub);

			Date QuestionDate = Calendar.getInstance().getTime();
			// Kiểm tra xem khách hàng đã trả lời quá 10 câu hỏi trong ngày
			// không
			if (mSubObject.QuestionByDayDate != null && mSubObject.QuestionByDayDate.getYear() == QuestionDate.getYear()
					&& mSubObject.QuestionByDayDate.getMonth() == QuestionDate.getMonth() && mSubObject.QuestionByDayDate.getDay() == QuestionDate.getDay()
					&& mSubObject.QuestionCountByDay >= 10)
			{
				MTContent = Common.GetDefineMT_Message(MTType.Answer_Exceed);
				AddToList(2);
				return ListMessOject;
			}
			else
			{
				if (mSubObject.QuestionCountByDay == 0 || mSubObject.QuestionCountByDay < 10)
					mSubObject.QuestionCountByDay++;
				else
					mSubObject.QuestionCountByDay = 1;
				mSubObject.QuestionByDayDate = QuestionDate;
			}

			boolean RightAnswer = false;

			Question mQuestion = new Question(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable_Question = mQuestion.Select(1, Integer.toString(mSubObject.LastQuestionID));

			if (mTable_Question.GetRowCount() < 1)
			{
				// Chua co cau hoi nao gui xuong cho KH nay
				MTContent = Common.GetDefineMT_Message(MTType.Answer_NoQuestion);
				AddToList();
				return ListMessOject;
			}

			if (Integer.parseInt(mTable_Question.GetValueAt(0, "Answer").toString()) == Answer)
			{
				// Tra loi dung
				MTContent = Common.GetDefineMT_Message(MTType.Answer_Right);
				mSubObject.LastMark = 100;

				mSubObject.WrongAnswerAgain = 0;
				mSubObject.RightAnswerAgain++;
				RightAnswer = true;
			}
			else
			{
				// tra loi sai
				MTContent = Common.GetDefineMT_Message(MTType.Answer_Wrong);
				mSubObject.LastMark = 50;
				mSubObject.RightAnswerAgain = 0;
				mSubObject.WrongAnswerAgain++;
				RightAnswer = false;
			}

			// Cập nhật lại Câu hỏi cho câu trả lời này
			mSubObject.PreQuestionID = mSubObject.LastQuestionID;
			mSubObject.LastAnswer = Answer;
			mSubObject.TotalMark += mSubObject.LastMark;
			mSubObject.QuestionCount++;

			if (MTContent.contains("{TotalMark}"))
			{
				MTContent = MTContent.replace("{TotalMark}", Integer.toString(mSubObject.TotalMark));
			}

			// Lấy câu hỏi tiếp theo
			mTable_Question = mQuestion.Select(3, mSubObject.ListQuestionID);

			if (mTable_Question.GetRowCount() < 1)
			{
				// Trường hợp trả lời hết tất cả các câu hỏi
				mTable_Question = mQuestion.Select(3, "0");
			}

			String MTQuestion = "";

			if (mTable_Question.GetRowCount() > 0)
			{
				MTQuestion = "Cau hoi " + mSubObject.QuestionCount + ": " + mTable_Question.GetValueAt(0, "Content").toString();
				mSubObject.LastQuestionID = Integer.parseInt(mTable_Question.GetValueAt(0, "QuestionID").toString());

				if (mSubObject.ListQuestionID != "")
					mSubObject.ListQuestionID += "," + Integer.toString(mSubObject.LastQuestionID);
				else
					mSubObject.ListQuestionID += "" + mSubObject.LastQuestionID;
			}

			MTContent += MTQuestion;

			if (!Update_Sub(mSubObject))
			{
				mLog.log.info("UPdate vao table Subscriber KHONG THANH CONG: XML Insert-->" + mTable_Sub.GetXML());
				MTContent = Common.GetDefineMT_Message(MTType.Overflow);
				AddToList();
				return ListMessOject;
			}

			// Insert AnswerLog
			Insert_AnswerLog(mSubObject.PreQuestionID, mSubObject.LastAnswer, MyConfig.Get_DateFormat_InsertDB().format(mSubObject.LastQuestionDate), CurrentDate,
					RightAnswer, mSubObject.TotalMark);

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

	private boolean Update_Sub(SubscriberObject mSubObject) throws Exception
	{
		try
		{
			Subscriber mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);

			MyTableModel mTable_Sub_New = ConsoleSRV.mTableTemplate.mSubscriber;
			mTable_Sub_New.Clear();
			// Tạo row để insert vào Table Sub
			MyDataRow mRow_Sub = mTable_Sub_New.CreateNewRow();

			mRow_Sub.SetValueCell("MSISDN", MSISDN);
			mRow_Sub.SetValueCell("TelcoID", mTelco.getValue());
			mRow_Sub.SetValueCell("LastQuestionID", mSubObject.LastQuestionID);
			mRow_Sub.SetValueCell("LastQuestionDate", CurrentDate);
			mRow_Sub.SetValueCell("PreQuestionID", mSubObject.PreQuestionID);
			mRow_Sub.SetValueCell("LastAnswerDate", CurrentDate);
			mRow_Sub.SetValueCell("LastAnswer", mSubObject.LastAnswer);
			mRow_Sub.SetValueCell("RightAnswerAgain", mSubObject.RightAnswerAgain);
			mRow_Sub.SetValueCell("WrongAnswerAgain", mSubObject.WrongAnswerAgain);
			mRow_Sub.SetValueCell("LastMark", mSubObject.LastMark);
			mRow_Sub.SetValueCell("TotalMark", mSubObject.TotalMark);
			mRow_Sub.SetValueCell("PID", PID);
			mRow_Sub.SetValueCell("StatusID", Subscriber.Status.Playing.GetValue());
			mRow_Sub.SetValueCell("RemindCount", 0);
			mRow_Sub.SetValueCell("ListQuestionID", mSubObject.ListQuestionID);
			mRow_Sub.SetValueCell("QuestionCount", mSubObject.QuestionCount);
			mRow_Sub.SetValueCell("QuestionCountByDay", mSubObject.QuestionCountByDay);
			mRow_Sub.SetValueCell("QuestionByDayDate", CurrentDate);
			mTable_Sub_New.AddNewRow(mRow_Sub);

			return mSub.Update(2, mTable_Sub_New.GetXML());
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private boolean Insert_AnswerLog(int QuestionID, int Answer, String QuestionDate, String AnswerDate, boolean RightAnswer, int CurrentMark) throws Exception
	{
		try
		{

			int LogPID = MyConvert.GetPIDByDate(Calendar.getInstance().getTime());

			AnswerLog mAnswerLog = new AnswerLog(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable = ConsoleSRV.mTableTemplate.mAnswerLog;
			mTable.Clear();

			MyDataRow mRow = mTable.CreateNewRow();
			mRow.SetValueCell("SessionID", Common.GetCurrentSession().SessionID);
			mRow.SetValueCell("MSISDN", MSISDN);
			mRow.SetValueCell("TelcoID", mTelco.getValue());
			mRow.SetValueCell("QuestionID", QuestionID);
			mRow.SetValueCell("Answer", Answer);
			mRow.SetValueCell("QuestionDate", QuestionDate);
			mRow.SetValueCell("AnswerDate", AnswerDate);
			mRow.SetValueCell("RightAnswer", RightAnswer);
			mRow.SetValueCell("LogPID", LogPID);
			mRow.SetValueCell("CurrentMark", CurrentMark);
			mRow.SetValueCell("PromoMarkt", 0);

			mTable.AddNewRow(mRow);

			boolean Result = mAnswerLog.Insert(0, mTable.GetXML());
			if (!Result)
			{
				mLog.log.info("Insert AnswerLog KHONG THANH CONG: XML Insert-->" + mTable.GetXML());
			}
			return Result;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
