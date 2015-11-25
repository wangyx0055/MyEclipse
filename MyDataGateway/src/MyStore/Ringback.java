package MyStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import MyConnection.*;
import MyDataSource.*;
import MyUtility.MyConfig;
import MyUtility.MyText;

public class Ringback
{

	public MyExecuteData mExec;
	public MyGetData mGet;
	
	public Ringback() throws Exception
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
	
	public Ringback(String PoolName) throws Exception
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
				Query = " SELECT TOP 10 * FROM Ringback";
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
	 * Lấy mã bài hát nhạc chờ theo tên (đã được bỏ khoảng trắng)
	 * @param SongName
	 * @param mTelco Mạng cần lấy mã
	 * @return Trả về mã nhạc chờ, nếu trả về là "" thì không tìm thầy bài hát
	 * @throws Exception
	 */
	public List<String> GetCodeByName(String SongName, MyConfig.Telco mTelco) throws Exception 
	{
		List<String> mList = new ArrayList<String>();
		
		try
		{
			SongName = SongName.trim();
			
			String ColumnCode_Name = "VNPCode";
			switch(mTelco)
			{
				case VIETTEL:
					ColumnCode_Name ="VTCode";
					break;
				case GPC:
					ColumnCode_Name ="VNPCode";
					break;
					
				case VMS:
					ColumnCode_Name ="VMSCode";
					break;
				case BEELINE:
					ColumnCode_Name ="BEECode";
					break;
				case HTC:
					ColumnCode_Name ="VNMCode";
					break;
				case SFONE:
					ColumnCode_Name ="SFCode";
					break;
			}
			
			String Query = " SELECT "+ColumnCode_Name + " FROM Ringback WHERE IsActive = 1 AND RingbackName_NS ='"+SongName+"' AND LEN("+ColumnCode_Name + ") > 0" ;
			
			MyTableModel mTable = mGet.GetData_Query(Query);
			
			if(!mTable.IsEmpty())
			{
				for(int i=0; i< mTable.GetRowCount(); i++)
				{
					if(mTable.GetValueAt(i, 0) == null)
						continue;
					
					mList.add(mTable.GetValueAt(i, 0).toString());
				}
			}
			
			return mList;
			
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	public Hashtable<String, String> GetRingbackByName(String SongName, MyConfig.Telco mTelco) throws Exception 
	{
		Hashtable<String, String> mList = new Hashtable<String, String>();
		
		try
		{
			SongName = SongName.trim();
			
			String ColumnCode_Name = "VNPCode";
			switch(mTelco)
			{
				case VIETTEL:
					ColumnCode_Name ="VTCode";
					break;
				case GPC:
					ColumnCode_Name ="VNPCode";
					break;
					
				case VMS:
					ColumnCode_Name ="VMSCode";
					break;
				case BEELINE:
					ColumnCode_Name ="BEECode";
					break;
				case HTC:
					ColumnCode_Name ="VNMCode";
					break;
				case SFONE:
					ColumnCode_Name ="SFCode";
					break;
			}
			
			String Query = " SELECT "+ColumnCode_Name + ", RingbackName FROM Ringback WHERE IsActive = 1 AND RingbackName_NS ='"+SongName+"' AND LEN("+ColumnCode_Name + ") > 0" ;
			
			MyTableModel mTable = mGet.GetData_Query(Query);
			
			if(!mTable.IsEmpty())
			{
				for(int i=0; i< mTable.GetRowCount(); i++)
				{
					if(mTable.GetValueAt(i, 0) == null)
						continue;
					
					mList.put(mTable.GetValueAt(i, ColumnCode_Name).toString(), MyText.RemoveSignVietnameseString( mTable.GetValueAt(i, "RingbackName").toString()));
				}
			}
			
			return mList;
			
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
	public Hashtable<String, String> GetRingbackByCode(String RingbackCode, MyConfig.Telco mTelco) throws Exception 
	{
		Hashtable<String, String> mList = new Hashtable<String, String>();
		
		try
		{
			RingbackCode = RingbackCode.trim();
			
			String ColumnCode_Name = "VNPCode";
			switch(mTelco)
			{
				case VIETTEL:
					ColumnCode_Name ="VTCode";
					break;
				case GPC:
					ColumnCode_Name ="VNPCode";
					break;
					
				case VMS:
					ColumnCode_Name ="VMSCode";
					break;
				case BEELINE:
					ColumnCode_Name ="BEECode";
					break;
				case HTC:
					ColumnCode_Name ="VNMCode";
					break;
				case SFONE:
					ColumnCode_Name ="SFCode";
					break;
			}
			
			String Query = " SELECT "+ColumnCode_Name + ", RingbackName FROM Ringback WHERE IsActive = 1 AND "+ColumnCode_Name+" ='"+RingbackCode+"' " ;
			
			MyTableModel mTable = mGet.GetData_Query(Query);
			
			if(!mTable.IsEmpty())
			{
				for(int i=0; i< mTable.GetRowCount(); i++)
				{
					if(mTable.GetValueAt(i, 0) == null)
						continue;
					
					mList.put(mTable.GetValueAt(i, ColumnCode_Name).toString(), MyText.RemoveSignVietnameseString( mTable.GetValueAt(i, "RingbackName").toString()));
				}
			}
			
			return mList;
			
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
}
