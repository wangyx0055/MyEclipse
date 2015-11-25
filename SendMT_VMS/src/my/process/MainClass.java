package my.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Vector;

import my.define.MTObject;
import my.define.MTQueue;
import my.define.MySetting;
import MyGateway.ems_send_queue;
import MyUtility.MyCurrent;
import MyUtility.MyFile;
import MyUtility.MyLogger;

public class MainClass extends Thread
{
	static MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), MainClass.class.toString());

	/**
	 * Load tất cả MT từ sms_sendqueue lên list này
	 */
	MTQueue mQueue_MT = new MTQueue();

	/**
	 * Sau khi gui MT thanh cong thi se duoc luu vao log
	 */
	MTQueue mQueue_MTLog = new MTQueue();

	/**
	 * Tất cả MT khi gửi thành công va luu log thanh cong sẽ được lưu vào list
	 * này để ghi cdr
	 */
	MTQueue mQueue_CDR = new MTQueue();

	public static LoadMT[] ListLoadMT = null;
	public static SendMT[] ListSendMT = null;
	public static SaveMTLog[] ListSaveMTLog = null;
	public static SaveCDRQueue[] ListSaveCDRQueue = null;

	public static void main(String[] args)
	{
		System.out.println("Bat dau chay chuong trinh...");
		System.out.println("Hay copy file config vao thu muc:" + MyCurrent.GetCurrentPath()
				+ MySetting.FolderConfigName);
		MainClass mMain = new MainClass();

		ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor(mMain);
		Runtime.getRuntime().addShutdownHook(shutdownInterceptor);

		mMain.start();
	}

	public void run()
	{
		try
		{
			System.out.println("Load config...");
			// Load config.
			MySetting.LoadConfig();
			MySetting.AllowRunning = true;

			//
			ems_send_queue mSendQueue = new ems_send_queue(MySetting.GetProxoolConfigPath(), MySetting.PoolName_GateWay);
			mSendQueue.Select(1, 0, 1);

			System.out.println("Load queue tu file .dat");
			LoadQueueThread();

			System.out.println("Chay thread LoadMT");
			ListLoadMT = new LoadMT[MySetting.LoadMT_Thread_Number];
			for (int i = 0; i < MySetting.LoadMT_Thread_Number; i++)
			{
				ListLoadMT[i] = new LoadMT(mQueue_MT, MySetting.LoadMT_Thread_Number, i);
				ListLoadMT[i].setPriority(Thread.MAX_PRIORITY);
				ListLoadMT[i].start();
				Thread.sleep(100);
			}
			
			System.out.println("Chay thread SendMT");
			ListSendMT = new SendMT[MySetting.SaveMTLog_Thread_Number];
			for (int i = 0; i < MySetting.SaveMTLog_Thread_Number; i++)
			{
				ListSendMT[i] = new SendMT(mQueue_MT, mQueue_MTLog, MySetting.LoadMT_Thread_Number, i);
				ListSendMT[i].setPriority(Thread.MAX_PRIORITY);
				ListSendMT[i].start();
				Thread.sleep(100);
			}
			System.out.println("Chay thread SaveMTLog");
			ListSaveMTLog = new SaveMTLog[MySetting.SaveMTLog_Thread_Number];
			for (int i = 0; i < MySetting.SaveMTLog_Thread_Number; i++)
			{
				ListSaveMTLog[i] = new SaveMTLog(mQueue_MTLog, mQueue_CDR, MySetting.LoadMT_Thread_Number, i);
				ListSaveMTLog[i].setPriority(Thread.MAX_PRIORITY);
				ListSaveMTLog[i].start();
				Thread.sleep(100);
			}
			System.out.println("Chay thread SaveCDRQueue");
			ListSaveCDRQueue = new SaveCDRQueue[MySetting.SaveCDRQueue_Thread_Number];
			for (int i = 0; i < MySetting.SaveCDRQueue_Thread_Number; i++)
			{
				ListSaveCDRQueue[i] = new SaveCDRQueue(mQueue_CDR, MySetting.LoadMT_Thread_Number, i);
				ListSaveCDRQueue[i].setPriority(Thread.MAX_PRIORITY);
				ListSaveCDRQueue[i].start();
				Thread.sleep(100);
			}

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	/**
	 * Lưu các queue xuống file .dat
	 */
	void SaveQueueThread()
	{
		try
		{
			SaveMTObject(MySetting.MTQueue_Path_Save, mQueue_MT.getVector());
			SaveMTObject(MySetting.MTLogQueue_Path_Save, mQueue_MTLog.getVector());
			SaveMTObject(MySetting.CDRQueue_Path_Save, mQueue_CDR.getVector());
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	Vector<MTObject> LoadMTObject(String FileName)
	{
		Vector<MTObject> mList = new Vector<MTObject>();
		boolean flag = true;
		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		FileOutputStream fout = null;
		try
		{
			File mFile = new File(FileName);
			if (!mFile.exists()) return mList;

			fin = new java.io.FileInputStream(FileName);

			if (fin.available() <= 0) { return mList; }

			objIn = new ObjectInputStream(fin);

			while (flag)
			{
				try
				{
					MTObject mObject = (MTObject) objIn.readObject();
					mList.add(mObject);
				}
				catch (Exception ex)
				{
					flag = false;
				}
			}
			return mList;

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
		finally
		{
			try
			{
				if (fin != null) fin.close();
				fout = new java.io.FileOutputStream(FileName, false);
				fout.close();
			}
			catch (Exception ex)
			{
				mLog.log.error(ex);
			}
		}
		return mList;
	}
	
	void SaveMTObject(String FileName,Vector<MTObject> mList)
	{
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		try
		{

			fout = new java.io.FileOutputStream(FileName, false);
			objOut = new ObjectOutputStream(fout);

			for (Iterator<MTObject> mItem = mList.listIterator(); mItem.hasNext();)
			{
				Object mObject = mItem.next();
				objOut.writeObject(mObject);
				objOut.flush();
			}
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
		finally
		{
			try
			{
				if (objOut != null) objOut.close();
				if (fout != null) fout.close();
			}
			catch (IOException ex)
			{
				mLog.log.error(ex);
			}
		}

	}

	/**
	 * Đọc các queue từ file .dat
	 */
	void LoadQueueThread()
	{
		try
		{
			// Load các thread đã lưuu
			Vector<MTObject> Temp_MTQueue = LoadMTObject(MySetting.MTQueue_Path_Save);
			if (Temp_MTQueue.size() > 0)
			{
				for (Iterator<MTObject> iter = Temp_MTQueue.iterator(); iter.hasNext();)
				{
					MTObject mObject =  iter.next();
					mQueue_MT.add(mObject);
				}
			}

			Vector<MTObject> Temp_MTLogQueue = LoadMTObject(MySetting.MTLogQueue_Path_Save);
			if (Temp_MTLogQueue.size() > 0)
			{
				for (Iterator<MTObject> iter = Temp_MTLogQueue.iterator(); iter.hasNext();)
				{
					MTObject mObject = (MTObject) iter.next();
					mQueue_MTLog.add(mObject);
				}
			}

			Vector<MTObject> Temp_CDRQueue = LoadMTObject(MySetting.CDRQueue_Path_Save);
			if (Temp_CDRQueue.size() > 0)
			{
				for (Iterator<MTObject> iter = Temp_CDRQueue.iterator(); iter.hasNext();)
				{
					MTObject mObject = (MTObject) iter.next();
					mQueue_CDR.add(mObject);
				}
			}

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	/**
	 * Hàm chạy trước khi tắt chương trình
	 */
	public void Closing()
	{
		try
		{
			MySetting.AllowRunning = false;
			System.out.print("\n Choi chut de tat chuong trinh.....");

			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException ex)
			{
				System.out.println(ex.toString());
			}

			System.out.println("Luu cac queue xuong file .dat");
			SaveQueueThread();

			System.out.print("\nTat ngum nao");
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}
}
