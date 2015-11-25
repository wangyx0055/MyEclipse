package MySportVote;

import java.sql.SQLException;
import java.util.Random;
import java.util.Vector;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class DefineMT
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public enum MTType
	{
		Default(100), Invalid(101), Overflow(102),

		Vote_Success(200), Vote_Unsuccess(201), Vote_SuccessAgain(202),

		Answer_Right(300), Answer_Wrong(301), Answer_Invalid(302), Answer_NotSub(303), Answer_NoQuestion(303),
		/**
		 * Vượt quá câu hỏi cho phép trong 1 ngày
		 */
		Answer_Exceed(304),
		

		Remind_Answer_Fist(400), Remind_Answer_Second(401), Remind_Answer_Third(402), 
		
		CheckMark(500), CheckMark_NotReg(501), CheckTop(502), CheckTop_NotReg(503),
		
		Register_Success(600), Register_Unsuccess(601), Register_SuccessAgain(602),
		DeRegister_Success(603), DeRegister_Unsuccess(604), DeRegister_NotRegister(605),
		
		Session_Expire(700)
		;
				

		private int value;

		private MTType(int value)
		{
			this.value = value;
		}

		public int GetValue()
		{
			return this.value;
		}

		public static MTType FromInt(int iValue)
		{
			for (MTType type : MTType.values())
			{
				if (type.GetValue() == iValue)
					return type;
			}
			return Default;
		}

	}

	public DefineMT() throws Exception
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

	public DefineMT(String PoolName) throws Exception
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
	 * 
	 * @param Type
	 *            : Cách thức lấy dữ liệu
	 *            <p>
	 *            Type = 1: lấy thông tin chi tiết 1 record Para_1 = DefineMTID
	 *            </p>
	 *            <p>
	 *            Type = 2: Lấy dữ liệu theo MTTypeID (Para_1 = MTTypeID
	 *            </p>
	 *            <p>
	 *            Type = 4: Lấy tất cả dữ liệu đã được active
	 *            </p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1" };
			String Arr_Value[] = { Integer.toString(Type), Para_1 };

			return mGet.GetData_Pro("Sp_DefineMT_Select", Arr_Name, Arr_Value);
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy danh sách các DefineMT trong database
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vector<DefineMTObject> GetAllMT() throws Exception
	{
		try
		{
			Vector<DefineMTObject> mList = new Vector<DefineMTObject>();
			MyTableModel mTable = Select(4, null);

			for (int i = 0; i < mTable.GetRowCount(); i++)
			{
				DefineMTObject mObject = new DefineMTObject();

				if (mTable.GetValueAt(i, "MTTypeID") != null)
				{
					mObject.mMTType = MTType.FromInt(Integer.parseInt(mTable.GetValueAt(i, "MTTypeID").toString()));
				}
				mObject.MTContent = mTable.GetValueAt(i, "MTContent").toString();

				mList.add(mObject);
			}
			return mList;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy nội dung MT mặc định theo MTType
	 * 
	 * @param MTTypeID
	 * @return
	 * @throws Exception
	 */
	private static String GetDefaultMT(MTType MTTypeID) throws Exception
	{
		try
		{
			String ShortCode = "6583";
			String MT = "Tin Nhan sai cu phap, xin vui long kiem tra lai";

			switch (MTTypeID)
			{
				case Invalid:
					MT = "Tin nhan sai cu phap, de bien thong tin chi tiet ve dich vu voi long soan HELP gui " + ShortCode;
					break;

			}
			return MT;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy 1 MT đã được định nghĩa trong datbase, Nếu trong database không có
	 * thì lấy MT mặc định của dịch vụ
	 * 
	 * @param mList
	 *            : Danh sách các DefineMT
	 * @param mMTType
	 *            : Loại MT cần lấy
	 * 
	 * @param ServiceID
	 *            : ID của dịch vụ cần lấy
	 * @return
	 * @throws Exception
	 */
	public static String GetMTContent(Vector<DefineMTObject> mList, MTType mMTType) throws Exception
	{
		try
		{
			if (mList.size() < 1)
				return GetDefaultMT(mMTType);

			Vector<DefineMTObject> mList_Random = new Vector<DefineMTObject>();

			for (DefineMTObject mObject : mList)
			{
				if (mObject.mMTType == mMTType && mObject.MTContent.length() > 0)
					mList_Random.add(mObject);
			}

			if (mList_Random.size() < 1)
				return GetDefaultMT(mMTType);

			if (mList_Random.size() == 1)
				return mList_Random.get(0).MTContent;

			Random mRandom = new Random();
			int Range = mList_Random.size();
			int Rand = mRandom.nextInt(Range);

			return mList_Random.get(Rand).MTContent;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

}
