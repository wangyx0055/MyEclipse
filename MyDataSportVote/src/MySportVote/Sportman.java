package MySportVote;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class Sportman
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public Sportman() throws Exception
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

	public Sportman(String PoolName) throws Exception
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
	 * @param Type =0: Lấy dữ liệu mẫu
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
			
			return mGet.GetData_Pro("Sp_Sportman_Select", Arr_Name, Arr_Value);
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
	 * 
	 * @param Type : Cách thức lấy dữ liệu
	 * <p>Type = 1: Lấy chi tiết 1 Record (Para_1 = SportmanID)</p>
	 * <p>Type = 2: Lấy tất cả Sportman đã được kích hoạt</p>
	 * <p>Type = 3: Lấy 1 record theo Code (Para_1 = code</p>
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
			
			return mGet.GetData_Pro("Sp_Sportman_Select", Arr_Name, Arr_Value);
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
	public Vector<SportmanObject> GetAllSportman() throws Exception
	{
		try
		{
			Vector<SportmanObject> mList = new Vector<SportmanObject>();
			MyTableModel mTable = Select(2, null);
			
			for(int i=0; i < mTable.GetRowCount(); i++)
			{
				SportmanObject mObject = new SportmanObject();
				
				mObject.SportmanID = Integer.parseInt(mTable.GetValueAt(i, "SportmanID").toString());
				mObject.Code = mTable.GetValueAt(i, "Code").toString();
				mObject.Name = mTable.GetValueAt(i, "Name").toString();
				
				if(mTable.GetValueAt(i, "VoteCount") != null)
				{
					mObject.VoteCount = Integer.parseInt(mTable.GetValueAt(i, "VoteCount").toString());
				}
				if(mTable.GetValueAt(i, "LastVodeDate") != null)
				{
					mObject.LastVodeDate = (Timestamp)mTable.GetValueAt(i, "LastVodeDate");
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

	public static SportmanObject GetSportmanByCode(Vector<SportmanObject> mList, String Code) throws Exception
	{
		try
		{
			for(SportmanObject item:mList)
			{
				if(item.Code.equalsIgnoreCase(Code))
					return item;
			}
			return null;
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Update dữ liệu vào table Sportman
	 * @param Type cách thức Update
	 * <p>Type = 0: Update full tất cả các column</p>
	 * <p>Type = 1: Update Vote
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
			return mExec.Execute_Pro("Sp_Sportman_Update", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

}
