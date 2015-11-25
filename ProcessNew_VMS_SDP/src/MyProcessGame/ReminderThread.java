package MyProcessGame;

import java.math.BigDecimal;
import java.sql.Timestamp;

import MyDataSource.MyTableModel;
import MyProcessServer.Common;
import MyProcessServer.ConsoleSRV;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MySportVote.Subscriber;
import MySportVote.DefineMT.MTType;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyLogger;

/**
 * Thread nhac nho khach hang neu trong 1 khoang thoi gian nhat dinh khong tra
 * loi cau hoi
 * 
 * @author Administrator
 * 
 */
public class ReminderThread extends Thread
{
	MyLogger mLog = new MyLogger(ReminderThread.class.toString());

	/**
	 * Thời gian mỗi lần quét (tính bằng phút)
	 */
	public int DelayTime = 15;

	/**
	 * KHoảng thời gian sẽ được push MT nhắc nhở sau khi Push câu hỏi
	 */
	public int MaxTime = 30;
	public String ServiceID = "6583";
	public String Keyword = "REMIND";
	Subscriber mSub = null;

	String MTContent_1 = "";
	String MTContent_2 = "";
	String MTContent_3 = "";

	public ReminderThread()
	{
		try
		{
			mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);
			MTContent_1 = Common.GetDefineMT_Message(MTType.Remind_Answer_Fist);
			MTContent_2 = Common.GetDefineMT_Message(MTType.Remind_Answer_Second);
			MTContent_3 = Common.GetDefineMT_Message(MTType.Remind_Answer_Third);

		}
		catch (Exception ex)
		{
			mLog.log.error("Contructor ReminderThread Error:", ex);
		}
	}

	public void run()
	{
		while (ConsoleSRV.getData)
		{
			try
			{
				for (int PID = 1; PID < 20; PID++)
				{
					int BeginRow = 1, EndRow = 10;
					boolean HasRow = true;
					while (HasRow)
					{
						MyTableModel mTable = mSub.Select(4, Integer.toString(PID), Integer.toString(MaxTime),
								Integer.toString(LocalConfig.CURRENT_TELCO.getValue()), Integer.toString(BeginRow),
								Integer.toString(EndRow));
						BeginRow += 10;
						EndRow += 10;

						if (mTable.GetRowCount() < 1)
						{
							HasRow = false;
							continue;
						}

						for (int i = 0; i < mTable.GetRowCount(); i++)
						{
							String UserID = mTable.GetValueAt(i, "MSISDN").toString();
							MyConfig.Telco mTelco = MyCheck.GetTelco(UserID);

							String Info = MTContent_1;
							if (Integer.parseInt(mTable.GetValueAt(i, "RemindCount").toString()) == 1)
							{
								Info = MTContent_2;
							}
							else if (Integer.parseInt(mTable.GetValueAt(i, "RemindCount").toString()) == 2)
							{
								Info = MTContent_3;
							}
							if (Info.contains("{QuestionCount}"))
							{
								Info = Info
										.replace("{QuestionCount}", mTable.GetValueAt(i, "QuestionCount").toString());
							}

							java.util.Date date = new java.util.Date();
							Timestamp RECEIVE_DATE = new Timestamp(date.getTime());

							String Operator = mTelco.toString();
							BigDecimal RequestId = new BigDecimal(0);

							long Receiveid = 0;
							String VMS_SVID = "";
							MsgObject mOjbect = new MsgObject(Receiveid, ServiceID, UserID, Keyword, Info, RequestId,
									RECEIVE_DATE, Operator, 0, 0, Receiveid, Info, VMS_SVID);

							mOjbect.setContenttype(21);
							// =0 phat sing tu dich vu, va khong tinh tien
							mOjbect.setMsgtype(0);

							int Result = Common.sendMT(mOjbect);
							mLog.log.info(Common.GetStringLog("Push MT Remid", "Result: " + Result, mOjbect));

							mTable.SetValueAt(Subscriber.Status.Remind.GetValue(), i, "StatusID");
						}

						// Update remid xuong table Subscriber
						mSub.Update(3, mTable.GetXML());
					}
				}
			}
			catch (Exception ex)
			{
				mLog.log.error("Run RemiderThread", ex);
			}
			try
			{
				sleep(DelayTime * 60 * 1000);
			}
			catch (InterruptedException ex)
			{
				mLog.log.error("Error run 3", ex);
			}
		}
	}
}
