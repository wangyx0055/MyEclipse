package Sub;

import java.sql.SQLException;

import MyConn.MyExecuteData;
import MyConn.MyGetData;
import MyDataSource.MyTableModel;

public class Subscriber
{
	public enum Status
	{
		NoThing(0), 
		/**
		 * Kích hoạt
		 */
		Active(1), 
		/**
		 * Hủy kích hoạt
		 */
		Deactive(2)
		;

		private int value;

		private Status(int value)
		{
			this.value = value;
		}

		public Integer GetValue()
		{
			return this.value;
		}

		public static Status FromInt(int iValue)
		{
			for (Status type : Status.values())
			{
				if (type.GetValue() == iValue)
					return type;
			}
			return NoThing;
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
	 * 
	 * @param Type
	 *            <p>
	 *            Type = 2: Lấy chi tiết 1 Record (Para_1 = PID, Para_2 =
	 *            MSISDN, Para_3 = ServiceID)
	 *            </p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1, String Para_2, String Para_3) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1", "Para_2", "Para_3" };
			String Arr_Value[] = { Integer.toString(Type), Para_1, Para_2, Para_3 };

			return mGet.GetData_Pro("Sp_Subscriber_Select", Arr_Name, Arr_Value);
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
	 * 
	 * @param Type
	 *            <p>
	 *            Type = 3: Lấy danh sách (Para_1 = RowCount, Para_2 = PID,
	 *            Para_3 = ServiceID, Para_4 = OrderID )
	 *            </p>
	 * @param Para_1
	 * @param Para_2
	 * @param Para_3
	 * @param Para_4
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1, String Para_2, String Para_3, String Para_4) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1", "Para_2", "Para_3", "Para_4" };
			String Arr_Value[] = { Integer.toString(Type), Para_1, Para_2, Para_3, Para_4 };

			return mGet.GetData_Pro("Sp_Subscriber_Select", Arr_Name, Arr_Value);
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
	 * 
	 * @param Type
	 *            <p>
	 *            Type = 4: Lấy danh sách (Para_1 = RowCount, Para_2 = PID,
	 *            Para_3 = ServiceID, Para_4 = OrderID, Para_5 = ProcessNumber,
	 *            Para_6 = ProcessIndex, Para_7 = StatusID )
	 *            </p>
	 * @param Para_1
	 * @param Para_2
	 * @param Para_3
	 * @param Para_4
	 * @param Para_5
	 * @param Para_6
	 * @param Para_7
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1, String Para_2, String Para_3, String Para_4, String Para_5, String Para_6, String Para_7) throws Exception,
			SQLException
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1", "Para_2", "Para_3", "Para_4", "Para_5", "Para_6","Para_7" };
			String Arr_Value[] = { Integer.toString(Type), Para_1, Para_2, Para_3, Para_4, Para_5, Para_6,Para_7 };

			return mGet.GetData_Pro("Sp_Subscriber_Select", Arr_Name, Arr_Value);
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
	 * 
	 * @param Type
	 * <p>Type = 0: Update full</p>
	 * <p>Type = 1: Update thông tin PUsh MT</p>
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
	
	
}
