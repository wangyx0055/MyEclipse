package Service;

import java.sql.SQLException;

import MyConn.MyExecuteData;
import MyConn.MyGetData;
import MyDataSource.MyTableModel;

public class NewsStreet
{
	public MyExecuteData mExec;
	public MyGetData mGet;
		
	public NewsStreet() throws Exception
	{
		try
		{
			mExec = new MyExecuteData();
			mGet = new MyGetData();
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
	
	public NewsStreet(String PoolName) throws Exception
	{
		try
		{
			mExec = new MyExecuteData(PoolName);
			mGet = new MyGetData(PoolName);
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
	
	/**
	 * 
	 * @param Type : Cách thức lấy dữ liệu
	 * <p>Type = 0:Lấy dữ liệu mẫu </p> 
	 * <p>Type = 2: Lấy dữ liệu theo NewsID</p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] ={"Type", "Para_1"};
			String Arr_Value[] ={Integer.toString(Type), Para_1};
			
			return mGet.GetData_Pro("Sp_NewsStreet_Select", Arr_Name, Arr_Value);
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
	 
}
