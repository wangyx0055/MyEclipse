package MySportVote;

import java.sql.SQLException;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class VoteLog
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public VoteLog() throws Exception
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

	public VoteLog(String PoolName) throws Exception
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
	 * @param Type : Cách thức lấy dữ liệu
	 * <p>Type = 1: lấy thông tin chi tiết 1 record Para_1 = VoteLogID</p>
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
			
			return mGet.GetData_Pro("Sp_VoteLog_Select", Arr_Name, Arr_Value);
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
	/**
	 * @param Type : Cách thức lấy dữ liệu
	 * <p>Type = 0: Lay du lieu mẫu</p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] ={"Type"};
			String Arr_Value[] ={Integer.toString(Type)};
			
			return mGet.GetData_Pro("Sp_VoteLog_Select", Arr_Name, Arr_Value);
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
	
	public boolean Insert(int Type, String XMLContent) throws Exception,SQLException
	{
		try
		{
			String[] Arr_Name = { "Type", "XMLContent" };
			String[] Arr_Value = { Integer.toString(Type), XMLContent };
			return mExec.Execute_Pro("Sp_VoteLog_Insert", Arr_Name, Arr_Value);
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
