package dbcdr;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Vector;

import cdr.LocalConfig;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;
import MyUtility.MyConfig;
import MyUtility.MyText;

public class CDRQueue_VMS
{

	public MyExecuteData mExec;
	public MyGetData mGet;

	public CDRQueue_VMS() throws Exception
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

	public CDRQueue_VMS(String PoolName) throws Exception
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
	 * @param Type
	 *            : Cách thức lấy dữ liệu
	 *            <p>
	 *            Type = 1: lấy thông tin chi tiết 1 record Para_1 = ChagringID
	 *            </p>
	 *            <p>
	 *            Type = 2: lấy tất cả thông tin
	 *            </p>
	 *            <p>
	 *            Type = 3: Lấy thông tin theo số dòng. (Para_1 = số dòng cần
	 *            lấy)
	 *            </p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = { "Type", "Para_1" };
			String Arr_Value[] = { Integer.toString(Type), Para_1 };

			return mGet.GetData_Pro("Sp_CDRQueue_VMS_Select", Arr_Name, Arr_Value);
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

	public boolean Delete(int Type, String XMLContent) throws Exception
	{
		try
		{
			String[] Arr_Name = { "Type", "XMLContent" };
			String[] Arr_Value = { Integer.toString(Type), XMLContent };
			return mExec.Execute_Pro("Sp_CDRQueue_VMS_Delete", Arr_Name, Arr_Value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy dữ liệu CDR theo số dòng.
	 * 
	 * @param RowCount
	 *            : là số dòng cần lấy
	 * @return
	 * @throws Exception
	 */
	public Vector<CDRObject> GetCDR(int RowCount) throws Exception
	{
		try
		{
			Vector<CDRObject> mList = new Vector<CDRObject>();

			MyTableModel mTable = Select(3, Integer.toString(RowCount));
			mList = Convert(mTable);
			return mList;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Chuyễn dữ liệu từ 1 table (database) sang 1 list CDRObject
	 * 
	 * @param mTable
	 * @return
	 * @throws Exception
	 */
	public Vector<CDRObject> Convert(MyTableModel mTable) throws Exception
	{
		try
		{
			Vector<CDRObject> mList = new Vector<CDRObject>();
			if (mTable.IsEmpty())
				return mList;
			
			for (int i = 0; i < mTable.GetRowCount(); i++)
			{

				CDRObject mObject = new CDRObject();
				mObject.A_Number = mTable.GetValueAt(i, "MSISDN").toString();
				mObject.B_Number = LocalConfig.SHORT_CODE;
				mObject.ChannelType = "WAP";
				mObject.ChargingID = Integer.parseInt(mTable.GetValueAt(i, "ChargingID").toString());
				
				DecimalFormat fm_Long = new DecimalFormat("##########");
				mObject.ContentID =fm_Long.format(Long.parseLong(MyText.RemoveSpecialLetter(1, mTable.GetValueAt(i, "MediaID").toString())));
				
				for(int j = mObject.ContentID.length();  j <10; j++)
				{
					mObject.ContentID ="0"+mObject.ContentID;
				}
				
				 DecimalFormat fm_Double = new DecimalFormat("#");
				  
				mObject.Cost = fm_Double.format(Double.parseDouble(mTable.GetValueAt(i, "Price").toString()));
				mObject.CPID = LocalConfig.CPID;

				if (mTable.GetValueAt(i, "DoneDate") != null)
				{

					mObject.Datetime = MyConfig.DateFormat_yyyymmddhhmmss.format((Timestamp) mTable.GetValueAt(i, "DoneDate"));
				}
				mObject.EventID = ConvertMediaTypeToEventID(Integer.parseInt(mTable.GetValueAt(i, "MediaType").toString()));
				mObject.Information = "1";

				if (mTable.GetValueAt(i, "ResultCodeTelco") != null && mTable.GetValueAt(i, "ResultCodeTelco").toString().equals("0"))
				{
					mObject.Status = "1";
				}
				else
					mObject.Status = "0";

				mList.add(mObject);

			}
			return mList;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
	/**
	 * Chuyển thể loại Media sang category của VMS (do VMS quy định)
	 * @param MediaType
	 * @return
	 */
	public String ConvertMediaTypeToEventID(int MediaType)
	{
		if (MediaType == 1)
			return "000040";
		if (MediaType == 2)
			return "000020";
		if (MediaType == 3)
			return "000050";
		if (MediaType == 4)
			return "000050";
		if (MediaType == 5)
			return "000030";
		if (MediaType == 6)
			return "000030";
		if (MediaType == 7)
			return "000000";
		else
			return "000000";
	}

}
