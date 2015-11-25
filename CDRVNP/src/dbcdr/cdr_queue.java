package dbcdr;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import cdr.LocalConfig;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;
import MyUtility.MyConfig;
import MyUtility.MyText;

public class cdr_queue
{

	public MyExecuteData mExec;
	public MyGetData mGet;

	public cdr_queue() throws Exception
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

	public cdr_queue(String PoolName) throws Exception
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
		String Query = "";
		try
		{
			if (Type == 1) // Lấy dữ liệu theo RequestID
			{
				Query = " SELECT * FROM cdr_queue WHERE ID = '" + Para_1 + "'";
			}
			else if (Type == 2)
			{
				Query = " SELECT * FROM cdr_queue WHERE ID = '" + Para_1 + "'";
			}
			else if (Type == 3)
			{
				Query = " SELECT * FROM cdr_queue  LIMIT 0," + Para_1;
			}
			else
				return new MyTableModel(null);
			
			return mGet.GetData_Query(Query);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * 
	 * @param Type:
	 * <p>Type = 1: Xóa 1 record với ID = Para_1</p>
	 * <p>Type = 2: Xóa 1 list  với ListID = Para_1 (Ex: 1,2,4)</p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 */
	public boolean Delete(int Type, String Para_1) throws Exception
	{
		String Query = "";
		try
		{
			Query = " DELETE FROM cdr_queue ";
			if(Type == 1)
			{
				Query += " WHERE ID = " + Para_1 + "";
			}
			else if(Type == 2)
			{
				Query += " WHERE ID IN (" + Para_1 + ")";
			}
			else
				return false;
			
			return mExec.Execute_Query(Query);
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
	 * 
	 * @param Type:
	 * <p>Type = 1: Chuyển 1 Row sang log có ID = Para_1</p>
	 * <p>Type = 2: Chuyển 1 list sang log ListID = Para_1 (Ex: 1,2,4)</p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 */
	public boolean MoveToLog(int Type,String Para_1) throws Exception
	{
		try
		{
			
			String Query = "";
			
			try
			{
				SimpleDateFormat DateFormat_Day = new SimpleDateFormat("yyyyMM");
				String TableName_Log = "cdr_log"+DateFormat_Day.format(Calendar.getInstance().getTime());
				
				Query = " 	INSERT INTO "+TableName_Log+"(id,user_id,service_id,mobile_operator,command_code,info,submit_date,done_date, "+
													"	total_segments,process_result,message_type,REQUEST_ID,CPId) "+
						"	SELECT id,user_id,service_id,mobile_operator,command_code,info,submit_date,done_date, "+
													"	total_segments,process_result,message_type,REQUEST_ID,CPId "+
						"	FROM cdr_queue ";
				if(Type == 1)
				{
					Query += " WHERE ID = " + Para_1 + "";
				}
				else if(Type == 2)
				{
					Query += " WHERE ID IN (" + Para_1 + ")";
				}
				else
					return false;		
				
				return mExec.Execute_Query(Query);
			}
			catch (Exception ex)
			{
				throw ex;
			}
		}
		catch(Exception ex)
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

			SimpleDateFormat DateFormat_Detail = new SimpleDateFormat("ddMMyyyy hh:mm:ss");
			SimpleDateFormat DateFormat_Day = new SimpleDateFormat("ddMMyyyy");
			
			for (int i = 0; i < mTable.GetRowCount(); i++)
			{
				CDRObject mObject = new CDRObject();
				mObject.ID = mTable.GetValueAt(i, "ID").toString();
				mObject.ShortCode = mTable.GetValueAt(i, "SERVICE_ID").toString();
				mObject.MSISDN = mTable.GetValueAt(i, "USER_ID").toString();
				mObject.ReceiveDate = DateFormat_Detail.format((Timestamp) mTable.GetValueAt(i, "SUBMIT_DATE"));
				mObject.PushDate = DateFormat_Detail.format((Timestamp) mTable.GetValueAt(i, "DONE_DATE"));
				mObject.Keyword = mTable.GetValueAt(i, "COMMAND_CODE").toString();
				mObject.CreateDate = DateFormat_Day.format((Timestamp) mTable.GetValueAt(i, "DONE_DATE"));
				mObject.Price = MyUtility.MyConvert.ShortCodeToPrice_String(mObject.ShortCode);
				
				String messageType = mTable.GetValueAt(i, "MESSAGE_TYPE").toString();
				String ProcessResult = mTable.GetValueAt(i, "PROCESS_RESULT").toString();
				
				if ("0".equals(ProcessResult))
				{ 
					// khong gui thanh cong sang telcos
					mObject.IsWriteCDR = true; // ghi cuoc hoan tien
				}
				else
				{ 
					// gui thanh cong sang telcos
					if (messageType.startsWith("2"))
					{ // ghi cuoc hoan tien tien
						
						mObject.IsWriteCDR = true;
					}
					else
					{
						mObject.IsWriteCDR = false;
					}
				}
				
				mList.add(mObject);
			}
			return mList;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
