package MyStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import MyConnection.*;
import MyDataSource.*;

public class Ringtone
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public Ringtone() throws Exception
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

	public Ringtone(String PoolName) throws Exception
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
			if (Type == 0)// Láº¥y táº¥t cáº£ dá»¯ liá»‡u
			{
				Query = " SELECT TOP 10 * FROM Ringtone";
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

	public List<String> GetIDByName(String SongName) throws Exception
	{
		List<String> mList = new ArrayList<String>();

		try
		{
			SongName = SongName.trim();
			SongName = SongName.replace(" ", "");

			String Query = " SELECT RingtoneID FROM Ringtone WHERE SearchContent LIKE '%" + SongName + ",%' ORDER BY NEWID()";

			MyTableModel mTable = mGet.GetData_Query(Query);

			if (!mTable.IsEmpty())
			{
				for (int i = 0; i < mTable.GetRowCount(); i++)
				{
					mList.add(mTable.GetValueAt(i, 0).toString());
				}
			}

			return mList;

		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public MyTableModel Select(int Type, String Para_1) throws Exception, SQLException
	{
		String Query = "";
		try
		{
			if (Type == 0)// Lấy tất cả dữ liệu
			{
				Query = " SELECT TOP 10 * FROM Ringtone";
			}
			else if (Type == 1)// Lay thong tin chi tiet cua 1 game
			{
				// Xoa bo ky tu dac biet, chi giu lai ky tu so
				Para_1 = MyUtility.MyText.RemoveSpecialLetter(1, Para_1);

				Query = " SELECT * FROM Ringtone WHERE IsActive = 1 AND RingtoneID = " + Para_1;
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
