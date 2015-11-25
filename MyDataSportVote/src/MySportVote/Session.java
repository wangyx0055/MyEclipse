package MySportVote;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class Session
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public Session() throws Exception
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

	public Session(String PoolName) throws Exception
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
	 * @param Type : Cách thức lấy dữ liệu
	 * <p>Type = 1: Lấy chi tiết 1 Record (Para_1 = SessionID)</p>
	 * <p>Type = 2: Lấy tất cả session đã được kích hoạt</p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = {"Type", "Para_1"};
			String Arr_Value[] ={Integer.toString(Type), Para_1};
			
			return mGet.GetData_Pro("Sp_Session_Select", Arr_Name, Arr_Value);
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
	 * Lấy danh sách các Session trong database
	 * @return
	 * @throws Exception
	 */
	public Vector<SessionObject> GetAllSession() throws Exception
	{
		try
		{
			Vector<SessionObject> mList = new Vector<SessionObject>();
			MyTableModel mTable = Select(2, null);
			
			for(int i=0; i < mTable.GetRowCount(); i++)
			{
				SessionObject mObject = new SessionObject();
				
				mObject.SessionID = Integer.parseInt(mTable.GetValueAt(i, "SessionID").toString());
				mObject.SessionName = mTable.GetValueAt(i, "SessionName").toString();
				
				if(mTable.GetValueAt(i, "BeginDate") != null)
				{
					mObject.BeginDate = (Timestamp)mTable.GetValueAt(i, "BeginDate");
				}
				if(mTable.GetValueAt(i, "EndDate") != null)
				{
					mObject.EndDate = (Timestamp)mTable.GetValueAt(i, "EndDate");
				}
								
				mList.add(mObject);
			}
			return mList;
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
	
}
