package MyProcessGame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import MySportVote.Question;
import MySportVote.Sportman;
import MySportVote.SportmanObject;
import MySportVote.Subscriber;
import MySportVote.SubscriberObject;
import MySportVote.VoteLog;
import MySportVote.DefineMT.MTType;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyText;
import MyUtility.MyConfig.Telco;
import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyProcessServer.Common;
import MyProcessServer.ConsoleSRV;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;

public class VoteProcess extends ContentAbstract
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
	SportmanObject mSportmanObj = null;
	SubscriberObject mSubObj = new SubscriberObject();
	Question mQuestion = null;

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

	private void Insert_VoteLog() throws Exception
	{
		try
		{

			VoteLog mVoteLog = new VoteLog(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable_VoteLog = ConsoleSRV.mTableTemplate.mVoteLog;
			mTable_VoteLog.Clear();
			MyDataRow mRow_VoteLog = mTable_VoteLog.CreateNewRow();

			mRow_VoteLog.SetValueCell("MSISDN", MSISDN);
			mRow_VoteLog.SetValueCell("TelcoID", mTelco.getValue());
			mRow_VoteLog.SetValueCell("SportmanID", mSportmanObj.SportmanID);
			mRow_VoteLog.SetValueCell("VoteDate", CurrentDate);
			mRow_VoteLog.SetValueCell("VoteCount", mSportmanObj.VoteCount);
			mRow_VoteLog.SetValueCell("LogPID", MyConvert.GetPIDByDate(Calendar.getInstance().getTime()));

			mTable_VoteLog.AddNewRow(mRow_VoteLog);

			mVoteLog.Insert(0, mTable_VoteLog.GetXML());

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
			mQuestion = new Question(LocalConfig.PoolName_Data_SQL);

			CurrentDate = MyConfig.Get_DateFormat_InsertDB().format(Calendar.getInstance().getTime());

			mObject = msgObject;
			MSISDN = msgObject.getUserid();
			Info = msgObject.getUsertext();
			Keyword = msgObject.getKeyword();

			// Mã vận động viên được vote
			String SportmanCode = "";
			MSISDN = MyCheck.ValidPhoneNumber(MSISDN, "84");

			// lấy PID cho số điện thoại
			PID = Common.GetPIDByMSISDN(MSISDN);

			mTelco = MyCheck.GetTelco(MSISDN);

			SportmanCode = Info.substring(Keyword.length(), Info.length()).trim();

			SportmanCode = SportmanCode.trim().toUpperCase();

			mSportmanObj = Sportman.GetSportmanByCode(ConsoleSRV.ListSportmanObject, SportmanCode);

			// Thông tin vận động viên không hợp lệ
			if (mSportmanObj == null)
			{
				MTContent = Common.GetDefineMT_Message(MTType.Vote_Unsuccess);
				AddToList();
				return ListMessOject;
			}
			mSportmanObj.VoteCount += 1;

			Sportman mSportman = new Sportman(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable_Sportman = ConsoleSRV.mTableTemplate.mSportman;
			mTable_Sportman.Clear();
			MyDataRow mRow_Sportman = mTable_Sportman.CreateNewRow();
			mRow_Sportman.SetValueCell("SportmanID", mSportmanObj.SportmanID);

			mRow_Sportman.SetValueCell("LastVodeDate", CurrentDate);

			mTable_Sportman.AddNewRow(mRow_Sportman);

			if (!mSportman.Update(1, mTable_Sportman.GetXML()))
			{
				MTContent = Common.GetDefineMT_Message(MTType.Overflow);
				AddToList();
				return ListMessOject;
			}

			// MT thanh cong
			MTContent = Common.GetDefineMT_Message(MTType.Vote_Success);

			// Kiem tra xem khach hang da vote truoc do hay chua

			MyTableModel mTable_Sub = mSub.Select(2, Integer.toString(PID), MSISDN);

			if (mTable_Sub.GetRowCount() > 0)
			{
				MTContent = Common.GetDefineMT_Message(MTType.Vote_SuccessAgain);
			}

			mSubObj = Subscriber.Convert(mTable_Sub);

			if (MTContent.contains("{SportmanName}"))
			{
				// Lay ten cua VDV và MTContent
				MTContent = MTContent.replace("{SportmanName}", MyText.RemoveSignVietnameseString(mSportmanObj.Name));
			}

			// Insert vao log
			Insert_VoteLog();

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
					// lấy tổng số điệm nếu đã chơi truoc71 đó
					MTContent = MTContent.replace("{TotalMark}", MyText.RemoveSignVietnameseString(Integer.toString(mSubObj.TotalMark)));
				}
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
