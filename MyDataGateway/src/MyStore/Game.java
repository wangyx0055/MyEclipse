package MyStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class Game
{
	public MyExecuteData mExec;
	public MyGetData mGet;
	
	public Game() throws Exception
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
	
	public Game(String PoolName) throws Exception
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
	
	public MyTableModel Select(int Type) throws Exception, SQLException
	{
		String Query = "";
		try
		{
			if(Type == 0)//Lấy tất cả dữ liệu
			{
				Query = " SELECT TOP 10 * FROM Game";
			}			
			
			return mGet.GetData_Query(Query);
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
	 * @param Type Cach thuc lay du lieu
	 * Type =1: Lay thong tin chi tiet 1 game (Para_1 = GameID)
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception, SQLException
	{
		String Query = "";
		try
		{
			if(Type == 0)//Lấy tất cả dữ liệu
			{
				Query = " SELECT TOP 10 * FROM Game";
			}			
			else if(Type == 1)//Lay thong tin chi tiet cua 1 game
			{
				//Xoa bo ky tu dac biet, chi giu lai ky tu so
				Para_1 = MyUtility.MyText.RemoveSpecialLetter(1, Para_1);
				
				Query = " SELECT * FROM Game WHERE IsActive = 1 AND GameID = " + Para_1;
			}
			
			return mGet.GetData_Query(Query);
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
