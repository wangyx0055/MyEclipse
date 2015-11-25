package Service;

import java.sql.SQLException;

import MyConn.MyExecuteData;
import MyConn.MyGetData;
import MyDataSource.MyTableModel;

public class News
{
	public enum Status
	{
		NoThing(0), New(1), Sending(2), Complete(3);

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
				if (type.GetValue() == iValue)
					return type;
			}
			return NoThing;
		}
	}

	public enum NewsType
	{
		NoThing(0), Normal(1), HOT(2);

		private int value;

		private NewsType(int value)
		{
			this.value = value;
		}

		public Integer GetValue()
		{
			return this.value;
		}

		public static NewsType FromInt(int iValue)
		{
			for (NewsType type : NewsType.values())
			{
				if (type.GetValue() == iValue)
					return type;
			}
			return NoThing;
		}
	}

	public MyExecuteData mExec;
	public MyGetData mGet;

	public News() throws Exception
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

	public News(String PoolName) throws Exception
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
	 * 			<p> Type = 0: Lấy dữ liệu mâu </p>
	 *            <p>
	 *            Type = 7: Lấy danh sách tin đã được kích hoạt và chưa gửi cho
	 *            1 dịch vụ, và sắp xếp theo mới đến cũ
	 *            </p>
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = { "Type" };
			String Arr_Value[] = { Integer.toString(Type) };

			return mGet.GetData_Pro("Sp_News_Select", Arr_Name, Arr_Value);
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
	 * <p>Type = 9: Lấy danh sách tin đã được kích hoạt và chưa gửi cho 1 dịch vụ, và sắp xếp theo mới đến cũ (Para_1 = ServiceID)</p>
	 * <p>Type = 11: Lấy 1 tin mới nhất của dịch vụ (Para_1 = ServiceID)</p>
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
			return mGet.GetData_Pro("Sp_News_Select", Arr_Name, Arr_Value);
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
	 * <p>Type = 10: Lấy tin theo (Para_1 =serviceID, para_2 = newsTypeID</p>
	 * @param Para_1
	 * @param Para_2
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1,String Para_2) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1","Para_2"};
			String Arr_Value[] = { Integer.toString(Type), Para_1,Para_2 };
			return mGet.GetData_Pro("Sp_News_Select", Arr_Name, Arr_Value);
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
	 *            Type = 8: Lấy danh sách tin đã được kích hoạt và chưa gửi cho
	 *            1 dịch vụ, và sắp xếp theo mới đến cũ, Theo thời khoảng gian
	 *            (Para_1 = ServiceID, Para_2 = BeginTime, Para_3 = EndTime)
	 *            </p>
	 * @param Para_1
	 * @param Para_2
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1, String Para_2, String Para_3) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1", "Para_2","Para_3" };
			String Arr_Value[] = { Integer.toString(Type), Para_1, Para_2,Para_3 };

			return mGet.GetData_Pro("Sp_News_Select", Arr_Name, Arr_Value);
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
	 * <p>Type = 1: Update lai Status</p>
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
			return mExec.Execute_Pro("Sp_News_Update", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}	
	
}
