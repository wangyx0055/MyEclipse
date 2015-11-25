package MySportVote;

import java.sql.SQLException;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class UnSubscriber
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public UnSubscriber() throws Exception
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

	public UnSubscriber(String PoolName) throws Exception
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

			return mGet.GetData_Pro("Sp_UnSubscriber_Select", Arr_Name, Arr_Value);
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

			return mGet.GetData_Pro("Sp_UnSubscriber_Select", Arr_Name, Arr_Value);
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

			return mGet.GetData_Pro("Sp_UnSubscriber_Select", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	


	/**
	 * Chuyển từ table đăng ký sang
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
			return mExec.Execute_Pro("Sp_UnSubscriber_Move", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
