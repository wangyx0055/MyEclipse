package cdr;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import MyUtility.MyLogger;
import dbcdr.*;

public class DBScanner extends Thread
{
	static MyLogger mLog = new MyLogger(DBScanner.class.toString());

	cdr_queue mCDRQueue;

	public DBScanner()
	{
		try
		{
			mCDRQueue = new cdr_queue(LocalConfig.PoolName_Data_MySQL);
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
				if (CheckTimeWriteCDR())
				{
					mLog.log.info("Bat dau ghi cdr tu db ra file");
					this.ReadDBToFile();
				}
				else
				{
					mLog.log.info("Ko co phep ghi cdr tu db ra file");
				}
				// nếu trong khoảng thời gian được Push CDR sang Telco
				if (CheckTimePushCDR())
				{

					mLog.log.info("Bat dau ftp cdr sang Telco ");
					FTP2CDRServer ftp = new FTP2CDRServer();
					ftp.runftp();
				}
				else
				{
					mLog.log.info("Ko co phep ftp cdr sang telco");
				}

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

	/**
	 * Kiểm tra thời gian cho phép đẩy file cdr sang Telco
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean CheckTimePushCDR() throws Exception
	{
		try
		{
			Calendar mStart_Push_Cal = Calendar.getInstance();

			Calendar mStop_Push_Cal = Calendar.getInstance();

			mStart_Push_Cal.set(Calendar.HOUR_OF_DAY, LocalConfig.START_PUSH_CDR_HOUR);
			mStart_Push_Cal.set(Calendar.MINUTE, LocalConfig.START_PUSH_CDR_MINUTE);
			mStart_Push_Cal.set(Calendar.SECOND, 0);
			mStart_Push_Cal.set(Calendar.MILLISECOND, 0);

			mStop_Push_Cal.set(Calendar.HOUR_OF_DAY, LocalConfig.STOP_PUSH_CDR_HOUR);
			mStop_Push_Cal.set(Calendar.MINUTE, LocalConfig.STOP_PUSH_CDR_MINUTE);
			mStop_Push_Cal.set(Calendar.SECOND, 0);
			mStop_Push_Cal.set(Calendar.MILLISECOND, 0);

			SimpleDateFormat DateFormat_Day = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			String s = DateFormat_Day.format(mStart_Push_Cal.getTime());
			s = DateFormat_Day.format(mStop_Push_Cal.getTime());

			// nếu trong khoảng thời gian được Push CDR sang Telco
			if (Calendar.getInstance().after(mStart_Push_Cal) && Calendar.getInstance().before(mStop_Push_Cal))
			{
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Kiểm tra thời gian cho phép ghi nội dung từ db ra file cdr
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean CheckTimeWriteCDR() throws Exception
	{
		try
		{
			Calendar mStart_WRITE_Cal = Calendar.getInstance();

			Calendar mStop_WRITE_Cal = Calendar.getInstance();

			mStart_WRITE_Cal.set(Calendar.HOUR_OF_DAY, LocalConfig.START_WRITE_CDR_HOUR);
			mStart_WRITE_Cal.set(Calendar.MINUTE, LocalConfig.START_WRITE_CDR_MINUTE);
			mStart_WRITE_Cal.set(Calendar.SECOND, 0);
			mStart_WRITE_Cal.set(Calendar.MILLISECOND, 0);

			mStop_WRITE_Cal.set(Calendar.HOUR_OF_DAY, LocalConfig.STOP_WRITE_CDR_HOUR);
			mStop_WRITE_Cal.set(Calendar.MINUTE, LocalConfig.STOP_WRITE_CDR_MINUTE);
			mStop_WRITE_Cal.set(Calendar.SECOND, 0);
			mStop_WRITE_Cal.set(Calendar.MILLISECOND, 0);

			if (Calendar.getInstance().after(mStart_WRITE_Cal) && Calendar.getInstance().before(mStop_WRITE_Cal))
			{
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			throw ex;
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
				try
				{
					String fileCDRftp = null;
					fileCDRftp = CDRCommon.GetNewFilenameFTPforVNP();
					String PathFile = LocalConfig.LOCAL_FOLDER + "/" + fileCDRftp;
					File CheckFile = new File(PathFile);

					//Nếu file chưa có thì tạo file
					if (!CheckFile.exists())
					{
						CheckFile.createNewFile();
					}

					Vector<CDRObject> mList = new Vector<CDRObject>();
					mList = mCDRQueue.GetCDR(LocalConfig.ROWCOUNT);
					if (mList.size() < 1)
					{
						ExistData = false;
						break;
					}

					for (CDRObject mObject : mList)
					{
						if (mObject.IsWriteCDR)// Chi ghi tin nhắn hoàn tiền
						{
							// Ghi dữ liệu ra file CDR trên Local
							CDRCommon.WriteCDRLocal(mObject);
						}

						// Chuyển sang cdr_log
						mCDRQueue.MoveToLog(1, mObject.ID);

						// Xóa dữ liệu trong DB
						mCDRQueue.Delete(1, mObject.ID);
					}
				}
				catch (Exception ex)
				{
					throw ex;
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
