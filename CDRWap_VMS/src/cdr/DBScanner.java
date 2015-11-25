package cdr;

import java.util.Calendar;
import java.util.Vector;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyUtility.MyLogger;
import dbcdr.*;

public class DBScanner extends Thread
{
	static MyLogger mLog = new MyLogger(DBScanner.class.toString());

	CDRQueue_VMS mCDRQueue;

	public DBScanner()
	{
		try
		{
			mCDRQueue = new CDRQueue_VMS(LocalConfig.PoolName_Data_SQL);
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	public void run()
	{
		mLog.log.info("CDRServer: Getting data from queue table");
		while (CDRServer.IsRunning)
		{
			try
			{
				this.ReadDBToFile();

				mLog.log.info("FTP: starting FTP cdr file");
				FTP2CDRServer ftp = new FTP2CDRServer();
				ftp.runftp();

				Sleep();
			}

			catch (Exception ex)
			{
				// when lost connection to db

				mLog.log.error(ex);
				mLog.log.warn("Billing system -> ERROR: Ket noi Database bi loi");
				Exit();

			}
			catch (ExceptionInInitializerError ex)
			{
				mLog.log.error(ex);
				mLog.log.warn("Billing system-> ERROR: Loi dinh dang MOBILE OPERATOR");
				Exit();
			}
		}
	}

	private void Sleep() throws InterruptedException
	{
		Calendar.getInstance();
		int currHour = Calendar.HOUR;
		if ((24 - currHour) * 60 < 2 * LocalConfig.SCHEDULE_TIME)
		{
			sleep((LocalConfig.SCHEDULE_TIME + 1) * 60000); // 00h:01
		}
		else
		{
			sleep(LocalConfig.SCHEDULE_TIME * 60000); // n * minutes;
		}
	}

	private static void Exit()
	{
		CDRServer.IsRunning = false;
		System.out.println("Stop.");
		System.exit(0);
	}

	/**
	 * Đọc tất cả các CDR Queue trong DB ra file
	 * 
	 * @throws Exception
	 */
	public void ReadDBToFile() throws Exception
	{

		try
		{

			boolean ExistData = true;
			while (ExistData)
			{
				Vector<CDRObject> mList = new Vector<CDRObject>();
				mList = mCDRQueue.GetCDR(LocalConfig.ROWCOUNT);
				if (mList.size() < 1)
				{
					ExistData = false;
					break;
				}

				Vector<Vector<Object>> mListRow = new Vector<Vector<Object>>();
				Vector<String> mListColumnName = new Vector<String>();
				mListColumnName.add("ID");

				MyTableModel mTable = new MyTableModel(mListRow, mListColumnName);

				try
				{
					for (CDRObject mObject : mList)
					{
						// Ghi dữ liệu ra file CDR trên Local
						CDRCommon.WriteCDRLocal(mObject);

						// Lấy ID đã ghi CDR
						MyDataRow mRow = mTable.CreateNewRow();
						mRow.SetValueCell("ID", mObject.ChargingID);
						mTable.AddNewRow(mRow);
					}
				}
				catch (Exception ex)
				{
					throw ex;
				}
				finally
				{
					// Xóa các CDR đã ghi ra file
					mCDRQueue.Delete(0, mTable.GetXML());
				}
			}
		}
		catch (Exception ex)
		{
			throw ex;
		}

	}

	public static void main(String[] args)
	{
		try
		{

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}
}
