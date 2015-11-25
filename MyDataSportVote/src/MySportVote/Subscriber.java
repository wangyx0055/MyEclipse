package MySportVote;

import java.sql.SQLException;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;
import MyUtility.MyConfig;

public class Subscriber
{
	public enum Status
	{
		NewRegister(100),
		Playing(101),
		Remind(102),
		Pause(103),
		;
		
		private int value;
		private Status(int value)
		{
			this.value = value;
		}
		public int GetValue()
		{
			return this.value;
		}
		
		public static Status FromInt(int iValue)
		{
			for (Status type : Status.values())
			{
				if(type.GetValue() == iValue)
					return type;
			}
			return NewRegister;
		}
	
	}
	
	public MyExecuteData mExec;
	public MyGetData mGet;

	public Subscriber() throws Exception
	{
		try
		{
			mExec = new MyExecuteData();
			mGet = new MyGetData();
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public Subscriber(String PoolName) throws Exception
	{
		try
		{
			mExec = new MyExecuteData(PoolName);
			mGet = new MyGetData(PoolName);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	 /**
	 * Lấy dữ liệu
	 * 
	 * @param Type
	 *            <p>
	 *            Type = 0: Lấy dữ liệu mẫu
	 *            </p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type) throws Exception
	{
		try
		{
			String Arr_Name[] = { "Type" };
			String Arr_Value[] = { Integer.toString(Type) };

			return mGet.GetData_Pro("Sp_Subscriber_Select", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
	/**
	 * Lấy dữ liệu
	 * 
	 * @param Type
	 *            <p>Type = 1: Lấy thông tin chi tiết theo MSISDN (Para_1 = MSISDN)</p>
	 *            <p>Type = 3: Check TOP TotalMark (Para_1 = MSISDN)</p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1" };
			String Arr_Value[] = { Integer.toString(Type), Para_1 };

			return mGet.GetData_Pro("Sp_Subscriber_Select", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}


	/**
	 * Lấy dữ liệu
	 * 
	 * @param Type
	 *            <p>
	 *            Type = 2: Lấy thông tin chi tiết 1 record theo PID, MSISDN (Para_1
	 *            = PID, @Para_2 = MSISDN)
	 *            </p>
	 * @param Para_1
	 * @param Para_2
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1, String Para_2) throws Exception
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1", "Para_2" };
			String Arr_Value[] = { Integer.toString(Type), Para_1, Para_2 };

			return mGet.GetData_Pro("Sp_Subscriber_Select", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	

	/**
	 * Lấy dữ liệu
	 * 
	 * @param Type
	 *            <p>
	 *            Type = 4: Lấy danh sách các số cần remider (PID = Para_1, Time = Para_2, TelcoID = Para_3, BeginRow=Para_4, EndRow=Para_5
	 *            </p>
	 * @param Para_1
	 * @param Para_2
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1, String Para_2, String Para_3, String Para_4, String Para_5) throws Exception
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1", "Para_2", "Para_3", "Para_4","Para_5" };
			String Arr_Value[] = { Integer.toString(Type), Para_1, Para_2, Para_3, Para_4,Para_5 };

			return mGet.GetData_Pro("Sp_Subscriber_Select", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	

	
	public boolean Insert(int Type, String XMLContent) throws Exception
	{
		try
		{
			String[] Arr_Name = { "Type", "XMLContent" };
			String[] Arr_Value = { Integer.toString(Type), XMLContent };
			return mExec.Execute_Pro("Sp_Subscriber_Insert", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
	/**
	 * 
	 * @param Type Cách thức Update
	 * <p>Type = 0: Update full các Column</p>
	 * <p>Type = 1: Update Status</p>
	 * <p>Type = 2: Update trả lời câu hỏi</p>
	 * <p>Type = 3: Update Remider</p>
	 * 
	 * @param XMLContent
	 * @return
	 * @throws Exception
	 */
	public boolean Update(int Type, String XMLContent) throws Exception
	{
		try
		{
			String[] Arr_Name = { "Type", "XMLContent" };
			String[] Arr_Value = { Integer.toString(Type), XMLContent };
			return mExec.Execute_Pro("Sp_Subscriber_Update", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	
	public static SubscriberObject Convert(MyTableModel mTable_Sub) throws Exception
	{
		try
		{
			if(mTable_Sub.GetRowCount() < 1)
				return new SubscriberObject();
			
			SubscriberObject mObject = new SubscriberObject();
			
			mObject.MSISDN = mTable_Sub.GetValueAt(0, "MSISDN").toString();
			
			if (mTable_Sub.GetValueAt(0, "TelcoID") != null)
			{
				mObject.TelcoID = Integer.parseInt(mTable_Sub.GetValueAt(0, "TelcoID").toString());
			}
			if (mTable_Sub.GetValueAt(0, "RegisterDate") != null)
			{
				mObject.RegisterDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_Sub.GetValueAt(0, "RegisterDate").toString());
			}
			if (mTable_Sub.GetValueAt(0, "FirstQuestionDate") != null)
			{
				mObject.FirstQuestionDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_Sub.GetValueAt(0, "FirstQuestionDate").toString());
			}
			if (mTable_Sub.GetValueAt(0, "LastQuestionID") != null)
			{
				mObject.LastQuestionID = Integer.parseInt(mTable_Sub.GetValueAt(0, "LastQuestionID").toString());
			}
			if (mTable_Sub.GetValueAt(0, "LastQuestionDate") != null)
			{
				mObject.LastQuestionDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_Sub.GetValueAt(0, "LastQuestionDate").toString());
			}
			if (mTable_Sub.GetValueAt(0, "PreQuestionID") != null)
			{
				mObject.PreQuestionID = Integer.parseInt(mTable_Sub.GetValueAt(0, "PreQuestionID").toString());
			}
			
			if (mTable_Sub.GetValueAt(0, "LastAnswerDate") != null)
			{
				mObject.LastAnswerDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_Sub.GetValueAt(0, "LastAnswerDate").toString());
			}
			if (mTable_Sub.GetValueAt(0, "LastAnswer") != null)
			{
				mObject.LastAnswer = Integer.parseInt(mTable_Sub.GetValueAt(0, "LastAnswer").toString());
			}
			if (mTable_Sub.GetValueAt(0, "RightAnswerAgain") != null)
			{
				mObject.RightAnswerAgain = Integer.parseInt(mTable_Sub.GetValueAt(0, "RightAnswerAgain").toString());
			}

			if (mTable_Sub.GetValueAt(0, "WrongAnswerAgain") != null)
			{
				mObject.WrongAnswerAgain = Integer.parseInt(mTable_Sub.GetValueAt(0, "WrongAnswerAgain").toString());
			}
			
			if (mTable_Sub.GetValueAt(0, "LastMark") != null)
			{
				mObject.LastMark = Integer.parseInt(mTable_Sub.GetValueAt(0, "LastMark").toString());
			}

			if (mTable_Sub.GetValueAt(0, "TotalMark") != null)
			{
				mObject.TotalMark = Integer.parseInt(mTable_Sub.GetValueAt(0, "TotalMark").toString());
			}
			if (mTable_Sub.GetValueAt(0, "PID") != null)
			{
				mObject.PID = Integer.parseInt(mTable_Sub.GetValueAt(0, "PID").toString());
			}
			if (mTable_Sub.GetValueAt(0, "StatusID") != null)
			{
				mObject.StatusID = Integer.parseInt(mTable_Sub.GetValueAt(0, "StatusID").toString());
			}
			if (mTable_Sub.GetValueAt(0, "RemindDate") != null)
			{
				mObject.RemindDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_Sub.GetValueAt(0, "RemindDate").toString());
			}

			if (mTable_Sub.GetValueAt(0, "RemindCount") != null)
			{
				mObject.RemindCount = Integer.parseInt(mTable_Sub.GetValueAt(0, "RemindCount").toString());
			}
			
			if (mTable_Sub.GetValueAt(0, "ListQuestionID") != null)
			{
				mObject.ListQuestionID = mTable_Sub.GetValueAt(0, "ListQuestionID").toString();
			}
			

			if (mTable_Sub.GetValueAt(0, "QuestionCount") != null)
			{
				mObject.QuestionCount = Integer.parseInt(mTable_Sub.GetValueAt(0, "QuestionCount").toString());
			}
			
			if (mTable_Sub.GetValueAt(0, "QuestionCountByDay") != null)
			{
				mObject.QuestionCountByDay = Integer.parseInt(mTable_Sub.GetValueAt(0, "QuestionCountByDay").toString());
			}
			
			if (mTable_Sub.GetValueAt(0, "QuestionByDayDate") != null)
			{
				mObject.QuestionByDayDate = MyConfig.Get_DateFormat_InsertDB().parse(mTable_Sub.GetValueAt(0, "QuestionByDayDate").toString());
			}
			return mObject;
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Chuyển từ table Hủy đăng ký sang
	 * @param Type
	 * @param Para_1
	 * @return
	 * @throws Exception
	 */
	public boolean Move(int Type, String Para_1) throws Exception
	{
		try
		{
			mExec.UseTransaction = true;
			
			String[] Arr_Name = { "Type", "Para_1" };
			String[] Arr_Value = { Integer.toString(Type), Para_1 };
			return mExec.Execute_Pro("Sp_Subscriber_Move", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
}
