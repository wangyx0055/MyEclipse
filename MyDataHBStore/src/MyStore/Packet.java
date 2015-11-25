package MyStore;

import java.sql.SQLException;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class Packet
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public Packet() throws Exception
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

	public Packet(String PoolName) throws Exception
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

	public MyTableModel Select(int Type) throws Exception, SQLException
	{
		String Query = "";
		try
		{
			if (Type == 0)// Lấy tất cả dữ liệu
			{
				Query = " SELECT TOP 10 * FROM Packet";
			}

			return mGet.GetData_Query(Query);
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
	 *            Cach thuc lay du lieu Type =1: Lay thong tin chi tiet 1 Packet
	 *            (Para_1 = PacketID)
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
			if (Type == 0)// Lấy tất cả dữ liệu
			{
				Query = " SELECT TOP 10 * FROM Packet";
			}
			else if (Type == 1)// Lay thong tin chi tiet cua 1 Packet
			{
				// Xoa bo ky tu dac biet, chi giu lai ky tu so
				Para_1 = MyUtility.MyText.RemoveSpecialLetter(1, Para_1);

				Query = " SELECT * FROM Packet WHERE IsActive = 1 AND PacketID = " + Para_1;
			}

			return mGet.GetData_Query(Query);
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

}
