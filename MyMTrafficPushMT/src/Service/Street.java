package Service;

import java.sql.SQLException;

import MyConn.MyExecuteData;
import MyConn.MyGetData;
import MyDataSource.MyTableModel;

public class Street
{
	public MyExecuteData mExec;
	public MyGetData mGet;
		
	public Street() throws Exception
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
	
	public Street(String PoolName) throws Exception
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
	 * <p>Type = 5: tìm kei6m1 tên đương Para_1 = Tên đường cần tìm kiếm</p>
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
			
			return mGet.GetData_Pro("Sp_Street_Select", Arr_Name, Arr_Value);
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
	 
	public boolean Insert(int Type, String XMLContent) throws Exception
	{
		try
		{
			String[] Arr_Name = { "Type", "XMLContent" };
			String[] Arr_Value = { Integer.toString(Type), XMLContent };
			return mExec.Execute_Pro("Sp_Street_Insert", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
	public boolean Delete(int Type, String XMLContent) throws Exception
	{
		try
		{
			String[] Arr_Name = { "Type", "XMLContent" };
			String[] Arr_Value = { Integer.toString(Type), XMLContent };
			return mExec.Execute_Pro("Sp_Street_Delete", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
	public boolean Delete(int Type,String[] ListID) throws Exception
	{
		try
		{
			String XMLContent = "";
			for(String item: ListID)
			{
				if(item.trim() == "")
					continue;
				
				XMLContent += "<Child>"+item +"</Child>";
			}
			XMLContent = "<Parent>"+XMLContent +"</Parent>";
			
			String[] Arr_Name = { "Type", "XMLContent" };
			String[] Arr_Value = { Integer.toString(Type), XMLContent };
			return mExec.Execute_Pro("Sp_Street_Delete", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
}
